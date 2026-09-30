package com.hrms.backend.controller;

import com.hrms.backend.dto.*;
import com.hrms.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService service;

    public UserController(UserService service) { this.service = service; }

    @GetMapping("/roles")
    public List<RoleResponse> roles() { return service.roleCatalog(); }

    @GetMapping("/users")
    public List<UserResponse> findAll() { return service.findAll(); }

    @GetMapping("/users/{id}")
    public UserResponse findById(@PathVariable UUID id) { return service.findById(id); }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) { return service.create(request); }

    @PutMapping("/users/{id}")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) { return service.update(id, request); }

    @PatchMapping("/users/{id}/active")
    public UserResponse setActive(@PathVariable UUID id, @RequestParam boolean value) { return service.setActive(id, value); }

    @PostMapping("/users/{id}/role-scopes")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRoleScopeResponse addRoleScope(@PathVariable UUID id, @Valid @RequestBody ScopeGrantRequest request) {
        return service.addRoleScope(id, request);
    }

    @DeleteMapping("/users/{id}/role-scopes/{assignmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRoleScope(@PathVariable UUID id, @PathVariable UUID assignmentId) {
        service.removeRoleScope(id, assignmentId);
    }
}
