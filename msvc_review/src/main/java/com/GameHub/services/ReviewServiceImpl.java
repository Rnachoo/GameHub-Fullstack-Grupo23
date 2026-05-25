package com.GameHub.services;

import com.GameHub.clients.OrderFeignClient;
import com.GameHub.clients.ProductFeignClient;
import com.GameHub.clients.UserFeignClient;
import com.GameHub.clients.dtos.OrdenClientDTO;
import com.GameHub.clients.dtos.ProductoClientDTO;
import com.GameHub.clients.dtos.UsuarioClientDTO;
import com.GameHub.exceptions.ReviewException;
import com.GameHub.models.Review;
import com.GameHub.models.dtos.ResenaRequestDTO;
import com.GameHub.models.dtos.ResenaResponseDTO;
import com.GameHub.models.dtos.ResenaUpdateDTO;
import com.GameHub.repositories.ResenaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Transactional
    @Override
    public ResenaResponseDTO createResena(ResenaRequestDTO requestDTO) {

        try {
            UsuarioClientDTO usuario = userFeignClient.getUsuarioById(requestDTO.getUsuarioId());
            if (!"Active".equals(usuario.getEstado())) {
                throw new ReviewException("Usuario no encontrado o inactivo.");
            }
        } catch (feign.FeignException e) {
            throw new ReviewException("Usuario no encontrado.");
        }

        try {
            ProductoClientDTO producto = productFeignClient.getProductoById(requestDTO.getProductoId());
            if (!producto.isEstado()) {
                throw new ReviewException("Producto no encontrado o inactivo.");
            }
        } catch (feign.FeignException e) {
            throw new ReviewException("Producto no encontrado.");
        }

        OrdenClientDTO orden;
        try {
            orden = orderFeignClient.getOrdenById(requestDTO.getOrdenId());
        } catch (feign.FeignException e) {
            throw new ReviewException("Orden no encontrada.");
        }

        if (!orden.getUsuarioId().equals(requestDTO.getUsuarioId())) {
            throw new ReviewException("El usuario no es dueño de la orden.");
        }

        if (resenaRepository.existsByUsuarioIdAndProductoIdAndOrdenId(
                requestDTO.getUsuarioId(), requestDTO.getProductoId(), requestDTO.getOrdenId())) {
            throw new ReviewException("Ya existe una reseña para esta compra y producto.");
        }

        Review resena = new Review();
        resena.setUsuarioId(requestDTO.getUsuarioId());
        resena.setProductoId(requestDTO.getProductoId());
        resena.setOrdenId(requestDTO.getOrdenId());
        resena.setPuntuacion(requestDTO.getPuntuacion());
        resena.setComentario(requestDTO.getComentario());
        resena.setEstado("Active");
        resena.setFecha(LocalDateTime.now());

        Review saved = resenaRepository.save(resena);
        log.info("Reseña creada con ID: {}", saved.getId());

        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public ResenaResponseDTO getResenaById(Long id) { //busca reseña x id
        Review resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ReviewException("Reseña no encontrada con ID: " + id));
        log.info("Reseña encontrada con ID: {}", id);
        return toResponseDTO(resena);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResenaResponseDTO> getResenasByProductoId(Long productoId) { //lista las reseñas de un producto
        List<Review> resenas = resenaRepository.findByProductoId(productoId);
        log.info("Reseñas encontradas para producto ID: {}", productoId);
        return resenas.stream().map(this::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResenaResponseDTO> getResenasByUsuarioId(Long usuarioId) { //lista las reseñas de un usuario
        List<Review> resenas = resenaRepository.findByUsuarioId(usuarioId);
        log.info("Reseñas encontradas para usuario ID: {}", usuarioId);
        return resenas.stream().map(this::toResponseDTO).toList();
    }

    @Transactional
    @Override
    public ResenaResponseDTO updateResena(Long id, ResenaUpdateDTO requestDTO) {
        Review resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ReviewException("Reseña no encontrada con ID: " + id));

        if ("Inactive".equals(resena.getEstado())) {
            throw new ReviewException("No se puede actualizar una reseña moderada.");
        }

        resena.setPuntuacion(requestDTO.getPuntuacion());
        resena.setComentario(requestDTO.getComentario());

        Review updated = resenaRepository.save(resena);
        log.info("Reseña actualizada con ID: {}", id);
        return toResponseDTO(updated);
    }

    @Transactional
    @Override
    public ResenaResponseDTO moderarResena(Long id) { //desactiva una reseña (moderacion)
        Review resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ReviewException("Reseña no encontrada con ID: " + id));

        resena.setEstado("Inactive");
        Review updated = resenaRepository.save(resena);
        log.info("Reseña moderada con ID: {}", id);
        return toResponseDTO(updated);
    }

    private ResenaResponseDTO toResponseDTO(Review resena) {
        ResenaResponseDTO dto = new ResenaResponseDTO();
        dto.setId(resena.getId());
        dto.setUsuarioId(resena.getUsuarioId());
        dto.setProductoId(resena.getProductoId());
        dto.setOrdenId(resena.getOrdenId());
        dto.setPuntuacion(resena.getPuntuacion());
        dto.setComentario(resena.getComentario());
        dto.setEstado(resena.getEstado());
        dto.setFecha(resena.getFecha());
        return dto;
    }
}