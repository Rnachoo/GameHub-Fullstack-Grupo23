package com.GameHub.services;

import com.GameHub.models.dtos.OrderDetalleDTO;
import com.GameHub.models.dtos.OrderSaveDTO;
import com.GameHub.models.dtos.OrderUpdateEstadoDTO;

import java.util.List;

public interface OrderService {
    List<OrderDetalleDTO> findByClient(Long userId);
    List<OrderDetalleDTO> findByEstado(String estado);
    OrderDetalleDTO findById(Long id);
    OrderDetalleDTO save (OrderSaveDTO orderSaveDTO);
    OrderDetalleDTO updateEstado(Long id, OrderUpdateEstadoDTO orderUpdateEstadoDTO);
    void cancelarOrden(Long id);
}
