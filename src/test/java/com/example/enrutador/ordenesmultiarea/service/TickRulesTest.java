package com.example.enrutador.ordenesmultiarea.service;


import com.example.enrutador.ordenesmultiarea.entity.Orden;
import com.example.enrutador.ordenesmultiarea.entity.OrdenArea;
import com.example.enrutador.ordenesmultiarea.repository.HistorialRepository;
import com.example.enrutador.ordenesmultiarea.repository.OrdenAreaRepository;
import com.example.enrutador.ordenesmultiarea.repository.OrdenRepository;
import com.example.enrutador.ordenesmultiarea.util.EstadoEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Pruebas mínimas de reglas de estado y tick (QA Automation)
 * Objetivo: validar lógica de estados, timeout SLA y acumulación de segundos.
 */
@SpringBootTest
@Transactional
public class TickRulesTest {

    @Autowired
    private OrdenServiceImpl ordenService;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private OrdenAreaRepository ordenAreaRepository;

    @Autowired
    private HistorialRepository historialRepository;

    private Orden orden;

    @BeforeEach
    void setup() {
        // Crear orden base con 2 áreas
        orden = new Orden();
        orden.setTitulo("Verificación QA");
        orden.setDescripcion("Test automatizado de reglas de SLA y estado global");
        orden.setEstadoGlobal(EstadoEnum.EstadoGlobal.NUEVA);
        ordenRepository.save(orden);

        OrdenArea a1 = new OrdenArea();
        a1.setOrden(orden);
        a1.setEstadoParcial(EstadoEnum.EstadoParcial.EN_PROGRESO);
        a1.setSegAcumulados(0);

        OrdenArea a2 = new OrdenArea();
        a2.setOrden(orden);
        a2.setEstadoParcial(EstadoEnum.EstadoParcial.COMPLETADA);
        a2.setSegAcumulados(0);

        ordenAreaRepository.saveAll(List.of(a1, a2));
    }

    @Test
    void testCaso1_EnProgresoYCompletada_GlobalDebeSerEnProgreso() {
        Orden o = ordenRepository.findById(orden.getId()).orElseThrow();
        ordenService.recalcularEstadoGlobal(o);
        assertEquals(EstadoEnum.EstadoGlobal.EN_PROGRESO, o.getEstadoGlobal(), "Debe ser EN_PROGRESO si al menos un área sigue activa");
    }

    @Test
    void testCaso2_TimeoutPorSLA_GlobalPasaAPendienteYEventoRegistrado() throws InterruptedException {
        // Simular tick que supera SLA
        OrdenArea area = ordenAreaRepository.findAll().stream()
                .filter(a -> a.getEstadoParcial() == EstadoEnum.EstadoParcial.EN_PROGRESO)
                .findFirst().orElseThrow();

        // Aumentar segundos manualmente para simular SLA superado
        area.setSegAcumulados(40);
        ordenAreaRepository.save(area);

        // Ejecutar tick que aplica regla SLA
        ordenService.ejecutarTick();

        OrdenArea actualizada = ordenAreaRepository.findById(area.getId()).orElseThrow();
        assertEquals(EstadoEnum.EstadoParcial.VENCIDA, actualizada.getEstadoParcial(),
                "Debe cambiar a VENCIDA al superar SLA");

        Orden o = ordenRepository.findById(orden.getId()).orElseThrow();
        assertEquals(EstadoEnum.EstadoGlobal.PENDIENTE, o.getEstadoGlobal(),
                "El estado global debe pasar a PENDIENTE si hay áreas vencidas");

        long eventosTimeout = historialRepository.findByOrdenIdOrderByTimestampAsc(orden.getId())
                .stream()
                .filter(h -> h.getEvento().toUpperCase().contains("TIMEOUT"))
                .count();

        assertTrue(eventosTimeout > 0, "Debe registrar un evento TIMEOUT en el historial");
    }

    @Test
    void testCaso3_TodasCompletadas_GlobalDebeSerCompletada() {
        // Cambiar ambas a COMPLETADA
        ordenAreaRepository.findAll().forEach(a -> {
            a.setEstadoParcial(EstadoEnum.EstadoParcial.COMPLETADA);
            a.setSegAcumulados(a.getSegAcumulados() + 10);
        });

        Orden o = ordenRepository.findById(orden.getId()).orElseThrow();
        ordenService.recalcularEstadoGlobal(o);

        assertEquals(EstadoEnum.EstadoGlobal.COMPLETADA, o.getEstadoGlobal(), "Global debe ser COMPLETADA si todas lo están");

        // Validar acumulación de segundos
        int total = ordenAreaRepository.findAll().stream()
                .mapToInt(OrdenArea::getSegAcumulados)
                .sum();
        assertTrue(total >= 20, "Debe acumular segundos correctamente");
    }
}
