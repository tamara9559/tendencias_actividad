package com.example.enrutador.ordenesmultiarea.dto;

import com.example.enrutador.ordenesmultiarea.util.EstadoEnum;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialDTO {
    private Long id;
    private Long ordenId;
    private String evento;
    private String detalle;
    private EstadoEnum.EstadoGlobal estadoGlobal;
    private String actor;
    private Instant timestamp;
}

