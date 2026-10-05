package com.hrms.backend.repository;

import com.hrms.backend.entity.RestaurantTable;
import com.hrms.backend.entity.RestaurantTableStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, UUID> {
    Page<RestaurantTable> findByRestaurantId(UUID restaurantId, Pageable pageable);
    boolean existsByRestaurantIdAndTableNumberIgnoreCase(UUID restaurantId, String tableNumber);
    boolean existsByRestaurantIdAndTableNumberIgnoreCaseAndIdNot(UUID restaurantId, String tableNumber, UUID id);
    long countByRestaurantIdAndStatusNot(UUID restaurantId, RestaurantTableStatus status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from RestaurantTable t where t.id = :id")
    Optional<RestaurantTable> findByIdForUpdate(@Param("id") UUID id);
}
