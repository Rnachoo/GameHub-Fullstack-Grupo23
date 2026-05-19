package com.GameHub.repositories;

import com.GameHub.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existeNombre(String nombreCategory);
}
