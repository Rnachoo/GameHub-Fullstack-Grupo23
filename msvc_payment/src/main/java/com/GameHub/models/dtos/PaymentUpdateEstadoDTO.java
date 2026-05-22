package com.GameHub.models.dtos;

import jakarta.validation.constraints.NotBlank;

public class PaymentUpdateEstadoDTO {
    @NotBlank(message = "El estado del pago es obligatorio")
    private String estado;

}
