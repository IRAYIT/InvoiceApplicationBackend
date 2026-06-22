package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.ProductRequestDTO;
import com.ikonicit.invoice.dto.ProductResponseDTO;
import java.util.List;

public interface ProductService {

    ProductResponseDTO create(ProductRequestDTO requestDTO);
    ProductResponseDTO update(Long id, ProductRequestDTO requestDTO);
    ProductResponseDTO getById(Long id);
    List<ProductResponseDTO> getAll();
    void delete(Long id);
}