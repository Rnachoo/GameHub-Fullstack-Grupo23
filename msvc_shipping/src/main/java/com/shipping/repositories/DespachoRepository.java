package com.shipping.repositories;

import com.shipping.models.Despacho;
import com.shipping.models.EstadoDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    List<Despacho> findByOrdenId(Long ordenId);
    List<Despacho> findByEstado(EstadoDespacho estado);
    Optional<Despacho> findByTracking(String tracking);
}