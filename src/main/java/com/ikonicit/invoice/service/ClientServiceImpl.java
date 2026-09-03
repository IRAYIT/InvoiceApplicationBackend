package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.*;
import com.ikonicit.invoice.entity.*;
import com.ikonicit.invoice.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        client.setWebsite(req.getWebsite());
        client.setPhoneMobile(req.getPhoneMobile());
        client.setPhoneHome(req.getPhoneHome());
        client.setFax(req.getFax());
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

        // Step 7b — Invoice settings (payment terms, language, currency, VAT, discount)
        if (req.getInvoiceSettings() != null) {
            ClientInvoiceSettings invSettings = new ClientInvoiceSettings();
            invSettings.setClient(client);
            invSettings.setPaymentTermsDays(req.getInvoiceSettings().getPaymentTermsDays());
            invSettings.setInvoiceLanguage(req.getInvoiceSettings().getInvoiceLanguage());
            invSettings.setCurrency(req.getInvoiceSettings().getCurrency());
            invSettings.setDefaultVatPercent(req.getInvoiceSettings().getDefaultVatPercent());
            invSettings.setDefaultDiscountPercent(req.getInvoiceSettings().getDefaultDiscountPercent());
            client.setInvoiceSettings(invSettings);
        }

// Step 7c — ROT deduction info (Sweden only, person clients)
        if (req.getRotInfo() != null) {
            ClientRotInfo rot = new ClientRotInfo();
            rot.setClient(client);
            rot.setApartmentDesignation(req.getRotInfo().getApartmentDesignation());
            rot.setPropertyDesignation(req.getRotInfo().getPropertyDesignation());
            rot.setAssocCorpIdNo(req.getRotInfo().getAssocCorpIdNo());
            client.setRotInfo(rot);
        }

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

        if (req.getWebsite() != null) client.setWebsite(req.getWebsite());
        if (req.getPhoneMobile() != null) client.setPhoneMobile(req.getPhoneMobile());
        if (req.getPhoneHome() != null) client.setPhoneHome(req.getPhoneHome());
        if (req.getFax() != null) client.setFax(req.getFax());

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

        // Step 7b — Update invoice settings
        if (req.getInvoiceSettings() != null) {
            ClientInvoiceSettings invSettings = client.getInvoiceSettings() != null
                    ? client.getInvoiceSettings()
                    : new ClientInvoiceSettings();

            invSettings.setClient(client);
            if (req.getInvoiceSettings().getPaymentTermsDays() != null)
                invSettings.setPaymentTermsDays(req.getInvoiceSettings().getPaymentTermsDays());
            if (req.getInvoiceSettings().getInvoiceLanguage() != null)
                invSettings.setInvoiceLanguage(req.getInvoiceSettings().getInvoiceLanguage());
            if (req.getInvoiceSettings().getCurrency() != null)
                invSettings.setCurrency(req.getInvoiceSettings().getCurrency());
            if (req.getInvoiceSettings().getDefaultVatPercent() != null)
                invSettings.setDefaultVatPercent(req.getInvoiceSettings().getDefaultVatPercent());
            if (req.getInvoiceSettings().getDefaultDiscountPercent() != null)
                invSettings.setDefaultDiscountPercent(req.getInvoiceSettings().getDefaultDiscountPercent());
            client.setInvoiceSettings(invSettings);
        }

// Step 7c — Update ROT info
        if (req.getRotInfo() != null) {
            ClientRotInfo rot = client.getRotInfo() != null
                    ? client.getRotInfo()
                    : new ClientRotInfo();

            rot.setClient(client);
            if (req.getRotInfo().getApartmentDesignation() != null)
                rot.setApartmentDesignation(req.getRotInfo().getApartmentDesignation());
            if (req.getRotInfo().getPropertyDesignation() != null)
                rot.setPropertyDesignation(req.getRotInfo().getPropertyDesignation());
            if (req.getRotInfo().getAssocCorpIdNo() != null)
                rot.setAssocCorpIdNo(req.getRotInfo().getAssocCorpIdNo());
            client.setRotInfo(rot);
        }

        // Step 8 — Save and return
        Client updated = clientRepository.save(client);
        return toResponse(updated);
    }

    // ─── GET ALL ─────────────────────────────────────────
    @Override
    public List<ClientResponseDTO> getAll() {
        return clientRepository.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── GET BY ID ───────────────────────────────────────
    @Override
    public ClientResponseDTO getById(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException(
                        "Client not found with id: " + clientId));
        return toResponse(client);
    }

    // ─── DELETE (Soft Delete) ────────────────────────────
    @Override
    public void delete(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException(
                        "Client not found with id: " + clientId));
        client.setIsActive(false);
        clientRepository.save(client);
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
        ClientInvoiceSettingsDTO invSettingsDTO = null;
        if (c.getInvoiceSettings() != null) {
            invSettingsDTO = new ClientInvoiceSettingsDTO();
            invSettingsDTO.setPaymentTermsDays(c.getInvoiceSettings().getPaymentTermsDays());
            invSettingsDTO.setInvoiceLanguage(c.getInvoiceSettings().getInvoiceLanguage());
            invSettingsDTO.setCurrency(c.getInvoiceSettings().getCurrency());
            invSettingsDTO.setDefaultVatPercent(c.getInvoiceSettings().getDefaultVatPercent());
            invSettingsDTO.setDefaultDiscountPercent(c.getInvoiceSettings().getDefaultDiscountPercent());
        }

        ClientRotInfoDTO rotInfoDTO = null;
        if (c.getRotInfo() != null) {
            rotInfoDTO = new ClientRotInfoDTO();
            rotInfoDTO.setApartmentDesignation(c.getRotInfo().getApartmentDesignation());
            rotInfoDTO.setPropertyDesignation(c.getRotInfo().getPropertyDesignation());
            rotInfoDTO.setAssocCorpIdNo(c.getRotInfo().getAssocCorpIdNo());
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
                .website(c.getWebsite())
                .phoneMobile(c.getPhoneMobile())
                .phoneHome(c.getPhoneHome())
                .fax(c.getFax())
                .isActive(c.getIsActive())
                .address(addressDTO)
                .deliveryAddress(deliveryDTO)
                .settings(settingsDTO)
                .invoiceSettings(invSettingsDTO)
                .rotInfo(rotInfoDTO)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();

    }
}

