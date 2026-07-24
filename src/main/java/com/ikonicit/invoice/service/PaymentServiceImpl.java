package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.AddPaymentRequestDTO;
import com.ikonicit.invoice.dto.InvoicePaymentSummaryDTO;
import com.ikonicit.invoice.dto.PaymentDTO;
import com.ikonicit.invoice.entity.Invoice;
import com.ikonicit.invoice.entity.Payment;
import com.ikonicit.invoice.repository.InvoiceRepository;
import com.ikonicit.invoice.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public InvoicePaymentSummaryDTO addPayment(Long invoiceId, AddPaymentRequestDTO req) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + invoiceId));

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setPaymentDate(req.getPaymentDate());
        payment.setAmountPaid(req.getAmountPaid());
        payment.setCash(req.isCash());
        paymentRepository.save(payment);

        return recalculate(invoice);
    }

    @Override
    @Transactional
    public InvoicePaymentSummaryDTO removePayment(Long invoiceId, Long paymentId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + invoiceId));
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found: " + paymentId));
        if (!payment.getInvoice().getId().equals(invoiceId)) {
            throw new IllegalArgumentException("Payment does not belong to this invoice");
        }
        paymentRepository.delete(payment);
        return recalculate(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoicePaymentSummaryDTO getSummary(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + invoiceId));
        return buildSummary(invoice, paymentRepository.findByInvoiceIdOrderByPaymentDateDesc(invoiceId));
    }

    private InvoicePaymentSummaryDTO recalculate(Invoice invoice) {
        List<Payment> payments = paymentRepository.findByInvoiceIdOrderByPaymentDateDesc(invoice.getId());
        BigDecimal totalPaid = payments.stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean fullyPaid = totalPaid.compareTo(invoice.getTotalAmount()) >= 0;

        invoice.setAmountPaid(totalPaid);
        invoice.setPaid(fullyPaid);
        // Only auto-flip to PAID; don't touch DRAFT invoices, and don't
        // downgrade PAID -> UNPAID here (that's an explicit "remove payment" action)
        if (fullyPaid) {
            invoice.setStatus("PAID");
        } else if (!"DRAFT".equals(invoice.getStatus())) {
            invoice.setStatus(invoice.getDueDate().isBefore(LocalDate.now()) ? "OVERDUE" : "UNPAID");
        }
        invoiceRepository.save(invoice);

        return buildSummary(invoice, payments);
    }

    private InvoicePaymentSummaryDTO buildSummary(Invoice invoice, List<Payment> payments) {
        BigDecimal totalPaid = payments.stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        InvoicePaymentSummaryDTO summary = new InvoicePaymentSummaryDTO();
        summary.setInvoiceId(invoice.getId());
        summary.setInvoiceTotal(invoice.getTotalAmount());
        summary.setTotalPaid(totalPaid);
        summary.setRemaining(invoice.getTotalAmount().subtract(totalPaid).max(BigDecimal.ZERO));
        summary.setStatus(invoice.getStatus());
        summary.setPaid(invoice.isPaid());
        summary.setPayments(payments.stream().map(this::toPaymentDto).toList());
        return summary;
    }

    private PaymentDTO toPaymentDto(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setAmountPaid(payment.getAmountPaid());
        dto.setCash(payment.isCash());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }
}