package com.hrms.backend.service;

import com.hrms.backend.dto.ScopeGrantRequest;
import com.hrms.backend.entity.HmsUser;
import com.hrms.backend.entity.ScopeType;
import com.hrms.backend.entity.UserRoleScope;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.HmsUserRepository;
import com.hrms.backend.repository.UserRoleScopeRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ScopeAuthorizationService {
    private final UserRoleScopeRepository assignments;
    private final HmsUserRepository users;

    public ScopeAuthorizationService(UserRoleScopeRepository assignments, HmsUserRepository users) {
        this.assignments = assignments;
        this.users = users;
    }

    public UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwt) || jwt.getToken().getSubject() == null) {
            throw new AccessDeniedException("A valid user access token is required");
        }
        try {
            return UUID.fromString(jwt.getToken().getSubject());
        } catch (IllegalArgumentException exception) {
            throw new AccessDeniedException("The access token subject is invalid");
        }
    }

    @Transactional(readOnly = true)
    public void requireCapability(String capability, UUID organizationId, UUID branchId,
                                  UUID propertyId, UUID departmentId) {
        UUID userId = currentUserId();
        requireActiveUser(userId);
        if (assignments.findByUserId(userId).stream()
                .filter(grant -> hasCapability(grant, capability))
                .anyMatch(grant -> allows(grant, organizationId, branchId, propertyId, departmentId, null))) {
            return;
        }
        throw new AccessDeniedException("Missing capability or resource scope");
    }

    @Transactional(readOnly = true)
    public void requireSelfCapability(String capability) {
        UUID userId = currentUserId();
        requireActiveUser(userId);
        if (assignments.findByUserId(userId).stream().anyMatch(grant -> hasCapability(grant, capability))) {
            return;
        }
        throw new AccessDeniedException("Missing capability");
    }

    @Transactional(readOnly = true)
    public void requireAnyCapability(String capability) {
        UUID userId = currentUserId();
        requireActiveUser(userId);
        if (assignments.findByUserId(userId).stream().anyMatch(grant -> hasCapability(grant, capability))) {
            return;
        }
        throw new AccessDeniedException("Missing capability");
    }

    @Transactional(readOnly = true)
    public void requireCanManageScope(ScopeGrantRequest target) {
        if (!isManageableByCurrentActor(target)) {
            throw new AccessDeniedException("The requested role scope exceeds the acting user's authority");
        }
    }

    @Transactional(readOnly = true)
    public void requireCanManageUser(HmsUser user) {
        UUID actorId = currentUserId();
        requireActiveUser(actorId);
        if (actorId.equals(user.getId())) {
            throw new AccessDeniedException("Manage the current account through the profile endpoints");
        }
        List<UserRoleScope> userGrants = assignments.findByUserId(user.getId());
        if (userGrants.isEmpty()) {
            throw new AccessDeniedException("The user is outside the acting user's authority");
        }
        for (UserRoleScope grant : userGrants) {
            ScopeGrantRequest target = toRequest(grant);
            if (!isManageableByCurrentActor(target)) {
                throw new AccessDeniedException("The user is outside the acting user's authority");
            }
        }
    }

    @Transactional(readOnly = true)
    public List<HmsUser> visibleUsers() {
        List<UserRoleScope> actorGrants = grantsWithCapability("USER_MANAGE");
        if (actorGrants.isEmpty()) throw new AccessDeniedException("Missing capability");
        if (actorGrants.stream().anyMatch(g -> g.getScopeType() == ScopeType.SYSTEM)) return users.findAll();

        Set<UUID> organizations = new LinkedHashSet<>();
        Set<UUID> branches = new LinkedHashSet<>();
        Set<UUID> properties = new LinkedHashSet<>();
        for (UserRoleScope grant : actorGrants) {
            switch (grant.getScopeType()) {
                case ORGANIZATION -> organizations.add(grant.getOrganization().getId());
                case BRANCH -> branches.add(grant.getBranch().getId());
                case PROPERTY, DEPARTMENT -> properties.add(grant.getProperty().getId());
                case SYSTEM, SELF -> { }
            }
        }
        Set<UUID> candidateIds = new LinkedHashSet<>();
        if (!organizations.isEmpty()) users.findDistinctByOrganizationIds(List.copyOf(organizations)).forEach(u -> candidateIds.add(u.getId()));
        if (!branches.isEmpty()) users.findDistinctByBranchIds(List.copyOf(branches)).forEach(u -> candidateIds.add(u.getId()));
        if (!properties.isEmpty()) users.findDistinctByPropertyIds(List.copyOf(properties)).forEach(u -> candidateIds.add(u.getId()));
        List<HmsUser> visible = new ArrayList<>();
        for (UUID id : candidateIds) users.findById(id).filter(user -> {
            try { requireCanManageUser(user); return true; }
            catch (AccessDeniedException ignored) { return false; }
        }).ifPresent(visible::add);
        return visible;
    }

    private boolean isManageableByCurrentActor(ScopeGrantRequest target) {
        UUID actorId = currentUserId();
        requireActiveUser(actorId);
        return assignments.findByUserId(actorId).stream()
                .filter(grant -> hasCapability(grant, "USER_MANAGE"))
                .anyMatch(grant -> containsGrant(grant, target));
    }

    private boolean containsGrant(UserRoleScope actor, ScopeGrantRequest target) {
        if (target.scopeType() == ScopeType.SELF) return true;
        return switch (actor.getScopeType()) {
            case SYSTEM -> true;
            case ORGANIZATION -> actor.getOrganization().getId().equals(target.organizationId());
            case BRANCH -> actor.getBranch().getId().equals(target.branchId());
            case PROPERTY -> actor.getProperty().getId().equals(target.propertyId());
            case DEPARTMENT -> actor.getDepartment().getId().equals(target.departmentId());
            case SELF -> false;
        };
    }

    @Transactional(readOnly = true)
    public List<UserRoleScope> grantsWithCapability(String capability) {
        UUID userId = currentUserId();
        requireActiveUser(userId);
        return assignments.findByUserId(userId).stream().filter(g -> hasCapability(g, capability)).toList();
    }

    @Transactional(readOnly = true)
    public ScopeVisibility visibility(String capability) {
        List<UserRoleScope> grants = grantsWithCapability(capability);
        if (grants.isEmpty()) throw new AccessDeniedException("Missing capability");
        if (grants.stream().anyMatch(g -> g.getScopeType() == ScopeType.SYSTEM)) {
            return new ScopeVisibility(true, Set.of(), Set.of(), Set.of(), Set.of());
        }
        Set<UUID> organizationIds = new LinkedHashSet<>();
        Set<UUID> branchIds = new LinkedHashSet<>();
        Set<UUID> propertyIds = new LinkedHashSet<>();
        Set<UUID> departmentIds = new LinkedHashSet<>();
        for (UserRoleScope grant : grants) {
            switch (grant.getScopeType()) {
                case ORGANIZATION -> organizationIds.add(grant.getOrganization().getId());
                case BRANCH -> branchIds.add(grant.getBranch().getId());
                case PROPERTY -> propertyIds.add(grant.getProperty().getId());
                case DEPARTMENT -> departmentIds.add(grant.getDepartment().getId());
                case SYSTEM, SELF -> { }
            }
        }
        return new ScopeVisibility(false, Set.copyOf(organizationIds), Set.copyOf(branchIds),
                Set.copyOf(propertyIds), Set.copyOf(departmentIds));
    }

    private void requireActiveUser(UUID userId) {
        if (users.findById(userId).filter(HmsUser::getActive).isEmpty()) {
            throw new AccessDeniedException("The user account is inactive or no longer exists");
        }
    }

    private boolean allows(UserRoleScope grant, UUID organizationId, UUID branchId,
                           UUID propertyId, UUID departmentId, UUID selfUserId) {
        return switch (grant.getScopeType()) {
            case SYSTEM -> true;
            case ORGANIZATION -> organizationId != null && grant.getOrganization().getId().equals(organizationId);
            case BRANCH -> organizationId != null && branchId != null
                    && grant.getOrganization().getId().equals(organizationId)
                    && grant.getBranch().getId().equals(branchId);
            case PROPERTY -> organizationId != null && branchId != null && propertyId != null
                    && grant.getOrganization().getId().equals(organizationId)
                    && grant.getBranch().getId().equals(branchId)
                    && grant.getProperty().getId().equals(propertyId);
            case DEPARTMENT -> organizationId != null && branchId != null && propertyId != null && departmentId != null
                    && grant.getOrganization().getId().equals(organizationId)
                    && grant.getBranch().getId().equals(branchId)
                    && grant.getProperty().getId().equals(propertyId)
                    && grant.getDepartment().getId().equals(departmentId);
            case SELF -> selfUserId != null && currentUserId().equals(selfUserId);
        };
    }

    private boolean hasCapability(UserRoleScope grant, String capability) {
        return grant.getRole().getCapabilities().stream()
                .anyMatch(item -> item.getCapabilityKey().equals(capability));
    }

    private ScopeGrantRequest toRequest(UserRoleScope grant) {
        return new ScopeGrantRequest(grant.getRole().getRoleKey(), grant.getScopeType(),
                grant.getOrganization() == null ? null : grant.getOrganization().getId(),
                grant.getBranch() == null ? null : grant.getBranch().getId(),
                grant.getProperty() == null ? null : grant.getProperty().getId(),
                grant.getDepartment() == null ? null : grant.getDepartment().getId());
    }
}
