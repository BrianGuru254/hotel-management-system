CREATE TABLE stays (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    stay_number VARCHAR(50) NOT NULL,
    reservation_id UUID,
    guest_id UUID NOT NULL,
    room_id UUID NOT NULL,
    active_room_id UUID,
    actual_check_in TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expected_check_out_date DATE NOT NULL,
    actual_check_out TIMESTAMPTZ,
    adults INTEGER NOT NULL DEFAULT 1,
    children INTEGER NOT NULL DEFAULT 0,
    nightly_rate NUMERIC(12,2) NOT NULL,
    deposit_amount NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    currency_code CHAR(3) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    notes TEXT,
    checkout_notes TEXT,
    checked_in_by UUID NOT NULL,
    checked_out_by UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stays_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
    CONSTRAINT fk_stays_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE RESTRICT,
    CONSTRAINT fk_stays_reservation_scope FOREIGN KEY (property_id, reservation_id)
        REFERENCES reservations(property_id, id) ON DELETE RESTRICT,
    CONSTRAINT fk_stays_guest FOREIGN KEY (guest_id) REFERENCES guests(id) ON DELETE RESTRICT,
    CONSTRAINT fk_stays_guest_scope FOREIGN KEY (property_id, guest_id)
        REFERENCES guests(property_id, id) ON DELETE RESTRICT,
    CONSTRAINT fk_stays_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT,
    CONSTRAINT fk_stays_room_scope FOREIGN KEY (property_id, room_id)
        REFERENCES rooms(property_id, id) ON DELETE RESTRICT,
    CONSTRAINT fk_stays_active_room FOREIGN KEY (active_room_id) REFERENCES rooms(id) ON DELETE RESTRICT,
    CONSTRAINT fk_stays_checked_in_by FOREIGN KEY (checked_in_by) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_stays_checked_out_by FOREIGN KEY (checked_out_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT uq_stays_property_scope UNIQUE (property_id, id),
    CONSTRAINT uq_stays_active_room UNIQUE (active_room_id),
    CONSTRAINT ck_stays_adults CHECK (adults > 0),
    CONSTRAINT ck_stays_children CHECK (children >= 0),
    CONSTRAINT ck_stays_nightly_rate CHECK (nightly_rate >= 0),
    CONSTRAINT ck_stays_deposit CHECK (deposit_amount >= 0),
    CONSTRAINT ck_stays_status CHECK (status IN ('ACTIVE', 'CHECKED_OUT', 'CANCELLED')),
    CONSTRAINT ck_stays_room_occupancy CHECK (
        (status = 'ACTIVE' AND active_room_id = room_id AND actual_check_out IS NULL) OR
        (status = 'CHECKED_OUT' AND active_room_id IS NULL AND actual_check_out IS NOT NULL) OR
        (status = 'CANCELLED' AND active_room_id IS NULL)
    )
);
CREATE INDEX idx_stays_property_status ON stays(property_id, status);
CREATE INDEX idx_stays_property_checkout ON stays(property_id, expected_check_out_date);
CREATE INDEX idx_stays_guest_id ON stays(guest_id);
CREATE INDEX idx_stays_room_id ON stays(room_id);
CREATE INDEX idx_stays_property_reservation ON stays(property_id, reservation_id);
CREATE UNIQUE INDEX uq_stays_property_number_ci ON stays(property_id, lower(stay_number));

CREATE TABLE stay_guests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    stay_id UUID NOT NULL,
    guest_id UUID NOT NULL,
    is_primary_guest BOOLEAN NOT NULL DEFAULT FALSE,
    primary_stay_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stay_guests_stay FOREIGN KEY (stay_id) REFERENCES stays(id) ON DELETE CASCADE,
    CONSTRAINT fk_stay_guests_stay_scope FOREIGN KEY (property_id, stay_id)
        REFERENCES stays(property_id, id) ON DELETE CASCADE,
    CONSTRAINT fk_stay_guests_guest FOREIGN KEY (guest_id) REFERENCES guests(id) ON DELETE RESTRICT,
    CONSTRAINT fk_stay_guests_guest_scope FOREIGN KEY (property_id, guest_id)
        REFERENCES guests(property_id, id) ON DELETE RESTRICT,
    CONSTRAINT fk_stay_guests_primary_stay FOREIGN KEY (primary_stay_id) REFERENCES stays(id) ON DELETE CASCADE,
    CONSTRAINT uq_stay_guests_stay_guest UNIQUE (stay_id, guest_id),
    CONSTRAINT uq_stay_guests_primary UNIQUE (primary_stay_id),
    CONSTRAINT ck_stay_guests_primary CHECK (
        (is_primary_guest = TRUE AND primary_stay_id = stay_id) OR
        (is_primary_guest = FALSE AND primary_stay_id IS NULL)
    )
);
CREATE INDEX idx_stay_guests_property_stay ON stay_guests(property_id, stay_id);
CREATE INDEX idx_stay_guests_guest_id ON stay_guests(guest_id);

