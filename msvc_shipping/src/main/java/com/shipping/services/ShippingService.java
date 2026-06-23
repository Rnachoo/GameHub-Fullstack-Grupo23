package com.shipping.services;

import com.shipping.models.dtos.DespachoRequestDTO;
import com.shipping.models.dtos.DespachoResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ShippingService {
    DespachoResponseDTO createDespacho(DespachoRequestDTO requestDTO);
    DespachoResponseDTO getDespachoById(Long id);
    List<DespachoResponseDTO> getDespachosByOrdenId(Long ordenId);
    List<DespachoResponseDTO> getDespachosByEstado(String estado);
    List<DespachoResponseDTO> getAllDespachos();
    DespachoResponseDTO cancelarDespacho(Long id);
    DespachoResponseDTO updateEstadoDespacho(Long id, String estado, String tracking, LocalDateTime fechaEntrega);

}