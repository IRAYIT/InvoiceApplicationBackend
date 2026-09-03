package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.AddPaymentRequestDTO;
import com.ikonicit.invoice.dto.InvoicePaymentSummaryDTO;

public interface PaymentService {

    InvoicePaymentSummaryDTO addPayment(Long invoiceId, AddPaymentRequestDTO request);

    InvoicePaymentSummaryDTO removePayment(Long invoiceId, Long paymentId);

    InvoicePaymentSummaryDTO getSummary(Long invoiceId);
}