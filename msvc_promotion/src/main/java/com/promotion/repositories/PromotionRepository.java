package com.promotion.repositories;

import com.promotion.models.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByEstado(String estado);
    Optional<Promotion> findByCodigo(String Codigo);

}
