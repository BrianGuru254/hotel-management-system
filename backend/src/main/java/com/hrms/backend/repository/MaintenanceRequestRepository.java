package com.hrms.backend.repository;

import com.hrms.backend.entity.MaintenanceRequest;
import com.hrms.backend.entity.MaintenanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.UUID;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, UUID> {
    @Query(value = "select m from MaintenanceRequest m where m.property.id in :propertyIds and (:propertyId is null or m.property.id = :propertyId) and (:roomId is null or m.room.id = :roomId) and (:status is null or m.status = :status) and (:priority is null or m.priority = :priority)",
        countQuery = "select count(m) from MaintenanceRequest m where m.property.id in :propertyIds and (:propertyId is null or m.property.id = :propertyId) and (:roomId is null or m.room.id = :roomId) and (:status is null or m.status = :status) and (:priority is null or m.priority = :priority)")
    Page<MaintenanceRequest> search(@Param("propertyIds") Collection<UUID> propertyIds, @Param("propertyId") UUID propertyId,
        @Param("roomId") UUID roomId, @Param("status") MaintenanceStatus status,
        @Param("priority") com.hrms.backend.entity.MaintenancePriority priority, Pageable pageable);
    boolean existsByRoomIdAndStatusIn(UUID roomId, Collection<MaintenanceStatus> statuses);
    boolean existsByAssignedEmployeeIdAndStatusIn(UUID employeeId, Collection<MaintenanceStatus> statuses);
}
