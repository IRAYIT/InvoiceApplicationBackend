package com.ikonicit.invoice.repository;


import com.ikonicit.invoice.entity.Order;
import com.ikonicit.invoice.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByClientId(Long clientId);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    boolean existsByOrderNumber(String orderNumber);

    // Counts existing orders for the current year to build the next
    // sequence number, e.g. ORD-2026-0007 -> next is ORD-2026-0008.
    // Mirrors however your Invoice module counts INV-{year}-{sequence}.
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderNumber LIKE CONCAT(:prefix, '%')")
    long countByOrderNumberStartingWith(@Param("prefix") String prefix);
}
