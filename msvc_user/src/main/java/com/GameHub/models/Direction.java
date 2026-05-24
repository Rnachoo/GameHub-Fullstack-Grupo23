package com.GameHub.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "directions")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Direction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private User user;

    //El informe no lo especifica, pero estos apartados no pueden ser vacio dado que no se encontraría la dirreción exacta para entregas de producto.
    @NotBlank(message = "El campo de Comuna no puede ser vacio")
    private String comuna;

    @NotBlank(message = "El campo de Ciudad no puede ser vacio")
    private String ciudad;

    @NotBlank(message = "El campo de Calle no puede ser vacio")
    private String calle;

    @NotBlank(message = "El campo de Numero no puede ser vacio")
    private String numero;
}
