package com.hrms.backend.controller;

import com.hrms.backend.dto.EmployeeRequest;
import com.hrms.backend.dto.EmployeeResponse;
import com.hrms.backend.entity.EmploymentStatus;
import com.hrms.backend.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService service;
    public EmployeeController(EmployeeService service) { this.service = service; }
    @GetMapping public PagedModel<EmployeeResponse> findAll(@RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) UUID branchId, @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) EmploymentStatus status, @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastName") String sort, @RequestParam(defaultValue = "asc") String direction) {
        return new PagedModel<>(service.findAll(organizationId, branchId, departmentId, status, search, page, size, sort, direction));
    }
    @GetMapping("/{id}") public EmployeeResponse findById(@PathVariable UUID id) { return service.findById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) { return service.create(request); }
    @PutMapping("/{id}") public EmployeeResponse update(@PathVariable UUID id, @Valid @RequestBody EmployeeRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}
