package com.hrms.backend.repository;

import com.hrms.backend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
    List<Property> findByOrganizationId(UUID organizationId);
    List<Property> findByBranchId(UUID branchId);
    boolean existsByBranchIdAndCodeIgnoreCase(UUID branchId, String code);
    boolean existsByBranchIdAndCodeIgnoreCaseAndIdNot(UUID branchId, String code, UUID id);
}
