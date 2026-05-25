package com.GameHub.services;

import com.GameHub.clients.CategoryFeignClient;
import com.GameHub.clients.dtos.CategoryClientDTO;
import com.GameHub.exceptions.ProductException;
import com.GameHub.models.Product;
import com.GameHub.models.dtos.ProductRequestDTO;
import com.GameHub.models.dtos.ProductResponseDTO;
import com.GameHub.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryFeignClient categoryFeignClient;

    @Transactional
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) { //crear producto
        CategoryClientDTO categoria = categoryFeignClient.getCategoryById(requestDTO.getCategoriaId());
        if (categoria == null || !categoria.getEstado().equals("Active")) {
            throw new ProductException("Categoría no encontrada o inactiva.");
        }

        Product product = new Product();
        product.setNombre(requestDTO.getNombre());
        product.setMarca(requestDTO.getMarca());
        product.setModelo(requestDTO.getModelo());
        product.setPrecio(requestDTO.getPrecio());
        product.setCategoriaId(requestDTO.getCategoriaId());
        product.setDescripcion(requestDTO.getDescripcion());
        product.setEstado("Activo");

        Product saved = productRepository.save(product);
        log.info("Producto creado con ID: {}", saved.getId());

        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponseDTO getProductById(Long id) { //buscar por Id
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Producto no encontrado con ID: " + id));
        log.info("Producto encontrado con ID: {}", id);
        return toResponseDTO(product);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponseDTO> getAllProducts() { //listar todos los products
        List<Product> products = productRepository.findAll();
        log.info("Total de productos encontrados: {}", products.size());
        return products.stream()
                .map(this::toResponseDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) { //actualizar info producto
        CategoryClientDTO categoria = categoryFeignClient.getCategoryById(requestDTO.getCategoriaId());
        if (categoria == null || !categoria.getEstado().equals("Active")) {
            throw new ProductException("Categoría no encontrada o inactiva.");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Producto no encontrado con ID: " + id));
        product.setNombre(requestDTO.getNombre());
        product.setMarca(requestDTO.getMarca());
        product.setModelo(requestDTO.getModelo());
        product.setPrecio(requestDTO.getPrecio());
        product.setCategoriaId(requestDTO.getCategoriaId());
        product.setDescripcion(requestDTO.getDescripcion());
        Product updated = productRepository.save(product);
        log.info("Producto actualizado con ID: {}", updated.getId());
        return toResponseDTO(updated);
    }

    @Transactional
    @Override
    public void deactivateProduct(Long id) { //Desactivar producto
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Producto no encontrado con ID: " + id));
        product.setEstado("Inactivo");
        productRepository.save(product);
        log.info("Producto desactivado con ID: {}", id);
    }

    private ProductResponseDTO toResponseDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setNombre(product.getNombre());
        dto.setMarca(product.getMarca());
        dto.setModelo(product.getModelo());
        dto.setPrecio(product.getPrecio());
        dto.setCategoriaId(product.getCategoriaId());
        dto.setDescripcion(product.getDescripcion());
        dto.setEstado(product.getEstado());
        return dto;
    }
}