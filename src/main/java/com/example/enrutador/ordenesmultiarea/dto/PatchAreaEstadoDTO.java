package com.example.enrutador.ordenesmultiarea.dto;


import com.example.enrutador.ordenesmultiarea.util.EstadoEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchAreaEstadoDTO {
    @NotNull
    private EstadoEnum.EstadoParcial estadoParcial;

    private String asignadaA;
}

