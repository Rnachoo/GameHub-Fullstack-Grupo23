package com.GameHub.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventarios")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @NotNull(message = "El ID del producto es obligatorio")
    @Column(unique = true, nullable = false)
    private Long productId;

    @NotNull(message = "El stock disponible es obligatorio")
    @Column(nullable = false)
    private Long stockDisponible;

    @NotNull(message = "El stock reservado es obligatorio")
    @Column(nullable = false)
    private Long stockReservado;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Column(nullable = false)
    private Long stockMinimo;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @OneToMany(mappedBy = "inventario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoInventario> movimientos = new ArrayList<>();

}
