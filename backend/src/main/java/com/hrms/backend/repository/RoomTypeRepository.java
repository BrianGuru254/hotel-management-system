package com.hrms.backend.repository;

import com.hrms.backend.entity.RoomType;
import com.hrms.backend.entity.RoomTypeStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;
import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomType, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select roomType from RoomType roomType where roomType.id = :id")
    Optional<RoomType> findByIdForUpdate(@Param("id") UUID id);

    @Query(value = """
            select roomType from RoomType roomType
            where roomType.property.id in :propertyIds
              and (:propertyId is null or roomType.property.id = :propertyId)
              and (:name is null or lower(roomType.name) like concat('%', lower(:name), '%'))
              and (:code is null or lower(roomType.code) like concat('%', lower(:code), '%'))
              and (:status is null or roomType.status = :status)
            """,
            countQuery = """
                    select count(roomType) from RoomType roomType
                    where roomType.property.id in :propertyIds
                      and (:propertyId is null or roomType.property.id = :propertyId)
                      and (:name is null or lower(roomType.name) like concat('%', lower(:name), '%'))
                      and (:code is null or lower(roomType.code) like concat('%', lower(:code), '%'))
                      and (:status is null or roomType.status = :status)
                    """)
    Page<RoomType> searchInProperties(
            @Param("propertyIds") Collection<UUID> propertyIds,
            @Param("propertyId") UUID propertyId,
            @Param("name") String name,
            @Param("code") String code,
            @Param("status") RoomTypeStatus status,
            Pageable pageable);

    boolean existsByPropertyIdAndCodeIgnoreCase(UUID propertyId, String code);

    boolean existsByPropertyIdAndCodeIgnoreCaseAndIdNot(UUID propertyId, String code, UUID id);
}
