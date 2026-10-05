package com.hrms.backend.repository;

import com.hrms.backend.entity.Stay;
import com.hrms.backend.entity.StayStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface StayRepository extends JpaRepository<Stay, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select stay from Stay stay where stay.id = :id")
    Optional<Stay> findByIdForUpdate(@Param("id") UUID id);

    @Query(value = """
            select stay from Stay stay
            where stay.property.id in :propertyIds
              and (:propertyId is null or stay.property.id = :propertyId)
              and (:status is null or stay.status = :status)
              and (:guestId is null or stay.guest.id = :guestId)
              and (:roomId is null or stay.room.id = :roomId)
              and (:checkOutFrom is null or stay.expectedCheckOutDate >= :checkOutFrom)
              and (:checkOutTo is null or stay.expectedCheckOutDate <= :checkOutTo)
            """,
            countQuery = """
            select count(stay) from Stay stay
            where stay.property.id in :propertyIds
              and (:propertyId is null or stay.property.id = :propertyId)
              and (:status is null or stay.status = :status)
              and (:guestId is null or stay.guest.id = :guestId)
              and (:roomId is null or stay.room.id = :roomId)
              and (:checkOutFrom is null or stay.expectedCheckOutDate >= :checkOutFrom)
              and (:checkOutTo is null or stay.expectedCheckOutDate <= :checkOutTo)
            """)
    Page<Stay> searchInProperties(
            @Param("propertyIds") Collection<UUID> propertyIds,
            @Param("propertyId") UUID propertyId,
            @Param("status") StayStatus status,
            @Param("guestId") UUID guestId,
            @Param("roomId") UUID roomId,
            @Param("checkOutFrom") LocalDate checkOutFrom,
            @Param("checkOutTo") LocalDate checkOutTo,
            Pageable pageable);

    @Query("""
            select count(stay) from Stay stay
            where stay.property.id = :propertyId and stay.room.roomType.id = :roomTypeId
              and stay.reservation is null and stay.status = :status
              and stay.expectedCheckOutDate > :checkInDate
            """)
    long countActiveWalkInRoomsByPropertyAndRoomType(
            @Param("propertyId") UUID propertyId,
            @Param("roomTypeId") UUID roomTypeId,
            @Param("status") StayStatus status,
            @Param("checkInDate") LocalDate checkInDate);

    boolean existsByRoomIdAndStatus(UUID roomId, StayStatus status);

    long countByPropertyIdAndReservationId(UUID propertyId, UUID reservationId);

    long countByReservationIdAndStatus(UUID reservationId, StayStatus status);

    boolean existsByPropertyIdAndStayNumberIgnoreCase(UUID propertyId, String stayNumber);
}
