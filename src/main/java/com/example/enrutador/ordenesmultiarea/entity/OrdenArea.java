package com.example.enrutador.ordenesmultiarea.entity;

import com.example.enrutador.ordenesmultiarea.util.EstadoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.Instant;

@Entity
@Table(name = "orden_area",
        indexes = {
                @Index(name = "idx_orden_area_orden", columnList = "orden_id"),
                @Index(name = "idx_orden_area_area", columnList = "area_id"),
                @Index(name = "idx_orden_area_estado", columnList = "estado_parcial")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @Column(name = "asignada_a", length = 150)
    private String asignadaA;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_parcial", length = 50, nullable = false)
    private EstadoEnum.EstadoParcial estadoParcial = EstadoEnum.EstadoParcial.NUEVA;

    @Column(name = "seg_acumulados", nullable = false)
    private Integer segAcumulados = 0;

    @UpdateTimestamp
    @Column(name = "ultima_actualizacion")
    private Instant ultimaActualizacion;
}


