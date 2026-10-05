package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "restaurant_order_items", indexes = @Index(name = "idx_restaurant_order_items_order", columnList = "order_id"))
@Getter @Setter
public class RestaurantOrderItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false) private RestaurantOrder order;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "menu_item_id", nullable = false) private MenuItem menuItem;
    @Column(name = "item_name", nullable = false, length = 150) private String itemName;
    @Column(name = "item_sku", nullable = false, length = 50) private String itemSku;
    @Column(nullable = false) private Integer quantity;
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2) private BigDecimal unitPrice;
    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2) private BigDecimal taxRate;
    @Column(name = "line_subtotal", nullable = false, precision = 12, scale = 2) private BigDecimal lineSubtotal;
    @Column(name = "line_tax", nullable = false, precision = 12, scale = 2) private BigDecimal lineTax;
    @Column(name = "line_total", nullable = false, precision = 12, scale = 2) private BigDecimal lineTotal;
    @Column(name = "special_instructions", columnDefinition = "text") private String specialInstructions;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @PrePersist void onCreate() { createdAt = OffsetDateTime.now(); }
}
