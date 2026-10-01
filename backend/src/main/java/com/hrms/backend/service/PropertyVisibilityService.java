package com.hrms.backend.service;

import com.hrms.backend.entity.Property;
import com.hrms.backend.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class PropertyVisibilityService {
    private final PropertyRepository properties;

    public PropertyVisibilityService(PropertyRepository properties) {
        this.properties = properties;
    }

    public Set<UUID> visiblePropertyIds(ScopeVisibility visibility, UUID requestedPropertyId) {
        Set<UUID> ids = new LinkedHashSet<>();
        if (visibility.systemWide()) {
            if (requestedPropertyId != null) return Set.of(requestedPropertyId);
            properties.findAll().forEach(property -> ids.add(property.getId()));
            return ids;
        }
        if (!visibility.organizationIds().isEmpty()) {
            properties.findByOrganizationIdIn(visibility.organizationIds()).forEach(property -> ids.add(property.getId()));
        }
        if (!visibility.branchIds().isEmpty()) {
            properties.findByBranchIdIn(visibility.branchIds()).forEach(property -> ids.add(property.getId()));
        }
        if (!visibility.propertyIds().isEmpty()) {
            properties.findByIdIn(visibility.propertyIds()).forEach(property -> ids.add(property.getId()));
        }
        if (requestedPropertyId != null && !ids.contains(requestedPropertyId)) return Set.of();
        return ids;
    }
}
