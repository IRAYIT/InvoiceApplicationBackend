package com.ikonicit.invoice.repository;

import com.ikonicit.invoice.entity.ProductGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long> {

    // Get all active product groups
    List<ProductGroup> findByIsActiveTrueOrderByCreatedAtDesc();

    // Check if name already exists
    boolean existsByName(String name);
}