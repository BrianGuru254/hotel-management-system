package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "maintenance_status_history", indexes = @Index(name = "idx_maintenance_history_request", columnList = "property_id,maintenance_request_id,changed_at"))
@Getter @Setter
public class MaintenanceStatusHistory {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "property_id", nullable = false) private UUID propertyId;
    @Column(name = "maintenance_request_id", nullable = false) private UUID maintenanceRequestId;
    @Enumerated(EnumType.STRING) @Column(name = "previous_status", length = 30) private MaintenanceStatus previousStatus;
    @Enumerated(EnumType.STRING) @Column(name = "new_status", nullable = false, length = 30) private MaintenanceStatus newStatus;
    @Column(length = 1000) private String reason;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "changed_by", nullable = false) private HmsUser changedBy;
    @Column(name = "changed_at", nullable = false) private OffsetDateTime changedAt;
    @PrePersist void onCreate() { if (changedAt == null) changedAt = OffsetDateTime.now(); }
}
