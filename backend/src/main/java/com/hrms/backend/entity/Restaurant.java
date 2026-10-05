package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "restaurants", indexes = @Index(name = "idx_restaurants_branch", columnList = "branch_id"),
        uniqueConstraints = @UniqueConstraint(name = "uq_restaurants_branch_name", columnNames = {"branch_id", "name"}))
@Getter @Setter
public class Restaurant {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "branch_id", nullable = false) private Branch branch;
    @Column(nullable = false, length = 150) private String name;
    @Column(columnDefinition = "text") private String description;
    @Column(name = "opening_time") private LocalTime openingTime;
    @Column(name = "closing_time") private LocalTime closingTime;
    @Column(name = "is_active", nullable = false) private Boolean active = true;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
    @PrePersist void onCreate() { OffsetDateTime now = OffsetDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
