package com.review.clients.dtos;

import lombok.*;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UsuarioClientDTO {
    private Long id;
    private String nombreUser;
    private String estado;
    private List<String> directionsDTO;
}