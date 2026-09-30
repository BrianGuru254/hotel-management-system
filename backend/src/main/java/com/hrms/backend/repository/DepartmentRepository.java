package com.hrms.backend.repository;

import com.hrms.backend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByPropertyId(UUID propertyId);
    boolean existsByPropertyIdAndCodeIgnoreCase(UUID propertyId, String code);
}
