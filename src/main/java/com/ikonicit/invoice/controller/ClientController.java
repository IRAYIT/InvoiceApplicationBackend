package com.ikonicit.invoice.controller;

import com.ikonicit.invoice.dto.ClientRequestDTO;
import com.ikonicit.invoice.dto.ClientResponseDTO;
import com.ikonicit.invoice.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(@RequestBody ClientRequestDTO clientRequestDTO) {
        return ResponseEntity.ok(clientService.create(clientRequestDTO));
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponseDTO> update(
            @PathVariable Long clientId,
            @RequestBody ClientRequestDTO clientRequestDTO) {
        return ResponseEntity.ok(clientService.update(clientId, clientRequestDTO));
    }
}
