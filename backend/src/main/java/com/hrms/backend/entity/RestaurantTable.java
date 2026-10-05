package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "restaurant_tables", indexes = @Index(name = "idx_restaurant_tables_restaurant", columnList = "restaurant_id"),
        uniqueConstraints = @UniqueConstraint(name = "uq_restaurant_tables_number", columnNames = {"restaurant_id", "table_number"}))
@Getter @Setter
public class RestaurantTable {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "restaurant_id", nullable = false) private Restaurant restaurant;
    @Column(name = "table_number", nullable = false, length = 50) private String tableNumber;
    @Column(nullable = false) private Integer capacity;
    @Column(length = 120) private String location;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private RestaurantTableStatus status = RestaurantTableStatus.AVAILABLE;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
    @PrePersist void onCreate() { OffsetDateTime now = OffsetDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
