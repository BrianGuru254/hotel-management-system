package com.hrms.backend.repository;

import com.hrms.backend.entity.RestaurantTable;
import com.hrms.backend.entity.RestaurantTableStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, UUID> {
    Page<RestaurantTable> findByRestaurantId(UUID restaurantId, Pageable pageable);
    boolean existsByRestaurantIdAndTableNumberIgnoreCase(UUID restaurantId, String tableNumber);
    boolean existsByRestaurantIdAndTableNumberIgnoreCaseAndIdNot(UUID restaurantId, String tableNumber, UUID id);
    long countByRestaurantIdAndStatusNot(UUID restaurantId, RestaurantTableStatus status);
}
