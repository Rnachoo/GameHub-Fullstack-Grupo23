package com.product.clients.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategoryClientDTO {
    private Long id;
    private String nombreCategory;
    private String estado;
}