package com.hrms.backend.controller;

import com.hrms.backend.dto.BranchRequest;
import com.hrms.backend.dto.BranchResponse;
import com.hrms.backend.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches")
public class BranchController {
    private final BranchService service;
    public BranchController(BranchService service) { this.service = service; }

    @GetMapping public List<BranchResponse> findAll(@RequestParam(required = false) UUID organizationId) { return service.findAll(organizationId); }
    @GetMapping("/{id}") public BranchResponse findById(@PathVariable UUID id) { return service.findById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public BranchResponse create(@Valid @RequestBody BranchRequest request) { return service.create(request); }
    @PutMapping("/{id}") public BranchResponse update(@PathVariable UUID id, @Valid @RequestBody BranchRequest request) { return service.update(id, request); }
    @PatchMapping("/{id}/active") public BranchResponse setActive(@PathVariable UUID id, @RequestParam boolean value) { return service.setActive(id, value); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}
