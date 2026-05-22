package com.gamehub.msvc_product.services;

import com.gamehub.msvc_product.models.dtos.ProductRequestDTO;
import com.gamehub.msvc_product.models.dtos.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);
    ProductResponseDTO getProductById(Long id);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO);
    void deactivateProduct(Long id); // Para el borrado lógico que pide el caso
}