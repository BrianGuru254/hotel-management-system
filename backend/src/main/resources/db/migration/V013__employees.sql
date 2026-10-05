ALTER TABLE departments
    ADD CONSTRAINT uq_departments_org_branch_id UNIQUE (organization_id, branch_id, id);

CREATE TABLE employees (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    branch_id UUID NOT NULL,
    department_id UUID,
    user_id UUID,
    employee_number VARCHAR(50) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    job_title VARCHAR(150),
    employment_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    hire_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT fk_employees_branch_scope FOREIGN KEY (organization_id, branch_id)
        REFERENCES branches(organization_id, id) ON DELETE RESTRICT,
    CONSTRAINT fk_employees_department_scope FOREIGN KEY (organization_id, branch_id, department_id)
        REFERENCES departments(organization_id, branch_id, id) ON DELETE RESTRICT,
    CONSTRAINT fk_employees_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT uq_employees_org_scope UNIQUE (organization_id, id),
    CONSTRAINT uq_employees_org_branch_id UNIQUE (organization_id, branch_id, id),
    CONSTRAINT uq_employees_org_number UNIQUE (organization_id, employee_number),
    CONSTRAINT uq_employees_user UNIQUE (user_id),
    CONSTRAINT ck_employees_status CHECK (employment_status IN ('ACTIVE','INACTIVE','ON_LEAVE','TERMINATED'))
);

CREATE UNIQUE INDEX uq_employees_org_number_ci ON employees(organization_id, lower(employee_number));
CREATE UNIQUE INDEX uq_employees_org_email_ci ON employees(organization_id, lower(email)) WHERE email IS NOT NULL AND deleted_at IS NULL;
CREATE INDEX idx_employees_org_status ON employees(organization_id, employment_status) WHERE deleted_at IS NULL;
CREATE INDEX idx_employees_branch ON employees(branch_id);
CREATE INDEX idx_employees_department ON employees(department_id);
