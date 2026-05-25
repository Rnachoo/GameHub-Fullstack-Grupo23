package com.GameHub.repositories;

import com.GameHub.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductoId(Long productoId);
    List<Review> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndProductoIdAndOrdenId(Long usuarioId, Long productoId, Long ordenId);
}