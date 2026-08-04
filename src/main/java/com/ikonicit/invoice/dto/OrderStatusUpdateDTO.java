package com.ikonicit.invoice.dto;

import com.ikonicit.invoice.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateDTO {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}
