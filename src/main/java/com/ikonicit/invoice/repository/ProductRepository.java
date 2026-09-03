package com.ikonicit.invoice.repository;

import com.ikonicit.invoice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {


    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    List<Product> findAllActiveProducts();
    // Check if product code already exists
    boolean existsByProductCode(String productCode);
}