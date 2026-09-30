-- Enforce that every property's organization owns its branch.
ALTER TABLE branches
    ADD CONSTRAINT uq_branches_organization_id_id UNIQUE (organization_id, id);

CREATE TABLE properties (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    branch_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description TEXT,
    email VARCHAR(150),
    phone VARCHAR(50),
    address_line_1 VARCHAR(255),
    address_line_2 VARCHAR(255),
    city VARCHAR(100),
    county VARCHAR(100),
    country VARCHAR(100) NOT NULL DEFAULT 'Kenya',
    postal_code VARCHAR(30),
    currency_code CHAR(3) NOT NULL DEFAULT 'KES',
    timezone VARCHAR(100) NOT NULL DEFAULT 'Africa/Nairobi',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_properties_organization
        FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_properties_branch
        FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    CONSTRAINT fk_properties_branch_organization
        FOREIGN KEY (organization_id, branch_id)
        REFERENCES branches(organization_id, id) ON DELETE CASCADE,
    CONSTRAINT uq_properties_branch_code UNIQUE (branch_id, code),
    CONSTRAINT ck_properties_code_upper CHECK (code = upper(code))
);

CREATE INDEX idx_properties_organization_id ON properties(organization_id);
CREATE INDEX idx_properties_branch_id ON properties(branch_id);
CREATE INDEX idx_properties_active ON properties(active);

CREATE TRIGGER trg_properties_updated_at
BEFORE UPDATE ON properties
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
