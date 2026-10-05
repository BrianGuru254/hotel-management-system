package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "housekeeping_tasks", indexes = {
        @Index(name = "idx_housekeeping_property_status", columnList = "property_id,status"),
        @Index(name = "idx_housekeeping_room_status", columnList = "room_id,status")
})
@Getter @Setter
public class HousekeepingTask {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private HmsUser assignedUser;

    @Enumerated(EnumType.STRING) @Column(name = "task_type", nullable = false, length = 50)
    private HousekeepingTaskType taskType = HousekeepingTaskType.ROOM_CLEANING;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private HousekeepingTaskStatus status = HousekeepingTaskStatus.PENDING;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private HousekeepingPriority priority = HousekeepingPriority.NORMAL;

    @Column(columnDefinition = "text") private String notes;
    @Column(name = "assigned_at") private OffsetDateTime assignedAt;
    @Column(name = "started_at") private OffsetDateTime startedAt;
    @Column(name = "completed_at") private OffsetDateTime completedAt;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;

    @PrePersist void onCreate() { OffsetDateTime now = OffsetDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
