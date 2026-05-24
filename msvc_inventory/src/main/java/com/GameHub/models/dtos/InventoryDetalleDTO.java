package com.GameHub.models.dtos;

import com.GameHub.models.MovimientoInventario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class InventoryDetalleDTO {
    private Long id;
    private Long productId;
    private Long stockDisponible;
    private Long stockReservado;
    private Long stockMinimo;
    private String ubicacion;
    private List<MovimientoDetalleDTO> movimientosDTO;



}
