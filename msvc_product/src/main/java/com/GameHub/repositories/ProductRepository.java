package com.GameHub.repositories;
import com.GameHub.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ProductRepository extends JpaRepository <Product, Long> {

    List<Product> findByCategoriaId(Long categoriaId);

    List<Product> findByMarca(String marca);

    List<Product> findByEstado(boolean estado);

}