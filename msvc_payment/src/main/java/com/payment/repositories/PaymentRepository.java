package com.payment.repositories;

import com.payment.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrdenId(Long ordenId);
    List<Payment> findByEstado(String estado);
    boolean existsByOrdenIdAndEstado(Long ordenId, String estado);}
