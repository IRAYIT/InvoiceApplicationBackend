package com.ikonicit.invoice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentDTO {

    // Payment record id
    private Long id;

    // Date the payment was made
    private LocalDate paymentDate;

    // Amount paid, in SEK
    private BigDecimal amountPaid;

    // Whether this payment was made in cash
    private boolean cash;

    // When the payment record was created
    private Instant createdAt;
}