CREATE TABLE stay_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    stay_id UUID NOT NULL,
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    reason TEXT,
    changed_by UUID NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stay_history_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
    CONSTRAINT fk_stay_history_stay FOREIGN KEY (stay_id) REFERENCES stays(id) ON DELETE CASCADE,
    CONSTRAINT fk_stay_history_stay_scope FOREIGN KEY (property_id, stay_id)
        REFERENCES stays(property_id, id) ON DELETE CASCADE,
    CONSTRAINT fk_stay_history_user FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT ck_stay_history_previous_status CHECK (previous_status IS NULL OR previous_status IN ('ACTIVE', 'CHECKED_OUT', 'CANCELLED')),
    CONSTRAINT ck_stay_history_new_status CHECK (new_status IN ('ACTIVE', 'CHECKED_OUT', 'CANCELLED'))
);
CREATE INDEX idx_stay_history_property_stay ON stay_status_history(property_id, stay_id, changed_at);

INSERT INTO capabilities (capability_key, description) VALUES
    ('STAY_READ', 'Read stays'),
    ('STAY_HISTORY_READ', 'Read stay status history'),
    ('STAY_GUEST_MANAGE', 'Manage guests assigned to a stay'),
    ('CHECK_IN', 'Check in a reservation'),
    ('WALK_IN_CHECK_IN', 'Check in a guest without a reservation'),
    ('CHECK_OUT', 'Check out an active stay'),
    ('CHECKOUT_PREVIEW_READ', 'Read estimated checkout balance')
ON CONFLICT (capability_key) DO NOTHING;

INSERT INTO role_capabilities (role_key, capability_key)
SELECT role_key, capability_key
FROM (VALUES ('SUPER_ADMIN'), ('ORGANIZATION_ADMIN'), ('BRANCH_MANAGER'), ('PROPERTY_MANAGER')) AS management_roles(role_key)
CROSS JOIN (VALUES
    ('STAY_READ'), ('STAY_HISTORY_READ'), ('STAY_GUEST_MANAGE'), ('CHECK_IN'),
    ('WALK_IN_CHECK_IN'), ('CHECK_OUT'), ('CHECKOUT_PREVIEW_READ')
) AS stay_capabilities(capability_key)
ON CONFLICT (role_key, capability_key) DO NOTHING;

INSERT INTO role_capabilities (role_key, capability_key)
SELECT role_key, capability_key
FROM (VALUES ('FRONT_DESK'), ('RESERVATIONS_AGENT')) AS front_desk_roles(role_key)
CROSS JOIN (VALUES
    ('STAY_READ'), ('STAY_HISTORY_READ'), ('STAY_GUEST_MANAGE'), ('CHECK_IN'),
    ('WALK_IN_CHECK_IN'), ('CHECK_OUT'), ('CHECKOUT_PREVIEW_READ')
) AS stay_capabilities(capability_key)
ON CONFLICT (role_key, capability_key) DO NOTHING;

INSERT INTO role_capabilities (role_key, capability_key)
SELECT 'REPORTING_USER', capability_key
FROM (VALUES ('STAY_READ'), ('STAY_HISTORY_READ'), ('CHECKOUT_PREVIEW_READ')) AS read_capabilities(capability_key)
ON CONFLICT (role_key, capability_key) DO NOTHING;
