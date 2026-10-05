package com.hrms.backend.repository;

import com.hrms.backend.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    Page<Restaurant> findByBranchIdIn(Collection<UUID> branchIds, Pageable pageable);
    boolean existsByBranchIdAndNameIgnoreCase(UUID branchId, String name);
    boolean existsByBranchIdAndNameIgnoreCaseAndIdNot(UUID branchId, String name, UUID id);
    long countByIdAndActiveTrue(UUID id);
}
