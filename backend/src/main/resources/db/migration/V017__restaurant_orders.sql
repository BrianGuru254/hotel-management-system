CREATE TABLE restaurant_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id UUID NOT NULL,
    table_id UUID,
    guest_id UUID,
    stay_id UUID,
    waiter_id UUID NOT NULL,
    table_status_before_order VARCHAR(30),
    order_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    subtotal NUMERIC(12,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    total NUMERIC(12,2) NOT NULL DEFAULT 0,
    notes TEXT,
    cancellation_reason VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_restaurant_orders_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE RESTRICT,
    CONSTRAINT fk_restaurant_orders_table FOREIGN KEY (table_id) REFERENCES restaurant_tables(id) ON DELETE RESTRICT,
    CONSTRAINT fk_restaurant_orders_guest FOREIGN KEY (guest_id) REFERENCES guests(id) ON DELETE RESTRICT,
    CONSTRAINT fk_restaurant_orders_stay FOREIGN KEY (stay_id) REFERENCES stays(id) ON DELETE RESTRICT,
    CONSTRAINT fk_restaurant_orders_waiter FOREIGN KEY (waiter_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT ck_restaurant_orders_type CHECK (order_type IN ('DINE_IN','TAKEAWAY','ROOM_SERVICE')),
    CONSTRAINT ck_restaurant_orders_status CHECK (status IN ('DRAFT','PLACED','CONFIRMED','PREPARING','READY','SERVED','COMPLETED','CANCELLED')),
    CONSTRAINT ck_restaurant_orders_money CHECK (subtotal >= 0 AND tax_amount >= 0 AND discount_amount >= 0 AND total >= 0)
);
CREATE INDEX idx_restaurant_orders_restaurant_status ON restaurant_orders(restaurant_id,status,created_at);
CREATE INDEX idx_restaurant_orders_table ON restaurant_orders(table_id);
CREATE INDEX idx_restaurant_orders_guest ON restaurant_orders(guest_id);

CREATE TABLE restaurant_order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    menu_item_id UUID NOT NULL,
    item_name VARCHAR(150) NOT NULL,
    item_sku VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,
    tax_rate NUMERIC(5,2) NOT NULL,
    line_subtotal NUMERIC(12,2) NOT NULL,
    line_tax NUMERIC(12,2) NOT NULL,
    line_total NUMERIC(12,2) NOT NULL,
    special_instructions TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_restaurant_order_items_order FOREIGN KEY (order_id) REFERENCES restaurant_orders(id) ON DELETE RESTRICT,
    CONSTRAINT fk_restaurant_order_items_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE RESTRICT,
    CONSTRAINT ck_restaurant_order_items_quantity CHECK (quantity > 0),
    CONSTRAINT ck_restaurant_order_items_money CHECK (unit_price >= 0 AND tax_rate BETWEEN 0 AND 100 AND line_subtotal >= 0 AND line_tax >= 0 AND line_total >= 0)
);
CREATE INDEX idx_restaurant_order_items_order ON restaurant_order_items(order_id);

INSERT INTO capabilities(capability_key, description) VALUES
    ('ORDER_READ','Read restaurant orders'),('ORDER_CREATE','Create restaurant orders'),('ORDER_UPDATE','Update restaurant orders')
ON CONFLICT (capability_key) DO NOTHING;
INSERT INTO role_capabilities(role_key, capability_key)
SELECT 'SUPER_ADMIN', capability_key FROM capabilities WHERE capability_key IN ('ORDER_READ','ORDER_CREATE','ORDER_UPDATE')
ON CONFLICT (role_key, capability_key) DO NOTHING;
INSERT INTO role_capabilities(role_key, capability_key) VALUES
    ('RESTAURANT_MANAGER','ORDER_READ'),('RESTAURANT_MANAGER','ORDER_CREATE'),('RESTAURANT_MANAGER','ORDER_UPDATE'),
    ('POS_CASHIER','ORDER_READ'),('POS_CASHIER','ORDER_CREATE'),('POS_CASHIER','ORDER_UPDATE')
ON CONFLICT (role_key, capability_key) DO NOTHING;
