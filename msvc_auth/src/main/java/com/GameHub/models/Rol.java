package com.GameHub.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Long rolId;

    @Column(unique = true, nullable = false)
    private String nombre; // Ej: "ROLE_USER", "ROLE_ADMIN"

    public Rol(String nombre){
        this.nombre = nombre;
    }
}