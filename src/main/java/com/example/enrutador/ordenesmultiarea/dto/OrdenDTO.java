package com.example.enrutador.ordenesmultiarea.dto;


import com.example.enrutador.ordenesmultiarea.util.EstadoEnum;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String creador;
    private EstadoEnum.EstadoGlobal estadoGlobal;
    private Instant creadaEn;
    private Instant actualizadaEn;
}

