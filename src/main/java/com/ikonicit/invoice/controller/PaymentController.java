package com.ikonicit.invoice.controller;

import com.ikonicit.invoice.dto.AddPaymentRequestDTO;
import com.ikonicit.invoice.dto.InvoicePaymentSummaryDTO;
import com.ikonicit.invoice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices/{invoiceId}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<InvoicePaymentSummaryDTO> list(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(paymentService.getSummary(invoiceId));
    }

    @PostMapping
    public ResponseEntity<InvoicePaymentSummaryDTO> add(
            @PathVariable Long invoiceId,
            @Valid @RequestBody AddPaymentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.addPayment(invoiceId, request));
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<InvoicePaymentSummaryDTO> remove(
            @PathVariable Long invoiceId, @PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.removePayment(invoiceId, paymentId));
    }
}
