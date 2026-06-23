package com.payment.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PaymentUpdateEstadoDTO {
    @NotBlank(message = "El estado del pago es obligatorio")
    private String estado;

}
