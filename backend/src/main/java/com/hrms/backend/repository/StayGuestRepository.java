package com.hrms.backend.repository;

import com.hrms.backend.entity.StayGuest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StayGuestRepository extends JpaRepository<StayGuest, UUID> {
    Page<StayGuest> findByPropertyIdAndGuestId(UUID propertyId, UUID guestId, Pageable pageable);
    List<StayGuest> findByPropertyIdAndStayIdOrderByCreatedAtAsc(UUID propertyId, UUID stayId);

    Optional<StayGuest> findByStayIdAndGuestId(UUID stayId, UUID guestId);

    long countByStayId(UUID stayId);

    List<StayGuest> findByStayIdAndPrimaryGuestTrue(UUID stayId);
}
