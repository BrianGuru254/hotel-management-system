package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "restaurant_orders", indexes = {
        @Index(name = "idx_restaurant_orders_restaurant_status", columnList = "restaurant_id,status,created_at"),
        @Index(name = "idx_restaurant_orders_table", columnList = "table_id"),
        @Index(name = "idx_restaurant_orders_guest", columnList = "guest_id")
})
@Getter @Setter
public class RestaurantOrder {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "restaurant_id", nullable = false) private Restaurant restaurant;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "table_id") private RestaurantTable table;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "guest_id") private Guest guest;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "stay_id") private Stay stay;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "waiter_id", nullable = false) private HmsUser waiter;
    @Enumerated(EnumType.STRING) @Column(name = "table_status_before_order", length = 30) private RestaurantTableStatus tableStatusBeforeOrder;
    @Enumerated(EnumType.STRING) @Column(name = "order_type", nullable = false, length = 30) private RestaurantOrderType orderType;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private RestaurantOrderStatus status = RestaurantOrderStatus.DRAFT;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal subtotal = BigDecimal.ZERO;
    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2) private BigDecimal taxAmount = BigDecimal.ZERO;
    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2) private BigDecimal discountAmount = BigDecimal.ZERO;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal total = BigDecimal.ZERO;
    @Column(columnDefinition = "text") private String notes;
    @Column(name = "cancellation_reason", length = 1000) private String cancellationReason;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
    @PrePersist void onCreate() { OffsetDateTime now = OffsetDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
