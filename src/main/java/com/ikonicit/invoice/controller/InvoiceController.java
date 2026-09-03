package com.ikonicit.invoice.controller;

import com.ikonicit.invoice.dto.InvoiceDTO;
import com.ikonicit.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // ─── Create Invoice ───────────────────────────────────
    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        InvoiceDTO created = invoiceService.createInvoice(invoiceDTO);
        return ResponseEntity.ok(created);
    }

    // ─── Get All Invoices ─────────────────────────────────
    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        List<InvoiceDTO> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByClientId(
            @PathVariable Long clientId) {

        List<InvoiceDTO> invoices =
                invoiceService.getInvoicesByClientId(clientId);

        return ResponseEntity.ok(invoices);
    }

    // ─── Get Invoice By Id ────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoice);
    }

    // ─── Update Invoice ───────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable Long id, @RequestBody InvoiceDTO invoiceDTO) {
        InvoiceDTO updated = invoiceService.updateInvoice(id, invoiceDTO);
        return ResponseEntity.ok(updated);
    }

    // ─── Delete Invoice (Soft Delete) ─────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok("Invoice deleted successfully");
    }
}