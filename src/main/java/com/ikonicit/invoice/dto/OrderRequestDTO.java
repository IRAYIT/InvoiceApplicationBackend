package com.ikonicit.invoice.dto;

import com.ikonicit.invoice.entity.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Field-for-field match with OrderForm.jsx's buildPayload():
 * clientId, orderDate, expectedDeliveryDate, paymentMethod, status,
 * notes, items[].
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    @NotNull(message = "Client is required")
    private Long clientId;

    @NotNull(message = "Order date is required")
    private LocalDate orderDate;

    private LocalDate expectedDeliveryDate;

    private String paymentMethod;

    // Optional on create (defaults to NOT_STARTED in the service);
    // required in practice once you're updating an existing order.
    private OrderStatus status;

    private String notes;

    @NotEmpty(message = "At least one order item is required")
    @Valid
    private List<OrderItemRequestDTO> items;
}
