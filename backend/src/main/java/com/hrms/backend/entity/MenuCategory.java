package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "menu_categories", indexes = @Index(name = "idx_menu_categories_restaurant", columnList = "restaurant_id"),
        uniqueConstraints = @UniqueConstraint(name = "uq_menu_categories_restaurant_name", columnNames = {"restaurant_id", "name"}))
@Getter @Setter
public class MenuCategory {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "restaurant_id", nullable = false) private Restaurant restaurant;
    @Column(nullable = false, length = 120) private String name;
    @Column(columnDefinition = "text") private String description;
    @Column(name = "display_order", nullable = false) private Integer displayOrder = 0;
    @Column(name = "is_active", nullable = false) private Boolean active = true;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
    @PrePersist void onCreate() { OffsetDateTime now = OffsetDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
