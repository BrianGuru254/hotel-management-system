package com.hrms.backend.repository;

import com.hrms.backend.entity.RestaurantOrder;
import com.hrms.backend.entity.RestaurantOrderStatus;
import com.hrms.backend.entity.RestaurantOrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.UUID;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, UUID> {
    @Query(value = "select o from RestaurantOrder o where o.restaurant.id in :restaurantIds and (:restaurantId is null or o.restaurant.id = :restaurantId) and (:tableId is null or o.table.id = :tableId) and (:guestId is null or o.guest.id = :guestId) and (:waiterId is null or o.waiter.id = :waiterId) and (:status is null or o.status = :status) and (:orderType is null or o.orderType = :orderType) and (:fromTime is null or o.createdAt >= :fromTime) and (:untilTime is null or o.createdAt < :untilTime)",
            countQuery = "select count(o) from RestaurantOrder o where o.restaurant.id in :restaurantIds and (:restaurantId is null or o.restaurant.id = :restaurantId) and (:tableId is null or o.table.id = :tableId) and (:guestId is null or o.guest.id = :guestId) and (:waiterId is null or o.waiter.id = :waiterId) and (:status is null or o.status = :status) and (:orderType is null or o.orderType = :orderType) and (:fromTime is null or o.createdAt >= :fromTime) and (:untilTime is null or o.createdAt < :untilTime)")
    Page<RestaurantOrder> search(@Param("restaurantIds") Collection<UUID> restaurantIds,
            @Param("restaurantId") UUID restaurantId, @Param("tableId") UUID tableId, @Param("guestId") UUID guestId,
            @Param("waiterId") UUID waiterId, @Param("status") RestaurantOrderStatus status,
            @Param("orderType") RestaurantOrderType orderType, @Param("fromTime") OffsetDateTime fromTime,
            @Param("untilTime") OffsetDateTime untilTime, Pageable pageable);
    long countByTableIdAndStatusIn(UUID tableId, Collection<RestaurantOrderStatus> statuses);
}
