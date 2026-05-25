package com.GameHub.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "despachos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String transportista;

    @Column(unique = true)
    private String tracking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDespacho estado;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;
}