package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Optional link back to the product register — null when a one-off
    // line item is typed in without picking a saved product.
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "quantity", nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit", length = 32)
    private String unit;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "tax_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxPercent;

    // Quantity actually delivered so far — drives auto-COMPLETED status
    // and back-order handling, matching fakturan.nu's "Lev ant." concept.
    @Column(name = "delivered_quantity", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal deliveredQuantity = BigDecimal.ZERO;

    public BigDecimal lineSubtotal() {
        if (quantity == null || unitPrice == null) return BigDecimal.ZERO;
        return quantity.multiply(unitPrice);
    }

    public BigDecimal lineTax() {
        if (taxPercent == null) return BigDecimal.ZERO;
        return lineSubtotal().multiply(taxPercent).divide(BigDecimal.valueOf(100));
    }

    public BigDecimal lineTotal() {
        return lineSubtotal().add(lineTax());
    }
}