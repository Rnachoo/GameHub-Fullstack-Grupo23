package com.review.models.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ResenaResponseDTO {

    private Long id;
    private Long usuarioId;
    private Long productoId;
    private Long ordenId;
    private Integer puntuacion;
    private String comentario;
    private String estado;
    private LocalDateTime fecha;
}