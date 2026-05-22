package com.GameHub.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PaymentSaveDTO {
    @NotNull(message = "El ID de la orden es obligatorio")
    private Long ordenId;
    @NotNull(message = "El monto es obligatorio")
    private double monto;
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodo;
    private String codigoTransaccion;

}
