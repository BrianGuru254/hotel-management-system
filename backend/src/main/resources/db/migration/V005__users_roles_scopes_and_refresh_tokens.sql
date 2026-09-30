-- Additive identity and authorization schema. Existing organization data is retained.
ALTER TABLE properties
    ADD CONSTRAINT uq_properties_scope UNIQUE (organization_id, branch_id, id);

CREATE TABLE departments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    branch_id UUID NOT NULL,
    property_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_departments_property_scope
        FOREIGN KEY (organization_id, branch_id, property_id)
        REFERENCES properties (organization_id, branch_id, id) ON DELETE CASCADE,
    CONSTRAINT uq_departments_property_code UNIQUE (property_id, code),
    CONSTRAINT uq_departments_scope UNIQUE (organization_id, branch_id, property_id, id)
);
CREATE INDEX idx_departments_property_id ON departments(property_id);
CREATE INDEX idx_departments_branch_id ON departments(branch_id);

CREATE TABLE roles (
    role_key VARCHAR(50) PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE capabilities (
    capability_key VARCHAR(100) PRIMARY KEY,
    description VARCHAR(300) NOT NULL
);

CREATE TABLE role_capabilities (
    role_key VARCHAR(50) NOT NULL REFERENCES roles(role_key) ON DELETE CASCADE,
    capability_key VARCHAR(100) NOT NULL REFERENCES capabilities(capability_key) ON DELETE CASCADE,
    PRIMARY KEY (role_key, capability_key)
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(320) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uq_users_email_ci ON users (lower(email));
CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE user_role_scopes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_key VARCHAR(50) NOT NULL REFERENCES roles(role_key),
    scope_type VARCHAR(20) NOT NULL,
    organization_id UUID REFERENCES organizations(id) ON DELETE CASCADE,
    branch_id UUID REFERENCES branches(id) ON DELETE CASCADE,
    property_id UUID REFERENCES properties(id) ON DELETE CASCADE,
    department_id UUID REFERENCES departments(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_user_role_scope_type CHECK (scope_type IN ('SYSTEM', 'ORGANIZATION', 'BRANCH', 'PROPERTY', 'DEPARTMENT', 'SELF')),
    CONSTRAINT ck_user_role_scope_shape CHECK (
        (scope_type = 'SYSTEM' AND role_key = 'SUPER_ADMIN' AND organization_id IS NULL AND branch_id IS NULL AND property_id IS NULL AND department_id IS NULL) OR
        (scope_type = 'ORGANIZATION' AND role_key <> 'SUPER_ADMIN' AND organization_id IS NOT NULL AND branch_id IS NULL AND property_id IS NULL AND department_id IS NULL) OR
        (scope_type = 'BRANCH' AND role_key <> 'SUPER_ADMIN' AND organization_id IS NOT NULL AND branch_id IS NOT NULL AND property_id IS NULL AND department_id IS NULL) OR
        (scope_type = 'PROPERTY' AND role_key <> 'SUPER_ADMIN' AND organization_id IS NOT NULL AND branch_id IS NOT NULL AND property_id IS NOT NULL AND department_id IS NULL) OR
        (scope_type = 'DEPARTMENT' AND role_key <> 'SUPER_ADMIN' AND organization_id IS NOT NULL AND branch_id IS NOT NULL AND property_id IS NOT NULL AND department_id IS NOT NULL) OR
        (scope_type = 'SELF' AND role_key <> 'SUPER_ADMIN' AND organization_id IS NULL AND branch_id IS NULL AND property_id IS NULL AND department_id IS NULL)
    ),
    CONSTRAINT fk_user_role_branch_org FOREIGN KEY (organization_id, branch_id)
        REFERENCES branches (organization_id, id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_property_scope FOREIGN KEY (organization_id, branch_id, property_id)
        REFERENCES properties (organization_id, branch_id, id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_department_scope FOREIGN KEY (organization_id, branch_id, property_id, department_id)
        REFERENCES departments (organization_id, branch_id, property_id, id) ON DELETE CASCADE
);
CREATE INDEX idx_user_role_scopes_user_id ON user_role_scopes(user_id);
CREATE INDEX idx_user_role_scopes_organization_id ON user_role_scopes(organization_id);
CREATE INDEX idx_user_role_scopes_branch_id ON user_role_scopes(branch_id);
CREATE INDEX idx_user_role_scopes_property_id ON user_role_scopes(property_id);
CREATE INDEX idx_user_role_scopes_department_id ON user_role_scopes(department_id);
CREATE UNIQUE INDEX uq_user_role_scope_system ON user_role_scopes(user_id, role_key) WHERE scope_type = 'SYSTEM';
CREATE UNIQUE INDEX uq_user_role_scope_organization ON user_role_scopes(user_id, role_key, organization_id) WHERE scope_type = 'ORGANIZATION';
CREATE UNIQUE INDEX uq_user_role_scope_branch ON user_role_scopes(user_id, role_key, branch_id) WHERE scope_type = 'BRANCH';
CREATE UNIQUE INDEX uq_user_role_scope_property ON user_role_scopes(user_id, role_key, property_id) WHERE scope_type = 'PROPERTY';
CREATE UNIQUE INDEX uq_user_role_scope_department ON user_role_scopes(user_id, role_key, department_id) WHERE scope_type = 'DEPARTMENT';
CREATE UNIQUE INDEX uq_user_role_scope_self ON user_role_scopes(user_id, role_key) WHERE scope_type = 'SELF';

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    family_id UUID NOT NULL,
    token_hash CHAR(64) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP WITH TIME ZONE,
    replaced_by_token_id UUID REFERENCES refresh_tokens(id) ON DELETE SET NULL,
    device_info VARCHAR(255),
    ip_address VARCHAR(45),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uq_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_family_id ON refresh_tokens(family_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

CREATE TABLE system_bootstrap_state (
    id SMALLINT PRIMARY KEY CHECK (id = 1),
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO system_bootstrap_state (id, completed) VALUES (1, FALSE);

INSERT INTO roles (role_key, display_name, description) VALUES
('SUPER_ADMIN', 'Super Admin', 'System-wide administrator; the only SYSTEM-scoped role.'),
('ORGANIZATION_ADMIN', 'Organization Admin', 'Administration within an assigned organization.'),
('BRANCH_MANAGER', 'Branch Manager', 'Management within an assigned branch and its properties.'),
('PROPERTY_MANAGER', 'Property Manager', 'Management within an assigned property.'),
('FRONT_DESK', 'Front Desk', 'Front desk operations within assigned scope.'),
('RESERVATIONS_AGENT', 'Reservations Agent', 'Reservation operations within assigned scope.'),
('HOUSEKEEPING_MANAGER', 'Housekeeping Manager', 'Housekeeping operations within assigned scope.'),
('HOUSEKEEPING_STAFF', 'Housekeeping Staff', 'Assigned housekeeping tasks within assigned scope.'),
('ACCOUNTANT', 'Accountant', 'Read-only financial operations within assigned scope.'),
('FINANCE_MANAGER', 'Finance Manager', 'Financial operations within assigned scope.'),
('RESTAURANT_MANAGER', 'Restaurant Manager', 'Restaurant operations within assigned scope.'),
('POS_CASHIER', 'POS Cashier', 'Point-of-sale and payment collection within assigned scope.'),
('KITCHEN_STAFF', 'Kitchen Staff', 'Kitchen operations within assigned scope.'),
('INVENTORY_MANAGER', 'Inventory Manager', 'Inventory operations within assigned scope.'),
('PROCUREMENT_OFFICER', 'Procurement Officer', 'Procurement operations within assigned scope.'),
('MAINTENANCE_MANAGER', 'Maintenance Manager', 'Maintenance operations within assigned scope.'),
('MAINTENANCE_STAFF', 'Maintenance Staff', 'Assigned maintenance work within assigned scope.'),
('HR_MANAGER', 'HR Manager', 'Human resources operations within assigned scope.'),
('REPORTING_USER', 'Reporting User', 'Read-only reports within assigned scope.');

INSERT INTO capabilities (capability_key, description) VALUES
('ORGANIZATION_READ', 'Read organization records'), ('ORGANIZATION_CREATE', 'Create organizations'), ('ORGANIZATION_UPDATE', 'Update organizations'), ('ORGANIZATION_DELETE', 'Delete organizations'),
('BRANCH_READ', 'Read branches'), ('BRANCH_CREATE', 'Create branches'), ('BRANCH_UPDATE', 'Update branches'), ('BRANCH_DELETE', 'Delete branches'),
('PROPERTY_READ', 'Read properties'), ('PROPERTY_CREATE', 'Create properties'), ('PROPERTY_UPDATE', 'Update properties'), ('PROPERTY_DELETE', 'Delete properties'),
('DEPARTMENT_READ', 'Read departments'), ('DEPARTMENT_CREATE', 'Create departments'), ('DEPARTMENT_UPDATE', 'Update departments'), ('DEPARTMENT_DELETE', 'Delete departments'),
('USER_MANAGE', 'Create and manage users and scoped role assignments'), ('ROLE_READ', 'Read the role and capability catalog'),
('PROFILE_READ', 'Read the authenticated user profile'), ('PROFILE_UPDATE', 'Update the authenticated user profile'), ('PASSWORD_CHANGE', 'Change the authenticated user password'),
('REPORT_READ', 'Read reports'), ('AUDIT_READ', 'Read audit records'), ('SECURITY_MANAGE', 'Manage system security settings'),
('GUEST_READ', 'Read guest records'), ('GUEST_CREATE', 'Create guest records'), ('GUEST_UPDATE', 'Update guest records'),
('ROOM_READ', 'Read room records'), ('ROOM_CREATE', 'Create room records'), ('ROOM_UPDATE', 'Update room records'), ('ROOM_STATUS_UPDATE', 'Update room status'),
('RESERVATION_READ', 'Read reservations'), ('RESERVATION_CREATE', 'Create reservations'), ('RESERVATION_UPDATE', 'Update reservations'), ('RESERVATION_CANCEL', 'Cancel reservations'),
('CHECKIN_CREATE', 'Check guests in'), ('CHECKOUT_CREATE', 'Check guests out'),
('HOUSEKEEPING_READ', 'Read housekeeping work'), ('HOUSEKEEPING_UPDATE', 'Update housekeeping work'), ('HOUSEKEEPING_ASSIGN', 'Assign housekeeping work'),
('MAINTENANCE_READ', 'Read maintenance work'), ('MAINTENANCE_UPDATE', 'Update maintenance work'), ('MAINTENANCE_ASSIGN', 'Assign maintenance work'),
('MENU_READ', 'Read menus'), ('MENU_CREATE', 'Create menu entries'), ('MENU_UPDATE', 'Update menu entries'),
('ORDER_READ', 'Read orders'), ('ORDER_CREATE', 'Create orders'), ('ORDER_UPDATE', 'Update orders'),
('KITCHEN_READ', 'Read kitchen work'), ('KITCHEN_UPDATE', 'Update kitchen work'),
('INVENTORY_READ', 'Read inventory'), ('INVENTORY_CREATE', 'Create inventory records'), ('INVENTORY_ADJUST', 'Adjust inventory'),
('SUPPLIER_READ', 'Read suppliers'), ('SUPPLIER_CREATE', 'Create suppliers'), ('SUPPLIER_UPDATE', 'Update suppliers'),
('PURCHASE_ORDER_CREATE', 'Create purchase orders'), ('PURCHASE_ORDER_UPDATE', 'Update purchase orders'), ('PURCHASE_ORDER_RECEIVE', 'Receive purchase orders'),
('INVOICE_READ', 'Read invoices'), ('INVOICE_CREATE', 'Create invoices'), ('PAYMENT_READ', 'Read payments'), ('PAYMENT_CREATE', 'Collect payments'), ('PAYMENT_REFUND', 'Refund payments'), ('FINANCE_READ', 'Read financial records'),
('EMPLOYEE_READ', 'Read employee records'), ('EMPLOYEE_CREATE', 'Create employee records'), ('EMPLOYEE_UPDATE', 'Update employee records');

INSERT INTO role_capabilities (role_key, capability_key)
    SELECT 'SUPER_ADMIN', capability_key FROM capabilities;

INSERT INTO role_capabilities (role_key, capability_key) VALUES
('ORGANIZATION_ADMIN','ORGANIZATION_READ'), ('ORGANIZATION_ADMIN','ORGANIZATION_UPDATE'),
('ORGANIZATION_ADMIN','BRANCH_READ'), ('ORGANIZATION_ADMIN','BRANCH_CREATE'), ('ORGANIZATION_ADMIN','BRANCH_UPDATE'),
('ORGANIZATION_ADMIN','PROPERTY_READ'), ('ORGANIZATION_ADMIN','PROPERTY_CREATE'), ('ORGANIZATION_ADMIN','PROPERTY_UPDATE'),
('ORGANIZATION_ADMIN','DEPARTMENT_READ'), ('ORGANIZATION_ADMIN','DEPARTMENT_CREATE'), ('ORGANIZATION_ADMIN','DEPARTMENT_UPDATE'),
('ORGANIZATION_ADMIN','USER_MANAGE'), ('ORGANIZATION_ADMIN','ROLE_READ'), ('ORGANIZATION_ADMIN','REPORT_READ'),
('BRANCH_MANAGER','BRANCH_READ'), ('BRANCH_MANAGER','BRANCH_UPDATE'),
('BRANCH_MANAGER','PROPERTY_READ'), ('BRANCH_MANAGER','PROPERTY_CREATE'), ('BRANCH_MANAGER','PROPERTY_UPDATE'),
('BRANCH_MANAGER','DEPARTMENT_READ'), ('BRANCH_MANAGER','DEPARTMENT_CREATE'), ('BRANCH_MANAGER','DEPARTMENT_UPDATE'),
('BRANCH_MANAGER','USER_MANAGE'), ('BRANCH_MANAGER','ROLE_READ'), ('BRANCH_MANAGER','REPORT_READ'),
('PROPERTY_MANAGER','PROPERTY_READ'), ('PROPERTY_MANAGER','PROPERTY_UPDATE'),
('PROPERTY_MANAGER','DEPARTMENT_READ'), ('PROPERTY_MANAGER','DEPARTMENT_CREATE'), ('PROPERTY_MANAGER','DEPARTMENT_UPDATE'),
('PROPERTY_MANAGER','USER_MANAGE'), ('PROPERTY_MANAGER','ROLE_READ'), ('PROPERTY_MANAGER','REPORT_READ'),
('FRONT_DESK','PROPERTY_READ'), ('FRONT_DESK','PROFILE_READ'), ('FRONT_DESK','PROFILE_UPDATE'), ('FRONT_DESK','PASSWORD_CHANGE'),
('FRONT_DESK','GUEST_READ'), ('FRONT_DESK','GUEST_CREATE'), ('FRONT_DESK','GUEST_UPDATE'), ('FRONT_DESK','ROOM_READ'), ('FRONT_DESK','RESERVATION_READ'), ('FRONT_DESK','RESERVATION_CREATE'), ('FRONT_DESK','RESERVATION_UPDATE'), ('FRONT_DESK','CHECKIN_CREATE'), ('FRONT_DESK','CHECKOUT_CREATE'), ('FRONT_DESK','INVOICE_READ'), ('FRONT_DESK','INVOICE_CREATE'),
('RESERVATIONS_AGENT','PROPERTY_READ'), ('RESERVATIONS_AGENT','PROFILE_READ'), ('RESERVATIONS_AGENT','PROFILE_UPDATE'), ('RESERVATIONS_AGENT','PASSWORD_CHANGE'),
('RESERVATIONS_AGENT','GUEST_READ'), ('RESERVATIONS_AGENT','GUEST_CREATE'), ('RESERVATIONS_AGENT','GUEST_UPDATE'), ('RESERVATIONS_AGENT','ROOM_READ'), ('RESERVATIONS_AGENT','RESERVATION_READ'), ('RESERVATIONS_AGENT','RESERVATION_CREATE'), ('RESERVATIONS_AGENT','RESERVATION_UPDATE'), ('RESERVATIONS_AGENT','RESERVATION_CANCEL'),
('HOUSEKEEPING_MANAGER','PROPERTY_READ'), ('HOUSEKEEPING_MANAGER','PROFILE_READ'), ('HOUSEKEEPING_MANAGER','PROFILE_UPDATE'), ('HOUSEKEEPING_MANAGER','PASSWORD_CHANGE'), ('HOUSEKEEPING_MANAGER','ROOM_READ'), ('HOUSEKEEPING_MANAGER','ROOM_STATUS_UPDATE'), ('HOUSEKEEPING_MANAGER','HOUSEKEEPING_READ'), ('HOUSEKEEPING_MANAGER','HOUSEKEEPING_UPDATE'), ('HOUSEKEEPING_MANAGER','HOUSEKEEPING_ASSIGN'),
('HOUSEKEEPING_STAFF','PROPERTY_READ'), ('HOUSEKEEPING_STAFF','PROFILE_READ'), ('HOUSEKEEPING_STAFF','PROFILE_UPDATE'), ('HOUSEKEEPING_STAFF','PASSWORD_CHANGE'), ('HOUSEKEEPING_STAFF','ROOM_READ'), ('HOUSEKEEPING_STAFF','ROOM_STATUS_UPDATE'), ('HOUSEKEEPING_STAFF','HOUSEKEEPING_READ'), ('HOUSEKEEPING_STAFF','HOUSEKEEPING_UPDATE'),
('ACCOUNTANT','PROPERTY_READ'), ('ACCOUNTANT','PROFILE_READ'), ('ACCOUNTANT','PROFILE_UPDATE'), ('ACCOUNTANT','PASSWORD_CHANGE'), ('ACCOUNTANT','INVOICE_READ'), ('ACCOUNTANT','PAYMENT_READ'), ('ACCOUNTANT','FINANCE_READ'), ('ACCOUNTANT','REPORT_READ'),
('FINANCE_MANAGER','PROPERTY_READ'), ('FINANCE_MANAGER','PROFILE_READ'), ('FINANCE_MANAGER','PROFILE_UPDATE'), ('FINANCE_MANAGER','PASSWORD_CHANGE'), ('FINANCE_MANAGER','INVOICE_READ'), ('FINANCE_MANAGER','INVOICE_CREATE'), ('FINANCE_MANAGER','PAYMENT_READ'), ('FINANCE_MANAGER','PAYMENT_CREATE'), ('FINANCE_MANAGER','FINANCE_READ'), ('FINANCE_MANAGER','REPORT_READ'),
('RESTAURANT_MANAGER','PROPERTY_READ'), ('RESTAURANT_MANAGER','PROFILE_READ'), ('RESTAURANT_MANAGER','PROFILE_UPDATE'), ('RESTAURANT_MANAGER','PASSWORD_CHANGE'), ('RESTAURANT_MANAGER','MENU_READ'), ('RESTAURANT_MANAGER','MENU_CREATE'), ('RESTAURANT_MANAGER','MENU_UPDATE'), ('RESTAURANT_MANAGER','ORDER_READ'), ('RESTAURANT_MANAGER','ORDER_CREATE'), ('RESTAURANT_MANAGER','ORDER_UPDATE'), ('RESTAURANT_MANAGER','REPORT_READ'),
('POS_CASHIER','PROPERTY_READ'), ('POS_CASHIER','PROFILE_READ'), ('POS_CASHIER','PROFILE_UPDATE'), ('POS_CASHIER','PASSWORD_CHANGE'), ('POS_CASHIER','ORDER_READ'), ('POS_CASHIER','ORDER_CREATE'), ('POS_CASHIER','INVOICE_READ'), ('POS_CASHIER','INVOICE_CREATE'), ('POS_CASHIER','PAYMENT_READ'), ('POS_CASHIER','PAYMENT_CREATE'),
('KITCHEN_STAFF','PROPERTY_READ'), ('KITCHEN_STAFF','PROFILE_READ'), ('KITCHEN_STAFF','PROFILE_UPDATE'), ('KITCHEN_STAFF','PASSWORD_CHANGE'), ('KITCHEN_STAFF','ORDER_READ'), ('KITCHEN_STAFF','ORDER_UPDATE'), ('KITCHEN_STAFF','KITCHEN_READ'), ('KITCHEN_STAFF','KITCHEN_UPDATE'),
('INVENTORY_MANAGER','PROPERTY_READ'), ('INVENTORY_MANAGER','PROFILE_READ'), ('INVENTORY_MANAGER','PROFILE_UPDATE'), ('INVENTORY_MANAGER','PASSWORD_CHANGE'), ('INVENTORY_MANAGER','INVENTORY_READ'), ('INVENTORY_MANAGER','INVENTORY_CREATE'), ('INVENTORY_MANAGER','INVENTORY_ADJUST'), ('INVENTORY_MANAGER','SUPPLIER_READ'),
('PROCUREMENT_OFFICER','PROPERTY_READ'), ('PROCUREMENT_OFFICER','PROFILE_READ'), ('PROCUREMENT_OFFICER','PROFILE_UPDATE'), ('PROCUREMENT_OFFICER','PASSWORD_CHANGE'), ('PROCUREMENT_OFFICER','SUPPLIER_READ'), ('PROCUREMENT_OFFICER','SUPPLIER_CREATE'), ('PROCUREMENT_OFFICER','SUPPLIER_UPDATE'), ('PROCUREMENT_OFFICER','PURCHASE_ORDER_CREATE'), ('PROCUREMENT_OFFICER','PURCHASE_ORDER_UPDATE'), ('PROCUREMENT_OFFICER','PURCHASE_ORDER_RECEIVE'),
('MAINTENANCE_MANAGER','PROPERTY_READ'), ('MAINTENANCE_MANAGER','PROFILE_READ'), ('MAINTENANCE_MANAGER','PROFILE_UPDATE'), ('MAINTENANCE_MANAGER','PASSWORD_CHANGE'), ('MAINTENANCE_MANAGER','ROOM_READ'), ('MAINTENANCE_MANAGER','MAINTENANCE_READ'), ('MAINTENANCE_MANAGER','MAINTENANCE_UPDATE'), ('MAINTENANCE_MANAGER','MAINTENANCE_ASSIGN'),
('MAINTENANCE_STAFF','PROPERTY_READ'), ('MAINTENANCE_STAFF','PROFILE_READ'), ('MAINTENANCE_STAFF','PROFILE_UPDATE'), ('MAINTENANCE_STAFF','PASSWORD_CHANGE'), ('MAINTENANCE_STAFF','ROOM_READ'), ('MAINTENANCE_STAFF','MAINTENANCE_READ'), ('MAINTENANCE_STAFF','MAINTENANCE_UPDATE'),
('HR_MANAGER','PROPERTY_READ'), ('HR_MANAGER','PROFILE_READ'), ('HR_MANAGER','PROFILE_UPDATE'), ('HR_MANAGER','PASSWORD_CHANGE'), ('HR_MANAGER','EMPLOYEE_READ'), ('HR_MANAGER','EMPLOYEE_CREATE'), ('HR_MANAGER','EMPLOYEE_UPDATE'),
('REPORTING_USER','PROPERTY_READ'), ('REPORTING_USER','PROFILE_READ'), ('REPORTING_USER','PROFILE_UPDATE'), ('REPORTING_USER','PASSWORD_CHANGE'), ('REPORTING_USER','REPORT_READ');
