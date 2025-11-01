package com.example.enrutador.ordenesmultiarea.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "areas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 150)
    private String responsable;

    @Column(name = "medio_contacto", length = 150)
    private String medioContacto;
}


