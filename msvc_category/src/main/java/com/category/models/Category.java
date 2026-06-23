package com.category.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="categorias")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Category {

    @NotBlank(message = "El nombre de cuenta es obligatorio")
    @Column(unique = true)
    private String nombreCategory;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private Long id;

    private String descripcion;
    private String estado;
}
