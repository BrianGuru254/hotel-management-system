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
 CONSTRAINT ck_properties_code_upper CHECK (code = UPPER(code))
);
