package com.hrms.backend.repository;

import com.hrms.backend.entity.HmsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HmsUserRepository extends JpaRepository<HmsUser, UUID> {
    Optional<HmsUser> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

    @Query("select distinct u from HmsUser u join u.roleScopes s where s.organization.id in :ids")
    List<HmsUser> findDistinctByOrganizationIds(@Param("ids") List<UUID> ids);

    @Query("select distinct u from HmsUser u join u.roleScopes s where s.branch.id in :ids")
    List<HmsUser> findDistinctByBranchIds(@Param("ids") List<UUID> ids);

    @Query("select distinct u from HmsUser u join u.roleScopes s where s.property.id in :ids")
    List<HmsUser> findDistinctByPropertyIds(@Param("ids") List<UUID> ids);
}
