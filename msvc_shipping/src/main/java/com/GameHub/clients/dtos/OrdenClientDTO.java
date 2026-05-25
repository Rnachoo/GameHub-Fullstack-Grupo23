package com.GameHub.clients.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrdenClientDTO {
    private Long id;
    private Long usuarioId;
    private String estado;
}