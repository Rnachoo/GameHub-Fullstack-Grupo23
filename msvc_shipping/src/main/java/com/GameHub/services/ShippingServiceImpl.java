package com.GameHub.services;

import com.GameHub.clients.OrderFeignClient;
import com.GameHub.clients.UserFeignClient;
import com.GameHub.clients.dtos.OrdenClientDTO;
import com.GameHub.clients.dtos.UsuarioClientDTO;
import com.GameHub.exceptions.ShippingException;
import com.GameHub.models.Despacho;
import com.GameHub.models.EstadoDespacho;
import com.GameHub.models.dtos.DespachoRequestDTO;
import com.GameHub.models.dtos.DespachoResponseDTO;
import com.GameHub.repositories.DespachoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private DespachoRepository despachoRepository;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Transactional
    @Override
    public DespachoResponseDTO createDespacho(DespachoRequestDTO requestDTO) { //crea el despacho y valida direccion, tracking e id del user
        OrdenClientDTO orden = orderFeignClient.getOrdenById(requestDTO.getOrdenId());
        if (orden == null || !orden.getEstado().equals("PAGADA")) {
            throw new ShippingException("Solo se puede despachar una orden pagada.");
        }

        UsuarioClientDTO usuario = userFeignClient.getUsuarioById(requestDTO.getUsuarioId());
        if (usuario == null) {
            throw new ShippingException("Usuario no encontrado con ID: " + requestDTO.getUsuarioId());
        }
        if (usuario.getDirectionsDTO() == null || usuario.getDirectionsDTO().isEmpty()) {
            throw new ShippingException("El usuario no tiene dirección registrada.");
        }

        if (requestDTO.getTracking() != null) {
            despachoRepository.findByTracking(requestDTO.getTracking())
                    .ifPresent(d -> { throw new ShippingException("El tracking ya existe: " + requestDTO.getTracking()); });
        }

        Despacho despacho = new Despacho();
        despacho.setOrdenId(requestDTO.getOrdenId());
        despacho.setUsuarioId(requestDTO.getUsuarioId());
        despacho.setDireccion(requestDTO.getDireccion());
        despacho.setTransportista(requestDTO.getTransportista());
        despacho.setTracking(requestDTO.getTracking());
        despacho.setEstado(EstadoDespacho.PENDIENTE);
        despacho.setFechaEnvio(LocalDateTime.now());

        Despacho saved = despachoRepository.save(despacho);
        log.info("Despacho creado con ID: {}", saved.getId());

        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public DespachoResponseDTO getDespachoById(Long id) { //busca despacho por id
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new ShippingException("Despacho no encontrado con ID: " + id));
        log.info("Despacho encontrado con ID: {}", id);
        return toResponseDTO(despacho);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DespachoResponseDTO> getAllDespachos() { //lista los despachos
        List<Despacho> despachos = despachoRepository.findAll();
        log.info("Total despachos encontrados: {}", despachos.size());
        return despachos.stream().map(this::toResponseDTO).toList();
    }


    @Transactional(readOnly = true)
    @Override
    public List<DespachoResponseDTO> getDespachosByOrdenId(Long ordenId) { //listar despacho por id
        List<Despacho> despachos = despachoRepository.findByOrdenId(ordenId);
        log.info("Despachos encontrados para orden ID: {}", ordenId);
        return despachos.stream().map(this::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<DespachoResponseDTO> getDespachosByEstado(String estado) { //lista despachos por estado
        EstadoDespacho estadoDespacho = EstadoDespacho.valueOf(estado.toUpperCase());
        List<Despacho> despachos = despachoRepository.findByEstado(estadoDespacho);
        log.info("Despachos encontrados con estado: {}", estado);
        return despachos.stream().map(this::toResponseDTO).toList();
    }

    @Transactional
    @Override
    public DespachoResponseDTO updateEstadoDespacho(Long id, String estado, String tracking, LocalDateTime fechaEntrega) { //cambiar estado de despacho
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new ShippingException("Despacho no encontrado con ID: " + id));

        EstadoDespacho nuevoEstado = EstadoDespacho.valueOf(estado.toUpperCase());

        if (nuevoEstado == EstadoDespacho.ENTREGADO) {
            if (fechaEntrega == null) {
                throw new ShippingException("Debe proporcionar fecha de entrega para marcar como entregado.");
            }
            despacho.setFechaEntrega(fechaEntrega);
        }


        if (tracking != null && !tracking.isBlank()) {
            despachoRepository.findByTracking(tracking)
                    .ifPresent(d -> { throw new ShippingException("El tracking ya existe: " + tracking); });
            despacho.setTracking(tracking);
        }

        despacho.setEstado(nuevoEstado);
        Despacho updated = despachoRepository.save(despacho);
        log.info("Estado de despacho actualizado a {} para ID: {}", estado, id);
        return toResponseDTO(updated);
    }

    @Transactional
    @Override
    public DespachoResponseDTO cancelarDespacho(Long id) { //cancelar despacho por id
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new ShippingException("Despacho no encontrado con ID: " + id));

        if (despacho.getEstado() == EstadoDespacho.ENTREGADO) {
            throw new ShippingException("No se puede cancelar un despacho ya entregado.");
        }

        despacho.setEstado(EstadoDespacho.CANCELADO);
        Despacho updated = despachoRepository.save(despacho);
        log.info("Despacho cancelado con ID: {}", id);
        return toResponseDTO(updated);
    }

    private DespachoResponseDTO toResponseDTO(Despacho despacho) {
        DespachoResponseDTO dto = new DespachoResponseDTO();
        dto.setId(despacho.getId());
        dto.setOrdenId(despacho.getOrdenId());
        dto.setUsuarioId(despacho.getUsuarioId());
        dto.setDireccion(despacho.getDireccion());
        dto.setTransportista(despacho.getTransportista());
        dto.setTracking(despacho.getTracking());
        dto.setEstado(despacho.getEstado());
        dto.setFechaEnvio(despacho.getFechaEnvio());
        dto.setFechaEntrega(despacho.getFechaEntrega());
        return dto;
    }
}