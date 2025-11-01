package com.example.enrutador.ordenesmultiarea.service;


import com.example.enrutador.ordenesmultiarea.dto.*;
import com.example.enrutador.ordenesmultiarea.entity.Area;
import com.example.enrutador.ordenesmultiarea.entity.Historial;
import com.example.enrutador.ordenesmultiarea.entity.Orden;
import com.example.enrutador.ordenesmultiarea.entity.OrdenArea;
import com.example.enrutador.ordenesmultiarea.mapper.DTOMapper;
import com.example.enrutador.ordenesmultiarea.repository.AreaRepository;
import com.example.enrutador.ordenesmultiarea.repository.HistorialRepository;
import com.example.enrutador.ordenesmultiarea.repository.OrdenAreaRepository;
import com.example.enrutador.ordenesmultiarea.repository.OrdenRepository;
import com.example.enrutador.ordenesmultiarea.util.EstadoEnum;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public abstract class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final AreaRepository areaRepository;
    private final OrdenAreaRepository ordenAreaRepository;
    private final HistorialRepository historialRepository;
    private final DTOMapper mapper;

    @Override
    @Transactional
    public OrdenDTO createOrden(CreateOrdenDTO dto) {
        Orden orden = Orden.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .creador(dto.getCreador())
                .estadoGlobal(EstadoEnum.EstadoGlobal.NUEVA)
                .build();
        orden = ordenRepository.save(orden);

        Historial h = Historial.builder()
                .orden(orden)
                .evento("CREACION")
                .detalle("Orden creada")
                .estadoGlobal(orden.getEstadoGlobal())
                .actor(dto.getCreador())
                .build();
        historialRepository.save(h);

        return mapper.toOrdenDTO(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenDTO> listOrdenes(Optional<String> estadoStr) {
        if (estadoStr.isPresent()) {
            try {
                EstadoEnum.EstadoGlobal eg = EstadoEnum.EstadoGlobal.valueOf(estadoStr.get());
                return ordenRepository.findByEstadoGlobal(eg).stream()
                        .map(mapper::toOrdenDTO).collect(Collectors.toList());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Estado desconocido: " + estadoStr.get());
            }
        } else {
            return ordenRepository.findAll().stream().map(mapper::toOrdenDTO).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenDetalleDTO getOrden(Long id) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada: " + id));
        List<OrdenArea> asigns = ordenAreaRepository.findByOrdenId(id);
        List<Historial> hist = historialRepository.findByOrdenIdOrderByTimestampDesc(id);
        return mapper.toOrdenDetalleDTO(orden, asigns, hist);
    }

    @Override
    @Transactional
    public OrdenDetalleDTO asignarAreas(Long ordenId, AsignacionesRequestDTO dto) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada: " + ordenId));

        for (Integer areaId : dto.getAreaIds()) {
            Area area = areaRepository.findById(areaId)
                    .orElseThrow(() -> new EntityNotFoundException("Area no encontrada: " + areaId));
            Optional<OrdenArea> existing = ordenAreaRepository.findByOrdenIdAndAreaId(ordenId, areaId);
            if (existing.isPresent()) {
                // actualizar asignadaA si viene
                if (dto.getAsignadaAporArea() != null && dto.getAsignadaAporArea().containsKey(areaId)) {
                    OrdenArea oa = existing.get();
                    oa.setAsignadaA(dto.getAsignadaAporArea().get(areaId));
                    ordenAreaRepository.save(oa);
                }
                continue;
            }
            OrdenArea oa = OrdenArea.builder()
                    .orden(orden)
                    .area(area)
                    .asignadaA(dto.getAsignadaAporArea() != null ? dto.getAsignadaAporArea().get(areaId) : null)
                    .estadoParcial(EstadoEnum.EstadoParcial.ASIGNADA)
                    .segAcumulados(0)
                    .build();
            ordenAreaRepository.save(oa);
            Historial h = Historial.builder()
                    .orden(orden)
                    .evento("ASIGNACION_AREA")
                    .detalle("Asignada area: " + area.getNombre())
                    .estadoGlobal(orden.getEstadoGlobal())
                    .actor(dto.getAsignadaAporArea() != null ? dto.getAsignadaAporArea().get(areaId) : "despachador")
                    .build();
            historialRepository.save(h);
        }

        // recalcular estado global
        orden.setEstadoGlobal(recalcularEstadoGlobalPorAsignaciones(orden));
        orden = ordenRepository.save(orden);

        List<OrdenArea> asigns = ordenAreaRepository.findByOrdenId(ordenId);
        List<Historial> hist = historialRepository.findByOrdenIdOrderByTimestampDesc(ordenId);
        return mapper.toOrdenDetalleDTO(orden, asigns, hist);
    }



    @Override
    @Transactional
    public OrdenAreaDTO updateAreaState(Long ordenId, Integer areaId, PatchAreaEstadoDTO dto) {
        OrdenArea oa = ordenAreaRepository.findByOrdenIdAndAreaId(ordenId, areaId)
                .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada para orden " + ordenId + " y area " + areaId));

        // Validación de transición básica (ejemplo)
        if (oa.getEstadoParcial() == EstadoEnum.EstadoParcial.COMPLETADA &&
                dto.getEstadoParcial() != EstadoEnum.EstadoParcial.COMPLETADA) {
            throw new IllegalStateException("No se permite revertir de COMPLETADA a otro estado.");
        }

        if (dto.getAsignadaA() != null) {
            oa.setAsignadaA(dto.getAsignadaA());
        }
        oa.setEstadoParcial(dto.getEstadoParcial());
        oa = ordenAreaRepository.save(oa);

        // Registrar historial
        Historial h = Historial.builder()
                .orden(oa.getOrden())
                .evento("CAMBIO_ESTADO_AREA")
                .detalle("Area " + oa.getArea().getNombre() + " -> " + dto.getEstadoParcial())
                .estadoGlobal(oa.getOrden().getEstadoGlobal())
                .actor(dto.getAsignadaA() != null ? dto.getAsignadaA() : oa.getAsignadaA())
                .build();
        historialRepository.save(h);

        // Recalcular estado global e insertar historial si cambia
        EstadoEnum.EstadoGlobal prev = oa.getOrden().getEstadoGlobal();
        EstadoEnum.EstadoGlobal nuevo = recalcularEstadoGlobalPorAsignaciones(oa.getOrden());
        if (prev != nuevo) {
            Orden orden = oa.getOrden();
            orden.setEstadoGlobal(nuevo);
            ordenRepository.save(orden);
            Historial h2 = Historial.builder()
                    .orden(oa.getOrden())
                    .evento("CAMBIO_ESTADO_GLOBAL")
                    .detalle("Estado global cambiado: " + nuevo)
                    .estadoGlobal(nuevo)
                    .actor("sistema")
                    .build();
            historialRepository.save(h2);
        }

        return mapper.toOrdenAreaDTO(oa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialDTO> getHistorial(Long ordenId) {
        ordenRepository.findById(ordenId)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada: " + ordenId));
        return historialRepository.findByOrdenIdOrderByTimestampAsc(ordenId)
                .stream()
                .map(mapper::toHistorialDTO)
                .collect(Collectors.toList());
    }


    // ----- helper -----
    private EstadoEnum.EstadoGlobal recalcularEstadoGlobalPorAsignaciones(Orden orden) {
        List<OrdenArea> asigns = ordenAreaRepository.findByOrdenId(orden.getId());
        if (asigns.isEmpty()) {
            return (orden.getEstadoGlobal() == null) ? EstadoEnum.EstadoGlobal.NUEVA : orden.getEstadoGlobal();
        }

        boolean anyVencida = asigns.stream().anyMatch(a -> a.getEstadoParcial() == EstadoEnum.EstadoParcial.VENCIDA);
        if (anyVencida) return EstadoEnum.EstadoGlobal.VENCIDA;

        boolean anyEnProgreso = asigns.stream().anyMatch(a -> a.getEstadoParcial() == EstadoEnum.EstadoParcial.EN_PROGRESO);
        if (anyEnProgreso) return EstadoEnum.EstadoGlobal.EN_PROGRESO;

        boolean anyPendiente = asigns.stream().anyMatch(a -> a.getEstadoParcial() == EstadoEnum.EstadoParcial.PENDIENTE);
        if (anyPendiente) return EstadoEnum.EstadoGlobal.PENDIENTE;

        boolean allCompletadas = asigns.stream().allMatch(a -> a.getEstadoParcial() == EstadoEnum.EstadoParcial.COMPLETADA);
        if (allCompletadas) return EstadoEnum.EstadoGlobal.COMPLETADA;

        // si hay asignaciones y no todas completadas -> ASIGNADA
        return EstadoEnum.EstadoGlobal.ASIGNADA;
    }
}

