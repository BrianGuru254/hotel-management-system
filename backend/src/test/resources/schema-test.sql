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
 CONSTRAINT ck_room_types_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);
CREATE INDEX idx_room_types_property_id ON room_types(property_id);
CREATE UNIQUE INDEX uq_room_types_property_code_ci ON room_types(property_id, code);

CREATE TABLE amenities (
 id UUID PRIMARY KEY, property_id UUID NOT NULL, name VARCHAR(100) NOT NULL, description TEXT,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_amenities_property FOREIGN KEY (property_id) REFERENCES properties(id)
);
CREATE INDEX idx_amenities_property_id ON amenities(property_id);
CREATE UNIQUE INDEX uq_amenities_property_name_ci ON amenities(property_id, name);

CREATE TABLE departments (
 id UUID PRIMARY KEY, organization_id UUID NOT NULL, branch_id UUID NOT NULL, property_id UUID NOT NULL,
 name VARCHAR(120) NOT NULL, code VARCHAR(50) NOT NULL, active BOOLEAN NOT NULL,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL, updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT fk_departments_property_scope FOREIGN KEY (organization_id, branch_id, property_id) REFERENCES properties(organization_id, branch_id, id),
 CONSTRAINT uq_departments_property_code UNIQUE (property_id, code),
 CONSTRAINT uq_departments_scope UNIQUE (organization_id, branch_id, property_id, id)
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
 ('DEPARTMENT_READ','Read departments'), ('DEPARTMENT_CREATE','Create departments'), ('DEPARTMENT_UPDATE','Update departments'), ('DEPARTMENT_DELETE','Delete departments'),
 ('USER_MANAGE','Manage users'), ('ROLE_READ','Read roles'), ('PROFILE_READ','Read profile'), ('PROFILE_UPDATE','Update profile'), ('PASSWORD_CHANGE','Change password'),
 ('REPORT_READ','Read reports'), ('AUDIT_READ','Read audit records'), ('SECURITY_MANAGE','Manage security');

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
