    package com.GameHub.controllers;

    import com.GameHub.models.dtos.ProductRequestDTO;
    import com.GameHub.models.dtos.ProductResponseDTO;
    import com.GameHub.services.ProductService;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/v1/productos")
    @Validated
    public class ProductController {

        @Autowired
        private ProductService productService;

        @GetMapping
        public ResponseEntity<List<ProductResponseDTO>> findAll() {
            return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts());
        }

        @GetMapping("/{id}")
        public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id) {
            return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(id));
        }

        @PostMapping
        public ResponseEntity<ProductResponseDTO> save(@Valid @RequestBody ProductRequestDTO requestDTO) {
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(requestDTO));
        }

        @PatchMapping("/{id}")
        public ResponseEntity<ProductResponseDTO> deactivate(@PathVariable Long id) {
            productService.deactivateProduct(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        @PatchMapping("/{id}/update")
        public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id,
                                                         @Valid @RequestBody ProductRequestDTO requestDTO) {
            return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(id, requestDTO));
        }
    }