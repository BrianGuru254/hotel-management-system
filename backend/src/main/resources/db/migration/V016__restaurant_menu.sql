CREATE TABLE menu_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    description TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_categories_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE RESTRICT,
    CONSTRAINT uq_menu_categories_restaurant_id UNIQUE (restaurant_id, id),
    CONSTRAINT uq_menu_categories_restaurant_name UNIQUE (restaurant_id, name)
);
CREATE UNIQUE INDEX uq_menu_categories_name_ci ON menu_categories(restaurant_id, lower(name));
CREATE INDEX idx_menu_categories_restaurant ON menu_categories(restaurant_id, display_order);

CREATE TABLE menu_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id UUID NOT NULL,
    category_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    sku VARCHAR(50) NOT NULL,
    description TEXT,
    selling_price NUMERIC(12,2) NOT NULL DEFAULT 0,
    tax_rate NUMERIC(5,2) NOT NULL DEFAULT 0,
    preparation_time_minutes INTEGER NOT NULL DEFAULT 0,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_items_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE RESTRICT,
    CONSTRAINT fk_menu_items_category_scope FOREIGN KEY (restaurant_id, category_id) REFERENCES menu_categories(restaurant_id, id) ON DELETE RESTRICT,
    CONSTRAINT uq_menu_items_restaurant_sku UNIQUE (restaurant_id, sku),
    CONSTRAINT ck_menu_items_price CHECK (selling_price >= 0),
    CONSTRAINT ck_menu_items_tax_rate CHECK (tax_rate BETWEEN 0 AND 100),
    CONSTRAINT ck_menu_items_preparation_time CHECK (preparation_time_minutes >= 0)
);
CREATE UNIQUE INDEX uq_menu_items_sku_ci ON menu_items(restaurant_id, lower(sku));
CREATE INDEX idx_menu_items_category ON menu_items(category_id);
CREATE INDEX idx_menu_items_restaurant_available ON menu_items(restaurant_id, available);
