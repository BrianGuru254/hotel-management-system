package com.hrms.backend.repository;

import com.hrms.backend.entity.MaintenanceStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MaintenanceStatusHistoryRepository extends JpaRepository<MaintenanceStatusHistory, UUID> {
    List<MaintenanceStatusHistory> findByPropertyIdAndMaintenanceRequestIdOrderByChangedAtAsc(UUID propertyId, UUID requestId);
}
