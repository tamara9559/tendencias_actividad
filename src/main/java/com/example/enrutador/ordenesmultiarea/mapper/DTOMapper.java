package com.example.enrutador.ordenesmultiarea.mapper;

import com.example.enrutador.ordenesmultiarea.dto.HistorialDTO;
import com.example.enrutador.ordenesmultiarea.dto.OrdenAreaDTO;
import com.example.enrutador.ordenesmultiarea.dto.OrdenDTO;
import com.example.enrutador.ordenesmultiarea.dto.OrdenDetalleDTO;
import com.example.enrutador.ordenesmultiarea.entity.Historial;
import com.example.enrutador.ordenesmultiarea.entity.Orden;
import com.example.enrutador.ordenesmultiarea.entity.OrdenArea;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DTOMapper {

    public OrdenDTO toOrdenDTO(Orden o) {
        if (o == null) return null;
        return OrdenDTO.builder()
                .id(o.getId())
                .titulo(o.getTitulo())
                .descripcion(o.getDescripcion())
                .creador(o.getCreador())
                .estadoGlobal(o.getEstadoGlobal())
                .creadaEn(o.getCreadaEn())
                .actualizadaEn(o.getActualizadaEn())
                .build();
    }

    public OrdenAreaDTO toOrdenAreaDTO(OrdenArea oa) {
        if (oa == null) return null;
        return OrdenAreaDTO.builder()
                .id(oa.getId())
                .areaId(oa.getArea() != null ? oa.getArea().getId() : null)
                .areaNombre(oa.getArea() != null ? oa.getArea().getNombre() : null)
                .asignadaA(oa.getAsignadaA())
                .estadoParcial(oa.getEstadoParcial())
                .segAcumulados(oa.getSegAcumulados())
                .build();
    }

    public HistorialDTO toHistorialDTO(Historial h) {
        if (h == null) return null;
        return HistorialDTO.builder()
                .id(h.getId())
                .ordenId(h.getOrden() != null ? h.getOrden().getId() : null)
                .evento(h.getEvento())
                .detalle(h.getDetalle())
                .estadoGlobal(h.getEstadoGlobal())
                .actor(h.getActor())
                .timestamp(h.getTimestamp())
                .build();
    }

    public OrdenDetalleDTO toOrdenDetalleDTO(Orden o, List<OrdenArea> asignaciones, List<Historial> historial) {
        return OrdenDetalleDTO.builder()
                .id(o.getId())
                .titulo(o.getTitulo())
                .descripcion(o.getDescripcion())
                .creador(o.getCreador())
                .estadoGlobal(o.getEstadoGlobal())
                .creadaEn(o.getCreadaEn())
                .actualizadaEn(o.getActualizadaEn())
                .asignaciones(asignaciones.stream().map(this::toOrdenAreaDTO).collect(Collectors.toList()))
                .historial(historial.stream().map(this::toHistorialDTO).collect(Collectors.toList()))
                .build();
    }
}

