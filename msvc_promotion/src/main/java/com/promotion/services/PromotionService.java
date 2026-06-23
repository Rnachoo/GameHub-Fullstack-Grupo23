package com.promotion.services;

import com.promotion.models.dtos.PromotionAplicarDescuentoDTO;
import com.promotion.models.dtos.PromotionDetalleDTO;
import com.promotion.models.dtos.PromotionSaveDTO;
import com.promotion.models.dtos.PromotionUpdateDateDTO;

import java.util.List;

public interface PromotionService {
    List<PromotionDetalleDTO> findAll();
    List<PromotionDetalleDTO> findCurrent();//Arreglar
    PromotionDetalleDTO findById(Long id);
    PromotionDetalleDTO findByCodigo(String codigo);
    PromotionDetalleDTO save(PromotionSaveDTO promotionSaveDTO);
    PromotionDetalleDTO updateDate(Long id, PromotionUpdateDateDTO promotionUpdateDateDTO);
    PromotionDetalleDTO desactiveById(Long id);
    PromotionDetalleDTO aplicarPromocion(String codigo, PromotionAplicarDescuentoDTO aplicarDescuentoDTO, Double totalOrden);

}
