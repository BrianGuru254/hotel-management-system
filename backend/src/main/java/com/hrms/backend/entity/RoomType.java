package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "room_types", indexes = {
        @Index(name = "idx_room_types_property_id", columnList = "property_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_room_types_property_id_id", columnNames = {"property_id", "id"})
})
@Getter
@Setter
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_room_types_property"))
    private Property property;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice = BigDecimal.ZERO;

    @Column(name = "max_occupancy", nullable = false)
    private Integer maxOccupancy = 1;

    @Column(name = "size_square_meters", precision = 8, scale = 2)
    private BigDecimal sizeSquareMeters;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoomTypeStatus status = RoomTypeStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
