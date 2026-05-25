package com.GameHub.models.dtos;

import com.GameHub.models.EstadoDespacho;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class DespachoResponseDTO {

    private Long id;
    private Long ordenId;
    private Long usuarioId;
    private String direccion;
    private String transportista;
    private String tracking;
    private EstadoDespacho estado;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEntrega;
}