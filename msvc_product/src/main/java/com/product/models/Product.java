package com.product.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Productos")
@Getter
@Setter
@ToString
@NoArgsConstructor


public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String marca;

    private String modelo;

    @Column(nullable = false)
    private Long precio;

    @Column(name =  "categoria_id", nullable = false)
    private Long categoriaId;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private String estado;

}