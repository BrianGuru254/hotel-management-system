CREATE TABLE housekeeping_tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    room_id UUID NOT NULL,
    assigned_user_id UUID,
    task_type VARCHAR(50) NOT NULL DEFAULT 'ROOM_CLEANING',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    notes TEXT,
    assigned_at TIMESTAMPTZ,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_housekeeping_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
    CONSTRAINT fk_housekeeping_room_scope FOREIGN KEY (property_id, room_id) REFERENCES rooms(property_id, id) ON DELETE RESTRICT,
    CONSTRAINT fk_housekeeping_assignee FOREIGN KEY (assigned_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT ck_housekeeping_type CHECK (task_type IN ('ROOM_CLEANING','ROOM_INSPECTION','LINEN_CHANGE','AMENITY_REPLENISHMENT','DEEP_CLEANING')),
    CONSTRAINT ck_housekeeping_status CHECK (status IN ('PENDING','ASSIGNED','IN_PROGRESS','COMPLETED','CANCELLED')),
    CONSTRAINT ck_housekeeping_priority CHECK (priority IN ('LOW','NORMAL','HIGH','URGENT'))
);
CREATE INDEX idx_housekeeping_property_status ON housekeeping_tasks(property_id, status, created_at DESC);
CREATE INDEX idx_housekeeping_room_status ON housekeeping_tasks(room_id, status);
