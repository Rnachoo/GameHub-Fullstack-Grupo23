package com.GameHub.controllers;

import com.GameHub.models.dtos.PromotionAplicarDescuentoDTO;
import com.GameHub.models.dtos.PromotionDetalleDTO;
import com.GameHub.models.dtos.PromotionSaveDTO;
import com.GameHub.models.dtos.PromotionUpdateDateDTO;
import com.GameHub.services.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promociones")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public ResponseEntity<List<PromotionDetalleDTO>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(promotionService.findAll());
    }

    @GetMapping("/{id}") //Arreglar
    public ResponseEntity<List<PromotionDetalleDTO>> findCurrent(){
        return ResponseEntity.status(HttpStatus.OK).body(promotionService.findCurrent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionDetalleDTO> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(promotionService.findById(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PromotionDetalleDTO> findByCodigo(@PathVariable String codigo){
        return ResponseEntity.status(HttpStatus.OK).body(promotionService.findByCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<PromotionDetalleDTO> save(@Valid @RequestBody PromotionSaveDTO promotionSaveDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(promotionService.save(promotionSaveDTO));
    }

    @PatchMapping ("/{id}")
    public ResponseEntity <PromotionDetalleDTO> desactiveById(@PathVariable Long id){
        PromotionDetalleDTO auth = promotionService.desactiveById(id);
        return ResponseEntity.ok(auth);

    }


    @PatchMapping("/{id}/Date")
    public ResponseEntity<PromotionDetalleDTO> updateDate (@PathVariable Long id, @Valid @RequestBody PromotionUpdateDateDTO promotionUpdateDateDTO){
        return ResponseEntity.status(HttpStatus.OK).body(promotionService.updateDate(id, promotionUpdateDateDTO));
    }

    @PostMapping("/{codigo}/aplicar")
    public ResponseEntity<PromotionDetalleDTO> aplicarPromocion(@PathVariable PromotionAplicarDescuentoDTO aplicarDescuentoDTO, @RequestParam Double totalOrden) {
        PromotionDetalleDTO promotionAplicada = promotionService.aplicarPromocion(aplicarDescuentoDTO, totalOrden);
        return ResponseEntity.ok(promotionAplicada);
    }
}
