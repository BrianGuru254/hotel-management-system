ALTER TABLE guests
    ADD CONSTRAINT uq_guests_property_id_id UNIQUE (property_id, id);

CREATE TABLE reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    guest_id UUID NOT NULL,
    room_type_id UUID NOT NULL,
    room_id UUID,
    reservation_number VARCHAR(50) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    adults INTEGER NOT NULL DEFAULT 1,
    children INTEGER NOT NULL DEFAULT 0,
    number_of_rooms INTEGER NOT NULL DEFAULT 1,
    nightly_rate NUMERIC(12,2) NOT NULL,
    deposit_amount NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    currency_code CHAR(3) NOT NULL,
    booking_source VARCHAR(50) NOT NULL DEFAULT 'DIRECT',
    special_requests TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    cancelled_at TIMESTAMPTZ,
    cancellation_reason TEXT,
    created_by UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservations_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reservations_guest FOREIGN KEY (guest_id) REFERENCES guests(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reservations_guest_scope FOREIGN KEY (property_id, guest_id)
        REFERENCES guests(property_id, id) ON DELETE RESTRICT,
    CONSTRAINT fk_reservations_room_type FOREIGN KEY (room_type_id) REFERENCES room_types(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reservations_room_type_scope FOREIGN KEY (property_id, room_type_id)
        REFERENCES room_types(property_id, id) ON DELETE RESTRICT,
    CONSTRAINT fk_reservations_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reservations_room_scope FOREIGN KEY (property_id, room_id)
        REFERENCES rooms(property_id, id) ON DELETE RESTRICT,
    CONSTRAINT fk_reservations_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT uq_reservations_property_scope UNIQUE (property_id, id),
    CONSTRAINT ck_reservations_dates CHECK (check_out_date > check_in_date),
    CONSTRAINT ck_reservations_adults CHECK (adults > 0),
    CONSTRAINT ck_reservations_children CHECK (children >= 0),
    CONSTRAINT ck_reservations_rooms CHECK (number_of_rooms > 0 AND (room_id IS NULL OR number_of_rooms = 1)),
    CONSTRAINT ck_reservations_nightly_rate CHECK (nightly_rate >= 0),
    CONSTRAINT ck_reservations_deposit CHECK (deposit_amount >= 0),
    CONSTRAINT ck_reservations_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'CHECKED_IN', 'COMPLETED', 'NO_SHOW')),
    CONSTRAINT ck_reservations_cancelled_data CHECK (
        (status = 'CANCELLED' AND cancelled_at IS NOT NULL AND cancellation_reason IS NOT NULL) OR
        (status <> 'CANCELLED' AND cancelled_at IS NULL AND cancellation_reason IS NULL)
    )
);
CREATE INDEX idx_reservations_property_status ON reservations(property_id, status);
CREATE INDEX idx_reservations_property_dates ON reservations(property_id, check_in_date, check_out_date);
CREATE INDEX idx_reservations_room_type_dates ON reservations(property_id, room_type_id, check_in_date, check_out_date);
CREATE INDEX idx_reservations_room_dates ON reservations(room_id, check_in_date, check_out_date);
CREATE INDEX idx_reservations_guest_id ON reservations(guest_id);
CREATE UNIQUE INDEX uq_reservations_property_number_ci ON reservations(property_id, lower(reservation_number));

CREATE TABLE reservation_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    reservation_id UUID NOT NULL,
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    reason TEXT,
    changed_by UUID,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservation_history_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_history_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_history_reservation_scope FOREIGN KEY (property_id, reservation_id)
        REFERENCES reservations(property_id, id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_history_user FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT ck_reservation_history_status CHECK (new_status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'CHECKED_IN', 'COMPLETED', 'NO_SHOW'))
);
CREATE INDEX idx_reservation_history_property_reservation
    ON reservation_status_history(property_id, reservation_id, changed_at);

INSERT INTO capabilities (capability_key, description) VALUES
    ('RESERVATION_READ', 'Read reservations'),
    ('RESERVATION_CREATE', 'Create reservations'),
    ('RESERVATION_UPDATE', 'Update pending reservations'),
    ('RESERVATION_CONFIRM', 'Confirm reservations'),
    ('RESERVATION_CANCEL', 'Cancel reservations'),
    ('RESERVATION_AVAILABILITY_READ', 'Check reservation availability'),
    ('RESERVATION_HISTORY_READ', 'Read reservation status history')
ON CONFLICT (capability_key) DO NOTHING;

INSERT INTO role_capabilities (role_key, capability_key)
SELECT role_key, capability_key
FROM (VALUES
    ('SUPER_ADMIN'), ('ORGANIZATION_ADMIN'), ('BRANCH_MANAGER'), ('PROPERTY_MANAGER')
) AS manager_roles(role_key)
CROSS JOIN (VALUES
    ('RESERVATION_READ'), ('RESERVATION_CREATE'), ('RESERVATION_UPDATE'), ('RESERVATION_CONFIRM'),
    ('RESERVATION_CANCEL'), ('RESERVATION_AVAILABILITY_READ'), ('RESERVATION_HISTORY_READ')
) AS reservation_capabilities(capability_key)
ON CONFLICT (role_key, capability_key) DO NOTHING;

INSERT INTO role_capabilities (role_key, capability_key)
SELECT role_key, capability_key
FROM (VALUES ('FRONT_DESK'), ('RESERVATIONS_AGENT')) AS operations_roles(role_key)
CROSS JOIN (VALUES
    ('RESERVATION_READ'), ('RESERVATION_CREATE'), ('RESERVATION_UPDATE'), ('RESERVATION_CONFIRM'),
    ('RESERVATION_CANCEL'), ('RESERVATION_AVAILABILITY_READ'), ('RESERVATION_HISTORY_READ')
) AS reservation_capabilities(capability_key)
ON CONFLICT (role_key, capability_key) DO NOTHING;

INSERT INTO role_capabilities (role_key, capability_key)
SELECT 'REPORTING_USER', capability_key
FROM (VALUES ('RESERVATION_READ'), ('RESERVATION_AVAILABILITY_READ'), ('RESERVATION_HISTORY_READ')) AS read_capabilities(capability_key)
ON CONFLICT (role_key, capability_key) DO NOTHING;
