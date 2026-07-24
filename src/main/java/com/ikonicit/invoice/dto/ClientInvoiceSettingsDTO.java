package com.ikonicit.invoice.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
public class ClientInvoiceSettingsDTO {
    private Integer paymentTermsDays;
    private String invoiceLanguage;
    private String currency;
    private BigDecimal defaultVatPercent;
    private BigDecimal defaultDiscountPercent;
}