package com.hrms.backend.service;

import com.hrms.backend.dto.*;
import com.hrms.backend.entity.*;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.*;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.*;

@Service @Transactional
public class HousekeepingService {
    private static final Set<String> SORTS = Set.of("createdAt", "priority", "status", "taskType");
    private final HousekeepingTaskRepository tasks;
    private final RoomRepository rooms;
    private final EmployeeRepository employees;
    private final PropertyRepository properties;
    private final PropertyVisibilityService visibility;
    private final ScopeAuthorizationService authorization;

    public HousekeepingService(HousekeepingTaskRepository tasks, RoomRepository rooms, EmployeeRepository employees,
            PropertyRepository properties, PropertyVisibilityService visibility, ScopeAuthorizationService authorization) {
        this.tasks = tasks; this.rooms = rooms; this.employees = employees; this.properties = properties;
        this.visibility = visibility; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<HousekeepingTaskResponse> findAll(UUID propertyId, UUID roomId, HousekeepingTaskStatus status,
            int page, int size, String sort, String direction) {
        if (page < 0 || size < 1 || size > 100) throw new IllegalArgumentException("page must be nonnegative and size must be between 1 and 100");
        if (!SORTS.contains(sort)) throw new IllegalArgumentException("Unsupported sort field");
        Pageable pageable = PageRequest.of(page, size, Sort.by("asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC, sort));
        ScopeVisibility scope = authorization.visibility("HOUSEKEEPING_READ");
        Set<UUID> ids = visibility.visiblePropertyIds(scope, propertyId);
        if (ids.isEmpty()) return Page.empty(pageable);
        return tasks.search(ids, propertyId, roomId, status, pageable).map(this::response);
    }

    @Transactional(readOnly = true)
    public HousekeepingTaskResponse findById(UUID id) {
        HousekeepingTask task = requireTask(id); authorize("HOUSEKEEPING_READ", task.getProperty()); return response(task);
    }

    public HousekeepingTaskResponse create(HousekeepingTaskCreateRequest request) {
        Property property = properties.findById(request.propertyId()).orElseThrow(() -> new ResourceNotFoundException("Property", request.propertyId()));
        authorize("HOUSEKEEPING_UPDATE", property);
        Room room = rooms.findByIdForUpdate(request.roomId()).orElseThrow(() -> new ResourceNotFoundException("Room", request.roomId()));
        if (!room.getProperty().getId().equals(property.getId())) throw new InvalidRelationshipException("Room does not belong to the specified property");
        if (room.getCurrentStatus() == RoomStatus.OCCUPIED || room.getCurrentStatus() == RoomStatus.RESERVED
                || room.getCurrentStatus() == RoomStatus.MAINTENANCE || room.getCurrentStatus() == RoomStatus.OUT_OF_ORDER)
            throw new InvalidRelationshipException("Housekeeping cannot be scheduled for an occupied or unavailable room");
        HousekeepingTask task = new HousekeepingTask(); task.setProperty(property); task.setRoom(room);
        task.setTaskType(request.taskType() == null ? HousekeepingTaskType.ROOM_CLEANING : request.taskType());
        task.setPriority(request.priority() == null ? HousekeepingPriority.NORMAL : request.priority());
        task.setNotes(normalize(request.notes()));
        return response(tasks.save(task));
    }

    public HousekeepingTaskResponse update(UUID id, HousekeepingTaskUpdateRequest request) {
        HousekeepingTask task = requireTask(id); authorize("HOUSEKEEPING_UPDATE", task.getProperty());
        if (task.getStatus() == HousekeepingTaskStatus.COMPLETED || task.getStatus() == HousekeepingTaskStatus.CANCELLED)
            throw new InvalidRelationshipException("A finished housekeeping task cannot be changed");
        if (request.assignedEmployeeId() != null) {
            authorization.requireCapability("HOUSEKEEPING_ASSIGN", task.getProperty().getOrganization().getId(),
                    task.getProperty().getBranch().getId(), task.getProperty().getId(), null);
            Employee assignee = employees.findById(request.assignedEmployeeId()).filter(e -> e.getDeletedAt() == null)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", request.assignedEmployeeId()));
            if (assignee.getEmploymentStatus() != EmploymentStatus.ACTIVE || !assignee.getBranch().getId().equals(task.getProperty().getBranch().getId())
                    || (assignee.getDepartment() != null && !assignee.getDepartment().getProperty().getId().equals(task.getProperty().getId())))
                throw new InvalidRelationshipException("Assignee must be active and assigned to this property or branch");
            task.setAssignedEmployee(assignee); task.setAssignedAt(OffsetDateTime.now());
            if (task.getStatus() == HousekeepingTaskStatus.PENDING) task.setStatus(HousekeepingTaskStatus.ASSIGNED);
        }
        if (request.priority() != null) task.setPriority(request.priority());
        if (request.notes() != null) task.setNotes(normalize(request.notes()));
        if (request.status() != null) transition(task, request.status());
        return response(tasks.save(task));
    }

    public HousekeepingTaskResponse complete(UUID id) {
        HousekeepingTask task = requireTask(id); authorize("HOUSEKEEPING_UPDATE", task.getProperty());
        transition(task, HousekeepingTaskStatus.COMPLETED);
        Room room = rooms.findByIdForUpdate(task.getRoom().getId()).orElseThrow(() -> new ResourceNotFoundException("Room", task.getRoom().getId()));
        if (task.getTaskType() == HousekeepingTaskType.ROOM_CLEANING || task.getTaskType() == HousekeepingTaskType.DEEP_CLEANING) {
            if (room.getCurrentStatus() != RoomStatus.DIRTY && room.getCurrentStatus() != RoomStatus.CLEANING)
                throw new InvalidRelationshipException("Only dirty or cleaning rooms can be released as available");
            authorization.requireCapability("ROOM_HOUSEKEEPING_STATUS_UPDATE", task.getProperty().getOrganization().getId(),
                    task.getProperty().getBranch().getId(), task.getProperty().getId(), null);
            room.setCurrentStatus(RoomStatus.AVAILABLE); room.setStatusReason("Housekeeping task completed"); room.setStatusChangedAt(OffsetDateTime.now());
        }
        return response(tasks.save(task));
    }

    private void transition(HousekeepingTask task, HousekeepingTaskStatus target) {
        HousekeepingTaskStatus current = task.getStatus();
        boolean valid = switch (current) {
            case PENDING -> target == HousekeepingTaskStatus.ASSIGNED || target == HousekeepingTaskStatus.CANCELLED;
            case ASSIGNED -> target == HousekeepingTaskStatus.IN_PROGRESS || target == HousekeepingTaskStatus.CANCELLED;
            case IN_PROGRESS -> target == HousekeepingTaskStatus.COMPLETED || target == HousekeepingTaskStatus.CANCELLED;
            default -> false;
        };
        if (!valid) throw new InvalidRelationshipException("Invalid housekeeping status transition: " + current + " to " + target);
        if (target == HousekeepingTaskStatus.ASSIGNED && task.getAssignedEmployee() == null)
            throw new InvalidRelationshipException("Assign an employee before setting task status to ASSIGNED");
        if (target == HousekeepingTaskStatus.IN_PROGRESS) {
            if (task.getAssignedEmployee().getUser() == null || !Objects.equals(task.getAssignedEmployee().getUser().getId(), authorization.currentUserId()))
                authorization.requireCapability("HOUSEKEEPING_ASSIGN", task.getProperty().getOrganization().getId(), task.getProperty().getBranch().getId(), task.getProperty().getId(), null);
            Room room = rooms.findByIdForUpdate(task.getRoom().getId()).orElseThrow(() -> new ResourceNotFoundException("Room", task.getRoom().getId()));
            if (room.getCurrentStatus() == RoomStatus.DIRTY) {
                room.setCurrentStatus(RoomStatus.CLEANING); room.setStatusReason("Housekeeping in progress"); room.setStatusChangedAt(OffsetDateTime.now());
            }
            task.setStartedAt(OffsetDateTime.now());
        }
        if (target == HousekeepingTaskStatus.COMPLETED) task.setCompletedAt(OffsetDateTime.now());
        task.setStatus(target);
    }

    private void authorize(String capability, Property p) { authorization.requireCapability(capability, p.getOrganization().getId(), p.getBranch().getId(), p.getId(), null); }
    private HousekeepingTask requireTask(UUID id) { return tasks.findById(id).orElseThrow(() -> new ResourceNotFoundException("Housekeeping task", id)); }
    private String normalize(String s) { return s == null || s.isBlank() ? null : s.trim(); }
    private HousekeepingTaskResponse response(HousekeepingTask t) {
        return new HousekeepingTaskResponse(t.getId(), t.getProperty().getId(), t.getRoom().getId(), t.getRoom().getRoomNumber(),
                t.getAssignedEmployee() == null ? null : t.getAssignedEmployee().getId(), t.getTaskType(), t.getStatus(), t.getPriority(),
                t.getNotes(), t.getAssignedAt(), t.getStartedAt(), t.getCompletedAt(), t.getCreatedAt());
    }
}
