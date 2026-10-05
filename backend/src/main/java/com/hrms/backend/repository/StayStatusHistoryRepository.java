package com.hrms.backend.repository;

import com.hrms.backend.entity.StayStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StayStatusHistoryRepository extends JpaRepository<StayStatusHistory, UUID> {
    List<StayStatusHistory> findByPropertyIdAndStayIdOrderByChangedAtAsc(UUID propertyId, UUID stayId);
}
