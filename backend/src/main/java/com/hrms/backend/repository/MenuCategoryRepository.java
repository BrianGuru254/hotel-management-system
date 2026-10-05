package com.hrms.backend.repository;

import com.hrms.backend.entity.MenuCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {
    Page<MenuCategory> findByRestaurantIdAndActiveTrue(UUID restaurantId, Pageable pageable);
    boolean existsByRestaurantIdAndNameIgnoreCase(UUID restaurantId, String name);
    boolean existsByRestaurantIdAndNameIgnoreCaseAndIdNot(UUID restaurantId, String name, UUID id);
}
