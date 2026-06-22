package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.ClientRequestDTO;
import com.ikonicit.invoice.dto.ClientResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {

    ClientResponseDTO create(ClientRequestDTO clientRequestDTO);

    ClientResponseDTO update(Long clientId, ClientRequestDTO clientRequestDTO);

    List<ClientResponseDTO> getAll();

    ClientResponseDTO getById(Long clientId);

    void delete(Long clientId);
}
