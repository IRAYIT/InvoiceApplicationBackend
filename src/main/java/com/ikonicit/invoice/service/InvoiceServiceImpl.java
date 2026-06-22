package com.ikonicit.invoice.serviceImpl;

import com.ikonicit.invoice.dto.InvoiceDTO;
import com.ikonicit.invoice.dto.InvoiceItemDTO;
import com.ikonicit.invoice.entity.Client;
import com.ikonicit.invoice.entity.Invoice;
import com.ikonicit.invoice.entity.InvoiceItem;
import com.ikonicit.invoice.repository.ClientRepository;
import com.ikonicit.invoice.repository.InvoiceRepository;
import com.ikonicit.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;

    // ─── Create Invoice ───────────────────────────────────
    @Override
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {

        // 1. Fetch client
        Client client = clientRepository.findById(invoiceDTO.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // 2. Build Invoice entity
        Invoice invoice = new Invoice();
        invoice.setClient(client);
        invoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
        invoice.setPaymentTerms(invoiceDTO.getPaymentTerms());
        invoice.setYourReference(invoiceDTO.getYourReference());
        invoice.setOurReference(invoiceDTO.getOurReference());
        invoice.setCurrency(invoiceDTO.getCurrency());
        invoice.setNotes(invoiceDTO.getNotes());
        invoice.setStatus("DRAFT");

        // 3. Auto generate invoice number
        invoice.setInvoiceNumber(generateInvoiceNumber());

        // 4. Auto calculate due date from payment terms
        invoice.setDueDate(calculateDueDate(invoiceDTO.getInvoiceDate(), invoiceDTO.getPaymentTerms()));

        // 5. Build invoice items and calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (InvoiceItemDTO itemDTO : invoiceDTO.getItems()) {

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProductId(itemDTO.getProductId());
            item.setDescription(itemDTO.getDescription());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnit(itemDTO.getUnit());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setTaxPercent(itemDTO.getTaxPercent());

            // lineTotal = quantity * unitPrice
            BigDecimal lineTotal = itemDTO.getQuantity()
                    .multiply(itemDTO.getUnitPrice());
            item.setLineTotal(lineTotal);

            // taxAmount = lineTotal * taxPercent / 100
            BigDecimal taxAmount = lineTotal
                    .multiply(itemDTO.getTaxPercent())
                    .divide(BigDecimal.valueOf(100));
            item.setTaxAmount(taxAmount);

            subtotal = subtotal.add(lineTotal);
            totalTax = totalTax.add(taxAmount);

            invoice.getItems().add(item);
        }

        // 6. Set totals on invoice
        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(totalTax);
        invoice.setTotalAmount(subtotal.add(totalTax));

        // 7. Save and return
        Invoice saved = invoiceRepository.save(invoice);
        return mapToDTO(saved);
    }

    // ─── Get All Invoices ─────────────────────────────────
    @Override
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ─── Get Invoice By Id ────────────────────────────────
    @Override
    public InvoiceDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return mapToDTO(invoice);
    }

    // ─── Helper: Map Entity to DTO ────────────────────────
    private InvoiceDTO mapToDTO(Invoice invoice) {

        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setClientId(invoice.getClient().getId());

        // Set client display name
        Client client = invoice.getClient();
        if ("company".equalsIgnoreCase(client.getClientType())) {
            dto.setClientName(client.getCompany());
        } else {
            dto.setClientName(client.getFirstName() + " " + client.getLastName());
        }

        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setPaymentTerms(invoice.getPaymentTerms());
        dto.setDueDate(invoice.getDueDate());
        dto.setYourReference(invoice.getYourReference());
        dto.setOurReference(invoice.getOurReference());
        dto.setStatus(invoice.getStatus());
        dto.setSubtotal(invoice.getSubtotal());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setCurrency(invoice.getCurrency());
        dto.setNotes(invoice.getNotes());
        dto.setIsActive(invoice.getIsActive());
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setUpdatedAt(invoice.getUpdatedAt());

        // Map items
        List<InvoiceItemDTO> itemDTOs = invoice.getItems()
                .stream()
                .map(item -> {
                    InvoiceItemDTO itemDTO = new InvoiceItemDTO();
                    itemDTO.setId(item.getId());
                    itemDTO.setInvoiceId(invoice.getId());
                    itemDTO.setProductId(item.getProductId());
                    itemDTO.setDescription(item.getDescription());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setUnit(item.getUnit());
                    itemDTO.setUnitPrice(item.getUnitPrice());
                    itemDTO.setTaxPercent(item.getTaxPercent());
                    itemDTO.setTaxAmount(item.getTaxAmount());
                    itemDTO.setLineTotal(item.getLineTotal());
                    return itemDTO;
                })
                .collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }

    // ─── Helper: Generate Invoice Number ──────────────────
    private String generateInvoiceNumber() {
        int year = LocalDate.now().getYear();
        long count = invoiceRepository.count() + 1;
        return String.format("INV-%d-%03d", year, count);
        // Output: INV-2025-001, INV-2025-002 ...
    }

    // ─── Helper: Calculate Due Date ───────────────────────
    private LocalDate calculateDueDate(LocalDate invoiceDate, String paymentTerms) {
        if (paymentTerms == null) return invoiceDate;
        return switch (paymentTerms) {
            case "Net 15"         -> invoiceDate.plusDays(15);
            case "Net 30"         -> invoiceDate.plusDays(30);
            case "Net 60"         -> invoiceDate.plusDays(60);
            case "Due on Receipt" -> invoiceDate;
            default               -> invoiceDate.plusDays(30);
        };
    }

    // ─── Update Invoice ───────────────────────────────────
    @Override
    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO) {

        // 1. Fetch existing invoice
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // 2. Fetch client if changed
        Client client = clientRepository.findById(invoiceDTO.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // 3. Update header fields
        invoice.setClient(client);
        invoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
        invoice.setPaymentTerms(invoiceDTO.getPaymentTerms());
        invoice.setDueDate(calculateDueDate(invoiceDTO.getInvoiceDate(), invoiceDTO.getPaymentTerms()));
        invoice.setYourReference(invoiceDTO.getYourReference());
        invoice.setOurReference(invoiceDTO.getOurReference());
        invoice.setCurrency(invoiceDTO.getCurrency());
        invoice.setNotes(invoiceDTO.getNotes());
        invoice.setStatus(invoiceDTO.getStatus());

        // Update invoice number only if provided
        if (invoiceDTO.getInvoiceNumber() != null && !invoiceDTO.getInvoiceNumber().isBlank()) {
            invoice.setInvoiceNumber(invoiceDTO.getInvoiceNumber());
        }

        // 4. Clear old items and add new items
        invoice.getItems().clear();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (InvoiceItemDTO itemDTO : invoiceDTO.getItems()) {

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProductId(itemDTO.getProductId());
            item.setDescription(itemDTO.getDescription());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnit(itemDTO.getUnit());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setTaxPercent(itemDTO.getTaxPercent());

            // lineTotal = quantity * unitPrice
            BigDecimal lineTotal = itemDTO.getQuantity()
                    .multiply(itemDTO.getUnitPrice());
            item.setLineTotal(lineTotal);

            // taxAmount = lineTotal * taxPercent / 100
            BigDecimal taxAmount = lineTotal
                    .multiply(itemDTO.getTaxPercent())
                    .divide(BigDecimal.valueOf(100));
            item.setTaxAmount(taxAmount);

            subtotal = subtotal.add(lineTotal);
            totalTax = totalTax.add(taxAmount);

            invoice.getItems().add(item);
        }

        // 5. Recalculate totals
        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(totalTax);
        invoice.setTotalAmount(subtotal.add(totalTax));

        // 6. Save and return
        Invoice saved = invoiceRepository.save(invoice);
        return mapToDTO(saved);
    }

    // ─── Delete Invoice (Soft Delete) ─────────────────────
    @Override
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoice.setIsActive(false);
        invoiceRepository.save(invoice);
    }
}