package com.GameHub.controllers;

import com.GameHub.models.dtos.DespachoRequestDTO;
import com.GameHub.models.dtos.DespachoResponseDTO;
import com.GameHub.services.ShippingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/despachos")
@Validated
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @GetMapping
    public ResponseEntity<List<DespachoResponseDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(shippingService.getAllDespachos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespachoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(shippingService.getDespachoById(id));
    }

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<DespachoResponseDTO>> findByOrdenId(@PathVariable Long ordenId) {
        return ResponseEntity.status(HttpStatus.OK).body(shippingService.getDespachosByOrdenId(ordenId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<DespachoResponseDTO>> findByEstado(@PathVariable String estado) {
        return ResponseEntity.status(HttpStatus.OK).body(shippingService.getDespachosByEstado(estado));
    }

    @PostMapping
    public ResponseEntity<DespachoResponseDTO> save(@Valid @RequestBody DespachoRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shippingService.createDespacho(requestDTO));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<DespachoResponseDTO> updateEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) String tracking,
            @RequestParam(required = false) LocalDateTime fechaEntrega) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(shippingService.updateEstadoDespacho(id, estado, tracking, fechaEntrega));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<DespachoResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(shippingService.cancelarDespacho(id));
    }
}