package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.ClientAddressDTO;
import com.ikonicit.invoice.dto.ClientRequestDTO;
import com.ikonicit.invoice.dto.ClientResponseDTO;
import com.ikonicit.invoice.dto.ClientSettingsDTO;
import com.ikonicit.invoice.entity.Client;
import com.ikonicit.invoice.entity.ClientAddress;
import com.ikonicit.invoice.entity.ClientDeliveryAddress;
import com.ikonicit.invoice.entity.ClientSettings;
import com.ikonicit.invoice.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{

     private final ClientRepository clientRepository;

    @Override
    public ClientResponseDTO create(ClientRequestDTO req) {

        Client client = new Client();
        client.setClientType(req.getClientType() != null ? req.getClientType() : "company");
        client.setNumber(req.getNumber());
        client.setEmail(req.getEmail());
        client.setIsActive(true);

        // Step 4 — Company or Person fields
        if ("company".equalsIgnoreCase(req.getClientType())) {
            client.setCompany(req.getCompany());
            client.setCompanyRegNo(req.getCompanyRegNo());
            client.setVatNo(req.getVatNo());
        } else {
            client.setFirstName(req.getFirstName());
            client.setLastName(req.getLastName());
            client.setPersonalIdNo(req.getPersonalIdNo());
        }

        // Step 5 — Billing Address
        if (req.getAddress() != null) {
            ClientAddress address = new ClientAddress();
            address.setClient(client);
            address.setCareOf(req.getAddress().getCareOf());
            address.setStreetAddress(req.getAddress().getStreetAddress());
            address.setZipCode(req.getAddress().getZipCode());
            address.setCity(req.getAddress().getCity());
            address.setCountry(req.getAddress().getCountry());
            client.setAddress(address);
        }

        // Step 6 — Delivery Address
        if (req.getDeliveryAddress() != null) {
            ClientDeliveryAddress delivery = new ClientDeliveryAddress();
            delivery.setClient(client);
            delivery.setCareOf(req.getDeliveryAddress().getCareOf());
            delivery.setStreetAddress(req.getDeliveryAddress().getStreetAddress());
            delivery.setZipCode(req.getDeliveryAddress().getZipCode());
            delivery.setCity(req.getDeliveryAddress().getCity());
            delivery.setCountry(req.getDeliveryAddress().getCountry());
            client.setDeliveryAddress(delivery);
        }

        // Step 7 — Settings
        ClientSettings settings = new ClientSettings();
        settings.setClient(client);
        if (req.getSettings() != null) {
            settings.setInvoiceDeliveryMethod(
                    req.getSettings().getInvoiceDeliveryMethod() != null
                            ? req.getSettings().getInvoiceDeliveryMethod() : "email");
            settings.setEmailAttachPdf(
                    req.getSettings().getEmailAttachPdf() != null
                            ? req.getSettings().getEmailAttachPdf() : false);
        } else {
            settings.setInvoiceDeliveryMethod("email");
            settings.setEmailAttachPdf(false);
        }
        client.setSettings(settings);

        // Step 8 — Save to DB
        Client saved = clientRepository.save(client);

        // Step 9 — Return Response
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ClientResponseDTO update(Long clientId, ClientRequestDTO req) {

        // Step 1 — Find existing client
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + clientId));

        // Step 2 — Update client type
        if (req.getClientType() != null) {
            client.setClientType(req.getClientType());
        }

        // Step 3 — Update company or person fields
        if ("company".equalsIgnoreCase(req.getClientType())) {
            client.setCompany(req.getCompany());
            client.setCompanyRegNo(req.getCompanyRegNo());
            client.setVatNo(req.getVatNo());
            // Clear person fields
            client.setFirstName(null);
            client.setLastName(null);
            client.setPersonalIdNo(null);
        } else {
            client.setFirstName(req.getFirstName());
            client.setLastName(req.getLastName());
            client.setPersonalIdNo(req.getPersonalIdNo());
            // Clear company fields
            client.setCompany(null);
            client.setCompanyRegNo(null);
            client.setVatNo(null);
        }

        // Step 4 — Update common fields
        if (req.getEmail() != null) client.setEmail(req.getEmail());
        if (req.getNumber() != null) client.setNumber(req.getNumber());

        // Step 5 — Update billing address
        if (req.getAddress() != null) {
            ClientAddress address = client.getAddress() != null
                    ? client.getAddress()       // update existing
                    : new ClientAddress();      // create new if not exists

            address.setClient(client);
            address.setCareOf(req.getAddress().getCareOf());
            address.setStreetAddress(req.getAddress().getStreetAddress());
            address.setZipCode(req.getAddress().getZipCode());
            address.setCity(req.getAddress().getCity());
            address.setCountry(req.getAddress().getCountry());
            client.setAddress(address);
        }

        // Step 6 — Update delivery address
        if (req.getDeliveryAddress() != null) {
            ClientDeliveryAddress delivery = client.getDeliveryAddress() != null
                    ? client.getDeliveryAddress()   // update existing
                    : new ClientDeliveryAddress();  // create new if not exists

            delivery.setClient(client);
            delivery.setCareOf(req.getDeliveryAddress().getCareOf());
            delivery.setStreetAddress(req.getDeliveryAddress().getStreetAddress());
            delivery.setZipCode(req.getDeliveryAddress().getZipCode());
            delivery.setCity(req.getDeliveryAddress().getCity());
            delivery.setCountry(req.getDeliveryAddress().getCountry());
            client.setDeliveryAddress(delivery);
        }

        // Step 7 — Update settings
        if (req.getSettings() != null) {
            ClientSettings settings = client.getSettings() != null
                    ? client.getSettings()      // update existing
                    : new ClientSettings();     // create new if not exists

            settings.setClient(client);
            if (req.getSettings().getInvoiceDeliveryMethod() != null)
                settings.setInvoiceDeliveryMethod(req.getSettings().getInvoiceDeliveryMethod());
            if (req.getSettings().getEmailAttachPdf() != null)
                settings.setEmailAttachPdf(req.getSettings().getEmailAttachPdf());
            client.setSettings(settings);
        }

        // Step 8 — Save and return
        Client updated = clientRepository.save(client);
        return toResponse(updated);
    }

    private ClientResponseDTO toResponse(Client c) {

        // Map billing address
        ClientAddressDTO addressDTO = null;
        if (c.getAddress() != null) {
            addressDTO = new ClientAddressDTO();
            addressDTO.setCareOf(c.getAddress().getCareOf());
            addressDTO.setStreetAddress(c.getAddress().getStreetAddress());
            addressDTO.setZipCode(c.getAddress().getZipCode());
            addressDTO.setCity(c.getAddress().getCity());
            addressDTO.setCountry(c.getAddress().getCountry());
        }

        // Map delivery address
        ClientAddressDTO deliveryDTO = null;
        if (c.getDeliveryAddress() != null) {
            deliveryDTO = new ClientAddressDTO();
            deliveryDTO.setCareOf(c.getDeliveryAddress().getCareOf());
            deliveryDTO.setStreetAddress(c.getDeliveryAddress().getStreetAddress());
            deliveryDTO.setZipCode(c.getDeliveryAddress().getZipCode());
            deliveryDTO.setCity(c.getDeliveryAddress().getCity());
            deliveryDTO.setCountry(c.getDeliveryAddress().getCountry());
        }

        // Map settings
        ClientSettingsDTO settingsDTO = null;
        if (c.getSettings() != null) {
            settingsDTO = new ClientSettingsDTO();
            settingsDTO.setInvoiceDeliveryMethod(c.getSettings().getInvoiceDeliveryMethod());
            settingsDTO.setEmailAttachPdf(c.getSettings().getEmailAttachPdf());
        }

        return ClientResponseDTO.builder()
                .id(c.getId())
                .clientType(c.getClientType())
                .company(c.getCompany())
                .companyRegNo(c.getCompanyRegNo())
                .vatNo(c.getVatNo())
                .firstName(c.getFirstName())
                .lastName(c.getLastName())
                .personalIdNo(c.getPersonalIdNo())
                .email(c.getEmail())
                .number(c.getNumber())
                .isActive(c.getIsActive())
                .address(addressDTO)
                .deliveryAddress(deliveryDTO)
                .settings(settingsDTO)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }



}

