package com.hrms.backend.repository;

import com.hrms.backend.entity.ReservationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservationStatusHistoryRepository extends JpaRepository<ReservationStatusHistory, UUID> {
    List<ReservationStatusHistory> findByPropertyIdAndReservationIdOrderByChangedAtAsc(UUID propertyId, UUID reservationId);
}
