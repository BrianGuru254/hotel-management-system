# HOTEL & RESTAURANT MANAGEMENT SYSTEM

# PostgreSQL Migration Scripts Specification

**Project:** Hotel & Restaurant Management System (HRMS)  
**Document:** PostgreSQL Migration Scripts  
**Version:** 1.0  
**Status:** Implementation-Ready  
**Date:** September 2026  
**Database:** PostgreSQL  
**Migration Tool:** Flyway  
**Backend:** Java + Spring Boot  
**ORM:** Hibernate / Spring Data JPA  
**API:** REST + JSON  
**Primary Key Strategy:** UUID  

---

# Table of Contents

1. [Document Purpose](#1-document-purpose)
2. [Database Objectives](#2-database-objectives)
3. [Database Technology](#3-database-technology)
4. [Database Architecture](#4-database-architecture)
5. [Database Naming Standards](#5-database-naming-standards)
6. [UUID Strategy](#6-uuid-strategy)
7. [Common Column Standards](#7-common-column-standards)
8. [Migration Directory](#8-migration-directory)
9. [Migration Execution Order](#9-migration-execution-order)
10. [Extensions](#10-extensions)
11. [Organizations](#11-organizations)
12. [Branches](#12-branches)
13. [Roles](#13-roles)
14. [Permissions](#14-permissions)
15. [Role Permissions](#15-role-permissions)
16. [Users](#16-users)
17. [User Branches](#17-user-branches)
18. [Employees](#18-employees)
19. [Departments](#19-departments)
20. [Guests](#20-guests)
21. [Room Types](#21-room-types)
22. [Amenities](#22-amenities)
23. [Room Type Amenities](#23-room-type-amenities)
24. [Rooms](#24-rooms)
25. [Reservations](#25-reservations)
26. [Reservation Guests](#26-reservation-guests)
27. [Stays](#27-stays)
28. [Housekeeping](#28-housekeeping)
29. [Maintenance](#29-maintenance)
30. [Restaurants](#30-restaurants)
31. [Restaurant Tables](#31-restaurant-tables)
32. [Menu Categories](#32-menu-categories)
33. [Menu Items](#33-menu-items)
34. [Menu Item Ingredients](#34-menu-item-ingredients)
35. [Orders](#35-orders)
36. [Order Items](#36-order-items)
37. [Kitchen Tickets](#37-kitchen-tickets)
38. [Kitchen Ticket Items](#38-kitchen-ticket-items)
39. [Inventory Categories](#39-inventory-categories)
40. [Inventory Units](#40-inventory-units)
41. [Inventory Items](#41-inventory-items)
42. [Inventory Transactions](#42-inventory-transactions)
43. [Suppliers](#43-suppliers)
44. [Supplier Items](#44-supplier-items)
45. [Purchase Orders](#45-purchase-orders)
46. [Purchase Order Items](#46-purchase-order-items)
47. [Goods Receipts](#47-goods-receipts)
48. [Goods Receipt Items](#48-goods-receipt-items)
49. [Taxes](#49-taxes)
50. [Discounts](#50-discounts)
51. [Invoices](#51-invoices)
52. [Invoice Items](#52-invoice-items)
53. [Payments](#53-payments)
54. [Payment Transactions](#54-payment-transactions)
55. [Refunds](#55-refunds)
56. [Loyalty Accounts](#56-loyalty-accounts)
57. [Loyalty Transactions](#57-loyalty-transactions)
58. [Notifications](#58-notifications)
59. [Audit Logs](#59-audit-logs)
60. [System Settings](#60-system-settings)
61. [Indexes](#61-indexes)
62. [Constraints](#62-constraints)
63. [Triggers](#63-triggers)
64. [Seed Data](#64-seed-data)
65. [Soft Deletion](#65-soft-deletion)
66. [Optimistic Locking](#66-optimistic-locking)
67. [Money and Financial Data](#67-money-and-financial-data)
68. [Date and Time Handling](#68-date-and-time-handling)
69. [Database Security](#69-database-security)
70. [Backup Strategy](#70-backup-strategy)
71. [Flyway Configuration](#71-flyway-configuration)
72. [Migration Naming Convention](#72-migration-naming-convention)
73. [Development Workflow](#73-development-workflow)
74. [Production Migration Rules](#74-production-migration-rules)
75. [Database Testing](#75-database-testing)
76. [Final Database Architecture](#76-final-database-architecture)
77. [Migration Checklist](#77-migration-checklist)

---

# 1. Document Purpose

This document defines the PostgreSQL database migration architecture for the Hotel & Restaurant Management System.

The database is responsible for storing and managing:

- Organizations
- Branches
- Users
- Roles
- Permissions
- Employees
- Guests
- Rooms
- Reservations
- Stays
- Housekeeping
- Maintenance
- Restaurants
- Tables
- Menus
- Orders
- Kitchen operations
- Inventory
- Suppliers
- Procurement
- Billing
- Payments
- Taxes
- Discounts
- Loyalty
- Notifications
- Audit records
- System settings

Database migrations will be managed using **Flyway**.

---

# 2. Database Objectives

The database must provide:

- Data integrity
- Referential integrity
- Transaction safety
- High consistency
- Query performance
- Scalability
- Multi-branch support
- Auditing
- Financial accuracy
- Reservation conflict prevention
- Inventory traceability
- Secure access

---

# 3. Database Technology

| Component | Technology |
|---|---|
| Database | PostgreSQL |
| Migration | Flyway |
| ORM | Hibernate |
| Data Access | Spring Data JPA |
| Primary Keys | UUID |
| Currency | NUMERIC |
| Timestamps | TIMESTAMPTZ |
| JSON Data | JSONB where appropriate |
| Indexing | B-tree / specialized indexes |
| Transactions | PostgreSQL transactions |

---

# 4. Database Architecture

The system uses a relational database.

```text
                         ORGANIZATION
                              │
                 ┌────────────┴────────────┐
                 │                         │
              BRANCHES                    USERS
                 │                         │
       ┌─────────┼──────────┐              │
       │         │          │              │
     ROOMS   RESTAURANTS INVENTORY       ROLES
       │         │          │              │
       │         │          │         PERMISSIONS
       │         │          │
       ▼         ▼          ▼
RESERVATIONS   ORDERS     STOCK
       │         │          │
       ▼         ▼          ▼
    STAYS      KITCHEN   PROCUREMENT
       │
       ▼
    BILLING
       │
       ▼
   PAYMENTS
```

---

# 5. Database Naming Standards

Use `snake_case`.

Examples:

```text
organization_id
first_name
last_name
created_at
updated_at
deleted_at
payment_status
```

Table names should generally be plural:

```text
users
guests
rooms
reservations
payments
```

Avoid:

```text
tbl_users
tbl_rooms
tbl_guests
```

---

# 6. UUID Strategy

All major entities use UUID primary keys.

Recommended PostgreSQL generation:

```sql
gen_random_uuid()
```

Example:

```sql
id UUID PRIMARY KEY DEFAULT gen_random_uuid()
```

UUIDs provide:

- globally unique identifiers
- safer distributed systems
- easier multi-branch scaling
- reduced predictability compared with sequential IDs

---

# 7. Common Column Standards

Most transactional entities should use:

```sql
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

created_by UUID,

updated_by UUID
```

Where soft deletion is appropriate:

```sql
deleted_at TIMESTAMPTZ
```

---

# 8. Migration Directory

Location:

```text
src/main/resources/db/migration/
```

Structure:

```text
db/migration/
├── V001__create_extensions.sql
├── V002__create_organizations.sql
├── V003__create_branches.sql
├── V004__create_roles.sql
├── V005__create_permissions.sql
├── V006__create_role_permissions.sql
├── V007__create_users.sql
├── V008__create_user_branches.sql
├── V009__create_departments.sql
├── V010__create_employees.sql
├── V011__create_guests.sql
├── V012__create_room_types.sql
├── V013__create_amenities.sql
├── V014__create_room_type_amenities.sql
├── V015__create_rooms.sql
├── V016__create_reservations.sql
├── V017__create_reservation_guests.sql
├── V018__create_stays.sql
├── V019__create_housekeeping.sql
├── V020__create_maintenance.sql
├── V021__create_restaurants.sql
├── V022__create_restaurant_tables.sql
├── V023__create_menu_categories.sql
├── V024__create_menu_items.sql
├── V025__create_menu_item_ingredients.sql
├── V026__create_orders.sql
├── V027__create_order_items.sql
├── V028__create_kitchen_tickets.sql
├── V029__create_kitchen_ticket_items.sql
├── V030__create_inventory_categories.sql
├── V031__create_inventory_units.sql
├── V032__create_inventory_items.sql
├── V033__create_inventory_transactions.sql
├── V034__create_suppliers.sql
├── V035__create_supplier_items.sql
├── V036__create_purchase_orders.sql
├── V037__create_purchase_order_items.sql
├── V038__create_goods_receipts.sql
├── V039__create_goods_receipt_items.sql
├── V040__create_taxes.sql
├── V041__create_discounts.sql
├── V042__create_invoices.sql
├── V043__create_invoice_items.sql
├── V044__create_payments.sql
├── V045__create_payment_transactions.sql
├── V046__create_refunds.sql
├── V047__create_loyalty_accounts.sql
├── V048__create_loyalty_transactions.sql
├── V049__create_notifications.sql
├── V050__create_audit_logs.sql
├── V051__create_system_settings.sql
├── V052__create_indexes.sql
├── V053__create_triggers.sql
└── V054__seed_initial_data.sql
```

---

# 9. Migration Execution Order

The database must be created in dependency order.

```text
Extensions
    ↓
Organization
    ↓
Branches
    ↓
Roles / Permissions
    ↓
Users
    ↓
Employees
    ↓
Guests
    ↓
Rooms
    ↓
Reservations
    ↓
Stays
    ↓
Restaurant
    ↓
Orders
    ↓
Inventory
    ↓
Procurement
    ↓
Billing
    ↓
Payments
    ↓
Loyalty
    ↓
Notifications
    ↓
Audit
    ↓
Indexes
    ↓
Triggers
    ↓
Seed Data
```

---

# 10. Extensions

Migration:

```text
V001__create_extensions.sql
```

Recommended:

```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;
```

`pgcrypto` provides `gen_random_uuid()`.

---

# 11. Organizations

Migration:

```text
V002__create_organizations.sql
```

```sql
CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(200) NOT NULL,
    legal_name VARCHAR(250),
    registration_number VARCHAR(100),
    tax_number VARCHAR(100),

    email VARCHAR(255),
    phone VARCHAR(50),
    website VARCHAR(255),

    address_line_1 VARCHAR(255),
    address_line_2 VARCHAR(255),
    city VARCHAR(100),
    country VARCHAR(100),

    currency_code CHAR(3) NOT NULL DEFAULT 'KES',
    timezone VARCHAR(100) NOT NULL DEFAULT 'Africa/Nairobi',

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

---

# 12. Branches

```sql
CREATE TABLE branches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL,

    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) NOT NULL,

    email VARCHAR(255),
    phone VARCHAR(50),

    address_line_1 VARCHAR(255),
    address_line_2 VARCHAR(255),
    city VARCHAR(100),
    country VARCHAR(100),

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_branches_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_branch_code
        UNIQUE (organization_id, code)
);
```

---

# 13. Roles

```sql
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID,

    name VARCHAR(100) NOT NULL,
    description TEXT,

    is_system_role BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_roles_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_role_name
        UNIQUE (organization_id, name)
);
```

---

# 14. Permissions

```sql
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(150) NOT NULL,
    description TEXT,

    module VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_permission_name
        UNIQUE (name)
);
```

Example permissions:

```text
reservation:create
reservation:view
reservation:update
reservation:cancel

payment:create
payment:view
payment:refund

inventory:view
inventory:adjust

report:view
user:manage
```

---

# 15. Role Permissions

```sql
CREATE TABLE role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,

    PRIMARY KEY (role_id, permission_id),

    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions(id)
        ON DELETE CASCADE
);
```

---

# 16. Users

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL,

    role_id UUID NOT NULL,

    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,

    password_hash VARCHAR(255) NOT NULL,

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,

    phone VARCHAR(50),

    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,

    last_login_at TIMESTAMPTZ,

    password_changed_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    deleted_at TIMESTAMPTZ,

    CONSTRAINT fk_users_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id),

    CONSTRAINT uq_users_username
        UNIQUE (organization_id, username),

    CONSTRAINT uq_users_email
        UNIQUE (organization_id, email)
);
```

---

# 17. User Branches

A user may have access to one or more branches.

```sql
CREATE TABLE user_branches (
    user_id UUID NOT NULL,
    branch_id UUID NOT NULL,

    is_primary BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (user_id, branch_id),

    CONSTRAINT fk_user_branches_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_branches_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id)
        ON DELETE CASCADE
);
```

---

# 18. Departments

```sql
CREATE TABLE departments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL,

    name VARCHAR(150) NOT NULL,
    description TEXT,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_departments_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_department_name
        UNIQUE (organization_id, name)
);
```

---

# 19. Employees

```sql
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

    employment_status VARCHAR(30)
        NOT NULL DEFAULT 'ACTIVE',

    hire_date DATE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    deleted_at TIMESTAMPTZ,

    CONSTRAINT fk_employees_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_employees_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),

    CONSTRAINT fk_employees_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id),

    CONSTRAINT fk_employees_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT uq_employee_number
        UNIQUE (organization_id, employee_number)
);
```

---

# 20. Guests

```sql
CREATE TABLE guests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL,

    guest_number VARCHAR(50),

    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,

    email VARCHAR(255),
    phone VARCHAR(50),

    nationality VARCHAR(100),

    identification_type VARCHAR(50),
    identification_number VARCHAR(100),

    date_of_birth DATE,

    address_line_1 VARCHAR(255),
    address_line_2 VARCHAR(255),
    city VARCHAR(100),
    country VARCHAR(100),

    notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    deleted_at TIMESTAMPTZ,

    CONSTRAINT fk_guests_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE
);
```

---

# 21. Room Types

```sql
CREATE TABLE room_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    branch_id UUID NOT NULL,

    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,

    description TEXT,

    capacity INTEGER NOT NULL DEFAULT 1,

    base_rate NUMERIC(14,2) NOT NULL DEFAULT 0,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_room_types_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_room_type_code
        UNIQUE (branch_id, code),

    CONSTRAINT chk_room_capacity
        CHECK (capacity > 0),

    CONSTRAINT chk_room_base_rate
        CHECK (base_rate >= 0)
);
```

---

# 22. Amenities

```sql
CREATE TABLE amenities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL,

    name VARCHAR(100) NOT NULL,
    description TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_amenities_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_amenity_name
        UNIQUE (organization_id, name)
);
```

---

# 23. Room Type Amenities

```sql
CREATE TABLE room_type_amenities (
    room_type_id UUID NOT NULL,
    amenity_id UUID NOT NULL,

    PRIMARY KEY (room_type_id, amenity_id),

    CONSTRAINT fk_room_type_amenities_type
        FOREIGN KEY (room_type_id)
        REFERENCES room_types(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_room_type_amenities_amenity
        FOREIGN KEY (amenity_id)
        REFERENCES amenities(id)
        ON DELETE CASCADE
);
```

---

# 24. Rooms

```sql
CREATE TABLE rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    branch_id UUID NOT NULL,
    room_type_id UUID NOT NULL,

    room_number VARCHAR(50) NOT NULL,
    floor_number INTEGER,

    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',

    notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    deleted_at TIMESTAMPTZ,

    CONSTRAINT fk_rooms_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),

    CONSTRAINT fk_rooms_room_type
        FOREIGN KEY (room_type_id)
        REFERENCES room_types(id),

    CONSTRAINT uq_room_number
        UNIQUE (branch_id, room_number),

    CONSTRAINT chk_room_status
        CHECK (
            status IN (
                'AVAILABLE',
                'OCCUPIED',
                'RESERVED',
                'DIRTY',
                'CLEAN',
                'OUT_OF_ORDER',
                'MAINTENANCE'
            )
        )
);
```

---

# 25. Reservations

```sql
CREATE TABLE reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    branch_id UUID NOT NULL,
    guest_id UUID NOT NULL,
    room_type_id UUID NOT NULL,
    room_id UUID,

    reservation_number VARCHAR(50) NOT NULL,

    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,

    adults INTEGER NOT NULL DEFAULT 1,
    children INTEGER NOT NULL DEFAULT 0,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    nightly_rate NUMERIC(14,2) NOT NULL,

    deposit_amount NUMERIC(14,2) NOT NULL DEFAULT 0,

    special_requests TEXT,

    source VARCHAR(50),

    created_by UUID,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    cancelled_at TIMESTAMPTZ,
    cancellation_reason TEXT,

    CONSTRAINT fk_reservations_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),

    CONSTRAINT fk_reservations_guest
        FOREIGN KEY (guest_id)
        REFERENCES guests(id),

    CONSTRAINT fk_reservations_room_type
        FOREIGN KEY (room_type_id)
        REFERENCES room_types(id),

    CONSTRAINT fk_reservations_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(id),

    CONSTRAINT uq_reservation_number
        UNIQUE (branch_id, reservation_number),

    CONSTRAINT chk_reservation_dates
        CHECK (check_out_date > check_in_date),

    CONSTRAINT chk_reservation_adults
        CHECK (adults > 0),

    CONSTRAINT chk_reservation_children
        CHECK (children >= 0),

    CONSTRAINT chk_reservation_rate
        CHECK (nightly_rate >= 0),

    CONSTRAINT chk_reservation_status
        CHECK (
            status IN (
                'PENDING',
                'CONFIRMED',
                'CHECKED_IN',
                'COMPLETED',
                'CANCELLED',
                'NO_SHOW'
            )
        )
);
```

---

# 26. Reservation Guests

A reservation can include multiple guests.

```sql
CREATE TABLE reservation_guests (
    reservation_id UUID NOT NULL,
    guest_id UUID NOT NULL,

    is_primary BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (reservation_id, guest_id),

    CONSTRAINT fk_reservation_guests_reservation
        FOREIGN KEY (reservation_id)
        REFERENCES reservations(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reservation_guests_guest
        FOREIGN KEY (guest_id)
        REFERENCES guests(id)
        ON DELETE CASCADE
);
```

---

# 27. Stays

```sql
CREATE TABLE stays (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    reservation_id UUID NOT NULL,
    guest_id UUID NOT NULL,
    room_id UUID NOT NULL,

    actual_check_in_at TIMESTAMPTZ,
    actual_check_out_at TIMESTAMPTZ,

    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    deposit_amount NUMERIC(14,2) NOT NULL DEFAULT 0,

    notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_stays_reservation
        FOREIGN KEY (reservation_id)
        REFERENCES reservations(id),

    CONSTRAINT fk_stays_guest
        FOREIGN KEY (guest_id)
        REFERENCES guests(id),

    CONSTRAINT fk_stays_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(id),

    CONSTRAINT chk_stay_status
        CHECK (
            status IN (
                'ACTIVE',
                'COMPLETED',
                'CANCELLED'
            )
        )
);
```

---

# 28. Housekeeping

```sql
CREATE TABLE housekeeping_tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    branch_id UUID NOT NULL,
    room_id UUID NOT NULL,

    assigned_employee_id UUID,

    task_type VARCHAR(50) NOT NULL DEFAULT 'CLEANING',

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',

    notes TEXT,

    assigned_at TIMESTAMPTZ,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_housekeeping_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),

    CONSTRAINT fk_housekeeping_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(id),

    CONSTRAINT fk_housekeeping_employee
        FOREIGN KEY (assigned_employee_id)
        REFERENCES employees(id)
);
```

---

# 29. Maintenance

The runtime schema in Flyway V012 uses `property_id` with a composite same-property room reference. Since an Employee table is not yet implemented, reporter and assigned technician IDs reference active users; the runtime migration adds a status-history table and one active room-blocking request per room. The SQL sketch below describes the earlier branch/employee model and is retained as historical design reference.

```sql
CREATE TABLE maintenance_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    branch_id UUID NOT NULL,
    room_id UUID,

    reported_by UUID,

    assigned_employee_id UUID,

    title VARCHAR(200) NOT NULL,
    description TEXT,

    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',

    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',

    reported_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_maintenance_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),

    CONSTRAINT fk_maintenance_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(id),

    CONSTRAINT fk_maintenance_reporter
        FOREIGN KEY (reported_by)
        REFERENCES users(id),

    CONSTRAINT fk_maintenance_employee
        FOREIGN KEY (assigned_employee_id)
        REFERENCES employees(id)
);
```

---

# 30. Restaurants

```sql
CREATE TABLE restaurants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    branch_id UUID NOT NULL,

    name VARCHAR(150) NOT NULL,
    description TEXT,

    opening_time TIME,
    closing_time TIME,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_restaurants_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id)
        ON DELETE CASCADE
);
```

---

# 31. Restaurant Tables

```sql
CREATE TABLE restaurant_tables (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    restaurant_id UUID NOT NULL,

    table_number VARCHAR(50) NOT NULL,

    capacity INTEGER NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_restaurant_tables_restaurant
        FOREIGN KEY (restaurant_id)
        REFERENCES restaurants(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_restaurant_table_number
        UNIQUE (restaurant_id, table_number),

    CONSTRAINT chk_table_capacity
        CHECK (capacity > 0)
);
```

---

# 32. Menu Categories

```sql
CREATE TABLE menu_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    restaurant_id UUID NOT NULL,

    name VARCHAR(100) NOT NULL,
    description TEXT,

    display_order INTEGER NOT NULL DEFAULT 0,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_menu_categories_restaurant
        FOREIGN KEY (restaurant_id)
        REFERENCES restaurants(id)
        ON DELETE CASCADE
);
```

---

# 33. Menu Items

```sql
CREATE TABLE menu_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    restaurant_id UUID NOT NULL,
    category_id UUID NOT NULL,

    name VARCHAR(200) NOT NULL,
    description TEXT,

    sku VARCHAR(100),

    price NUMERIC(14,2) NOT NULL,

    is_available BOOLEAN NOT NULL DEFAULT TRUE,

    preparation_time_minutes INTEGER,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    deleted_at TIMESTAMPTZ,

    CONSTRAINT fk_menu_items_restaurant
        FOREIGN KEY (restaurant_id)
        REFERENCES restaurants(id),

    CONSTRAINT fk_menu_items_category
        FOREIGN KEY (category_id)
        REFERENCES menu_categories(id),

    CONSTRAINT chk_menu_item_price
        CHECK (price >= 0),

    CONSTRAINT chk_preparation_time
        CHECK (
            preparation_time_minutes IS NULL
            OR preparation_time_minutes >= 0
        )
);
```

---

# 34. Menu Item Ingredients

```sql
CREATE TABLE menu_item_ingredients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    menu_item_id UUID NOT NULL,
    inventory_item_id UUID NOT NULL,

    quantity_required NUMERIC(14,4) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_menu_item_ingredients_menu_item
        FOREIGN KEY (menu_item_id)
        REFERENCES menu_items(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_quantity_required
        CHECK (quantity_required > 0)
);
```

The foreign key to `inventory_items` is added after the inventory table is created.

---

# 35. Orders

```sql
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    restaurant_id UUID NOT NULL,
    table_id UUID,

    guest_id UUID,
    stay_id UUID,

    order_number VARCHAR(50) NOT NULL,

    order_type VARCHAR(30) NOT NULL DEFAULT 'DINE_IN',

    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    subtotal NUMERIC(14,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(14,2) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(14,2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(14,2) NOT NULL DEFAULT 0,

    notes TEXT,

    created_by UUID,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_orders_restaurant
        FOREIGN KEY (restaurant_id)
        REFERENCES restaurants(id),

    CONSTRAINT fk_orders_table
        FOREIGN KEY (table_id)
        REFERENCES restaurant_tables(id),

    CONSTRAINT fk_orders_guest
        FOREIGN KEY (guest_id)
        REFERENCES guests(id),

    CONSTRAINT fk_orders_stay
        FOREIGN KEY (stay_id)
        REFERENCES stays(id),

    CONSTRAINT uq_order_number
        UNIQUE (restaurant_id, order_number)
);
```

---

# 36. Order Items

```sql
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    order_id UUID NOT NULL,
    menu_item_id UUID NOT NULL,

    quantity INTEGER NOT NULL,

    unit_price NUMERIC(14,2) NOT NULL,

    discount_amount NUMERIC(14,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(14,2) NOT NULL DEFAULT 0,

    total_amount NUMERIC(14,2) NOT NULL,

    notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_order_items_menu_item
        FOREIGN KEY (menu_item_id)
        REFERENCES menu_items(id),

    CONSTRAINT chk_order_item_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_order_item_price
        CHECK (unit_price >= 0)
);
```

---

# 37. Kitchen Tickets

```sql
CREATE TABLE kitchen_tickets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    order_id UUID NOT NULL,

    ticket_number VARCHAR(50) NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',

    started_at TIMESTAMPTZ,
    ready_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_kitchen_tickets_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_kitchen_ticket_number
        UNIQUE (ticket_number)
);
```

---

# 38. Kitchen Ticket Items

```sql
CREATE TABLE kitchen_ticket_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    kitchen_ticket_id UUID NOT NULL,
    order_item_id UUID NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    quantity INTEGER NOT NULL,

    notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_kitchen_ticket_items_ticket
        FOREIGN KEY (kitchen_ticket_id)
        REFERENCES kitchen_tickets(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_kitchen_ticket_items_order_item
        FOREIGN KEY (order_item_id)
        REFERENCES order_items(id),

    CONSTRAINT chk_kitchen_quantity
        CHECK (quantity > 0)
);
```

---

# 39. Inventory Categories

```sql
CREATE TABLE inventory_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL,

    name VARCHAR(150) NOT NULL,
    description TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_inventory_categories_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_inventory_category
        UNIQUE (organization_id, name)
);
```

---

# 40. Inventory Units

```sql
CREATE TABLE inventory_units (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL,

    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(20) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_inventory_units_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_inventory_unit
        UNIQUE (organization_id, abbreviation)
);
```

---

# 41. Inventory Items

```sql
CREATE TABLE inventory_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    branch_id UUID NOT NULL,
    category_id UUID NOT NULL,
    unit_id UUID NOT NULL,

    sku VARCHAR(100),
    name VARCHAR(200) NOT NULL,

    description TEXT,

    current_quantity NUMERIC(14,4) NOT NULL DEFAULT 0,
    reorder_level NUMERIC(14,4) NOT NULL DEFAULT 0,

    unit_cost NUMERIC(14,2) NOT NULL DEFAULT 0,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    deleted_at TIMESTAMPTZ,

    CONSTRAINT fk_inventory_items_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),

    CONSTRAINT fk_inventory_items_category
        FOREIGN KEY (category_id)
        REFERENCES inventory_categories(id),

    CONSTRAINT fk_inventory_items_unit
        FOREIGN KEY (unit_id)
        REFERENCES inventory_units(id),

    CONSTRAINT chk_inventory_quantity
        CHECK (current_quantity >= 0),

    CONSTRAINT chk_inventory_reorder_level
        CHECK (reorder_level >= 0),

    CONSTRAINT chk_inventory_unit_cost
        CHECK (unit_cost >= 0)
);
```

---

# 42. Inventory Transactions

Inventory transactions provide an immutable stock movement history.

```sql
CREATE TABLE inventory_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    inventory_item_id UUID NOT NULL,
    branch_id UUID NOT NULL,

    transaction_type VARCHAR(40) NOT NULL,

    quantity NUMERIC(14,4) NOT NULL,

    unit_cost NUMERIC(14,2),

    reference_type VARCHAR(50),
    reference_id UUID,

    notes TEXT,

    created_by UUID,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_inventory_transactions_item
        FOREIGN KEY (inventory_item_id)
        REFERENCES inventory_items(id),

    CONSTRAINT fk_inventory_transactions_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),

    CONSTRAINT chk_inventory_transaction_quantity
        CHECK (quantity <> 0)
);
```

Transaction types:

```text
OPENING_BALANCE
PURCHASE_RECEIPT
SALE
ISSUE
WASTAGE
ADJUSTMENT
RETURN
TRANSFER_IN
TRANSFER_OUT
```

---

# 43. Suppliers

```sql
CREATE TABLE suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL,

    name VARCHAR(200) NOT NULL,

    registration_number VARCHAR(100),

    email VARCHAR(255),
    phone VARCHAR(50),

    address_line_1 VARCHAR(255),
    address_line_2 VARCHAR(255),
    city VARCHAR(100),
    country VARCHAR(100),

    contact_person VARCHAR(150),

    payment_terms VARCHAR(100),

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

---

# 44. Supplier Items

```sql
CREATE TABLE supplier_items (
    supplier_id UUID NOT NULL,
    inventory_item_id UUID NOT NULL,

    supplier_sku VARCHAR(100),

    last_purchase_price NUMERIC(14,2),

    PRIMARY KEY (supplier_id, inventory_item_id),

    CONSTRAINT fk_supplier_items_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_supplier_items_inventory
        FOREIGN KEY (inventory_item_id)
        REFERENCES inventory_items(id)
        ON DELETE CASCADE
);
```

---

# 45. Purchase Orders

```sql
CREATE TABLE purchase_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    branch_id UUID NOT NULL,
    supplier_id UUID NOT NULL,

    purchase_order_number VARCHAR(50) NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    order_date DATE NOT NULL DEFAULT CURRENT_DATE,

    expected_delivery_date DATE,

    subtotal NUMERIC(14,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(14,2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(14,2) NOT NULL DEFAULT 0,

    notes TEXT,

    created_by UUID,

    approved_by UUID,
    approved_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_purchase_orders_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),

    CONSTRAINT fk_purchase_orders_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(id),

    CONSTRAINT uq_purchase_order_number
        UNIQUE (branch_id, purchase_order_number)
);
```

---

# 46. Purchase Order Items

```sql
CREATE TABLE purchase_order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    purchase_order_id UUID NOT NULL,
    inventory_item_id UUID NOT NULL,

    quantity NUMERIC(14,4) NOT NULL,

    unit_cost NUMERIC(14,2) NOT NULL,

    tax_amount NUMERIC(14,2) NOT NULL DEFAULT 0,

    total_amount NUMERIC(14,2) NOT NULL,

    received_quantity NUMERIC(14,4) NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_purchase_order_items_order
        FOREIGN KEY (purchase_order_id)
        REFERENCES purchase_orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_purchase_order_items_inventory
        FOREIGN KEY (inventory_item_id)
        REFERENCES inventory_items(id),

    CONSTRAINT chk_purchase_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_purchase_unit_cost
        CHECK (unit_cost >= 0),

    CONSTRAINT chk_received_quantity
        CHECK (received_quantity >= 0)
);
```

---

# 47. Goods Receipts

```sql
CREATE TABLE goods_receipts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    purchase_order_id UUID NOT NULL,
    branch_id UUID NOT NULL,

    receipt_number VARCHAR(50) NOT NULL,

    received_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    received_by UUID,

    notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_goods_receipts_purchase_order
        FOREIGN KEY (purchase_order_id)
        REFERENCES purchase_orders(id),

    CONSTRAINT fk_goods_receipts_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),

    CONSTRAINT uq_goods_receipt_number
        UNIQUE (branch_id, receipt_number)
);
```

---

# 48. Goods Receipt Items

```sql
CREATE TABLE goods_receipt_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    goods_receipt_id UUID NOT NULL,
    purchase_order_item_id UUID NOT NULL,
    inventory_item_id UUID NOT NULL,

    quantity_received NUMERIC(14,4) NOT NULL,

    unit_cost NUMERIC(14,2) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_receipt_items_receipt
        FOREIGN KEY (goods_receipt_id)
        REFERENCES goods_receipts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_receipt_items_po_item
        FOREIGN KEY (purchase_order_item_id)
        REFERENCES purchase_order_items(id),

    CONSTRAINT fk_receipt_items_inventory
        FOREIGN KEY (inventory_item_id)
        REFERENCES inventory_items(id),

    CONSTRAINT chk_quantity_received
        CHECK (quantity_received > 0)
);
```

---

# 49. Taxes

```sql
CREATE TABLE taxes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL,

    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,

    rate NUMERIC(7,4) NOT NULL,

    is_inclusive BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_taxes_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_tax_rate
        CHECK (rate >= 0)
);
```

---

# 50. Discounts

```sql
CREATE TABLE discounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL,

    name VARCHAR(150) NOT NULL,

    discount_type VARCHAR(30) NOT NULL,

    value NUMERIC(14,4) NOT NULL,

    minimum_amount NUMERIC(14,2),

    maximum_discount NUMERIC(14,2),

    starts_at TIMESTAMPTZ,
    ends_at TIMESTAMPTZ,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_discounts_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_discount_value
        CHECK (value >= 0)
);
```

---

# 51. Invoices

```sql
CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    branch_id UUID NOT NULL,

    guest_id UUID,

    stay_id UUID,

    invoice_number VARCHAR(50) NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    issue_date DATE NOT NULL DEFAULT CURRENT_DATE,

    due_date DATE,

    subtotal NUMERIC(14,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(14,2) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(14,2) NOT NULL DEFAULT 0,

    total_amount NUMERIC(14,2) NOT NULL DEFAULT 0,
    paid_amount NUMERIC(14,2) NOT NULL DEFAULT 0,

    balance_amount NUMERIC(14,2) NOT NULL DEFAULT 0,

    notes TEXT,

    created_by UUID,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_invoices_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),

    CONSTRAINT fk_invoices_guest
        FOREIGN KEY (guest_id)
        REFERENCES guests(id),

    CONSTRAINT fk_invoices_stay
        FOREIGN KEY (stay_id)
        REFERENCES stays(id),

    CONSTRAINT uq_invoice_number
        UNIQUE (branch_id, invoice_number)
);
```

---

# 52. Invoice Items

```sql
CREATE TABLE invoice_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    invoice_id UUID NOT NULL,

    description VARCHAR(255) NOT NULL,

    reference_type VARCHAR(50),
    reference_id UUID,

    quantity NUMERIC(14,4) NOT NULL DEFAULT 1,

    unit_price NUMERIC(14,2) NOT NULL,

    tax_amount NUMERIC(14,2) NOT NULL DEFAULT 0,

    discount_amount NUMERIC(14,2) NOT NULL DEFAULT 0,

    total_amount NUMERIC(14,2) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_invoice_items_invoice
        FOREIGN KEY (invoice_id)
        REFERENCES invoices(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_invoice_quantity
        CHECK (quantity > 0)
);
```

---

# 53. Payments

```sql
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    invoice_id UUID NOT NULL,
    branch_id UUID NOT NULL,

    payment_reference VARCHAR(100) NOT NULL,

    amount NUMERIC(14,2) NOT NULL,

    payment_method VARCHAR(30) NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    currency_code CHAR(3) NOT NULL DEFAULT 'KES',

    paid_at TIMESTAMPTZ,

    received_by UUID,

    idempotency_key VARCHAR(150),

    notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_payments_invoice
        FOREIGN KEY (invoice_id)
        REFERENCES invoices(id),

    CONSTRAINT fk_payments_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),

    CONSTRAINT chk_payment_amount
        CHECK (amount > 0),

    CONSTRAINT uq_payment_reference
        UNIQUE (payment_reference)
);
```

---

# 54. Payment Transactions

For external payment providers:

```sql
CREATE TABLE payment_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    payment_id UUID NOT NULL,

    provider VARCHAR(100) NOT NULL,

    provider_transaction_id VARCHAR(200),

    request_payload JSONB,
    response_payload JSONB,

    status VARCHAR(30) NOT NULL,

    error_code VARCHAR(100),
    error_message TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_payment_transactions_payment
        FOREIGN KEY (payment_id)
        REFERENCES payments(id)
        ON DELETE CASCADE
);
```

Sensitive payment information must not be stored unnecessarily.

---

# 55. Refunds

```sql
CREATE TABLE refunds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    payment_id UUID NOT NULL,

    refund_reference VARCHAR(100) NOT NULL,

    amount NUMERIC(14,2) NOT NULL,

    reason TEXT,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    processed_by UUID,

    processed_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_refunds_payment
        FOREIGN KEY (payment_id)
        REFERENCES payments(id),

    CONSTRAINT chk_refund_amount
        CHECK (amount > 0),

    CONSTRAINT uq_refund_reference
        UNIQUE (refund_reference)
);
```

---

# 56. Loyalty Accounts

```sql
CREATE TABLE loyalty_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    guest_id UUID NOT NULL,

    membership_number VARCHAR(100) NOT NULL,

    tier VARCHAR(50) NOT NULL DEFAULT 'STANDARD',

    points_balance BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_loyalty_accounts_guest
        FOREIGN KEY (guest_id)
        REFERENCES guests(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_loyalty_membership
        UNIQUE (membership_number),

    CONSTRAINT uq_loyalty_guest
        UNIQUE (guest_id),

    CONSTRAINT chk_points_balance
        CHECK (points_balance >= 0)
);
```

---

# 57. Loyalty Transactions

```sql
CREATE TABLE loyalty_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    loyalty_account_id UUID NOT NULL,

    transaction_type VARCHAR(30) NOT NULL,

    points BIGINT NOT NULL,

    reference_type VARCHAR(50),
    reference_id UUID,

    description TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_loyalty_transactions_account
        FOREIGN KEY (loyalty_account_id)
        REFERENCES loyalty_accounts(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_loyalty_points
        CHECK (points <> 0)
);
```

---

# 58. Notifications

```sql
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,

    notification_type VARCHAR(50) NOT NULL,

    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,

    channel VARCHAR(30) NOT NULL DEFAULT 'IN_APP',

    is_read BOOLEAN NOT NULL DEFAULT FALSE,

    read_at TIMESTAMPTZ,

    metadata JSONB,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
```

---

# 59. Audit Logs

Audit logs should preserve a history of important system actions.

```sql
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID,

    user_id UUID,

    action VARCHAR(100) NOT NULL,

    entity_type VARCHAR(100),
    entity_id UUID,

    old_values JSONB,
    new_values JSONB,

    ip_address INET,

    user_agent TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_audit_logs_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_audit_logs_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);
```

Important actions:

```text
LOGIN
LOGOUT
CREATE
UPDATE
DELETE
CHECK_IN
CHECK_OUT
PAYMENT
REFUND
VOID_INVOICE
STOCK_ADJUSTMENT
ROLE_CHANGE
PASSWORD_RESET
```

Audit logs should generally be append-only.

---

# 60. System Settings

```sql
CREATE TABLE system_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL,

    setting_key VARCHAR(150) NOT NULL,

    setting_value TEXT,

    data_type VARCHAR(30) NOT NULL DEFAULT 'STRING',

    description TEXT,

    is_sensitive BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_system_settings_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_system_setting
        UNIQUE (organization_id, setting_key)
);
```

Secrets should not be stored here merely for convenience. Sensitive credentials belong in a proper secrets-management mechanism.

---

# 61. Indexes

Indexes should support frequent queries.

## Organization

```sql
CREATE INDEX idx_branches_organization
ON branches(organization_id);
```

## Users

```sql
CREATE INDEX idx_users_role
ON users(role_id);

CREATE INDEX idx_users_organization
ON users(organization_id);

CREATE INDEX idx_users_email
ON users(email);
```

## Employees

```sql
CREATE INDEX idx_employees_branch
ON employees(branch_id);

CREATE INDEX idx_employees_department
ON employees(department_id);
```

## Guests

```sql
CREATE INDEX idx_guests_email
ON guests(email);

CREATE INDEX idx_guests_phone
ON guests(phone);

CREATE INDEX idx_guests_identification
ON guests(identification_number);
```

## Rooms

```sql
CREATE INDEX idx_rooms_branch_status
ON rooms(branch_id, status);

CREATE INDEX idx_rooms_room_type
ON rooms(room_type_id);
```

## Reservations

```sql
CREATE INDEX idx_reservations_branch_dates
ON reservations(branch_id, check_in_date, check_out_date);

CREATE INDEX idx_reservations_guest
ON reservations(guest_id);

CREATE INDEX idx_reservations_status
ON reservations(status);

CREATE INDEX idx_reservations_room
ON reservations(room_id);
```

## Stays

```sql
CREATE INDEX idx_stays_room
ON stays(room_id);

CREATE INDEX idx_stays_guest
ON stays(guest_id);

CREATE INDEX idx_stays_status
ON stays(status);
```

## Orders

```sql
CREATE INDEX idx_orders_restaurant
ON orders(restaurant_id);

CREATE INDEX idx_orders_status
ON orders(status);

CREATE INDEX idx_orders_created_at
ON orders(created_at);
```

## Inventory

```sql
CREATE INDEX idx_inventory_branch
ON inventory_items(branch_id);

CREATE INDEX idx_inventory_category
ON inventory_items(category_id);

CREATE INDEX idx_inventory_transactions_item
ON inventory_transactions(inventory_item_id);

CREATE INDEX idx_inventory_transactions_created
ON inventory_transactions(created_at);
```

## Billing

```sql
CREATE INDEX idx_invoices_guest
ON invoices(guest_id);

CREATE INDEX idx_invoices_stay
ON invoices(stay_id);

CREATE INDEX idx_invoices_status
ON invoices(status);

CREATE INDEX idx_payments_invoice
ON payments(invoice_id);

CREATE INDEX idx_payments_status
ON payments(status);
```

---

# 62. Constraints

Database constraints must enforce important business invariants.

Examples:

```text
NOT NULL
UNIQUE
PRIMARY KEY
FOREIGN KEY
CHECK
```

Examples:

```sql
CHECK (amount >= 0)

CHECK (quantity > 0)

CHECK (check_out_date > check_in_date)
```

Database constraints are the final layer of data protection.

---

# 63. Triggers

Triggers should be used carefully.

Recommended trigger:

```text
updated_at
```

Example:

```sql
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

Then:

```sql
CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
```

The same trigger pattern can be applied to appropriate mutable tables.

Business logic should generally remain in Spring Boot rather than being hidden inside complex database triggers.

---

# 64. Seed Data

Seed data should be separated from schema migrations where practical.

Example:

```text
V054__seed_initial_data.sql
```

Initial roles:

```text
SUPER_ADMIN
ADMIN
MANAGER
RECEPTIONIST
HOUSEKEEPING
RESTAURANT_MANAGER
WAITER
CHEF
INVENTORY_MANAGER
ACCOUNTANT
MAINTENANCE
AUDITOR
```

Initial permissions should cover the API modules.

Example:

```text
dashboard:view

guest:view
guest:create
guest:update
guest:delete

room:view
room:create
room:update

reservation:view
reservation:create
reservation:update
reservation:cancel

payment:view
payment:create
payment:refund

inventory:view
inventory:create
inventory:update
inventory:adjust

report:view

user:view
user:create
user:update
user:delete
```

Do not seed real production passwords into migration files.

---

# 65. Soft Deletion

Soft deletion should be used for records where historical integrity matters.

Examples:

```text
users
employees
guests
rooms
menu_items
inventory_items
```

Column:

```sql
deleted_at TIMESTAMPTZ
```

A deleted record should normally remain available for:

- audit
- historical reports
- financial records
- legal/compliance purposes

Transactional financial records should not simply be deleted.

Use:

```text
VOID
REFUND
REVERSAL
ADJUSTMENT
```

where appropriate.

---

# 66. Optimistic Locking

Frequently updated entities may use a version column.

Example:

```sql
version BIGINT NOT NULL DEFAULT 0
```

Recommended for:

```text
rooms
reservations
orders
inventory_items
purchase_orders
invoices
```

Hibernate can map this using:

```java
@Version
private Long version;
```

This helps prevent lost updates when multiple users modify the same record.

---

# 67. Money and Financial Data

Do not use floating-point types for monetary values.

Use:

```sql
NUMERIC(14,2)
```

Example:

```sql
price NUMERIC(14,2)
```

Calculations should be performed using precise decimal types in Java:

```java
BigDecimal
```

Currency:

```text
KES
```

should be represented by an ISO-style three-letter currency code where needed.

---

# 68. Date and Time Handling

Use:

```sql
DATE
```

for dates without a time component.

Example:

```text
check_in_date
check_out_date
birth_date
```

Use:

```sql
TIMESTAMPTZ
```

for timestamps representing an actual point in time.

Example:

```text
created_at
updated_at
payment_at
check_in_at
check_out_at
```

The organization/branch timezone should be configurable.

Default project timezone:

```text
Africa/Nairobi
```

---

# 69. Database Security

The PostgreSQL database must not be directly exposed to the public internet.

Recommended architecture:

```text
Internet
   │
   ▼
Next.js
   │
   ▼
Spring Boot
   │
   ▼
Private PostgreSQL
```

Database credentials must be supplied through environment variables or a secret manager.

Production database access should use:

- TLS
- strong credentials
- least-privilege users
- restricted network access
- regular backups
- monitoring

---

# 70. Backup Strategy

Recommended:

```text
Daily Full Backup
+
Point-in-Time Recovery
+
Off-Site Backup
```

Backup requirements:

- encrypted
- monitored
- retained according to policy
- tested through restoration

A backup that has never been restored/tested should not be considered fully verified.

---

# 71. Flyway Configuration

Spring Boot configuration:

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    validate-on-migrate: true
```

Database:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
```

---

# 72. Migration Naming Convention

Use:

```text
V<number>__<description>.sql
```

Examples:

```text
V001__create_extensions.sql
V002__create_organizations.sql
V003__create_branches.sql
V004__create_roles.sql
```

Do not rename or modify an already-applied migration in production.

If a change is required, create a new migration.

Example:

```text
V055__add_guest_preferences.sql
```

---

# 73. Development Workflow

Developer workflow:

```text
Design
  ↓
Create Migration
  ↓
Run Migration
  ↓
Update Entity
  ↓
Update Repository
  ↓
Update Service
  ↓
Update API
  ↓
Write Tests
```

Example:

```text
Add guest preferences
        ↓
V055__add_guest_preferences.sql
        ↓
Guest entity
        ↓
Guest DTO
        ↓
Guest service
        ↓
Guest API
        ↓
Frontend
```

---

# 74. Production Migration Rules

Production database migrations must:

1. Be version controlled.
2. Be tested before deployment.
3. Be backward-compatible where possible.
4. Avoid destructive changes without a migration plan.
5. Have rollback/recovery considerations.
6. Be applied automatically or through a controlled deployment process.
7. Never be manually modified after execution.

For major schema changes:

```text
Old Column
    ↓
Add New Column
    ↓
Deploy Compatible Code
    ↓
Migrate Data
    ↓
Switch Application
    ↓
Remove Old Column Later
```

This reduces downtime and deployment risk.

---

# 75. Database Testing

Database tests should verify:

- migrations
- constraints
- foreign keys
- unique constraints
- transaction behavior
- indexes where relevant
- reservation conflicts
- inventory updates
- invoice calculations
- payment consistency

Recommended tool:

```text
Testcontainers
```

Example:

```text
Spring Boot Test
       ↓
Testcontainers
       ↓
PostgreSQL Container
       ↓
Flyway Migrations
       ↓
Integration Tests
```

Testing against a real PostgreSQL instance is preferred for database-specific behavior.

---

# 76. Final Database Architecture

```text
                         ┌─────────────────────┐
                         │     ORGANIZATION    │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │       BRANCHES      │
                         └──────────┬──────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │                     │                     │
              ▼                     ▼                     ▼
        ┌───────────┐        ┌─────────────┐       ┌────────────┐
        │   USERS   │        │    HOTEL    │       │ RESTAURANT │
        └─────┬─────┘        └──────┬──────┘       └─────┬──────┘
              │                     │                    │
              ▼                     ▼                    ▼
       ROLES/PERMISSIONS      RESERVATIONS             ORDERS
                                    │                    │
                                    ▼                    ▼
                                  STAYS                KITCHEN
                                    │
                                    ▼
                                BILLING
                                    │
                                    ▼
                                PAYMENTS

                         ┌─────────────────┐
                         │    INVENTORY    │
                         └────────┬────────┘
                                  │
                     ┌────────────┴────────────┐
                     ▼                         ▼
                PROCUREMENT                 SUPPLIERS

                         ┌─────────────────┐
                         │   AUDIT LOGS    │
                         └─────────────────┘
```

---

# 77. Migration Checklist

## Core Database

- [ ] PostgreSQL created
- [ ] `pgcrypto` enabled
- [ ] UUID strategy configured
- [ ] Organizations created
- [ ] Branches created
- [ ] Roles created
- [ ] Permissions created
- [ ] Users created
- [ ] User branch access created

## Hotel

- [ ] Departments
- [ ] Employees
- [ ] Guests
- [ ] Room types
- [ ] Amenities
- [ ] Rooms
- [ ] Reservations
- [ ] Reservation guests
- [ ] Stays
- [ ] Housekeeping
- [ ] Maintenance

## Restaurant

- [ ] Restaurants
- [ ] Tables
- [ ] Menu categories
- [ ] Menu items
- [ ] Menu ingredients
- [ ] Orders
- [ ] Order items
- [ ] Kitchen tickets
- [ ] Kitchen ticket items

## Inventory

- [ ] Categories
- [ ] Units
- [ ] Inventory items
- [ ] Inventory transactions
- [ ] Suppliers
- [ ] Supplier items
- [ ] Purchase orders
- [ ] Purchase order items
- [ ] Goods receipts
- [ ] Goods receipt items

## Finance

- [ ] Taxes
- [ ] Discounts
- [ ] Invoices
- [ ] Invoice items
- [ ] Payments
- [ ] Payment transactions
- [ ] Refunds

## Management

- [ ] Loyalty
- [ ] Notifications
- [ ] Audit logs
- [ ] System settings

## Performance and Integrity

- [ ] Foreign keys
- [ ] Unique constraints
- [ ] Check constraints
- [ ] Indexes
- [ ] Updated-at triggers
- [ ] Optimistic locking
- [ ] Soft deletion
- [ ] Transaction strategy

## Production

- [ ] Backup strategy
- [ ] Restore testing
- [ ] Database credentials secured
- [ ] Network access restricted
- [ ] TLS configured
- [ ] Flyway validation enabled
- [ ] Migration pipeline tested

---

# Project Design Status

```text
DOCUMENT 1  ✓ Complete Requirements Specification
DOCUMENT 2  ✓ Database ERD + Complete Table Definitions
DOCUMENT 3  ✓ Complete API Specification
DOCUMENT 4  ✓ UI/UX Screen Map + Navigation
DOCUMENT 5  ✓ System Architecture Specification
DOCUMENT 6  ✓ Backend Project Structure
DOCUMENT 7  ✓ Frontend Project Structure
DOCUMENT 8  ✓ PostgreSQL Migration Scripts
DOCUMENT 9  → Spring Boot Backend Implementation
DOCUMENT 10 → Next.js Frontend Implementation
DOCUMENT 11 → Testing Strategy
DOCUMENT 12 → Deployment Specification
```

---

# Next Step

The design documentation has now reached the point where we can begin converting the architecture into an actual working system.

The next document/implementation should be:

# `SPRING_BOOT_BACKEND_IMPLEMENTATION.md`

This phase will establish the actual backend project, including:

```text
Spring Boot project
        ↓
Maven configuration
        ↓
PostgreSQL connection
        ↓
Flyway migrations
        ↓
Entities
        ↓
Repositories
        ↓
DTOs
        ↓
Services
        ↓
Controllers
        ↓
JWT Security
        ↓
Exception Handling
        ↓
Validation
        ↓
OpenAPI / Swagger
        ↓
Tests
```

The first implementation milestone should be **project initialization + PostgreSQL/Flyway + security foundation**, before implementing individual hotel modules.

---

**Document Status:** COMPLETE  
**Version:** 1.0  
**Database Status:** Ready for Spring Boot Integration
