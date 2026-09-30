package com.hrms.backend.service;

import com.hrms.backend.dto.*;
import com.hrms.backend.entity.*;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    private final HmsUserRepository users;
    private final RoleRepository roles;
    private final UserRoleScopeRepository scopes;
    private final OrganizationRepository organizations;
    private final BranchRepository branches;
    private final PropertyRepository properties;
    private final DepartmentRepository departments;
    private final PasswordEncoder passwordEncoder;
    private final ScopeAuthorizationService authorization;
    private final RefreshTokenRepository refreshTokens;

    public UserService(HmsUserRepository users, RoleRepository roles, UserRoleScopeRepository scopes,
                       OrganizationRepository organizations, BranchRepository branches, PropertyRepository properties,
                       DepartmentRepository departments, PasswordEncoder passwordEncoder,
                       ScopeAuthorizationService authorization, RefreshTokenRepository refreshTokens) {
        this.users = users;
        this.roles = roles;
        this.scopes = scopes;
        this.organizations = organizations;
        this.branches = branches;
        this.properties = properties;
        this.departments = departments;
        this.passwordEncoder = passwordEncoder;
        this.authorization = authorization;
        this.refreshTokens = refreshTokens;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return authorization.visibleUsers().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        HmsUser user = get(id);
        authorization.requireCanManageUser(user);
        return toResponse(user);
    }

    public UserResponse create(UserCreateRequest request) {
        authorization.requireAnyCapability("USER_MANAGE");
        validatePassword(request.password());
        if (users.existsByEmailIgnoreCase(request.email().trim())) throw new DuplicateResourceException("Email address already exists");
        ensureNoDuplicateGrants(request.roleScopes());
        HmsUser user = new HmsUser();
        user.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        for (ScopeGrantRequest grant : request.roleScopes()) {
            user.getRoleScopes().add(toEntity(user, grant));
        }
        return toResponse(users.save(user));
    }

    public UserResponse update(UUID id, UserUpdateRequest request) {
        HmsUser user = get(id);
        authorization.requireCanManageUser(user);
        users.findByEmailIgnoreCase(request.email().trim())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new DuplicateResourceException("Email address already exists"); });
        user.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        return toResponse(users.save(user));
    }

    public UserResponse setActive(UUID id, boolean active) {
        HmsUser user = get(id);
        authorization.requireCanManageUser(user);
        user.setActive(active);
        HmsUser saved = users.save(user);
        if (!active) refreshTokens.revokeAllForUser(id, OffsetDateTime.now(ZoneOffset.UTC));
        return toResponse(saved);
    }

    public UserRoleScopeResponse addRoleScope(UUID userId, ScopeGrantRequest request) {
        HmsUser user = get(userId);
        authorization.requireCanManageUser(user);
        ensureNoDuplicateGrants(List.of(request));
        UserRoleScope grant = toEntity(user, request);
        user.getRoleScopes().add(grant);
        users.save(user);
        return toScopeResponse(grant);
    }

    public void removeRoleScope(UUID userId, UUID assignmentId) {
        HmsUser user = get(userId);
        authorization.requireCanManageUser(user);
        if (user.getRoleScopes().size() <= 1) throw new InvalidRelationshipException("A user must retain at least one role and scope assignment");
        UserRoleScope grant = scopes.findByIdAndUserId(assignmentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Role scope assignment", assignmentId));
        user.getRoleScopes().removeIf(item -> item.getId().equals(grant.getId()));
        users.save(user);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> roleCatalog() {
        authorization.requireAnyCapability("ROLE_READ");
        return roles.findAll().stream()
                .map(role -> new RoleResponse(role.getRoleKey(), role.getDisplayName(), role.getDescription(),
                        role.getCapabilities().stream().map(Capability::getCapabilityKey).collect(java.util.stream.Collectors.toUnmodifiableSet())))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse currentProfile() {
        authorization.requireSelfCapability("PROFILE_READ");
        return toResponse(get(authorization.currentUserId()));
    }

    public UserResponse updateCurrentProfile(UserUpdateRequest request) {
        authorization.requireSelfCapability("PROFILE_UPDATE");
        HmsUser user = get(authorization.currentUserId());
        users.findByEmailIgnoreCase(request.email().trim())
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> { throw new DuplicateResourceException("Email address already exists"); });
        user.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        return toResponse(users.save(user));
    }

    private UserRoleScope toEntity(HmsUser user, ScopeGrantRequest request) {
        authorization.requireCanManageScope(request);
        Role role = roles.findById(request.roleKey().trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new ResourceNotFoundException("Role", request.roleKey()));
        validateShape(request);
        UserRoleScope grant = new UserRoleScope();
        grant.setUser(user);
        grant.setRole(role);
        grant.setScopeType(request.scopeType());
        switch (request.scopeType()) {
            case SYSTEM, SELF -> { }
            case ORGANIZATION -> grant.setOrganization(organizations.findById(request.organizationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organization", request.organizationId())));
            case BRANCH -> {
                Organization organization = organizations.findById(request.organizationId())
                        .orElseThrow(() -> new ResourceNotFoundException("Organization", request.organizationId()));
                Branch branch = branches.findById(request.branchId())
                        .orElseThrow(() -> new ResourceNotFoundException("Branch", request.branchId()));
                if (!branch.getOrganization().getId().equals(organization.getId())) throw new InvalidRelationshipException("The branch does not belong to the supplied organization");
                grant.setOrganization(organization);
                grant.setBranch(branch);
            }
            case PROPERTY -> {
                Organization organization = organizations.findById(request.organizationId())
                        .orElseThrow(() -> new ResourceNotFoundException("Organization", request.organizationId()));
                Branch branch = branches.findById(request.branchId())
                        .orElseThrow(() -> new ResourceNotFoundException("Branch", request.branchId()));
                Property property = properties.findById(request.propertyId())
                        .orElseThrow(() -> new ResourceNotFoundException("Property", request.propertyId()));
                if (!branch.getOrganization().getId().equals(organization.getId())
                        || !property.getOrganization().getId().equals(organization.getId())
                        || !property.getBranch().getId().equals(branch.getId())) {
                    throw new InvalidRelationshipException("The property, branch, and organization must belong to the same hierarchy");
                }
                grant.setOrganization(organization);
                grant.setBranch(branch);
                grant.setProperty(property);
            }
            case DEPARTMENT -> {
                Organization organization = organizations.findById(request.organizationId())
                        .orElseThrow(() -> new ResourceNotFoundException("Organization", request.organizationId()));
                Branch branch = branches.findById(request.branchId())
                        .orElseThrow(() -> new ResourceNotFoundException("Branch", request.branchId()));
                Property property = properties.findById(request.propertyId())
                        .orElseThrow(() -> new ResourceNotFoundException("Property", request.propertyId()));
                Department department = departments.findById(request.departmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));
                if (!branch.getOrganization().getId().equals(organization.getId())
                        || !property.getOrganization().getId().equals(organization.getId())
                        || !property.getBranch().getId().equals(branch.getId())
                        || !department.getOrganization().getId().equals(organization.getId())
                        || !department.getBranch().getId().equals(branch.getId())
                        || !department.getProperty().getId().equals(property.getId())) {
                    throw new InvalidRelationshipException("The department must belong to the supplied property hierarchy");
                }
                grant.setOrganization(organization);
                grant.setBranch(branch);
                grant.setProperty(property);
                grant.setDepartment(department);
            }
        }
        return grant;
    }

    private void validateShape(ScopeGrantRequest request) {
        boolean valid = switch (request.scopeType()) {
            case SYSTEM, SELF -> request.organizationId() == null && request.branchId() == null
                    && request.propertyId() == null && request.departmentId() == null;
            case ORGANIZATION -> request.organizationId() != null && request.branchId() == null
                    && request.propertyId() == null && request.departmentId() == null;
            case BRANCH -> request.organizationId() != null && request.branchId() != null
                    && request.propertyId() == null && request.departmentId() == null;
            case PROPERTY -> request.organizationId() != null && request.branchId() != null
                    && request.propertyId() != null && request.departmentId() == null;
            case DEPARTMENT -> request.organizationId() != null && request.branchId() != null
                    && request.propertyId() != null && request.departmentId() != null;
        };
        if (!valid) throw new InvalidRelationshipException("The scope IDs do not match the requested scope type");
        if (request.scopeType() == ScopeType.SYSTEM && !request.roleKey().equalsIgnoreCase("SUPER_ADMIN")) {
            throw new InvalidRelationshipException("Only SUPER_ADMIN may have SYSTEM scope");
        }
        if (request.scopeType() != ScopeType.SYSTEM && request.roleKey().equalsIgnoreCase("SUPER_ADMIN")) {
            throw new InvalidRelationshipException("SUPER_ADMIN must use SYSTEM scope");
        }
    }

    private void ensureNoDuplicateGrants(List<ScopeGrantRequest> grants) {
        Set<String> keys = new HashSet<>();
        for (ScopeGrantRequest grant : grants) {
            String key = grant.roleKey().toUpperCase(Locale.ROOT) + ":" + grant.scopeType() + ":"
                    + grant.organizationId() + ":" + grant.branchId() + ":" + grant.propertyId() + ":" + grant.departmentId();
            if (!keys.add(key)) throw new InvalidRelationshipException("Duplicate role and scope assignment");
        }
    }

    private HmsUser get(UUID id) {
        return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private UserResponse toResponse(HmsUser user) {
        List<UserRoleScopeResponse> roleResponses = scopes.findByUserId(user.getId()).stream().map(this::toScopeResponse).toList();
        return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getActive(),
                roleResponses, user.getCreatedAt(), user.getUpdatedAt());
    }

    private UserRoleScopeResponse toScopeResponse(UserRoleScope grant) {
        return new UserRoleScopeResponse(grant.getId(), grant.getRole().getRoleKey(), grant.getScopeType(),
                grant.getOrganization() == null ? null : grant.getOrganization().getId(),
                grant.getBranch() == null ? null : grant.getBranch().getId(),
                grant.getProperty() == null ? null : grant.getProperty().getId(),
                grant.getDepartment() == null ? null : grant.getDepartment().getId());
    }

    private void validatePassword(String password) {
        if (password == null || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password must not exceed 72 UTF-8 bytes");
        }
    }
}
