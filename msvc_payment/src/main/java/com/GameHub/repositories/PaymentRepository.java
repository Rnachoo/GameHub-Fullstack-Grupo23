package com.GameHub.repositories;

import com.GameHub.models.Payment;
import com.GameHub.models.dtos.PaymentDetalleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrdenId(Long ordenId);
    List<Payment> findByEstado(String estado);
    boolean existsByOrdenIdAndEstado(Long ordenId, String estado);}
