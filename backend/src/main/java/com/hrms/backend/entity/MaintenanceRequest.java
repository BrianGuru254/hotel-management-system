package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "maintenance_requests", indexes = {
        @Index(name = "idx_maintenance_property_status", columnList = "property_id,status"),
        @Index(name = "idx_maintenance_room_status", columnList = "room_id,status")
}, uniqueConstraints = @UniqueConstraint(name = "uq_maintenance_property_scope", columnNames = {"property_id", "id"}))
@Getter @Setter
public class MaintenanceRequest {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "property_id", nullable = false) private Property property;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "room_id") private Room room;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "reported_by", nullable = false) private HmsUser reportedBy;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assigned_user_id") private HmsUser assignedUser;
    @Column(nullable = false, length = 200) private String title;
    @Column(columnDefinition = "text") private String description;
    @Column(columnDefinition = "text") private String resolution;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private MaintenancePriority priority = MaintenancePriority.NORMAL;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private MaintenanceStatus status = MaintenanceStatus.OPEN;
    @Enumerated(EnumType.STRING) @Column(name = "room_status_before", length = 30) private RoomStatus roomStatusBefore;
    @Column(name = "reported_at", nullable = false) private OffsetDateTime reportedAt;
    @Column(name = "assigned_at") private OffsetDateTime assignedAt;
    @Column(name = "started_at") private OffsetDateTime startedAt;
    @Column(name = "resolved_at") private OffsetDateTime resolvedAt;
    @Column(name = "closed_at") private OffsetDateTime closedAt;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
    @PrePersist void onCreate() { OffsetDateTime now = OffsetDateTime.now(); reportedAt = now; createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
