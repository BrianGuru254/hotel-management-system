package com.hrms.backend.repository;

import com.hrms.backend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByPropertyId(UUID propertyId);
    List<Department> findByOrganizationIdIn(Iterable<UUID> organizationIds);
    List<Department> findByBranchIdIn(Iterable<UUID> branchIds);
    List<Department> findByPropertyIdIn(Iterable<UUID> propertyIds);
    List<Department> findByIdIn(Iterable<UUID> departmentIds);
    boolean existsByPropertyIdAndCodeIgnoreCase(UUID propertyId, String code);
    boolean existsByPropertyIdAndCodeIgnoreCaseAndIdNot(UUID propertyId, String code, UUID id);
}
