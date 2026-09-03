package com.ikonicit.invoice.repository;

import com.ikonicit.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByIsActiveTrue();

    List<Invoice> findByClient_IdAndIsActiveTrue(Long clientId);
}