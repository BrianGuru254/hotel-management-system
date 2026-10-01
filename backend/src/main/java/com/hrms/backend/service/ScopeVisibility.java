package com.hrms.backend.service;

import java.util.Set;
import java.util.UUID;

public record ScopeVisibility(boolean systemWide, Set<UUID> organizationIds, Set<UUID> branchIds,
                              Set<UUID> propertyIds, Set<UUID> departmentIds) {
}
