package com.GameHub.repositories;

import com.GameHub.models.Promotion;
import com.GameHub.models.dtos.PromotionDetalleDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<PromotionDetalleDTO> findByEstado(String estado);
    Optional<Promotion> findByCodigo(String Codigo);

}
