ALTER TABLE room_types
    ADD CONSTRAINT uq_room_types_property_id_id UNIQUE (property_id, id);

ALTER TABLE amenities
    ADD CONSTRAINT uq_amenities_property_id_id UNIQUE (property_id, id);

CREATE TABLE rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    room_type_id UUID NOT NULL,
    room_number VARCHAR(30) NOT NULL,
    floor_number INTEGER,
    capacity INTEGER NOT NULL DEFAULT 1,
    current_status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    status_reason VARCHAR(500),
    status_changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rooms_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
    CONSTRAINT fk_rooms_room_type FOREIGN KEY (room_type_id) REFERENCES room_types(id) ON DELETE RESTRICT,
    CONSTRAINT fk_rooms_room_type_scope FOREIGN KEY (property_id, room_type_id)
        REFERENCES room_types(property_id, id) ON DELETE RESTRICT,
    CONSTRAINT uq_rooms_property_scope UNIQUE (property_id, id),
    CONSTRAINT ck_rooms_number_upper CHECK (room_number = upper(room_number)),
    CONSTRAINT ck_rooms_capacity CHECK (capacity > 0),
    CONSTRAINT ck_rooms_status CHECK (current_status IN (
        'AVAILABLE', 'RESERVED', 'OCCUPIED', 'DIRTY', 'CLEANING', 'MAINTENANCE', 'OUT_OF_ORDER'
    ))
);
CREATE INDEX idx_rooms_property_id ON rooms(property_id);
CREATE INDEX idx_rooms_property_status ON rooms(property_id, current_status);
CREATE INDEX idx_rooms_room_type_id ON rooms(room_type_id);
CREATE UNIQUE INDEX uq_rooms_property_room_number_ci ON rooms(property_id, lower(room_number));

CREATE TABLE room_amenities (
    property_id UUID NOT NULL,
    room_id UUID NOT NULL,
    amenity_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_room_amenities PRIMARY KEY (room_id, amenity_id),
    CONSTRAINT fk_room_amenities_room_scope FOREIGN KEY (property_id, room_id)
        REFERENCES rooms(property_id, id) ON DELETE CASCADE,
    CONSTRAINT fk_room_amenities_amenity_scope FOREIGN KEY (property_id, amenity_id)
        REFERENCES amenities(property_id, id) ON DELETE CASCADE
);
CREATE INDEX idx_room_amenities_property_id ON room_amenities(property_id);
CREATE INDEX idx_room_amenities_amenity_id ON room_amenities(amenity_id);

INSERT INTO capabilities (capability_key, description) VALUES
    ('ROOM_READ', 'Read rooms'),
    ('ROOM_CREATE', 'Create rooms'),
    ('ROOM_UPDATE', 'Update rooms'),
    ('ROOM_DELETE', 'Delete rooms'),
    ('ROOM_STATUS_UPDATE', 'Update room status'),
    ('ROOM_HOUSEKEEPING_STATUS_UPDATE', 'Update room status for housekeeping work'),
    ('ROOM_MAINTENANCE_STATUS_UPDATE', 'Update room status for maintenance work'),
    ('ROOM_AMENITY_UPDATE', 'Assign amenities to rooms')
ON CONFLICT (capability_key) DO NOTHING;

INSERT INTO role_capabilities (role_key, capability_key) VALUES
    ('SUPER_ADMIN', 'ROOM_READ'),
    ('SUPER_ADMIN', 'ROOM_CREATE'),
    ('SUPER_ADMIN', 'ROOM_UPDATE'),
    ('SUPER_ADMIN', 'ROOM_DELETE'),
    ('SUPER_ADMIN', 'ROOM_STATUS_UPDATE'),
    ('SUPER_ADMIN', 'ROOM_HOUSEKEEPING_STATUS_UPDATE'),
    ('SUPER_ADMIN', 'ROOM_MAINTENANCE_STATUS_UPDATE'),
    ('SUPER_ADMIN', 'ROOM_AMENITY_UPDATE'),
    ('ORGANIZATION_ADMIN', 'ROOM_READ'),
    ('ORGANIZATION_ADMIN', 'ROOM_CREATE'),
    ('ORGANIZATION_ADMIN', 'ROOM_UPDATE'),
    ('ORGANIZATION_ADMIN', 'ROOM_DELETE'),
    ('ORGANIZATION_ADMIN', 'ROOM_STATUS_UPDATE'),
    ('ORGANIZATION_ADMIN', 'ROOM_AMENITY_UPDATE'),
    ('BRANCH_MANAGER', 'ROOM_READ'),
    ('BRANCH_MANAGER', 'ROOM_CREATE'),
    ('BRANCH_MANAGER', 'ROOM_UPDATE'),
    ('BRANCH_MANAGER', 'ROOM_DELETE'),
    ('BRANCH_MANAGER', 'ROOM_STATUS_UPDATE'),
    ('BRANCH_MANAGER', 'ROOM_AMENITY_UPDATE'),
    ('PROPERTY_MANAGER', 'ROOM_READ'),
    ('PROPERTY_MANAGER', 'ROOM_CREATE'),
    ('PROPERTY_MANAGER', 'ROOM_UPDATE'),
    ('PROPERTY_MANAGER', 'ROOM_DELETE'),
    ('PROPERTY_MANAGER', 'ROOM_STATUS_UPDATE'),
    ('PROPERTY_MANAGER', 'ROOM_AMENITY_UPDATE'),
    ('FRONT_DESK', 'ROOM_READ'),
    ('RESERVATIONS_AGENT', 'ROOM_READ'),
    ('HOUSEKEEPING_MANAGER', 'ROOM_READ'),
    ('HOUSEKEEPING_MANAGER', 'ROOM_HOUSEKEEPING_STATUS_UPDATE'),
    ('HOUSEKEEPING_STAFF', 'ROOM_READ'),
    ('HOUSEKEEPING_STAFF', 'ROOM_HOUSEKEEPING_STATUS_UPDATE'),
    ('MAINTENANCE_MANAGER', 'ROOM_READ'),
    ('MAINTENANCE_MANAGER', 'ROOM_MAINTENANCE_STATUS_UPDATE'),
    ('MAINTENANCE_STAFF', 'ROOM_READ'),
    ('MAINTENANCE_STAFF', 'ROOM_MAINTENANCE_STATUS_UPDATE'),
    ('REPORTING_USER', 'ROOM_READ')
ON CONFLICT (role_key, capability_key) DO NOTHING;
