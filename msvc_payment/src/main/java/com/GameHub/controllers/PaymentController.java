package com.GameHub.controllers;

import com.GameHub.models.dtos.PaymentDetalleDTO;
import com.GameHub.models.dtos.PaymentSaveDTO;
import com.GameHub.models.dtos.PaymentUpdateEstadoDTO;
import com.GameHub.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Validated
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<PaymentDetalleDTO>> findAllByOrdenId(@PathVariable Long ordenId){
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.findAllByOrdenId(ordenId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PaymentDetalleDTO>> findAllByEstado(@PathVariable String estado){
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.findAllByEstado(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDetalleDTO> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PaymentDetalleDTO> save(@Valid @RequestBody PaymentSaveDTO paymentSaveDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.save(paymentSaveDTO));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PaymentDetalleDTO> updateEstado  (@PathVariable Long id, @Valid @RequestBody PaymentUpdateEstadoDTO PaymentUpdateEstadoDTO){
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.updateEstado(id, PaymentUpdateEstadoDTO));
    }

    @PatchMapping ("/{id}")
    public ResponseEntity <PaymentDetalleDTO> nullById (@PathVariable Long id) {
        PaymentDetalleDTO auth = paymentService.nullById(id);
        return ResponseEntity.ok(auth);
    }
}
