package com.GameHub.controllers;

import com.GameHub.models.dtos.ResenaRequestDTO;
import com.GameHub.models.dtos.ResenaResponseDTO;
import com.GameHub.models.dtos.ResenaUpdateDTO;
import com.GameHub.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resenas")
@Validated
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{id}")
    public ResponseEntity<ResenaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getResenaById(id));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ResenaResponseDTO>> findByProductoId(@PathVariable Long productoId) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getResenasByProductoId(productoId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaResponseDTO>> findByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getResenasByUsuarioId(usuarioId));
    }

    @PostMapping
    public ResponseEntity<ResenaResponseDTO> save(@Valid @RequestBody ResenaRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createResena(requestDTO));
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<ResenaResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody ResenaUpdateDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.updateResena(id, requestDTO));
    }

    @PatchMapping("/{id}/moderar")
    public ResponseEntity<ResenaResponseDTO> moderar(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.moderarResena(id));
    }
}