package com.ikonicit.invoice.repository;

import com.ikonicit.invoice.entity.InvoiceHistoryEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceHistoryEventRepository extends JpaRepository<InvoiceHistoryEvent, Long> {
    List<InvoiceHistoryEvent> findByInvoiceIdOrderByTimestampDesc(Long invoiceId);
}