package com.GameHub.services;

import com.GameHub.clients.CategoryClient;
import com.GameHub.exceptions.PromotionException;
import com.GameHub.models.Promotion;
import com.GameHub.models.dtos.*;
import com.GameHub.repositories.PromotionRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Service
@Slf4j
public class PromotionServiceImpl implements PromotionService{
    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private CategoryClient categoryClient;

    @Transactional(readOnly = true)
    @Override
    public List<PromotionDetalleDTO> findAll() {
        log.info("Abriendo lista de historial de promociones");
        return this.promotionRepository.findAll().stream().map(promotion -> {
            PromotionDetalleDTO dto = new PromotionDetalleDTO();
            dto.setId(promotion.getId());
            dto.setCodigo(promotion.getCodigo());
            dto.setValor(promotion.getValor());
            dto.setTipo(promotion.getTipo());
            dto.setFechaInicio(promotion.getFechaInicio());
            dto.setFechaFin(promotion.getFechaFin());
            dto.setMontoMinimo(promotion.getMontoMinimo());
            dto.setUsosMaximos(promotion.getUsosMaximos());
            dto.setEstado(promotion.getEstado());

            return dto;
        }).toList();

    }

    @Transactional(readOnly = true)
    @Override
    public List<PromotionDetalleDTO> findCurrent() {
        log.info("Abriendo lista de historial de promociones");
        return this.promotionRepository.findByEstado("Active").stream().map(promotion -> {
            PromotionDetalleDTO dto = new PromotionDetalleDTO();
            dto.setId(promotion.getId());
            dto.setCodigo(promotion.getCodigo());
            dto.setValor(promotion.getValor());
            dto.setTipo(promotion.getTipo());
            dto.setFechaInicio(promotion.getFechaInicio());
            dto.setFechaFin(promotion.getFechaFin());
            dto.setMontoMinimo(promotion.getMontoMinimo());
            dto.setUsosMaximos(promotion.getUsosMaximos());
            dto.setEstado(promotion.getEstado());

            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public PromotionDetalleDTO findById(Long id) {
        log.info("Buscando promociones registradas en el sistema!");
        Promotion promotion = this.promotionRepository.findById(id).orElseThrow(
                () -> new PromotionException("Promocion con Id "+ id + " no encontrada"));
        PromotionDetalleDTO dto = new PromotionDetalleDTO();
        dto.setId(promotion.getId());
        dto.setCodigo(promotion.getCodigo());
        dto.setValor(promotion.getValor());
        dto.setTipo(promotion.getTipo());
        dto.setFechaInicio(promotion.getFechaInicio());
        dto.setFechaFin(promotion.getFechaFin());
        dto.setMontoMinimo(promotion.getMontoMinimo());
        dto.setUsosMaximos(promotion.getUsosMaximos());
        dto.setEstado(promotion.getEstado());

        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public PromotionDetalleDTO findByCodigo(String codigo) {
        log.info("Buscando promociones registradas en el sistema!");
        Promotion promotion = this.promotionRepository.findByCodigo(codigo).orElseThrow(
                ()-> new PromotionException("Promocion con codigo "+codigo+" no encontrado"));
        PromotionDetalleDTO dto = new PromotionDetalleDTO();
        dto.setId(promotion.getId());
        dto.setCodigo(promotion.getCodigo());
        dto.setValor(promotion.getValor());
        dto.setTipo(promotion.getTipo());
        dto.setFechaInicio(promotion.getFechaInicio());
        dto.setFechaFin(promotion.getFechaFin());
        dto.setMontoMinimo(promotion.getMontoMinimo());
        dto.setUsosMaximos(promotion.getUsosMaximos());
        dto.setEstado(promotion.getEstado());

        return dto;
    }

    @Transactional
    @Override
    public PromotionDetalleDTO save(PromotionSaveDTO promotionSaveDTO) {
        if (this.promotionRepository.findByCodigo(promotionSaveDTO.getCodigo()).isPresent()) {
            throw new PromotionException("Promocion con codigo " + promotionSaveDTO.getCodigo() + " ya está registrada");
        }
        if (promotionSaveDTO.getCategoryId() != null) {
            try {
                categoryClient.getCategoryById(promotionSaveDTO.getCategoryId());
            } catch (FeignException e) {
                log.error("Error al consultar category-service para el ID " + promotionSaveDTO.getCategoryId());
                throw new PromotionException("La categoría ingresada no existe o el servicio no está disponible");
            }
        }
        Promotion promotion = new Promotion();
        promotion.setCodigo(promotionSaveDTO.getCodigo());
        promotion.setValor(promotionSaveDTO.getValor());
        promotion.setTipo(promotionSaveDTO.getTipo());
        promotion.setFechaInicio(java.time.LocalDateTime.now());
        promotion.setFechaFin(promotionSaveDTO.getFechaFin());
        promotion.setMontoMinimo(promotionSaveDTO.getMontoMinimo());
        promotion.setUsosMaximos(promotionSaveDTO.getUsosMaximos());
        promotion.setEstado("Active");

        promotion = promotionRepository.save(promotion);
        log.info("Promocion con codigo " + promotion.getCodigo() + " creada con exito!");

        PromotionDetalleDTO dto = new PromotionDetalleDTO();
        dto.setId(promotion.getId());
        dto.setCodigo(promotion.getCodigo());
        dto.setValor(promotion.getValor());
        dto.setTipo(promotion.getTipo());
        dto.setFechaInicio(promotion.getFechaInicio());
        dto.setFechaFin(promotion.getFechaFin());
        dto.setMontoMinimo(promotion.getMontoMinimo());
        dto.setUsosMaximos(promotion.getUsosMaximos());
        dto.setEstado(promotion.getEstado());

        return dto;
    }

    @Transactional
    @Override
    public PromotionDetalleDTO updateDate(Long id, PromotionUpdateDateDTO promotionUpdateDateDTO) {
        return this.promotionRepository.findById(id).map(promotion -> {
            promotion.setFechaInicio(promotionUpdateDateDTO.getFechaInicio());
            promotion.setFechaFin(promotionUpdateDateDTO.getFechaFin());
            log.info("Fecha de inicio y fin actualizada con exito!");

            promotion = this.promotionRepository.save(promotion);
            PromotionDetalleDTO dto = new PromotionDetalleDTO();
            dto.setId(promotion.getId());
            dto.setCodigo(promotion.getCodigo());
            dto.setValor(promotion.getValor());
            dto.setTipo(promotion.getTipo());
            dto.setFechaInicio(promotion.getFechaInicio());
            dto.setFechaFin(promotion.getFechaFin());
            dto.setMontoMinimo(promotion.getMontoMinimo());
            dto.setUsosMaximos(promotion.getUsosMaximos());
            dto.setEstado(promotion.getEstado());

            return dto;
        }).orElseThrow(
                ()-> new PromotionException("Promocion no encontrada")
        );
    }


    @Transactional
    @Override
    public PromotionDetalleDTO desactiveById(Long id) {
        Promotion promotion = this.promotionRepository.findById(id).orElseThrow(
                () -> new PromotionException("Promocion con ID" +id+ " no encontrada"));
        promotion.setEstado("Inactive");
        promotion = promotionRepository.save(promotion);
        log.info("Promocion con ID "+ id+ " ha sido desactivada");

        PromotionDetalleDTO dto = new PromotionDetalleDTO();
        dto.setId(promotion.getId());
        dto.setCodigo(promotion.getCodigo());
        dto.setValor(promotion.getValor());
        dto.setTipo(promotion.getTipo());
        dto.setFechaInicio(promotion.getFechaInicio());
        dto.setFechaFin(promotion.getFechaFin());
        dto.setMontoMinimo(promotion.getMontoMinimo());
        dto.setUsosMaximos(promotion.getUsosMaximos());
        dto.setEstado(promotion.getEstado());

        return dto;
    }

    @Transactional
    @Override
    public PromotionDetalleDTO aplicarPromocion(PromotionAplicarDescuentoDTO aplicarDescuentoDTO, Double totalOrden) {
        String codigo = aplicarDescuentoDTO.getCodigo();
        Promotion promotion = this.promotionRepository.findByCodigo(codigo).orElseThrow(
                () -> new PromotionException("El cupon " + codigo + " no existe"));
        if (!"Active".equalsIgnoreCase(promotion.getEstado())) {
            throw new PromotionException("El cupon no está activo");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promotion.getFechaInicio()) || now.isAfter(promotion.getFechaFin())) {
            throw new PromotionException("El cupon está vencido o aún no es valido para esta fecha");
        }
        if (totalOrden < promotion.getMontoMinimo()) {
            throw new PromotionException("El total de la orden no alcanza el mínimo requerido para usar este cupón");
        }
        if (promotion.getUsosMaximos() <= 0) {
            throw new PromotionException("El cupon ha alcanzado su limite de usos maximos");
        }

        promotion.setUsosMaximos(promotion.getUsosMaximos() - 1);
        promotion = promotionRepository.save(promotion);
        log.info("cupon" +codigo+" aplicado exitosamente, Usos restantes: " +promotion.getUsosMaximos());

        PromotionDetalleDTO dto = new PromotionDetalleDTO();
        dto.setId(promotion.getId());
        dto.setCodigo(promotion.getCodigo());
        dto.setValor(promotion.getValor());
        dto.setTipo(promotion.getTipo());
        dto.setFechaInicio(promotion.getFechaInicio());
        dto.setFechaFin(promotion.getFechaFin());
        dto.setMontoMinimo(promotion.getMontoMinimo());
        dto.setUsosMaximos(promotion.getUsosMaximos());
        dto.setEstado(promotion.getEstado());

        return dto;
    }
}
