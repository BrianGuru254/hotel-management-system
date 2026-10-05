package com.hrms.backend.repository;

import com.hrms.backend.entity.Employee;
import com.hrms.backend.entity.EmploymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    @Query(value = "select e from Employee e left join e.department d where e.deletedAt is null and (:systemWide = true or e.organization.id in :organizationIds or e.branch.id in :branchIds or d.property.id in :propertyIds or d.id in :departmentIds) and (:organizationId is null or e.organization.id = :organizationId) and (:branchId is null or e.branch.id = :branchId) and (:departmentId is null or d.id = :departmentId) and (:status is null or e.employmentStatus = :status) and (:search is null or lower(e.firstName) like concat('%', :search, '%') or lower(e.lastName) like concat('%', :search, '%') or lower(e.employeeNumber) like concat('%', :search, '%'))",
            countQuery = "select count(e) from Employee e left join e.department d where e.deletedAt is null and (:systemWide = true or e.organization.id in :organizationIds or e.branch.id in :branchIds or d.property.id in :propertyIds or d.id in :departmentIds) and (:organizationId is null or e.organization.id = :organizationId) and (:branchId is null or e.branch.id = :branchId) and (:departmentId is null or d.id = :departmentId) and (:status is null or e.employmentStatus = :status) and (:search is null or lower(e.firstName) like concat('%', :search, '%') or lower(e.lastName) like concat('%', :search, '%') or lower(e.employeeNumber) like concat('%', :search, '%'))")
    Page<Employee> search(@Param("systemWide") boolean systemWide, @Param("organizationIds") Collection<UUID> organizationIds,
        @Param("branchIds") Collection<UUID> branchIds, @Param("propertyIds") Collection<UUID> propertyIds,
        @Param("departmentIds") Collection<UUID> departmentIds, @Param("organizationId") UUID organizationId,
        @Param("branchId") UUID branchId, @Param("departmentId") UUID departmentId,
        @Param("status") EmploymentStatus status, @Param("search") String search, Pageable pageable);

    boolean existsByOrganizationIdAndEmployeeNumberIgnoreCaseAndDeletedAtIsNull(UUID organizationId, String number);
    boolean existsByOrganizationIdAndEmployeeNumberIgnoreCaseAndIdNotAndDeletedAtIsNull(UUID organizationId, String number, UUID id);
    boolean existsByOrganizationIdAndEmailIgnoreCaseAndDeletedAtIsNull(UUID organizationId, String email);
    boolean existsByOrganizationIdAndEmailIgnoreCaseAndIdNotAndDeletedAtIsNull(UUID organizationId, String email, UUID id);
}
