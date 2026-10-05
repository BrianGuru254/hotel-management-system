CREATE TABLE maintenance_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    room_id UUID,
    reported_by UUID NOT NULL,
    assigned_user_id UUID,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    resolution TEXT,
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    room_status_before VARCHAR(30),
    reported_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_at TIMESTAMPTZ,
    started_at TIMESTAMPTZ,
    resolved_at TIMESTAMPTZ,
    closed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_maintenance_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
    CONSTRAINT fk_maintenance_room_scope FOREIGN KEY (property_id, room_id) REFERENCES rooms(property_id, id) ON DELETE RESTRICT,
    CONSTRAINT fk_maintenance_reporter FOREIGN KEY (reported_by) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_maintenance_assignee FOREIGN KEY (assigned_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT uq_maintenance_property_scope UNIQUE (property_id, id),
    CONSTRAINT ck_maintenance_priority CHECK (priority IN ('LOW','NORMAL','HIGH','URGENT')),
    CONSTRAINT ck_maintenance_status CHECK (status IN ('OPEN','ASSIGNED','IN_PROGRESS','RESOLVED','CLOSED','CANCELLED'))
);

CREATE UNIQUE INDEX uq_maintenance_active_room ON maintenance_requests(room_id)
    WHERE room_id IS NOT NULL AND status IN ('OPEN','ASSIGNED','IN_PROGRESS');
CREATE INDEX idx_maintenance_property_status ON maintenance_requests(property_id, status, reported_at DESC);
CREATE INDEX idx_maintenance_room_status ON maintenance_requests(room_id, status);

CREATE TABLE maintenance_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    maintenance_request_id UUID NOT NULL,
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    reason VARCHAR(1000),
    changed_by UUID NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_maintenance_history_request_scope FOREIGN KEY (property_id, maintenance_request_id)
        REFERENCES maintenance_requests(property_id, id) ON DELETE RESTRICT,
    CONSTRAINT fk_maintenance_history_actor FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT ck_maintenance_history_new_status CHECK (new_status IN ('OPEN','ASSIGNED','IN_PROGRESS','RESOLVED','CLOSED','CANCELLED'))
);
CREATE INDEX idx_maintenance_history_request ON maintenance_status_history(property_id, maintenance_request_id, changed_at);
