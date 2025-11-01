package com.example.enrutador.ordenesmultiarea.dto;


import com.example.enrutador.ordenesmultiarea.util.EstadoEnum;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenDetalleDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String creador;
    private EstadoEnum.EstadoGlobal estadoGlobal;
    private Instant creadaEn;
    private Instant actualizadaEn;
    private List<OrdenAreaDTO> asignaciones;
    private List<HistorialDTO> historial;
}

