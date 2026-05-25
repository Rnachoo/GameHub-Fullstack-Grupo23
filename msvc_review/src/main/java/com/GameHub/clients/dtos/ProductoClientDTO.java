package com.GameHub.clients.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductoClientDTO {
    private Long id;
    private String nombre;
    private boolean estado;
}