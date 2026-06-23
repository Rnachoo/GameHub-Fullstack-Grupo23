package com.order.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderDetalleDTO {
    private Long id;
    private Long userId;
    private LocalDateTime fecha;
    private String estado;
    private Long subtotal;
    private Long descuento;
    private Long total;
    private List<OrderItemDTO> items;
}

