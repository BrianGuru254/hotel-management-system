CREATE TABLE organizations (
 id UUID PRIMARY KEY, name VARCHAR(150) NOT NULL, legal_name VARCHAR(200), registration_number VARCHAR(100), tax_number VARCHAR(100),
 email VARCHAR(150), phone VARCHAR(50), website VARCHAR(255), address_line_1 VARCHAR(255), address_line_2 VARCHAR(255),
 city VARCHAR(100), county VARCHAR(100), country VARCHAR(100) NOT NULL, postal_code VARCHAR(30), currency_code CHAR(3) NOT NULL,
 timezone VARCHAR(100) NOT NULL, active BOOLEAN NOT NULL, created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE branches (
 id UUID PRIMARY KEY, organization_id UUID NOT NULL, name VARCHAR(150) NOT NULL, code VARCHAR(50) NOT NULL, description VARCHAR(100000),
 email VARCHAR(150), phone VARCHAR(50), address_line_1 VARCHAR(255), address_line_2 VARCHAR(255), city VARCHAR(100), county VARCHAR(100),
 country VARCHAR(100) NOT NULL, postal_code VARCHAR(30), currency_code CHAR(3) NOT NULL, timezone VARCHAR(100) NOT NULL,
 active BOOLEAN NOT NULL, created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_branches_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
 CONSTRAINT uq_branches_organization_id_id UNIQUE (organization_id, id),
 CONSTRAINT uq_branches_organization_code UNIQUE (organization_id, code)
);
CREATE TABLE properties (
 id UUID PRIMARY KEY, organization_id UUID NOT NULL, branch_id UUID NOT NULL, name VARCHAR(150) NOT NULL, code VARCHAR(50) NOT NULL,
 description VARCHAR(100000), email VARCHAR(150), phone VARCHAR(50), address_line_1 VARCHAR(255), address_line_2 VARCHAR(255),
 city VARCHAR(100), county VARCHAR(100), country VARCHAR(100) NOT NULL, postal_code VARCHAR(30), currency_code CHAR(3) NOT NULL,
 timezone VARCHAR(100) NOT NULL, active BOOLEAN NOT NULL, created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_properties_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
 CONSTRAINT fk_properties_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
 CONSTRAINT fk_properties_branch_organization FOREIGN KEY (organization_id, branch_id) REFERENCES branches(organization_id, id),
 CONSTRAINT uq_properties_branch_code UNIQUE (branch_id, code),
 CONSTRAINT uq_properties_scope UNIQUE (organization_id, branch_id, id),
    CONSTRAINT ck_properties_code_upper CHECK (code = UPPER(code))
);

CREATE TABLE guests (
 id UUID PRIMARY KEY, property_id UUID NOT NULL, guest_number VARCHAR(50) NOT NULL,
 first_name VARCHAR(100) NOT NULL, middle_name VARCHAR(100), last_name VARCHAR(100) NOT NULL,
 phone VARCHAR(30), email VARCHAR(150), nationality VARCHAR(100), identification_type VARCHAR(50),
 identification_number VARCHAR(100), address_line_1 VARCHAR(200), address_line_2 VARCHAR(200),
 city VARCHAR(100), county VARCHAR(100), country VARCHAR(100), date_of_birth DATE,
 emergency_contact_name VARCHAR(150), emergency_contact_phone VARCHAR(30), notes VARCHAR(100000),
 created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_guests_property FOREIGN KEY (property_id) REFERENCES properties(id),
 CONSTRAINT uq_guests_property_guest_number UNIQUE (property_id, guest_number),
 CONSTRAINT uq_guests_property_identification UNIQUE (property_id, identification_type, identification_number),
 CONSTRAINT uq_guests_property_id_id UNIQUE (property_id, id),
 CONSTRAINT ck_guests_identification_pair CHECK (
   (identification_type IS NULL AND identification_number IS NULL) OR
   (identification_type IS NOT NULL AND identification_number IS NOT NULL)
 )
);

CREATE TABLE room_types (
 id UUID PRIMARY KEY, property_id UUID NOT NULL, name VARCHAR(100) NOT NULL, code VARCHAR(50) NOT NULL,
 description TEXT, base_price NUMERIC(12,2) NOT NULL DEFAULT 0.00, max_occupancy INTEGER NOT NULL DEFAULT 1,
 size_square_meters NUMERIC(8,2), status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
 created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_room_types_property FOREIGN KEY (property_id) REFERENCES properties(id),
 CONSTRAINT ck_room_types_code_upper CHECK (code = UPPER(code)),
 CONSTRAINT ck_room_types_base_price CHECK (base_price >= 0),
 CONSTRAINT ck_room_types_max_occupancy CHECK (max_occupancy > 0),
 CONSTRAINT ck_room_types_size CHECK (size_square_meters IS NULL OR size_square_meters > 0),
 CONSTRAINT ck_room_types_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
 CONSTRAINT uq_room_types_property_id_id UNIQUE (property_id, id)
);
CREATE INDEX idx_room_types_property_id ON room_types(property_id);
CREATE UNIQUE INDEX uq_room_types_property_code_ci ON room_types(property_id, code);

CREATE TABLE amenities (
 id UUID PRIMARY KEY, property_id UUID NOT NULL, name VARCHAR(100) NOT NULL, description TEXT,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_amenities_property FOREIGN KEY (property_id) REFERENCES properties(id),
 CONSTRAINT uq_amenities_property_id_id UNIQUE (property_id, id)
);
CREATE INDEX idx_amenities_property_id ON amenities(property_id);
CREATE UNIQUE INDEX uq_amenities_property_name_ci ON amenities(property_id, name);

CREATE TABLE rooms (
 id UUID PRIMARY KEY, property_id UUID NOT NULL, room_type_id UUID NOT NULL,
 room_number VARCHAR(30) NOT NULL, floor_number INTEGER, capacity INTEGER NOT NULL DEFAULT 1,
 current_status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE', status_reason VARCHAR(500),
 status_changed_at TIMESTAMP WITH TIME ZONE NOT NULL,
 description TEXT, created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_rooms_property FOREIGN KEY (property_id) REFERENCES properties(id),
 CONSTRAINT fk_rooms_room_type FOREIGN KEY (room_type_id) REFERENCES room_types(id),
 CONSTRAINT fk_rooms_room_type_scope FOREIGN KEY (property_id, room_type_id) REFERENCES room_types(property_id, id),
 CONSTRAINT uq_rooms_property_scope UNIQUE (property_id, id),
 CONSTRAINT ck_rooms_number_upper CHECK (room_number = UPPER(room_number)),
 CONSTRAINT ck_rooms_capacity CHECK (capacity > 0),
 CONSTRAINT ck_rooms_status CHECK (current_status IN ('AVAILABLE', 'RESERVED', 'OCCUPIED', 'DIRTY', 'CLEANING', 'MAINTENANCE', 'OUT_OF_ORDER'))
);
CREATE INDEX idx_rooms_property_id ON rooms(property_id);
CREATE INDEX idx_rooms_property_status ON rooms(property_id, current_status);
CREATE INDEX idx_rooms_room_type_id ON rooms(room_type_id);
CREATE UNIQUE INDEX uq_rooms_property_room_number_ci ON rooms(property_id, room_number);

CREATE TABLE room_amenities (
 property_id UUID NOT NULL, room_id UUID NOT NULL, amenity_id UUID NOT NULL,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT pk_room_amenities PRIMARY KEY (room_id, amenity_id),
 CONSTRAINT fk_room_amenities_room_scope FOREIGN KEY (property_id, room_id) REFERENCES rooms(property_id, id) ON DELETE CASCADE,
 CONSTRAINT fk_room_amenities_amenity_scope FOREIGN KEY (property_id, amenity_id) REFERENCES amenities(property_id, id) ON DELETE CASCADE
);
CREATE INDEX idx_room_amenities_property_id ON room_amenities(property_id);
CREATE INDEX idx_room_amenities_amenity_id ON room_amenities(amenity_id);

CREATE TABLE departments (
 id UUID PRIMARY KEY, organization_id UUID NOT NULL, branch_id UUID NOT NULL, property_id UUID NOT NULL,
 name VARCHAR(120) NOT NULL, code VARCHAR(50) NOT NULL, active BOOLEAN NOT NULL,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_departments_property_scope FOREIGN KEY (organization_id, branch_id, property_id) REFERENCES properties(organization_id, branch_id, id),
 CONSTRAINT uq_departments_property_code UNIQUE (property_id, code),
 CONSTRAINT uq_departments_scope UNIQUE (organization_id, branch_id, property_id, id),
 CONSTRAINT uq_departments_org_branch_id UNIQUE (organization_id, branch_id, id)
);

CREATE TABLE roles (
 role_key VARCHAR(50) PRIMARY KEY, display_name VARCHAR(100) NOT NULL, description VARCHAR(500) NOT NULL,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE capabilities (capability_key VARCHAR(100) PRIMARY KEY, description VARCHAR(300) NOT NULL);
CREATE TABLE role_capabilities (
 role_key VARCHAR(50) NOT NULL REFERENCES roles(role_key),
 capability_key VARCHAR(100) NOT NULL REFERENCES capabilities(capability_key),
 PRIMARY KEY (role_key, capability_key)
);

CREATE TABLE users (
 id UUID PRIMARY KEY, email VARCHAR(320) NOT NULL, password_hash VARCHAR(255) NOT NULL,
 first_name VARCHAR(100) NOT NULL, last_name VARCHAR(100) NOT NULL, active BOOLEAN NOT NULL,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
ALTER TABLE users ADD CONSTRAINT uq_users_email UNIQUE(email);
CREATE TABLE user_role_scopes (
 id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
 role_key VARCHAR(50) NOT NULL REFERENCES roles(role_key), scope_type VARCHAR(20) NOT NULL,
 organization_id UUID REFERENCES organizations(id), branch_id UUID REFERENCES branches(id),
 property_id UUID REFERENCES properties(id), department_id UUID REFERENCES departments(id),
 created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT ck_user_role_scope_type CHECK (scope_type IN ('SYSTEM','ORGANIZATION','BRANCH','PROPERTY','DEPARTMENT','SELF')),
 CONSTRAINT ck_user_role_scope_shape CHECK (
   (scope_type = 'SYSTEM' AND role_key = 'SUPER_ADMIN' AND organization_id IS NULL AND branch_id IS NULL AND property_id IS NULL AND department_id IS NULL) OR
   (scope_type = 'ORGANIZATION' AND role_key <> 'SUPER_ADMIN' AND organization_id IS NOT NULL AND branch_id IS NULL AND property_id IS NULL AND department_id IS NULL) OR
   (scope_type = 'BRANCH' AND role_key <> 'SUPER_ADMIN' AND organization_id IS NOT NULL AND branch_id IS NOT NULL AND property_id IS NULL AND department_id IS NULL) OR
   (scope_type = 'PROPERTY' AND role_key <> 'SUPER_ADMIN' AND organization_id IS NOT NULL AND branch_id IS NOT NULL AND property_id IS NOT NULL AND department_id IS NULL) OR
   (scope_type = 'DEPARTMENT' AND role_key <> 'SUPER_ADMIN' AND organization_id IS NOT NULL AND branch_id IS NOT NULL AND property_id IS NOT NULL AND department_id IS NOT NULL) OR
   (scope_type = 'SELF' AND role_key <> 'SUPER_ADMIN' AND organization_id IS NULL AND branch_id IS NULL AND property_id IS NULL AND department_id IS NULL)
 ),
 CONSTRAINT fk_user_role_branch_org FOREIGN KEY (organization_id, branch_id) REFERENCES branches(organization_id, id),
 CONSTRAINT fk_user_role_property_scope FOREIGN KEY (organization_id, branch_id, property_id) REFERENCES properties(organization_id, branch_id, id),
 CONSTRAINT fk_user_role_department_scope FOREIGN KEY (organization_id, branch_id, property_id, department_id) REFERENCES departments(organization_id, branch_id, property_id, id)
);

CREATE TABLE refresh_tokens (
 id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE, family_id UUID NOT NULL,
 token_hash CHAR(64) NOT NULL, expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL, revoked_at TIMESTAMP WITH TIME ZONE,
 replaced_by_token_id UUID REFERENCES refresh_tokens(id), device_info VARCHAR(255), ip_address VARCHAR(45),
 updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE UNIQUE INDEX uq_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_family_id ON refresh_tokens(family_id);

CREATE TABLE system_bootstrap_state (
 id SMALLINT PRIMARY KEY CHECK (id = 1), completed BOOLEAN NOT NULL,
 updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
INSERT INTO system_bootstrap_state(id, completed, updated_at) VALUES (1, FALSE, CURRENT_TIMESTAMP);

CREATE TABLE reservations (
 id UUID PRIMARY KEY,
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
 cancelled_at TIMESTAMP WITH TIME ZONE,
 cancellation_reason TEXT,
 created_by UUID REFERENCES users(id),
 created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_reservations_property FOREIGN KEY (property_id) REFERENCES properties(id),
 CONSTRAINT fk_reservations_guest FOREIGN KEY (guest_id) REFERENCES guests(id),
 CONSTRAINT fk_reservations_guest_scope FOREIGN KEY (property_id, guest_id) REFERENCES guests(property_id, id),
 CONSTRAINT fk_reservations_room_type FOREIGN KEY (room_type_id) REFERENCES room_types(id),
 CONSTRAINT fk_reservations_room_type_scope FOREIGN KEY (property_id, room_type_id) REFERENCES room_types(property_id, id),
 CONSTRAINT fk_reservations_room FOREIGN KEY (room_id) REFERENCES rooms(id),
 CONSTRAINT fk_reservations_room_scope FOREIGN KEY (property_id, room_id) REFERENCES rooms(property_id, id),
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
CREATE UNIQUE INDEX uq_reservations_property_number_ci ON reservations(property_id, reservation_number);

CREATE TABLE reservation_status_history (
 id UUID PRIMARY KEY,
 property_id UUID NOT NULL,
 reservation_id UUID NOT NULL,
 previous_status VARCHAR(30),
 new_status VARCHAR(30) NOT NULL,
 reason TEXT,
 changed_by UUID REFERENCES users(id),
 changed_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_reservation_history_property FOREIGN KEY (property_id) REFERENCES properties(id),
 CONSTRAINT fk_reservation_history_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
 CONSTRAINT fk_reservation_history_reservation_scope FOREIGN KEY (property_id, reservation_id) REFERENCES reservations(property_id, id) ON DELETE CASCADE,
 CONSTRAINT ck_reservation_history_status CHECK (new_status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'CHECKED_IN', 'COMPLETED', 'NO_SHOW'))
);
CREATE INDEX idx_reservation_history_property_reservation ON reservation_status_history(property_id, reservation_id, changed_at);

CREATE TABLE stays (
 id UUID PRIMARY KEY,
 property_id UUID NOT NULL,
 stay_number VARCHAR(50) NOT NULL,
 reservation_id UUID,
 guest_id UUID NOT NULL,
 room_id UUID NOT NULL,
 active_room_id UUID,
 actual_check_in TIMESTAMP WITH TIME ZONE NOT NULL,
 expected_check_out_date DATE NOT NULL,
 actual_check_out TIMESTAMP WITH TIME ZONE,
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
 created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_stays_property FOREIGN KEY (property_id) REFERENCES properties(id),
 CONSTRAINT fk_stays_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id),
 CONSTRAINT fk_stays_reservation_scope FOREIGN KEY (property_id, reservation_id) REFERENCES reservations(property_id, id),
 CONSTRAINT fk_stays_guest FOREIGN KEY (guest_id) REFERENCES guests(id),
 CONSTRAINT fk_stays_guest_scope FOREIGN KEY (property_id, guest_id) REFERENCES guests(property_id, id),
 CONSTRAINT fk_stays_room FOREIGN KEY (room_id) REFERENCES rooms(id),
 CONSTRAINT fk_stays_room_scope FOREIGN KEY (property_id, room_id) REFERENCES rooms(property_id, id),
 CONSTRAINT fk_stays_active_room FOREIGN KEY (active_room_id) REFERENCES rooms(id),
 CONSTRAINT fk_stays_checked_in_by FOREIGN KEY (checked_in_by) REFERENCES users(id),
 CONSTRAINT fk_stays_checked_out_by FOREIGN KEY (checked_out_by) REFERENCES users(id),
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
CREATE UNIQUE INDEX uq_stays_property_number_ci ON stays(property_id, stay_number);

CREATE TABLE stay_guests (
 id UUID PRIMARY KEY,
 property_id UUID NOT NULL,
 stay_id UUID NOT NULL,
 guest_id UUID NOT NULL,
 is_primary_guest BOOLEAN NOT NULL DEFAULT FALSE,
 primary_stay_id UUID,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_stay_guests_stay FOREIGN KEY (stay_id) REFERENCES stays(id) ON DELETE CASCADE,
 CONSTRAINT fk_stay_guests_stay_scope FOREIGN KEY (property_id, stay_id) REFERENCES stays(property_id, id) ON DELETE CASCADE,
 CONSTRAINT fk_stay_guests_guest FOREIGN KEY (guest_id) REFERENCES guests(id),
 CONSTRAINT fk_stay_guests_guest_scope FOREIGN KEY (property_id, guest_id) REFERENCES guests(property_id, id),
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
 id UUID PRIMARY KEY,
 property_id UUID NOT NULL,
 stay_id UUID NOT NULL,
 previous_status VARCHAR(30),
 new_status VARCHAR(30) NOT NULL,
 reason TEXT,
 changed_by UUID NOT NULL,
 changed_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_stay_history_property FOREIGN KEY (property_id) REFERENCES properties(id),
 CONSTRAINT fk_stay_history_stay FOREIGN KEY (stay_id) REFERENCES stays(id) ON DELETE CASCADE,
 CONSTRAINT fk_stay_history_stay_scope FOREIGN KEY (property_id, stay_id) REFERENCES stays(property_id, id) ON DELETE CASCADE,
 CONSTRAINT fk_stay_history_user FOREIGN KEY (changed_by) REFERENCES users(id),
 CONSTRAINT ck_stay_history_previous_status CHECK (previous_status IS NULL OR previous_status IN ('ACTIVE', 'CHECKED_OUT', 'CANCELLED')),
 CONSTRAINT ck_stay_history_new_status CHECK (new_status IN ('ACTIVE', 'CHECKED_OUT', 'CANCELLED'))
);
CREATE INDEX idx_stay_history_property_stay ON stay_status_history(property_id, stay_id, changed_at);


INSERT INTO roles(role_key, display_name, description, created_at, updated_at)
VALUES
 ('SUPER_ADMIN','Super Admin','System-wide administrator',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('ORGANIZATION_ADMIN','Organization Admin','Organization administrator',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('BRANCH_MANAGER','Branch Manager','Branch manager',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('PROPERTY_MANAGER','Property Manager','Property manager',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('FRONT_DESK','Front Desk','Front desk operations',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('RESERVATIONS_AGENT','Reservations Agent','Reservation operations',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('HOUSEKEEPING_MANAGER','Housekeeping Manager','Housekeeping manager',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('HOUSEKEEPING_STAFF','Housekeeping Staff','Housekeeping staff',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('ACCOUNTANT','Accountant','Accounting',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('FINANCE_MANAGER','Finance Manager','Finance manager',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('RESTAURANT_MANAGER','Restaurant Manager','Restaurant manager',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('POS_CASHIER','POS Cashier','POS cashier',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('KITCHEN_STAFF','Kitchen Staff','Kitchen staff',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('INVENTORY_MANAGER','Inventory Manager','Inventory manager',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('PROCUREMENT_OFFICER','Procurement Officer','Procurement officer',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('MAINTENANCE_MANAGER','Maintenance Manager','Maintenance manager',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('MAINTENANCE_STAFF','Maintenance Staff','Maintenance staff',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('HR_MANAGER','HR Manager','HR manager',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
 ('REPORTING_USER','Reporting User','Read-only reporting',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

INSERT INTO capabilities(capability_key, description) VALUES
 ('ORGANIZATION_READ','Read organizations'), ('ORGANIZATION_CREATE','Create organizations'), ('ORGANIZATION_UPDATE','Update organizations'), ('ORGANIZATION_DELETE','Delete organizations'),
 ('BRANCH_READ','Read branches'), ('BRANCH_CREATE','Create branches'), ('BRANCH_UPDATE','Update branches'), ('BRANCH_DELETE','Delete branches'),
 ('PROPERTY_READ','Read properties'), ('PROPERTY_CREATE','Create properties'), ('PROPERTY_UPDATE','Update properties'), ('PROPERTY_DELETE','Delete properties'),
 ('GUEST_READ','Read guest records'), ('GUEST_CREATE','Create guest records'), ('GUEST_UPDATE','Update guest records'),
 ('ROOM_TYPE_READ','Read room type catalogs'), ('ROOM_TYPE_CREATE','Create room types'), ('ROOM_TYPE_UPDATE','Update room types'), ('ROOM_TYPE_DELETE','Delete room types'),
 ('AMENITY_READ','Read property amenities'), ('AMENITY_CREATE','Create property amenities'), ('AMENITY_UPDATE','Update property amenities'), ('AMENITY_DELETE','Delete property amenities'),
 ('ROOM_READ','Read rooms'), ('ROOM_CREATE','Create rooms'), ('ROOM_UPDATE','Update rooms'), ('ROOM_DELETE','Delete rooms'),
 ('ROOM_STATUS_UPDATE','Update room status'), ('ROOM_HOUSEKEEPING_STATUS_UPDATE','Update room status for housekeeping work'),
 ('ROOM_MAINTENANCE_STATUS_UPDATE','Update room status for maintenance work'), ('ROOM_AMENITY_UPDATE','Assign amenities to rooms'),
 ('DEPARTMENT_READ','Read departments'), ('DEPARTMENT_CREATE','Create departments'), ('DEPARTMENT_UPDATE','Update departments'), ('DEPARTMENT_DELETE','Delete departments'),
 ('USER_MANAGE','Manage users'), ('ROLE_READ','Read roles'), ('PROFILE_READ','Read profile'), ('PROFILE_UPDATE','Update profile'), ('PASSWORD_CHANGE','Change password'),
 ('REPORT_READ','Read reports'), ('AUDIT_READ','Read audit records'), ('SECURITY_MANAGE','Manage security'),
 ('RESERVATION_READ','Read reservations'), ('RESERVATION_CREATE','Create reservations'), ('RESERVATION_UPDATE','Update pending reservations'), ('RESERVATION_CONFIRM','Confirm reservations'), ('RESERVATION_CANCEL','Cancel reservations'), ('RESERVATION_AVAILABILITY_READ','Check reservation availability'), ('RESERVATION_HISTORY_READ','Read reservation status history'),
 ('STAY_READ','Read stays'), ('STAY_HISTORY_READ','Read stay status history'), ('STAY_GUEST_MANAGE','Manage guests assigned to a stay'),
 ('CHECK_IN','Check in a reservation'), ('WALK_IN_CHECK_IN','Check in a guest without a reservation'),
 ('CHECK_OUT','Check out an active stay'), ('CHECKOUT_PREVIEW_READ','Read estimated checkout balance');

INSERT INTO role_capabilities(role_key, capability_key)
SELECT 'SUPER_ADMIN', capability_key FROM capabilities;
INSERT INTO role_capabilities(role_key, capability_key) VALUES
 ('ORGANIZATION_ADMIN','ORGANIZATION_READ'),('ORGANIZATION_ADMIN','ORGANIZATION_UPDATE'),('ORGANIZATION_ADMIN','BRANCH_READ'),('ORGANIZATION_ADMIN','BRANCH_CREATE'),('ORGANIZATION_ADMIN','BRANCH_UPDATE'),('ORGANIZATION_ADMIN','PROPERTY_READ'),('ORGANIZATION_ADMIN','PROPERTY_CREATE'),('ORGANIZATION_ADMIN','PROPERTY_UPDATE'),('ORGANIZATION_ADMIN','DEPARTMENT_READ'),('ORGANIZATION_ADMIN','DEPARTMENT_CREATE'),('ORGANIZATION_ADMIN','DEPARTMENT_UPDATE'),('ORGANIZATION_ADMIN','USER_MANAGE'),('ORGANIZATION_ADMIN','ROLE_READ'),('ORGANIZATION_ADMIN','REPORT_READ'),('ORGANIZATION_ADMIN','PROFILE_READ'),('ORGANIZATION_ADMIN','PROFILE_UPDATE'),('ORGANIZATION_ADMIN','PASSWORD_CHANGE'),
 ('BRANCH_MANAGER','BRANCH_READ'),('BRANCH_MANAGER','BRANCH_UPDATE'),('BRANCH_MANAGER','PROPERTY_READ'),('BRANCH_MANAGER','PROPERTY_CREATE'),('BRANCH_MANAGER','PROPERTY_UPDATE'),('BRANCH_MANAGER','DEPARTMENT_READ'),('BRANCH_MANAGER','DEPARTMENT_CREATE'),('BRANCH_MANAGER','DEPARTMENT_UPDATE'),('BRANCH_MANAGER','USER_MANAGE'),('BRANCH_MANAGER','ROLE_READ'),('BRANCH_MANAGER','REPORT_READ'),('BRANCH_MANAGER','PROFILE_READ'),('BRANCH_MANAGER','PROFILE_UPDATE'),('BRANCH_MANAGER','PASSWORD_CHANGE'),
 ('PROPERTY_MANAGER','PROPERTY_READ'),('PROPERTY_MANAGER','PROPERTY_UPDATE'),('PROPERTY_MANAGER','DEPARTMENT_READ'),('PROPERTY_MANAGER','DEPARTMENT_CREATE'),('PROPERTY_MANAGER','DEPARTMENT_UPDATE'),('PROPERTY_MANAGER','USER_MANAGE'),('PROPERTY_MANAGER','ROLE_READ'),('PROPERTY_MANAGER','REPORT_READ'),('PROPERTY_MANAGER','PROFILE_READ'),('PROPERTY_MANAGER','PROFILE_UPDATE'),('PROPERTY_MANAGER','PASSWORD_CHANGE'),
 ('FRONT_DESK','PROPERTY_READ'),('FRONT_DESK','PROFILE_READ'),('FRONT_DESK','PROFILE_UPDATE'),('FRONT_DESK','PASSWORD_CHANGE'),
 ('RESERVATIONS_AGENT','PROPERTY_READ'),('RESERVATIONS_AGENT','PROFILE_READ'),('RESERVATIONS_AGENT','PROFILE_UPDATE'),('RESERVATIONS_AGENT','PASSWORD_CHANGE'),
 ('REPORTING_USER','PROPERTY_READ'),('REPORTING_USER','PROFILE_READ'),('REPORTING_USER','PROFILE_UPDATE'),('REPORTING_USER','PASSWORD_CHANGE'),('REPORTING_USER','REPORT_READ');
INSERT INTO role_capabilities(role_key, capability_key) VALUES
 ('FRONT_DESK','GUEST_READ'),('FRONT_DESK','GUEST_CREATE'),('FRONT_DESK','GUEST_UPDATE'),
 ('RESERVATIONS_AGENT','GUEST_READ'),('RESERVATIONS_AGENT','GUEST_CREATE'),('RESERVATIONS_AGENT','GUEST_UPDATE');
INSERT INTO role_capabilities(role_key, capability_key) VALUES
 ('ORGANIZATION_ADMIN','ROOM_TYPE_READ'),('ORGANIZATION_ADMIN','ROOM_TYPE_CREATE'),('ORGANIZATION_ADMIN','ROOM_TYPE_UPDATE'),('ORGANIZATION_ADMIN','ROOM_TYPE_DELETE'),
 ('ORGANIZATION_ADMIN','AMENITY_READ'),('ORGANIZATION_ADMIN','AMENITY_CREATE'),('ORGANIZATION_ADMIN','AMENITY_UPDATE'),('ORGANIZATION_ADMIN','AMENITY_DELETE'),
 ('BRANCH_MANAGER','ROOM_TYPE_READ'),('BRANCH_MANAGER','ROOM_TYPE_CREATE'),('BRANCH_MANAGER','ROOM_TYPE_UPDATE'),('BRANCH_MANAGER','ROOM_TYPE_DELETE'),
 ('BRANCH_MANAGER','AMENITY_READ'),('BRANCH_MANAGER','AMENITY_CREATE'),('BRANCH_MANAGER','AMENITY_UPDATE'),('BRANCH_MANAGER','AMENITY_DELETE'),
 ('PROPERTY_MANAGER','ROOM_TYPE_READ'),('PROPERTY_MANAGER','ROOM_TYPE_CREATE'),('PROPERTY_MANAGER','ROOM_TYPE_UPDATE'),('PROPERTY_MANAGER','ROOM_TYPE_DELETE'),
 ('PROPERTY_MANAGER','AMENITY_READ'),('PROPERTY_MANAGER','AMENITY_CREATE'),('PROPERTY_MANAGER','AMENITY_UPDATE'),('PROPERTY_MANAGER','AMENITY_DELETE'),
 ('FRONT_DESK','ROOM_TYPE_READ'),('FRONT_DESK','AMENITY_READ'),
 ('RESERVATIONS_AGENT','ROOM_TYPE_READ'),('RESERVATIONS_AGENT','AMENITY_READ'),
 ('REPORTING_USER','ROOM_TYPE_READ'),('REPORTING_USER','AMENITY_READ');
INSERT INTO role_capabilities(role_key, capability_key) VALUES
 ('ORGANIZATION_ADMIN','ROOM_READ'),('ORGANIZATION_ADMIN','ROOM_CREATE'),('ORGANIZATION_ADMIN','ROOM_UPDATE'),('ORGANIZATION_ADMIN','ROOM_DELETE'),('ORGANIZATION_ADMIN','ROOM_STATUS_UPDATE'),('ORGANIZATION_ADMIN','ROOM_AMENITY_UPDATE'),
 ('BRANCH_MANAGER','ROOM_READ'),('BRANCH_MANAGER','ROOM_CREATE'),('BRANCH_MANAGER','ROOM_UPDATE'),('BRANCH_MANAGER','ROOM_DELETE'),('BRANCH_MANAGER','ROOM_STATUS_UPDATE'),('BRANCH_MANAGER','ROOM_AMENITY_UPDATE'),
 ('PROPERTY_MANAGER','ROOM_READ'),('PROPERTY_MANAGER','ROOM_CREATE'),('PROPERTY_MANAGER','ROOM_UPDATE'),('PROPERTY_MANAGER','ROOM_DELETE'),('PROPERTY_MANAGER','ROOM_STATUS_UPDATE'),('PROPERTY_MANAGER','ROOM_AMENITY_UPDATE'),
 ('FRONT_DESK','ROOM_READ'),('RESERVATIONS_AGENT','ROOM_READ'),('REPORTING_USER','ROOM_READ'),
 ('HOUSEKEEPING_MANAGER','ROOM_READ'),('HOUSEKEEPING_MANAGER','ROOM_HOUSEKEEPING_STATUS_UPDATE'),
 ('HOUSEKEEPING_STAFF','ROOM_READ'),('HOUSEKEEPING_STAFF','ROOM_HOUSEKEEPING_STATUS_UPDATE'),
 ('MAINTENANCE_MANAGER','ROOM_READ'),('MAINTENANCE_MANAGER','ROOM_MAINTENANCE_STATUS_UPDATE'),
 ('MAINTENANCE_STAFF','ROOM_READ'),('MAINTENANCE_STAFF','ROOM_MAINTENANCE_STATUS_UPDATE');
INSERT INTO role_capabilities(role_key, capability_key) VALUES
 ('ORGANIZATION_ADMIN','RESERVATION_READ'),('ORGANIZATION_ADMIN','RESERVATION_CREATE'),('ORGANIZATION_ADMIN','RESERVATION_UPDATE'),('ORGANIZATION_ADMIN','RESERVATION_CONFIRM'),('ORGANIZATION_ADMIN','RESERVATION_CANCEL'),('ORGANIZATION_ADMIN','RESERVATION_AVAILABILITY_READ'),('ORGANIZATION_ADMIN','RESERVATION_HISTORY_READ'),
 ('BRANCH_MANAGER','RESERVATION_READ'),('BRANCH_MANAGER','RESERVATION_CREATE'),('BRANCH_MANAGER','RESERVATION_UPDATE'),('BRANCH_MANAGER','RESERVATION_CONFIRM'),('BRANCH_MANAGER','RESERVATION_CANCEL'),('BRANCH_MANAGER','RESERVATION_AVAILABILITY_READ'),('BRANCH_MANAGER','RESERVATION_HISTORY_READ'),
 ('PROPERTY_MANAGER','RESERVATION_READ'),('PROPERTY_MANAGER','RESERVATION_CREATE'),('PROPERTY_MANAGER','RESERVATION_UPDATE'),('PROPERTY_MANAGER','RESERVATION_CONFIRM'),('PROPERTY_MANAGER','RESERVATION_CANCEL'),('PROPERTY_MANAGER','RESERVATION_AVAILABILITY_READ'),('PROPERTY_MANAGER','RESERVATION_HISTORY_READ'),
 ('FRONT_DESK','RESERVATION_READ'),('FRONT_DESK','RESERVATION_CREATE'),('FRONT_DESK','RESERVATION_UPDATE'),('FRONT_DESK','RESERVATION_CONFIRM'),('FRONT_DESK','RESERVATION_CANCEL'),('FRONT_DESK','RESERVATION_AVAILABILITY_READ'),('FRONT_DESK','RESERVATION_HISTORY_READ'),
 ('RESERVATIONS_AGENT','RESERVATION_READ'),('RESERVATIONS_AGENT','RESERVATION_CREATE'),('RESERVATIONS_AGENT','RESERVATION_UPDATE'),('RESERVATIONS_AGENT','RESERVATION_CONFIRM'),('RESERVATIONS_AGENT','RESERVATION_CANCEL'),('RESERVATIONS_AGENT','RESERVATION_AVAILABILITY_READ'),('RESERVATIONS_AGENT','RESERVATION_HISTORY_READ'),
 ('REPORTING_USER','RESERVATION_READ'),('REPORTING_USER','RESERVATION_AVAILABILITY_READ'),('REPORTING_USER','RESERVATION_HISTORY_READ');
INSERT INTO role_capabilities(role_key, capability_key) VALUES
 ('ORGANIZATION_ADMIN','STAY_READ'),('ORGANIZATION_ADMIN','STAY_HISTORY_READ'),('ORGANIZATION_ADMIN','STAY_GUEST_MANAGE'),('ORGANIZATION_ADMIN','CHECK_IN'),('ORGANIZATION_ADMIN','WALK_IN_CHECK_IN'),('ORGANIZATION_ADMIN','CHECK_OUT'),('ORGANIZATION_ADMIN','CHECKOUT_PREVIEW_READ'),
 ('BRANCH_MANAGER','STAY_READ'),('BRANCH_MANAGER','STAY_HISTORY_READ'),('BRANCH_MANAGER','STAY_GUEST_MANAGE'),('BRANCH_MANAGER','CHECK_IN'),('BRANCH_MANAGER','WALK_IN_CHECK_IN'),('BRANCH_MANAGER','CHECK_OUT'),('BRANCH_MANAGER','CHECKOUT_PREVIEW_READ'),
 ('PROPERTY_MANAGER','STAY_READ'),('PROPERTY_MANAGER','STAY_HISTORY_READ'),('PROPERTY_MANAGER','STAY_GUEST_MANAGE'),('PROPERTY_MANAGER','CHECK_IN'),('PROPERTY_MANAGER','WALK_IN_CHECK_IN'),('PROPERTY_MANAGER','CHECK_OUT'),('PROPERTY_MANAGER','CHECKOUT_PREVIEW_READ'),
 ('FRONT_DESK','STAY_READ'),('FRONT_DESK','STAY_HISTORY_READ'),('FRONT_DESK','STAY_GUEST_MANAGE'),('FRONT_DESK','CHECK_IN'),('FRONT_DESK','WALK_IN_CHECK_IN'),('FRONT_DESK','CHECK_OUT'),('FRONT_DESK','CHECKOUT_PREVIEW_READ'),
 ('RESERVATIONS_AGENT','STAY_READ'),('RESERVATIONS_AGENT','STAY_HISTORY_READ'),('RESERVATIONS_AGENT','STAY_GUEST_MANAGE'),('RESERVATIONS_AGENT','CHECK_IN'),('RESERVATIONS_AGENT','WALK_IN_CHECK_IN'),('RESERVATIONS_AGENT','CHECK_OUT'),('RESERVATIONS_AGENT','CHECKOUT_PREVIEW_READ'),
 ('REPORTING_USER','STAY_READ'),('REPORTING_USER','STAY_HISTORY_READ'),('REPORTING_USER','CHECKOUT_PREVIEW_READ');

CREATE TABLE housekeeping_tasks (
 id UUID PRIMARY KEY, property_id UUID NOT NULL, room_id UUID NOT NULL, assigned_user_id UUID,
 task_type VARCHAR(50) NOT NULL, status VARCHAR(30) NOT NULL, priority VARCHAR(20) NOT NULL, notes VARCHAR(100000),
 assigned_at TIMESTAMP WITH TIME ZONE, started_at TIMESTAMP WITH TIME ZONE, completed_at TIMESTAMP WITH TIME ZONE,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_housekeeping_property FOREIGN KEY (property_id) REFERENCES properties(id),
 CONSTRAINT fk_housekeeping_room_scope FOREIGN KEY (property_id, room_id) REFERENCES rooms(property_id, id),
 CONSTRAINT fk_housekeeping_assignee FOREIGN KEY (assigned_user_id) REFERENCES users(id)
);
CREATE INDEX idx_housekeeping_property_status ON housekeeping_tasks(property_id, status, created_at);
CREATE INDEX idx_housekeeping_room_status ON housekeeping_tasks(room_id, status);

CREATE TABLE maintenance_requests (
 id UUID PRIMARY KEY, property_id UUID NOT NULL, room_id UUID, reported_by UUID NOT NULL, assigned_user_id UUID,
 title VARCHAR(200) NOT NULL, description VARCHAR(100000), resolution VARCHAR(100000), priority VARCHAR(20) NOT NULL,
 status VARCHAR(30) NOT NULL, room_status_before VARCHAR(30), reported_at TIMESTAMP WITH TIME ZONE NOT NULL,
 assigned_at TIMESTAMP WITH TIME ZONE, started_at TIMESTAMP WITH TIME ZONE, resolved_at TIMESTAMP WITH TIME ZONE,
 closed_at TIMESTAMP WITH TIME ZONE, created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_maintenance_property FOREIGN KEY (property_id) REFERENCES properties(id),
 CONSTRAINT fk_maintenance_room_scope FOREIGN KEY (property_id, room_id) REFERENCES rooms(property_id, id),
 CONSTRAINT fk_maintenance_reporter FOREIGN KEY (reported_by) REFERENCES users(id),
 CONSTRAINT fk_maintenance_assignee FOREIGN KEY (assigned_user_id) REFERENCES users(id),
 CONSTRAINT uq_maintenance_property_scope UNIQUE (property_id, id)
);
CREATE INDEX idx_maintenance_property_status ON maintenance_requests(property_id, status, reported_at);
CREATE INDEX idx_maintenance_room_status ON maintenance_requests(room_id, status);
CREATE TABLE maintenance_status_history (
 id UUID PRIMARY KEY, property_id UUID NOT NULL, maintenance_request_id UUID NOT NULL,
 previous_status VARCHAR(30), new_status VARCHAR(30) NOT NULL, reason VARCHAR(1000), changed_by UUID NOT NULL,
 changed_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_maintenance_history_request_scope FOREIGN KEY (property_id, maintenance_request_id) REFERENCES maintenance_requests(property_id, id),
 CONSTRAINT fk_maintenance_history_actor FOREIGN KEY (changed_by) REFERENCES users(id)
);
CREATE INDEX idx_maintenance_history_request ON maintenance_status_history(property_id, maintenance_request_id, changed_at);
CREATE TABLE employees (
 id UUID PRIMARY KEY, organization_id UUID NOT NULL, branch_id UUID NOT NULL, department_id UUID, user_id UUID,
 employee_number VARCHAR(50) NOT NULL, first_name VARCHAR(100) NOT NULL, last_name VARCHAR(100) NOT NULL,
 email VARCHAR(255), phone VARCHAR(50), job_title VARCHAR(150), employment_status VARCHAR(30) NOT NULL,
 hire_date DATE, created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL, deleted_at TIMESTAMP WITH TIME ZONE,
 CONSTRAINT fk_employees_branch_scope FOREIGN KEY (organization_id, branch_id) REFERENCES branches(organization_id, id),
 CONSTRAINT fk_employees_department_scope FOREIGN KEY (organization_id, branch_id, department_id) REFERENCES departments(organization_id, branch_id, id),
 CONSTRAINT fk_employees_user FOREIGN KEY (user_id) REFERENCES users(id),
 CONSTRAINT uq_employees_org_scope UNIQUE (organization_id, id),
 CONSTRAINT uq_employees_org_branch_id UNIQUE (organization_id, branch_id, id),
 CONSTRAINT uq_employees_org_number UNIQUE (organization_id, employee_number),
 CONSTRAINT uq_employees_user UNIQUE (user_id)
);
CREATE INDEX idx_employees_org_status ON employees(organization_id, employment_status);
CREATE INDEX idx_employees_branch ON employees(branch_id);
CREATE INDEX idx_employees_department ON employees(department_id);
ALTER TABLE housekeeping_tasks ADD assigned_employee_id UUID REFERENCES employees(id);
ALTER TABLE maintenance_requests ADD assigned_employee_id UUID REFERENCES employees(id);
INSERT INTO capabilities(capability_key, description) VALUES
 ('HOUSEKEEPING_READ','Read housekeeping work'),('HOUSEKEEPING_UPDATE','Update housekeeping work'),('HOUSEKEEPING_ASSIGN','Assign housekeeping work'),
 ('MAINTENANCE_READ','Read maintenance work'),('MAINTENANCE_UPDATE','Update maintenance work'),('MAINTENANCE_ASSIGN','Assign maintenance work'),
 ('EMPLOYEE_READ','Read employee records'),('EMPLOYEE_CREATE','Create employee records'),('EMPLOYEE_UPDATE','Update employee records');
INSERT INTO role_capabilities(role_key, capability_key) VALUES
 ('SUPER_ADMIN','HOUSEKEEPING_READ'),('SUPER_ADMIN','HOUSEKEEPING_UPDATE'),('SUPER_ADMIN','HOUSEKEEPING_ASSIGN'),
 ('SUPER_ADMIN','MAINTENANCE_READ'),('SUPER_ADMIN','MAINTENANCE_UPDATE'),('SUPER_ADMIN','MAINTENANCE_ASSIGN'),
 ('HOUSEKEEPING_MANAGER','HOUSEKEEPING_READ'),('HOUSEKEEPING_MANAGER','HOUSEKEEPING_UPDATE'),('HOUSEKEEPING_MANAGER','HOUSEKEEPING_ASSIGN'),
 ('HOUSEKEEPING_STAFF','HOUSEKEEPING_READ'),('HOUSEKEEPING_STAFF','HOUSEKEEPING_UPDATE'),
 ('MAINTENANCE_MANAGER','MAINTENANCE_READ'),('MAINTENANCE_MANAGER','MAINTENANCE_UPDATE'),('MAINTENANCE_MANAGER','MAINTENANCE_ASSIGN'),
 ('MAINTENANCE_STAFF','MAINTENANCE_READ'),('MAINTENANCE_STAFF','MAINTENANCE_UPDATE'),
 ('HR_MANAGER','EMPLOYEE_READ'),('HR_MANAGER','EMPLOYEE_CREATE'),('HR_MANAGER','EMPLOYEE_UPDATE');
