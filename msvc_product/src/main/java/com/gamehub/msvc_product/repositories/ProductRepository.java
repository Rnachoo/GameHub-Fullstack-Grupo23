package com.gamehub.msvc_product.repositories;
import com.gamehub.msvc_product.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface ProductRepository extends JpaRepository <Product, Long> {

    List<Product> findByCategoriaId(Long categoriaId);

    List<Product> findByMarca(String marca);

    List<Product> findByEstado(boolean estado);

}