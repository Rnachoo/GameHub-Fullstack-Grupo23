package com.GameHub.controllers;

import com.GameHub.models.dtos.OrderDetalleDTO;
import com.GameHub.models.dtos.OrderSaveDTO;
import com.GameHub.models.dtos.OrderUpdateEstadoDTO;
import com.GameHub.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@Validated
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/client/{client}")
    public ResponseEntity<List<OrderDetalleDTO>> findByClient(@PathVariable Long userId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findByClient(userId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<OrderDetalleDTO>> findByEstado(@PathVariable String estado){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findByEstado(estado));
    }

    @GetMapping("/id/{id}")
    private ResponseEntity<OrderDetalleDTO> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findById(id));
    }
    @PostMapping
    public ResponseEntity<OrderDetalleDTO> save(@Valid @RequestBody OrderSaveDTO orderSaveDTO) {
        OrderDetalleDTO nuevaOrden = orderService.save(orderSaveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaOrden);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrderDetalleDTO> updateEstado(@PathVariable Long id, @Valid @RequestBody OrderUpdateEstadoDTO orderUpdateEstadoDTO) {
        OrderDetalleDTO ordenActualizada = orderService.updateEstado(id, orderUpdateEstadoDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ordenActualizada);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarOrden(@PathVariable Long id) {
        orderService.cancelarOrden(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

