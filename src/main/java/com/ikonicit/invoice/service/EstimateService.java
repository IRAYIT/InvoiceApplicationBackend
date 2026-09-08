package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.EstimateRequestDTO;
import com.ikonicit.invoice.dto.EstimateResponseDTO;
import java.util.List;

public interface EstimateService {

    EstimateResponseDTO create(EstimateRequestDTO requestDTO);
    EstimateResponseDTO getById(Long id);
    List<EstimateResponseDTO> getAll();
    List<EstimateResponseDTO> getEstimatesByClientId(Long clientId);
    EstimateResponseDTO update(Long id, EstimateRequestDTO requestDTO); // ← add
    void delete(Long id);
    EstimateResponseDTO updateCurrency(Long id, String newCurrency);
    EstimateResponseDTO approve(Long id);
    EstimateResponseDTO reject(Long id);
    EstimateResponseDTO convertToInvoice(Long id);
}