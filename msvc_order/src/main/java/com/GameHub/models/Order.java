package com.GameHub.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Column(nullable = false)
    private Long userId;

    @NotNull(message = "La fecha de la orden es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fecha;

    @NotBlank(message = "El estado de la orden es obligatorio")
    @Column(nullable = false)
    private String estado; // PENDIENTE_PAGO, PAGADA, CANCELADA

    @NotNull(message = "El subtotal es obligatorio")
    @Column(nullable = false)
    private Long subtotal;

    @NotNull(message = "El descuento es obligatorio")
    @Column(nullable = false)
    private Long descuento;

    @NotNull(message = "El total es obligatorio")
    @Column(nullable = false)
    private Long total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

}