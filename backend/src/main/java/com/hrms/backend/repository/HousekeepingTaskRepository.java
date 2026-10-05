package com.hrms.backend.repository;

import com.hrms.backend.entity.HousekeepingTask;
import com.hrms.backend.entity.HousekeepingTaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.UUID;

public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, UUID> {
    @Query(value = "select t from HousekeepingTask t where t.property.id in :propertyIds and (:propertyId is null or t.property.id = :propertyId) and (:roomId is null or t.room.id = :roomId) and (:status is null or t.status = :status)",
            countQuery = "select count(t) from HousekeepingTask t where t.property.id in :propertyIds and (:propertyId is null or t.property.id = :propertyId) and (:roomId is null or t.room.id = :roomId) and (:status is null or t.status = :status)")
    Page<HousekeepingTask> search(@Param("propertyIds") Collection<UUID> propertyIds,
        @Param("propertyId") UUID propertyId, @Param("roomId") UUID roomId,
        @Param("status") HousekeepingTaskStatus status, Pageable pageable);
}
