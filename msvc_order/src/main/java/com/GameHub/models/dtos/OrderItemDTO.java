package com.GameHub.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private Long cantidad;
    private Long precioUnitario;
}
