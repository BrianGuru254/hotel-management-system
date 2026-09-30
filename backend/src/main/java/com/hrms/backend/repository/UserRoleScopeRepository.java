package com.hrms.backend.repository;

import com.hrms.backend.entity.UserRoleScope;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleScopeRepository extends JpaRepository<UserRoleScope, UUID> {
    @EntityGraph(attributePaths = {"role", "role.capabilities", "organization", "branch", "property", "department"})
    List<UserRoleScope> findByUserId(UUID userId);

    @EntityGraph(attributePaths = {"role", "role.capabilities"})
    Optional<UserRoleScope> findByIdAndUserId(UUID id, UUID userId);
}
