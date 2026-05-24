package com.GameHub.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class InventoryCantidadDTO {
    @NotNull(message = "La cantidad es obligatoria")
    private Long cantidad;
}
