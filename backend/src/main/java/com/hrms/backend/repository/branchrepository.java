package com.hrms.backend.repository;

import com.hrms.backend.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {

    List<Branch> findByOrganizationId(UUID organizationId);

    boolean existsByOrganizationIdAndCodeIgnoreCase(
            UUID organizationId,
            String code
    );
}