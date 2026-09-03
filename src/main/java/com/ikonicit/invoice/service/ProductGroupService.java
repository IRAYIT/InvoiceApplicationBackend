package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.ProductGroupRequestDTO;
import com.ikonicit.invoice.dto.ProductGroupResponseDTO;
import java.util.List;

public interface ProductGroupService {

    ProductGroupResponseDTO create(ProductGroupRequestDTO requestDTO);
    ProductGroupResponseDTO update(Long id, ProductGroupRequestDTO requestDTO);
    ProductGroupResponseDTO getById(Long id);
    List<ProductGroupResponseDTO> getAll();
    void delete(Long id);
}