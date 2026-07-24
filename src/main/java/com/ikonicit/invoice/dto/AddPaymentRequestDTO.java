package com.ikonicit.invoice.dto;

import lombok.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AddPaymentRequestDTO {

    // Date the payment was made
    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    // Amount paid, in SEK
    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.01", message = "Amount paid must be greater than 0")
    private BigDecimal amountPaid;

    // Whether this payment was made in cash
    private boolean cash;
}