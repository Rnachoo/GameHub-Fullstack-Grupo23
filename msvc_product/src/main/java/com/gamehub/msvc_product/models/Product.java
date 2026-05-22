package com.gamehub.msvc_product.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "Productos")
@Data
@NoArgsConstructor
@AllArgsConstructor

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
    private BigDecimal precio;

    @Column(name =  "categoria_id", nullable = false)
    private Long categoriaId;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private boolean estado;

}