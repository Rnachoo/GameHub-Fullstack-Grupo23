package com.GameHub.services;

import com.GameHub.models.Promotion;
import com.GameHub.models.dtos.PromotionDetalleDTO;
import com.GameHub.models.dtos.PromotionSaveDTO;
import com.GameHub.models.dtos.PromotionUpdateDateDTO;

import java.util.List;

public interface PromotionService {
    List<PromotionDetalleDTO> findAll();
    List<PromotionDetalleDTO> findCurrent();//Arreglar
    PromotionDetalleDTO findById(Long id);
    PromotionDetalleDTO findByCodigo(String codigo);
    PromotionDetalleDTO save(PromotionSaveDTO promotionSaveDTO);
    PromotionDetalleDTO updateDate(Long id, PromotionUpdateDateDTO promotionUpdateDateDTO);
    PromotionDetalleDTO desactiveById(Long id);

}
