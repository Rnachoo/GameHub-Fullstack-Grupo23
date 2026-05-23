package com.GameHub.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="pagos")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @NotNull(message = "El ID de la orden es obligatorio")
    @Column(name = "order_id", nullable = false)
    private Long ordenId;

    @NotNull(message = "El monto es obligatorio")
    private double monto;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodo;

    @NotBlank(message = "El estado del pago es obligatorio")
    private String estado;

    @Column(name = "codigo_transaccion", unique = true)
    private String codigoTransaccion;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDateTime fecha;




}
