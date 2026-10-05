package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "stay_status_history", indexes = {
        @Index(name = "idx_stay_history_property_stay", columnList = "property_id,stay_id,changed_at")
})
@Getter
@Setter
public class StayStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stay_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_stay_history_stay"))
    private Stay stay;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 30)
    private StayStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private StayStatus newStatus;

    @Column(columnDefinition = "text")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "changed_by", nullable = false,
            foreignKey = @ForeignKey(name = "fk_stay_history_user"))
    private HmsUser changedBy;

    @Column(name = "changed_at", nullable = false)
    private OffsetDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = OffsetDateTime.now();
    }
}
