package com.ikonicit.invoice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class InvoicePaymentSummaryDTO {

    // Invoice this summary belongs to
    private Long invoiceId;

    // Invoice grand total
    private BigDecimal invoiceTotal;

    // Sum of all payments made so far
    private BigDecimal totalPaid;

    // invoiceTotal - totalPaid (never below zero)
    private BigDecimal remaining;

    // OVERDUE / UNPAID / PAID / DRAFT
    private String status;

    // true once totalPaid >= invoiceTotal
    private boolean paid;

    // Full payment history for this invoice
    private List<PaymentDTO> payments;
}