package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "menu_items", indexes = @Index(name = "idx_menu_items_category", columnList = "category_id"),
        uniqueConstraints = @UniqueConstraint(name = "uq_menu_items_restaurant_sku", columnNames = {"restaurant_id", "sku"}))
@Getter @Setter
public class MenuItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "restaurant_id", nullable = false) private Restaurant restaurant;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "category_id", nullable = false) private MenuCategory category;
    @Column(nullable = false, length = 150) private String name;
    @Column(nullable = false, length = 50) private String sku;
    @Column(columnDefinition = "text") private String description;
    @Column(name = "selling_price", nullable = false, precision = 12, scale = 2) private BigDecimal sellingPrice;
    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2) private BigDecimal taxRate = BigDecimal.ZERO;
    @Column(name = "preparation_time_minutes", nullable = false) private Integer preparationTimeMinutes = 0;
    @Column(nullable = false) private Boolean available = true;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
    @PrePersist void onCreate() { OffsetDateTime now = OffsetDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
