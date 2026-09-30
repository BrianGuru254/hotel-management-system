package com.hrms.backend.dto;

import java.util.Set;

public record RoleResponse(String roleKey, String displayName, String description, Set<String> capabilities) {
}
