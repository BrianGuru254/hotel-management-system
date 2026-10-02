package com.hrms.backend.repository;

import com.hrms.backend.entity.Reservation;
import com.hrms.backend.entity.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select reservation from Reservation reservation where reservation.id = :id")
    Optional<Reservation> findByIdForUpdate(@Param("id") UUID id);

    @Query(value = """
            select reservation from Reservation reservation
            where reservation.property.id in :propertyIds
              and (:propertyId is null or reservation.property.id = :propertyId)
              and (:status is null or reservation.status = :status)
              and (:guestId is null or reservation.guest.id = :guestId)
              and (:fromDate is null or reservation.checkOutDate > :fromDate)
              and (:toDate is null or reservation.checkInDate < :toDate)
              and (:reservationNumber is null or lower(reservation.reservationNumber) like concat('%', lower(:reservationNumber), '%'))
            """,
            countQuery = """
            select count(reservation) from Reservation reservation
            where reservation.property.id in :propertyIds
              and (:propertyId is null or reservation.property.id = :propertyId)
              and (:status is null or reservation.status = :status)
              and (:guestId is null or reservation.guest.id = :guestId)
              and (:fromDate is null or reservation.checkOutDate > :fromDate)
              and (:toDate is null or reservation.checkInDate < :toDate)
              and (:reservationNumber is null or lower(reservation.reservationNumber) like concat('%', lower(:reservationNumber), '%'))
            """)
    Page<Reservation> searchInProperties(
            @Param("propertyIds") Collection<UUID> propertyIds,
            @Param("propertyId") UUID propertyId,
            @Param("status") ReservationStatus status,
            @Param("guestId") UUID guestId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("reservationNumber") String reservationNumber,
            Pageable pageable);

    @Query("""
            select coalesce(sum(reservation.numberOfRooms), 0) from Reservation reservation
            where reservation.property.id = :propertyId and reservation.roomType.id = :roomTypeId
              and reservation.status in :statuses
              and reservation.checkInDate < :checkOutDate and reservation.checkOutDate > :checkInDate
            """)
    long countReservedRooms(@Param("propertyId") UUID propertyId,
                            @Param("roomTypeId") UUID roomTypeId,
                            @Param("statuses") Collection<ReservationStatus> statuses,
                            @Param("checkInDate") LocalDate checkInDate,
                            @Param("checkOutDate") LocalDate checkOutDate);

    @Query("""
            select coalesce(sum(reservation.numberOfRooms), 0) from Reservation reservation
            where reservation.property.id = :propertyId and reservation.roomType.id = :roomTypeId
              and reservation.id <> :excludeReservationId
              and reservation.status in :statuses
              and reservation.checkInDate < :checkOutDate and reservation.checkOutDate > :checkInDate
            """)
    long countReservedRoomsExcluding(@Param("propertyId") UUID propertyId,
                                     @Param("roomTypeId") UUID roomTypeId,
                                     @Param("excludeReservationId") UUID excludeReservationId,
                                     @Param("statuses") Collection<ReservationStatus> statuses,
                                     @Param("checkInDate") LocalDate checkInDate,
                                     @Param("checkOutDate") LocalDate checkOutDate);

    boolean existsByPropertyIdAndReservationNumberIgnoreCase(UUID propertyId, String reservationNumber);

    boolean existsByRoomIdAndStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            UUID roomId, Collection<ReservationStatus> statuses, LocalDate checkOutDate, LocalDate checkInDate);

    boolean existsByRoomIdAndIdNotAndStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            UUID roomId, UUID excludeReservationId, Collection<ReservationStatus> statuses, LocalDate checkOutDate, LocalDate checkInDate);
}
