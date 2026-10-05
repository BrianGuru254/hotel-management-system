package com.hrms.backend.repository;

import com.hrms.backend.entity.RestaurantOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantOrderItemRepository extends JpaRepository<RestaurantOrderItem, UUID> {
    List<RestaurantOrderItem> findByOrderIdOrderByCreatedAtAsc(UUID orderId);
    Optional<RestaurantOrderItem> findByIdAndOrderId(UUID id, UUID orderId);
}
