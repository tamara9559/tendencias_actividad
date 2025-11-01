package com.example.enrutador.ordenesmultiarea.entity;


import com.example.enrutador.ordenesmultiarea.util.EstadoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 150)
    private String creador;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_global", nullable = false, length = 50)
    private EstadoEnum.EstadoGlobal estadoGlobal = EstadoEnum.EstadoGlobal.NUEVA;

    @CreationTimestamp
    @Column(name = "creada_en", updatable = false)
    private Instant creadaEn;

    @UpdateTimestamp
    @Column(name = "actualizada_en")
    private Instant actualizadaEn;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrdenArea> asignaciones = new ArrayList<>();

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Historial> historial = new ArrayList<>();
}


