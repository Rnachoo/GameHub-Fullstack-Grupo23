package com.GameHub.services;

import com.GameHub.models.dtos.PaymentDetalleDTO;
import com.GameHub.models.dtos.PaymentSaveDTO;
import com.GameHub.models.dtos.PaymentUpdateEstadoDTO;

import java.util.List;

public interface PaymentService {
    List<PaymentDetalleDTO> findAllByOrdenId(Long ordenId);
    List<PaymentDetalleDTO> findAllByEstado(String estado);
    PaymentDetalleDTO findById(Long id);
    PaymentDetalleDTO save (PaymentSaveDTO paymentSaveDTO);
    PaymentDetalleDTO updateEstado(PaymentUpdateEstadoDTO paymentUpdateEstadoDTO);
    PaymentDetalleDTO nullById();
}
