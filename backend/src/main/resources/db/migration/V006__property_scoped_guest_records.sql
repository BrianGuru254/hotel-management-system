CREATE TABLE guests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    guest_number VARCHAR(50) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(150),
    nationality VARCHAR(100),
    identification_type VARCHAR(50),
    identification_number VARCHAR(100),
    address_line_1 VARCHAR(200),
    address_line_2 VARCHAR(200),
    city VARCHAR(100),
    county VARCHAR(100),
    country VARCHAR(100),
    date_of_birth DATE,
    emergency_contact_name VARCHAR(150),
    emergency_contact_phone VARCHAR(30),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_guests_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
    CONSTRAINT uq_guests_property_guest_number UNIQUE (property_id, guest_number),
    CONSTRAINT ck_guests_identification_pair CHECK (
        (identification_type IS NULL AND identification_number IS NULL)
        OR (identification_type IS NOT NULL AND identification_number IS NOT NULL)
    )
);

CREATE INDEX idx_guests_property_name ON guests (property_id, lower(last_name), lower(first_name));
CREATE INDEX idx_guests_property_phone ON guests (property_id, phone) WHERE phone IS NOT NULL;
CREATE INDEX idx_guests_property_email ON guests (property_id, lower(email)) WHERE email IS NOT NULL;
CREATE UNIQUE INDEX uq_guests_property_identification
    ON guests (property_id, upper(identification_type), upper(identification_number))
    WHERE identification_type IS NOT NULL AND identification_number IS NOT NULL;

INSERT INTO capabilities (capability_key, description) VALUES
    ('GUEST_READ', 'Read guest records'),
    ('GUEST_CREATE', 'Create guest records'),
    ('GUEST_UPDATE', 'Update guest records')
ON CONFLICT (capability_key) DO NOTHING;

INSERT INTO role_capabilities (role_key, capability_key) VALUES
    ('SUPER_ADMIN', 'GUEST_READ'),
    ('SUPER_ADMIN', 'GUEST_CREATE'),
    ('SUPER_ADMIN', 'GUEST_UPDATE'),
    ('FRONT_DESK', 'GUEST_READ'),
    ('FRONT_DESK', 'GUEST_CREATE'),
    ('FRONT_DESK', 'GUEST_UPDATE'),
    ('RESERVATIONS_AGENT', 'GUEST_READ'),
    ('RESERVATIONS_AGENT', 'GUEST_CREATE'),
    ('RESERVATIONS_AGENT', 'GUEST_UPDATE')
ON CONFLICT (role_key, capability_key) DO NOTHING;
