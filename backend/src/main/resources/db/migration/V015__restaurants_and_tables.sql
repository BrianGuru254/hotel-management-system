CREATE TABLE restaurants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    opening_time TIME,
    closing_time TIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_restaurants_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE RESTRICT,
    CONSTRAINT uq_restaurants_branch_name UNIQUE (branch_id, name)
);
CREATE UNIQUE INDEX uq_restaurants_branch_name_ci ON restaurants(branch_id, lower(name));
CREATE INDEX idx_restaurants_branch ON restaurants(branch_id);

CREATE TABLE restaurant_tables (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id UUID NOT NULL,
    table_number VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL,
    location VARCHAR(120),
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_restaurant_tables_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE RESTRICT,
    CONSTRAINT uq_restaurant_tables_number UNIQUE (restaurant_id, table_number),
    CONSTRAINT ck_restaurant_tables_capacity CHECK (capacity > 0),
    CONSTRAINT ck_restaurant_tables_status CHECK (status IN ('AVAILABLE','OCCUPIED','RESERVED','CLEANING','OUT_OF_SERVICE'))
);
CREATE UNIQUE INDEX uq_restaurant_tables_number_ci ON restaurant_tables(restaurant_id, lower(table_number));
CREATE INDEX idx_restaurant_tables_restaurant ON restaurant_tables(restaurant_id);

INSERT INTO capabilities(capability_key, description) VALUES
    ('RESTAURANT_READ','Read restaurant records'),('RESTAURANT_CREATE','Create restaurants'),('RESTAURANT_UPDATE','Update restaurants'),
    ('RESTAURANT_TABLE_READ','Read restaurant tables'),('RESTAURANT_TABLE_CREATE','Create restaurant tables'),
    ('RESTAURANT_TABLE_UPDATE','Update restaurant tables'),('RESTAURANT_TABLE_STATUS_UPDATE','Change restaurant table status')
ON CONFLICT (capability_key) DO NOTHING;

INSERT INTO role_capabilities(role_key, capability_key)
SELECT 'SUPER_ADMIN', capability_key FROM capabilities WHERE capability_key LIKE 'RESTAURANT%'
ON CONFLICT (role_key, capability_key) DO NOTHING;
INSERT INTO role_capabilities(role_key, capability_key) VALUES
    ('RESTAURANT_MANAGER','RESTAURANT_READ'),('RESTAURANT_MANAGER','RESTAURANT_CREATE'),('RESTAURANT_MANAGER','RESTAURANT_UPDATE'),
    ('RESTAURANT_MANAGER','RESTAURANT_TABLE_READ'),('RESTAURANT_MANAGER','RESTAURANT_TABLE_CREATE'),('RESTAURANT_MANAGER','RESTAURANT_TABLE_UPDATE'),('RESTAURANT_MANAGER','RESTAURANT_TABLE_STATUS_UPDATE'),
    ('BRANCH_MANAGER','RESTAURANT_READ'),('BRANCH_MANAGER','RESTAURANT_CREATE'),('BRANCH_MANAGER','RESTAURANT_UPDATE'),
    ('BRANCH_MANAGER','RESTAURANT_TABLE_READ'),('BRANCH_MANAGER','RESTAURANT_TABLE_CREATE'),('BRANCH_MANAGER','RESTAURANT_TABLE_UPDATE'),('BRANCH_MANAGER','RESTAURANT_TABLE_STATUS_UPDATE')
ON CONFLICT (role_key, capability_key) DO NOTHING;
