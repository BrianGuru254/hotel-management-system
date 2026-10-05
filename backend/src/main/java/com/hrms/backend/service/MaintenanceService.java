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
public class MaintenanceService {
    private static final Set<String> SORTS = Set.of("reportedAt", "priority", "status", "title");
    private static final List<MaintenanceStatus> ROOM_BLOCKING = List.of(
            MaintenanceStatus.OPEN, MaintenanceStatus.ASSIGNED, MaintenanceStatus.IN_PROGRESS);
    private final MaintenanceRequestRepository requests;
    private final MaintenanceStatusHistoryRepository histories;
    private final RoomRepository rooms;
    private final PropertyRepository properties;
    private final EmployeeRepository employees;
    private final HmsUserRepository users;
    private final PropertyVisibilityService visibility;
    private final ScopeAuthorizationService authorization;

    public MaintenanceService(MaintenanceRequestRepository requests, MaintenanceStatusHistoryRepository histories,
            RoomRepository rooms, PropertyRepository properties, EmployeeRepository employees, HmsUserRepository users,
            PropertyVisibilityService visibility, ScopeAuthorizationService authorization) {
        this.requests = requests; this.histories = histories; this.rooms = rooms; this.properties = properties;
        this.employees = employees; this.users = users; this.visibility = visibility; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<MaintenanceResponse> findAll(UUID propertyId, UUID roomId, MaintenanceStatus status,
            MaintenancePriority priority, int page, int size, String sort, String direction) {
        if (page < 0 || size < 1 || size > 100) throw new IllegalArgumentException("page must be nonnegative and size must be between 1 and 100");
        if (!SORTS.contains(sort)) throw new IllegalArgumentException("Unsupported sort field");
        Pageable pageable = PageRequest.of(page, size, Sort.by("asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC, sort));
        ScopeVisibility scope = authorization.visibility("MAINTENANCE_READ");
        Set<UUID> ids = visibility.visiblePropertyIds(scope, propertyId);
        if (ids.isEmpty()) return Page.empty(pageable);
        return requests.search(ids, propertyId, roomId, status, priority, pageable).map(this::response);
    }

    @Transactional(readOnly = true)
    public MaintenanceResponse findById(UUID id) {
        MaintenanceRequest request = requireRequest(id); authorize("MAINTENANCE_READ", request.getProperty()); return response(request);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceHistoryResponse> history(UUID id) {
        MaintenanceRequest request = requireRequest(id); authorize("MAINTENANCE_READ", request.getProperty());
        return histories.findByPropertyIdAndMaintenanceRequestIdOrderByChangedAtAsc(request.getProperty().getId(), id)
                .stream().map(h -> new MaintenanceHistoryResponse(h.getId(), h.getPreviousStatus(), h.getNewStatus(),
                        h.getReason(), h.getChangedBy().getId(), h.getChangedAt())).toList();
    }

    public MaintenanceResponse create(MaintenanceCreateRequest body) {
        Property property = properties.findById(body.propertyId()).orElseThrow(() -> new ResourceNotFoundException("Property", body.propertyId()));
        authorize("MAINTENANCE_UPDATE", property);
        MaintenanceRequest request = new MaintenanceRequest();
        request.setProperty(property); request.setTitle(body.title().trim()); request.setDescription(normalize(body.description()));
        request.setPriority(body.priority() == null ? MaintenancePriority.NORMAL : body.priority());
        request.setReportedBy(currentUser());
        if (body.roomId() != null) {
            Room room = rooms.findByIdForUpdate(body.roomId()).orElseThrow(() -> new ResourceNotFoundException("Room", body.roomId()));
            if (!room.getProperty().getId().equals(property.getId())) throw new InvalidRelationshipException("Room does not belong to the specified property");
            if (room.getCurrentStatus() == RoomStatus.RESERVED || room.getCurrentStatus() == RoomStatus.OCCUPIED)
                throw new InvalidRelationshipException("A reserved or occupied room cannot be blocked for maintenance");
            if (room.getCurrentStatus() == RoomStatus.MAINTENANCE || room.getCurrentStatus() == RoomStatus.OUT_OF_ORDER)
                throw new InvalidRelationshipException("Room is already unavailable for maintenance");
            if (requests.existsByRoomIdAndStatusIn(room.getId(), ROOM_BLOCKING))
                throw new InvalidRelationshipException("An active maintenance request already blocks this room");
            requireRoomMaintenanceCapability(property);
            request.setRoom(room); request.setRoomStatusBefore(room.getCurrentStatus());
            setRoomStatus(room, RoomStatus.MAINTENANCE, "Maintenance request: " + request.getTitle());
        }
        request = requests.save(request);
        record(request, null, MaintenanceStatus.OPEN, "Request reported");
        return response(request);
    }

    public MaintenanceResponse assign(UUID id, MaintenanceAssignRequest body) {
        MaintenanceRequest request = requireRequest(id); authorize("MAINTENANCE_ASSIGN", request.getProperty());
        if (request.getStatus() != MaintenanceStatus.OPEN && request.getStatus() != MaintenanceStatus.ASSIGNED)
            throw new InvalidRelationshipException("Only open or assigned requests can be reassigned");
        Employee assignee = employees.findById(body.employeeId()).filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", body.employeeId()));
        if (assignee.getEmploymentStatus() != EmploymentStatus.ACTIVE
                || !assignee.getBranch().getId().equals(request.getProperty().getBranch().getId())
                || (assignee.getDepartment() != null && !assignee.getDepartment().getProperty().getId().equals(request.getProperty().getId()))
                || assignee.getUser() == null || !hasMaintenanceCapability(assignee.getUser(), request.getProperty()))
            throw new InvalidRelationshipException("Technician must be active, linked to a maintenance-enabled account, and assigned to this property or branch");
        request.setAssignedEmployee(assignee); request.setAssignedAt(OffsetDateTime.now());
        if (request.getStatus() == MaintenanceStatus.OPEN) transition(request, MaintenanceStatus.ASSIGNED, "Technician assigned");
        return response(requests.save(request));
    }

    public MaintenanceResponse start(UUID id) {
        MaintenanceRequest request = requireRequest(id); authorize("MAINTENANCE_UPDATE", request.getProperty());
        if (request.getAssignedEmployee() == null) throw new InvalidRelationshipException("Assign a technician before starting work");
        requireAssigneeOrManager(request);
        if (request.getStatus() != MaintenanceStatus.ASSIGNED) throw new InvalidRelationshipException("Only assigned requests can be started");
        request.setStartedAt(OffsetDateTime.now()); transition(request, MaintenanceStatus.IN_PROGRESS, "Work started");
        return response(requests.save(request));
    }

    public MaintenanceResponse resolve(UUID id, MaintenanceResolutionRequest body) {
        MaintenanceRequest request = requireRequest(id); authorize("MAINTENANCE_UPDATE", request.getProperty()); requireAssigneeOrManager(request);
        if (request.getStatus() != MaintenanceStatus.IN_PROGRESS) throw new InvalidRelationshipException("Only in-progress requests can be resolved");
        request.setResolution(body.resolution().trim()); request.setResolvedAt(OffsetDateTime.now());
        transition(request, MaintenanceStatus.RESOLVED, "Resolved: " + body.resolution().trim());
        restoreRoom(request);
        return response(requests.save(request));
    }

    public MaintenanceResponse close(UUID id) {
        MaintenanceRequest request = requireRequest(id); authorize("MAINTENANCE_UPDATE", request.getProperty());
        if (request.getStatus() != MaintenanceStatus.RESOLVED) throw new InvalidRelationshipException("Only resolved requests can be closed");
        request.setClosedAt(OffsetDateTime.now()); transition(request, MaintenanceStatus.CLOSED, "Request closed");
        return response(requests.save(request));
    }

    public MaintenanceResponse cancel(UUID id) {
        MaintenanceRequest request = requireRequest(id); authorize("MAINTENANCE_UPDATE", request.getProperty());
        if (request.getStatus() != MaintenanceStatus.OPEN && request.getStatus() != MaintenanceStatus.ASSIGNED)
            throw new InvalidRelationshipException("Only open or assigned requests can be cancelled");
        transition(request, MaintenanceStatus.CANCELLED, "Request cancelled"); restoreRoom(request);
        return response(requests.save(request));
    }

    private void restoreRoom(MaintenanceRequest request) {
        if (request.getRoom() == null) return;
        Room room = rooms.findByIdForUpdate(request.getRoom().getId()).orElseThrow(() -> new ResourceNotFoundException("Room", request.getRoom().getId()));
        if (room.getCurrentStatus() != RoomStatus.MAINTENANCE) return;
        requireRoomMaintenanceCapability(request.getProperty());
        RoomStatus previous = request.getRoomStatusBefore();
        if (previous == RoomStatus.AVAILABLE) setRoomStatus(room, RoomStatus.AVAILABLE, "Maintenance request resolved");
        else setRoomStatus(room, RoomStatus.DIRTY, "Maintenance completed; housekeeping required");
    }

    private void transition(MaintenanceRequest request, MaintenanceStatus target, String reason) {
        MaintenanceStatus previous = request.getStatus(); request.setStatus(target); record(request, previous, target, reason);
    }
    private void record(MaintenanceRequest request, MaintenanceStatus previous, MaintenanceStatus next, String reason) {
        MaintenanceStatusHistory h = new MaintenanceStatusHistory(); h.setPropertyId(request.getProperty().getId());
        h.setMaintenanceRequestId(request.getId()); h.setPreviousStatus(previous); h.setNewStatus(next);
        h.setReason(reason == null || reason.length() <= 1000 ? reason : reason.substring(0, 1000)); h.setChangedBy(currentUser()); histories.save(h);
    }
    private void setRoomStatus(Room room, RoomStatus status, String reason) {
        room.setCurrentStatus(status); room.setStatusReason(reason.length() <= 500 ? reason : reason.substring(0, 500)); room.setStatusChangedAt(OffsetDateTime.now());
    }
    private void requireRoomMaintenanceCapability(Property p) {
        if (!authorization.hasCapabilityForResource("ROOM_STATUS_UPDATE", p.getOrganization().getId(), p.getBranch().getId(), p.getId(), null))
            authorization.requireCapability("ROOM_MAINTENANCE_STATUS_UPDATE", p.getOrganization().getId(), p.getBranch().getId(), p.getId(), null);
    }
    private void requireAssigneeOrManager(MaintenanceRequest r) {
        if (r.getAssignedEmployee().getUser() == null || !r.getAssignedEmployee().getUser().getId().equals(authorization.currentUserId()))
            authorization.requireCapability("MAINTENANCE_ASSIGN", r.getProperty().getOrganization().getId(), r.getProperty().getBranch().getId(), r.getProperty().getId(), null);
    }
    private boolean hasMaintenanceCapability(HmsUser user, Property property) {
        return Boolean.TRUE.equals(user.getActive()) && user.getRoleScopes().stream()
                .filter(g -> g.getRole().getCapabilities().stream().anyMatch(c -> "MAINTENANCE_UPDATE".equals(c.getCapabilityKey())))
                .anyMatch(g -> switch (g.getScopeType()) {
                    case SYSTEM -> true;
                    case ORGANIZATION -> g.getOrganization() != null && g.getOrganization().getId().equals(property.getOrganization().getId());
                    case BRANCH -> g.getBranch() != null && g.getBranch().getId().equals(property.getBranch().getId());
                    case PROPERTY, DEPARTMENT -> g.getProperty() != null && g.getProperty().getId().equals(property.getId());
                    case SELF -> false;
                });
    }
    private void authorize(String cap, Property p) { authorization.requireCapability(cap, p.getOrganization().getId(), p.getBranch().getId(), p.getId(), null); }
    private HmsUser currentUser() { return users.findById(authorization.currentUserId()).orElseThrow(() -> new AccessDeniedException("Current user is unavailable")); }
    private MaintenanceRequest requireRequest(UUID id) { return requests.findById(id).orElseThrow(() -> new ResourceNotFoundException("Maintenance request", id)); }
    private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private MaintenanceResponse response(MaintenanceRequest r) {
        return new MaintenanceResponse(r.getId(), r.getProperty().getId(), r.getRoom() == null ? null : r.getRoom().getId(),
                r.getRoom() == null ? null : r.getRoom().getRoomNumber(), r.getReportedBy().getId(),
                r.getAssignedEmployee() == null ? null : r.getAssignedEmployee().getId(), r.getTitle(), r.getDescription(), r.getResolution(),
                r.getPriority(), r.getStatus(), r.getReportedAt(), r.getAssignedAt(), r.getStartedAt(), r.getResolvedAt(), r.getClosedAt());
    }
}
