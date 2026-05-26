package com.GameHub.services;

import com.GameHub.clients.OrdenClient;
import com.GameHub.exceptions.PaymentException;
import com.GameHub.models.Payment;
import com.GameHub.models.dtos.*;
import com.GameHub.repositories.PaymentRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrdenClient ordenClient;

    @Transactional(readOnly = true)
    @Override
    public List<PaymentDetalleDTO> findAllByOrdenId(Long ordenId) {
        log.info("Abriendo lista de pagos por orden");
        return this.paymentRepository.findByOrdenId(ordenId).stream().map(payment -> {
            PaymentDetalleDTO dto = new PaymentDetalleDTO();
            dto.setId(payment.getId());
            dto.setOrdenId(payment.getOrdenId());
            dto.setMonto(payment.getMonto());
            dto.setMetodo(payment.getMetodo());
            dto.setEstado(payment.getEstado());
            dto.setCodigoTransaccion(payment.getCodigoTransaccion());
            dto.setFecha(payment.getFecha());

            return dto;

        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaymentDetalleDTO> findAllByEstado(String estado) {//Arreglar, para poder "filtrar" por estado
        log.info("Abriendo lista de pagos por estado");
        return this.paymentRepository.findByEstado(estado).stream().map(payment -> {
            PaymentDetalleDTO dto = new PaymentDetalleDTO();
            dto.setId(payment.getId());
            dto.setOrdenId(payment.getOrdenId());
            dto.setMonto(payment.getMonto());
            dto.setMetodo(payment.getMetodo());
            dto.setEstado(payment.getEstado());
            dto.setCodigoTransaccion(payment.getCodigoTransaccion());
            dto.setFecha(payment.getFecha());

            return dto;

        }).toList();
    }


    @Transactional(readOnly = true)
    @Override
    public PaymentDetalleDTO findById(Long id) {
        log.info("Buscando pagos registrados por el id");
        Payment payment = this.paymentRepository.findById(id).orElseThrow(
                () -> new PaymentException("Pago con ID " + id + " no encontrado"));
        PaymentDetalleDTO dto = new PaymentDetalleDTO();
        dto.setId(payment.getId());
        dto.setOrdenId(payment.getOrdenId());
        dto.setMonto(payment.getMonto());
        dto.setMetodo(payment.getMetodo());
        dto.setEstado(payment.getEstado());
        dto.setCodigoTransaccion(payment.getCodigoTransaccion());
        dto.setFecha(payment.getFecha());

        return dto;
    }

    @Transactional
    @Override
    public PaymentDetalleDTO save(PaymentSaveDTO paymentSaveDTO) {

        if (paymentSaveDTO.getOrdenId() == null) {
            throw new PaymentException("El ID de la orden es obligatorio");
        }
        OrderDTO orden;
        try {
            orden = ordenClient.getOrdenById(paymentSaveDTO.getOrdenId());
        } catch (FeignException e) {
            log.error("Error al consultar la orden ID "+ paymentSaveDTO.getOrdenId());
            throw new PaymentException("La orden a pagar no existe o el servicio no responde");
        }
        if (!orden.getTotal().equals(paymentSaveDTO.getMonto())) {
            throw new PaymentException("El monto de la compra debe coincidir con el total de la orden");
        }
        if (this.paymentRepository.existsByOrdenIdAndEstado(paymentSaveDTO.getOrdenId(), "APROBADO")) {
            throw new PaymentException("Esta orden ya ha sido pagada");//No duplicar pagos
        }
        Payment payment = new Payment();
        payment.setOrdenId(paymentSaveDTO.getOrdenId());
        payment.setMonto(paymentSaveDTO.getMonto());
        payment.setMetodo(paymentSaveDTO.getMetodo());
        payment.setEstado("APROBADO");
        payment.setCodigoTransaccion(UUID.randomUUID().toString());
        payment.setFecha(java.time.LocalDateTime.now());

        payment = paymentRepository.save(payment);
        log.info("Pago registrado con exito");
        try {
            log.info("Notificando al order-service para actualizar estado de la orden " + orden.getId());

            OrderUpdateEstadoDTO estadoDTO = new OrderUpdateEstadoDTO();
            estadoDTO.setEstado("PAGADA");

            ordenClient.actualizarEstadoOrden(orden.getId(), estadoDTO);
        } catch (FeignException e) {
            log.error("Error critico al actualizar el estado en order-service para la orden ID"+orden.getId());
            throw new PaymentException("El pago fue procesado, pero no se pudo actualizar el estado de la orden. Operación cancelada.");
        }

        PaymentDetalleDTO dto = new PaymentDetalleDTO();
        dto.setId(payment.getId());
        dto.setOrdenId(payment.getOrdenId());
        dto.setMonto(payment.getMonto());
        dto.setMetodo(payment.getMetodo());
        dto.setEstado(payment.getEstado());
        dto.setCodigoTransaccion(payment.getCodigoTransaccion());
        dto.setFecha(payment.getFecha());

        return dto;
    }

    @Transactional
    @Override
    public PaymentDetalleDTO updateEstado(Long id, PaymentUpdateEstadoDTO paymentUpdateEstadoDTO) {
        return this.paymentRepository.findById(id).map(payment -> {
            payment.setEstado(paymentUpdateEstadoDTO.getEstado());
            log.info("Estado del pago actualizado con exito");
            payment = this.paymentRepository.save(payment);
            PaymentDetalleDTO dto = new PaymentDetalleDTO();
            dto.setId(payment.getId());
            dto.setOrdenId(payment.getOrdenId());
            dto.setMonto(payment.getMonto());
            dto.setMetodo(payment.getMetodo());
            dto.setEstado(payment.getEstado());
            dto.setCodigoTransaccion(payment.getCodigoTransaccion());
            dto.setFecha(payment.getFecha());
            return dto;

        }).orElseThrow(
                () -> new PaymentException("Pago no encontrado, no se puede actualizar el estado")
        );
}


    @Transactional
    @Override
    public PaymentDetalleDTO nullById(Long id) {
        Payment payment = this.paymentRepository.findById(id).orElseThrow(
                () -> new PaymentException("Pago con ID " + id+ " no encontrada"));
        payment.setEstado("ANULADO");
        payment = paymentRepository.save(payment);
        log.info("Pago con id "+id+" ha sido anulado");
        PaymentDetalleDTO dto = new PaymentDetalleDTO();
        dto.setId(payment.getId());
        dto.setOrdenId(payment.getOrdenId());
        dto.setMonto(payment.getMonto());
        dto.setMetodo(payment.getMetodo());
        dto.setEstado(payment.getEstado());
        dto.setCodigoTransaccion(payment.getCodigoTransaccion());
        dto.setFecha(payment.getFecha());
        return dto;
    }


}
