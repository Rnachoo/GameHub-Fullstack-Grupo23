package com.GameHub.services;

import com.GameHub.exceptions.PromotionException;
import com.GameHub.models.Promotion;
import com.GameHub.models.dtos.PromotionDetalleDTO;
import com.GameHub.models.dtos.PromotionSaveDTO;
import com.GameHub.models.dtos.PromotionUpdateDateDTO;
import com.GameHub.repositories.PromotionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Slf4j
public class PromotionServiceImpl implements PromotionService{
    @Autowired
    private PromotionRepository promotionRepository;

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
    public List<PromotionDetalleDTO> findCurrent(String estado) {
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
                () -> new PromotionException("Cuenta con Id "+ id + " no encontrada"));
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
        if(this.promotionRepository.findByCodigo(promotionSaveDTO.getCodigo()).isPresent()){
            throw new PromotionException("Promocion con codigo "+promotionSaveDTO.getCodigo()+ " no encontrada");
        }
        Promotion promotion = new Promotion();
        promotion.setCodigo(promotionSaveDTO.getEstado());
        promotion.setValor(promotionSaveDTO.getValor());
        promotion.setTipo(promotionSaveDTO.getTipo());
        promotion.setFechaInicio(promotionSaveDTO.getFechaInicio());
        promotion.setFechaFin(promotionSaveDTO.getFechaFin());
        promotion.setMontoMinimo(promotionSaveDTO.getMontoMinimo());
        promotion.setUsosMaximos(promotionSaveDTO.getUsosMaximos());
        promotion.setEstado("Active");

        promotion = promotionRepository.save(promotion);
        log.info("Promocion con codigo "+promotion.getCodigo()+" creada con exito!");
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
        log.info("Cuenta con ID "+ id+ " ha sido desactivada");

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
