package com.GameHub.controllers;

import com.GameHub.models.dtos.InventoryCantidadDTO;
import com.GameHub.models.dtos.InventoryDetalleDTO;
import com.GameHub.models.dtos.InventorySaveDTO;
import com.GameHub.models.dtos.InventoryUpdateCantidadDisponibleDTO;
import com.GameHub.services.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
@Validated
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryDetalleDTO>> findAllByProduct(@PathVariable Long productId){
        return ResponseEntity.status(HttpStatus.OK).body(inventoryService.findAllByProduct(productId));
    }
    @PostMapping
    public ResponseEntity<InventoryDetalleDTO> save(@Valid @RequestBody InventorySaveDTO inventorySaveDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.save(inventorySaveDTO));
    }

    @PatchMapping("/{id}/cantidad")
    public ResponseEntity<InventoryDetalleDTO> updateCantidadDisponible  (@PathVariable Long id, @Valid @RequestBody InventoryUpdateCantidadDisponibleDTO cantidadDisponibleDTO){
        return ResponseEntity.status(HttpStatus.OK).body(inventoryService.updateCantidadDisponible(id, cantidadDisponibleDTO));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        this.inventoryService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/producto/{productId}/reservar")
    public ResponseEntity<InventoryDetalleDTO> reservarStock (@PathVariable Long productId, @Valid @RequestBody InventoryCantidadDTO cantidadDTO){
        InventoryDetalleDTO response = inventoryService.reservarStock(productId, cantidadDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/producto/{productId}/liberar")
    public ResponseEntity<InventoryDetalleDTO> liberarStock(@PathVariable Long productId, @Valid @RequestBody InventoryCantidadDTO cantidadDTO){
        InventoryDetalleDTO response = inventoryService.liberarStock(productId, cantidadDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/producto/{productId}/confirmar-venta")
    public ResponseEntity<InventoryDetalleDTO> confirmarVenta (@PathVariable Long productId, @Valid @RequestBody InventoryCantidadDTO cantidadDTO){
        InventoryDetalleDTO response = inventoryService.confirmarVenta(productId, cantidadDTO);
        return ResponseEntity.ok(response);
    }



}
