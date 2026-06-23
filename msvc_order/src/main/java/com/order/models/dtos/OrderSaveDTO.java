package com.order.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderSaveDTO {
    private Long userId;
    private String codigoPromocion;
    private List<OrderSaveItemDTO> items;
}
