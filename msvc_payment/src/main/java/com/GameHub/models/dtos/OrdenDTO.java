package com.GameHub.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrdenDTO {
    private Long id;
    private double total;
    private String estado;
}