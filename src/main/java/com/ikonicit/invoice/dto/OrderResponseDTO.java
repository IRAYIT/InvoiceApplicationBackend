package com.ikonicit.invoice.dto;

import com.ikonicit.invoice.entity.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Shape matches what OrderForm.jsx reads on load: clientId, clientName,
 * orderNumber, orderDate, expectedDeliveryDate, paymentMethod, status,
 * notes, items[]. Extra computed fields (subtotal/taxAmount/totalAmount,
 * timestamps) are additive — safe for the frontend to ignore.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long id;
    private String orderNumber;
    private Long clientId;
    private String clientName;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private String paymentMethod;
    private OrderStatus status;
    private String notes;
    private List<OrderItemResponseDTO> items;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
