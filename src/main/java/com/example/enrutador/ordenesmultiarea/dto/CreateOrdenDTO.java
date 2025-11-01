package com.example.enrutador.ordenesmultiarea.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrdenDTO {
    @NotBlank
    private String titulo;

    private String descripcion;

    @NotBlank
    @Email
    private String creador;
}

