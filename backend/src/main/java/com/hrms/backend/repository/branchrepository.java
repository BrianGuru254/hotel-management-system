package com.hrms.backend.repository;

import com.hrms.backend.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {

    List<Branch> findByOrganizationId(UUID organizationId);

    List<Branch> findByOrganizationIdIn(Iterable<UUID> organizationIds);

    List<Branch> findByIdIn(Iterable<UUID> branchIds);

    boolean existsByOrganizationIdAndCodeIgnoreCase(
            UUID organizationId,
            String code
    );

    boolean existsByOrganizationIdAndCodeIgnoreCaseAndIdNot(UUID organizationId, String code, UUID id);
}
