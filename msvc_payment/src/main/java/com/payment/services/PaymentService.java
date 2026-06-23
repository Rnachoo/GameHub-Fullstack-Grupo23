package com.payment.services;

import com.payment.models.dtos.PaymentDetalleDTO;
import com.payment.models.dtos.PaymentSaveDTO;
import com.payment.models.dtos.PaymentUpdateEstadoDTO;

import java.util.List;

public interface PaymentService {
    List<PaymentDetalleDTO> findAllByOrdenId(Long ordenId);
    List<PaymentDetalleDTO> findAllByEstado(String estado);
    PaymentDetalleDTO findById(Long id);
    PaymentDetalleDTO save (PaymentSaveDTO paymentSaveDTO);
    PaymentDetalleDTO updateEstado(Long id, PaymentUpdateEstadoDTO paymentUpdateEstadoDTO);
    PaymentDetalleDTO nullById(Long id);
}
