package com.example.enrutador.ordenesmultiarea.service;


import com.example.enrutador.ordenesmultiarea.entity.Historial;
import com.example.enrutador.ordenesmultiarea.entity.Orden;
import com.example.enrutador.ordenesmultiarea.entity.OrdenArea;
import com.example.enrutador.ordenesmultiarea.repository.HistorialRepository;
import com.example.enrutador.ordenesmultiarea.repository.OrdenAreaRepository;
import com.example.enrutador.ordenesmultiarea.repository.OrdenRepository;
import com.example.enrutador.ordenesmultiarea.util.EstadoEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdenAreaService {

    private final OrdenAreaRepository ordenAreaRepository;
    private final OrdenRepository ordenRepository;
    private final HistorialRepository historialRepository;

    @Value("${app.sla.segundos:1800}")
    private int slaSegundos;

    @Value("${app.estado.timeout:VENCIDA}")
    private String estadoTimeout;

    public OrdenAreaService(
            OrdenAreaRepository ordenAreaRepository,
            OrdenRepository ordenRepository,
            HistorialRepository historialRepository
    ) {
        this.ordenAreaRepository = ordenAreaRepository;
        this.ordenRepository = ordenRepository;
        this.historialRepository = historialRepository;
    }

    /**
     * Ejecuta un tick del SLA.
     * - Aumenta seg_acumulados en áreas EN_PROGRESO o PENDIENTE.
     * - Marca VENCIDA si supera SLA.
     * - Recalcula estado global.
     */
    @Transactional
    public void ejecutarTick() {
        List<OrdenArea> activas = ordenAreaRepository.findByEstadoParcialIn(List.of("EN_PROGRESS", "PENDENT"));
        if (activas.isEmpty()) return;

        for (OrdenArea oa : activas) {
            oa.setSegAcumulados(oa.getSegAcumulados() + 10); // N_SEG fijo o configurable si deseas

            // Si supera el SLA
            if (oa.getSegAcumulados() >= slaSegundos && !oa.getEstadoParcial().equals(estadoTimeout)) {
                oa.setEstadoParcial(EstadoEnum.EstadoParcial.valueOf(estadoTimeout));
                registrarHistorialTimeout(oa);
            }

            ordenAreaRepository.save(oa);

            // Recalcular estado global
            recalcularEstadoGlobal(oa.getOrden().getId());
        }
    }

    private void registrarHistorialTimeout(OrdenArea oa) {
        Historial h = new Historial();
        h.setOrden(oa.getOrden());
        h.setEvento("TIMEOUT");
        h.setDetalle("Área " + oa.getArea().getNombre() + " excedió SLA de " + slaSegundos + " segundos");
        h.setEstadoGlobal(EstadoEnum.EstadoGlobal.valueOf(estadoTimeout));
        h.setTimestamp(Instant.from(LocalDateTime.now()));
        h.setActor("Sistema");
        historialRepository.save(h);
    }

    /**
     * Recalcula el estado global de la orden según los estados parciales.
     */
    private void recalcularEstadoGlobal(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId).orElse(null);
        if (orden == null) return;

        List<OrdenArea> areas = ordenAreaRepository.findByOrdenId(ordenId);
        boolean todasCompletadas = areas.stream().allMatch(a -> a.getEstadoParcial().equals("COMPLETADA"));
        boolean algunaVencida = areas.stream().anyMatch(a -> a.getEstadoParcial().equals("VENCIDA"));
        boolean algunaEnProgreso = areas.stream().anyMatch(a -> a.getEstadoParcial().equals("EN_PROGRESO"));

        if (todasCompletadas) {
            orden.setEstadoGlobal(EstadoEnum.EstadoGlobal.valueOf("COMPLETADA"));
        } else if (algunaVencida) {
            orden.setEstadoGlobal(EstadoEnum.EstadoGlobal.valueOf("VENCIDA"));
        } else if (algunaEnProgreso) {
            orden.setEstadoGlobal(EstadoEnum.EstadoGlobal.valueOf("EN_PROGRESO"));
        } else {
            orden.setEstadoGlobal(EstadoEnum.EstadoGlobal.valueOf("PENDIENTE"));
        }

        ordenRepository.save(orden);
    }
}
