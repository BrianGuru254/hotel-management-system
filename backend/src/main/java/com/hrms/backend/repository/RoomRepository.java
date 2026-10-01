package com.hrms.backend.repository;

import com.hrms.backend.entity.Room;
import com.hrms.backend.entity.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    @Query(value = """
            select room from Room room
            where room.property.id in :propertyIds
              and (:propertyId is null or room.property.id = :propertyId)
              and (:roomTypeId is null or room.roomType.id = :roomTypeId)
              and (:roomNumber is null or lower(room.roomNumber) like concat('%', lower(:roomNumber), '%'))
              and (:status is null or room.currentStatus = :status)
              and (:floorNumber is null or room.floorNumber = :floorNumber)
            """,
            countQuery = """
                    select count(room) from Room room
                    where room.property.id in :propertyIds
                      and (:propertyId is null or room.property.id = :propertyId)
                      and (:roomTypeId is null or room.roomType.id = :roomTypeId)
                      and (:roomNumber is null or lower(room.roomNumber) like concat('%', lower(:roomNumber), '%'))
                      and (:status is null or room.currentStatus = :status)
                      and (:floorNumber is null or room.floorNumber = :floorNumber)
                    """)
    Page<Room> searchInProperties(
            @Param("propertyIds") Collection<UUID> propertyIds,
            @Param("propertyId") UUID propertyId,
            @Param("roomTypeId") UUID roomTypeId,
            @Param("roomNumber") String roomNumber,
            @Param("status") RoomStatus status,
            @Param("floorNumber") Integer floorNumber,
            Pageable pageable);

    boolean existsByPropertyIdAndRoomNumberIgnoreCase(UUID propertyId, String roomNumber);

    boolean existsByPropertyIdAndRoomNumberIgnoreCaseAndIdNot(UUID propertyId, String roomNumber, UUID id);
}
