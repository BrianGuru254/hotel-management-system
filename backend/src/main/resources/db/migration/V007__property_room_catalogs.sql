CREATE TABLE room_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description TEXT,
    base_price NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    max_occupancy INTEGER NOT NULL DEFAULT 1,
    size_square_meters NUMERIC(8, 2),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_room_types_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
    CONSTRAINT ck_room_types_code_upper CHECK (code = upper(code)),
    CONSTRAINT ck_room_types_base_price CHECK (base_price >= 0),
    CONSTRAINT ck_room_types_max_occupancy CHECK (max_occupancy > 0),
    CONSTRAINT ck_room_types_size CHECK (size_square_meters IS NULL OR size_square_meters > 0),
    CONSTRAINT ck_room_types_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);
CREATE INDEX idx_room_types_property_id ON room_types(property_id);
CREATE UNIQUE INDEX uq_room_types_property_code_ci ON room_types(property_id, lower(code));

CREATE TABLE amenities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_amenities_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT
);
CREATE INDEX idx_amenities_property_id ON amenities(property_id);
CREATE UNIQUE INDEX uq_amenities_property_name_ci ON amenities(property_id, lower(name));

INSERT INTO capabilities (capability_key, description) VALUES
    ('ROOM_TYPE_READ', 'Read room type catalogs'),
    ('ROOM_TYPE_CREATE', 'Create room types'),
    ('ROOM_TYPE_UPDATE', 'Update room types'),
    ('ROOM_TYPE_DELETE', 'Delete room types'),
    ('AMENITY_READ', 'Read property amenities'),
    ('AMENITY_CREATE', 'Create property amenities'),
    ('AMENITY_UPDATE', 'Update property amenities'),
    ('AMENITY_DELETE', 'Delete property amenities')
ON CONFLICT (capability_key) DO NOTHING;

INSERT INTO role_capabilities (role_key, capability_key) VALUES
    ('SUPER_ADMIN', 'ROOM_TYPE_READ'),
    ('SUPER_ADMIN', 'ROOM_TYPE_CREATE'),
    ('SUPER_ADMIN', 'ROOM_TYPE_UPDATE'),
    ('SUPER_ADMIN', 'ROOM_TYPE_DELETE'),
    ('SUPER_ADMIN', 'AMENITY_READ'),
    ('SUPER_ADMIN', 'AMENITY_CREATE'),
    ('SUPER_ADMIN', 'AMENITY_UPDATE'),
    ('SUPER_ADMIN', 'AMENITY_DELETE'),
    ('ORGANIZATION_ADMIN', 'ROOM_TYPE_READ'),
    ('ORGANIZATION_ADMIN', 'ROOM_TYPE_CREATE'),
    ('ORGANIZATION_ADMIN', 'ROOM_TYPE_UPDATE'),
    ('ORGANIZATION_ADMIN', 'ROOM_TYPE_DELETE'),
    ('ORGANIZATION_ADMIN', 'AMENITY_READ'),
    ('ORGANIZATION_ADMIN', 'AMENITY_CREATE'),
    ('ORGANIZATION_ADMIN', 'AMENITY_UPDATE'),
    ('ORGANIZATION_ADMIN', 'AMENITY_DELETE'),
    ('BRANCH_MANAGER', 'ROOM_TYPE_READ'),
    ('BRANCH_MANAGER', 'ROOM_TYPE_CREATE'),
    ('BRANCH_MANAGER', 'ROOM_TYPE_UPDATE'),
    ('BRANCH_MANAGER', 'ROOM_TYPE_DELETE'),
    ('BRANCH_MANAGER', 'AMENITY_READ'),
    ('BRANCH_MANAGER', 'AMENITY_CREATE'),
    ('BRANCH_MANAGER', 'AMENITY_UPDATE'),
    ('BRANCH_MANAGER', 'AMENITY_DELETE'),
    ('PROPERTY_MANAGER', 'ROOM_TYPE_READ'),
    ('PROPERTY_MANAGER', 'ROOM_TYPE_CREATE'),
    ('PROPERTY_MANAGER', 'ROOM_TYPE_UPDATE'),
    ('PROPERTY_MANAGER', 'ROOM_TYPE_DELETE'),
    ('PROPERTY_MANAGER', 'AMENITY_READ'),
    ('PROPERTY_MANAGER', 'AMENITY_CREATE'),
    ('PROPERTY_MANAGER', 'AMENITY_UPDATE'),
    ('PROPERTY_MANAGER', 'AMENITY_DELETE'),
    ('FRONT_DESK', 'ROOM_TYPE_READ'),
    ('FRONT_DESK', 'AMENITY_READ'),
    ('RESERVATIONS_AGENT', 'ROOM_TYPE_READ'),
    ('RESERVATIONS_AGENT', 'AMENITY_READ'),
    ('REPORTING_USER', 'ROOM_TYPE_READ'),
    ('REPORTING_USER', 'AMENITY_READ')
ON CONFLICT (role_key, capability_key) DO NOTHING;
