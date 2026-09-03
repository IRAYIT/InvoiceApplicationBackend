//package com.ikonicit.invoice.service;
//
//import com.ikonicit.invoice.dto.CompanySettingsDTO;
//import com.ikonicit.invoice.entity.CompanySettings;
//import com.ikonicit.invoice.repository.CompanySettingsRepository;
//import com.ikonicit.invoice.service.CompanySettingsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CompanySettingsServiceImpl implements CompanySettingsService {
//
//    private final CompanySettingsRepository companySettingsRepository;
//
//    @Override
//    public CompanySettingsDTO get() {
//        return companySettingsRepository.findFirstByIsActiveTrue()
//                .map(this::toDTO)
//                .orElse(null); // no profile set up yet
//    }
//
//    @Override
//    public CompanySettingsDTO save(CompanySettingsDTO dto) {
//        // Reuse the existing active row if present, otherwise create one
//        CompanySettings entity = companySettingsRepository.findFirstByIsActiveTrue()
//                .orElseGet(CompanySettings::new);
//
//        entity.setCompanyName(dto.getCompanyName());
//        entity.setCompanyRegNo(dto.getCompanyRegNo());
//        entity.setVatNo(dto.getVatNo());
//        entity.setCompanySeat(dto.getCompanySeat());
//        entity.setFTaxCertificate(dto.getFTaxCertificate() != null ? dto.getFTaxCertificate() : true);
//        entity.setLogoUrl(dto.getLogoUrl());
//
//        entity.setCareOf(dto.getCareOf());
//        entity.setAddress(dto.getAddress());
//        entity.setZipCode(dto.getZipCode());
//        entity.setCity(dto.getCity());
//        entity.setCountry(dto.getCountry());
//
//        entity.setCompanyEmail(dto.getCompanyEmail());
//        entity.setWebsite(dto.getWebsite());
//        entity.setPhone(dto.getPhone());
//        entity.setMobilePhone(dto.getMobilePhone());
//        entity.setFax(dto.getFax());
//
//        entity.setPaymentMethod(dto.getPaymentMethod());
//        entity.setBankgiroNumber(dto.getBankgiroNumber());
//        entity.setPlusgiroNumber(dto.getPlusgiroNumber());
//        entity.setBankAccountNumber(dto.getBankAccountNumber());
//        entity.setSwishNumber(dto.getSwishNumber());
//
//        entity.setIsActive(true);
//
//        CompanySettings saved = companySettingsRepository.save(entity);
//        return toDTO(saved);
//    }
//
//    private CompanySettingsDTO toDTO(CompanySettings c) {
//        return CompanySettingsDTO.builder()
//                .id(c.getId())
//                .companyName(c.getCompanyName())
//                .companyRegNo(c.getCompanyRegNo())
//                .vatNo(c.getVatNo())
//                .companySeat(c.getCompanySeat())
//                .fTaxCertificate(c.getFTaxCertificate())
//                .logoUrl(c.getLogoUrl())
//                .careOf(c.getCareOf())
//                .address(c.getAddress())
//                .zipCode(c.getZipCode())
//                .city(c.getCity())
//                .country(c.getCountry())
//                .companyEmail(c.getCompanyEmail())
//                .website(c.getWebsite())
//                .phone(c.getPhone())
//                .mobilePhone(c.getMobilePhone())
//                .fax(c.getFax())
//                .paymentMethod(c.getPaymentMethod())
//                .bankgiroNumber(c.getBankgiroNumber())
//                .plusgiroNumber(c.getPlusgiroNumber())
//                .bankAccountNumber(c.getBankAccountNumber())
//                .swishNumber(c.getSwishNumber())
//                .build();
//    }
//}