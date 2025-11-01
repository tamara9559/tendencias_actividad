package com.example.enrutador.ordenesmultiarea.dto;


import com.example.enrutador.ordenesmultiarea.util.EstadoEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenAreaDTO {
    private Long id;
    private Integer areaId;
    private String areaNombre;
    private String asignadaA;
    private EstadoEnum.EstadoParcial estadoParcial;
    private Integer segAcumulados;
}

