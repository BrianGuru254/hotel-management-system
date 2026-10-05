package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "stay_guests", uniqueConstraints = {
        @UniqueConstraint(name = "uq_stay_guests_stay_guest", columnNames = {"stay_id", "guest_id"})
}, indexes = {
        @Index(name = "idx_stay_guests_property_stay", columnList = "property_id,stay_id")
})
@Getter
@Setter
public class StayGuest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stay_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_stay_guests_stay"))
    private Stay stay;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_stay_guests_guest"))
    private Guest guest;

    @Column(name = "is_primary_guest", nullable = false)
    private Boolean primaryGuest = false;

    @Column(name = "primary_stay_id")
    private UUID primaryStayId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
