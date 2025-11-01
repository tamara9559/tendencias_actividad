package com.example.enrutador.ordenesmultiarea.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionesRequestDTO {
    @NotEmpty
    private List<Integer> areaIds;

    // opcional: map areaId -> asignadaA
    private Map<Integer, String> asignadaAporArea;
}

