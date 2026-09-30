package com.hrms.backend.repository;

import com.hrms.backend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
    List<Property> findByOrganizationId(UUID organizationId);
    List<Property> findByBranchId(UUID branchId);
    List<Property> findByOrganizationIdIn(Iterable<UUID> organizationIds);
    List<Property> findByBranchIdIn(Iterable<UUID> branchIds);
    List<Property> findByIdIn(Iterable<UUID> propertyIds);
    boolean existsByBranchIdAndCodeIgnoreCase(UUID branchId, String code);
    boolean existsByBranchIdAndCodeIgnoreCaseAndIdNot(UUID branchId, String code, UUID id);
}
