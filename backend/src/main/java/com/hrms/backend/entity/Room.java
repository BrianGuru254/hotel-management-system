package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "rooms", indexes = {
        @Index(name = "idx_rooms_property_id", columnList = "property_id"),
        @Index(name = "idx_rooms_property_status", columnList = "property_id,current_status"),
        @Index(name = "idx_rooms_room_type_id", columnList = "room_type_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_rooms_property_scope", columnNames = {"property_id", "id"})
})
@Getter
@Setter
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_rooms_property"))
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_type_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_rooms_room_type"))
    private RoomType roomType;

    @Column(name = "room_number", nullable = false, length = 30)
    private String roomNumber;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(nullable = false)
    private Integer capacity = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false, length = 30)
    private RoomStatus currentStatus = RoomStatus.AVAILABLE;

    @Column(name = "status_reason", length = 500)
    private String statusReason;

    @Column(name = "status_changed_at", nullable = false)
    private OffsetDateTime statusChangedAt;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
        statusChangedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
