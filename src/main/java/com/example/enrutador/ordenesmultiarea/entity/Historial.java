package com.example.enrutador.ordenesmultiarea.entity;


import com.example.enrutador.ordenesmultiarea.util.EstadoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.Instant;

@Entity
@Table(name = "historial", indexes = {
        @Index(name = "idx_historial_orden", columnList = "orden_id"),
        @Index(name = "idx_historial_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @Column(length = 150, nullable = false)
    private String evento;

    @Column(columnDefinition = "TEXT")
    private String detalle;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_global", length = 50, nullable = false)
    private EstadoEnum.EstadoGlobal estadoGlobal;

    @Column(length = 150)
    private String actor;

    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private Instant timestamp;
}


