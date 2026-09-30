# HOTEL & RESTAURANT MANAGEMENT SYSTEM

# SPRING BOOT BACKEND IMPLEMENTATION

**Version:** 1.0  
**Status:** Implementation-Ready  
**Date:** September 2026  
**Backend:** Java + Spring Boot  
**Database:** PostgreSQL  
**ORM:** Hibernate / Spring Data JPA  
**Migration:** Flyway  
**API:** REST / JSON  
**Authentication:** JWT  
**API Documentation:** OpenAPI 3 / Swagger  
**Primary Keys:** UUID  
**Default Currency:** KES  
**Default Timezone:** Africa/Nairobi  

---

# Table of Contents

1. [Document Purpose](#1-document-purpose)
2. [Backend Objectives](#2-backend-objectives)
3. [Technology Stack](#3-technology-stack)
4. [Backend Architecture](#4-backend-architecture)
5. [Project Initialization](#5-project-initialization)
6. [Maven Dependencies](#6-maven-dependencies)
7. [Project Directory Structure](#7-project-directory-structure)
8. [Application Configuration](#8-application-configuration)
9. [Database Configuration](#9-database-configuration)
10. [Flyway Configuration](#10-flyway-configuration)
11. [Core Domain Model](#11-core-domain-model)
12. [Entity Implementation Standards](#12-entity-implementation-standards)
13. [Organization Module](#13-organization-module)
14. [Branch Module](#14-branch-module)
15. [User and Role Module](#15-user-and-role-module)
16. [Employee Module](#16-employee-module)
17. [Guest Module](#17-guest-module)
18. [Room Management Module](#18-room-management-module)
19. [Reservation Module](#19-reservation-module)
20. [Stay and Front Desk Module](#20-stay-and-front-desk-module)
21. [Housekeeping Module](#21-housekeeping-module)
22. [Maintenance Module](#22-maintenance-module)
23. [Restaurant Module](#23-restaurant-module)
24. [Menu Module](#24-menu-module)
25. [Order and POS Module](#25-order-and-pos-module)
26. [Kitchen Module](#26-kitchen-module)
27. [Inventory Module](#27-inventory-module)
28. [Supplier and Procurement Module](#28-supplier-and-procurement-module)
29. [Billing Module](#29-billing-module)
30. [Payment Module](#30-payment-module)
31. [Tax and Discount Module](#31-tax-and-discount-module)
32. [Loyalty Module](#32-loyalty-module)
33. [Notification Module](#33-notification-module)
34. [Audit Module](#34-audit-module)
35. [Settings Module](#35-settings-module)
36. [JWT Authentication](#36-jwt-authentication)
37. [Authorization](#37-authorization)
38. [Password Security](#38-password-security)
39. [DTO Design](#39-dto-design)
40. [Mapper Layer](#40-mapper-layer)
41. [Repository Layer](#41-repository-layer)
42. [Service Layer](#42-service-layer)
43. [Controller Layer](#43-controller-layer)
44. [Validation](#44-validation)
45. [Exception Handling](#45-exception-handling)
46. [API Response Format](#46-api-response-format)
47. [Pagination and Filtering](#47-pagination-and-filtering)
48. [Transactions](#48-transactions)
49. [Reservation Business Logic](#49-reservation-business-logic)
50. [Check-In Business Logic](#50-check-in-business-logic)
51. [Check-Out Business Logic](#51-check-out-business-logic)
52. [Restaurant Order Business Logic](#52-restaurant-order-business-logic)
53. [Inventory Business Logic](#53-inventory-business-logic)
54. [Procurement Business Logic](#54-procurement-business-logic)
55. [Billing Business Logic](#55-billing-business-logic)
56. [Payment Idempotency](#56-payment-idempotency)
57. [Audit Logging](#57-audit-logging)
58. [Notifications](#58-notifications)
59. [OpenAPI / Swagger](#59-openapi--swagger)
60. [CORS](#60-cors)
61. [Security Configuration](#61-security-configuration)
62. [Logging](#62-logging)
63. [Environment Configuration](#63-environment-configuration)
64. [Testing Strategy](#64-testing-strategy)
65. [Integration Testing](#65-integration-testing)
66. [Testcontainers](#66-testcontainers)
67. [Backend Error Codes](#67-backend-error-codes)
68. [Health Checks](#68-health-checks)
69. [Performance Requirements](#69-performance-requirements)
70. [Deployment Requirements](#70-deployment-requirements)
71. [CI/CD](#71-cicd)
72. [Implementation Phases](#72-implementation-phases)
73. [Definition of Done](#73-definition-of-done)
74. [Backend Implementation Checklist](#74-backend-implementation-checklist)

---

# 1. Document Purpose

This document defines how the Hotel & Restaurant Management System backend will be implemented using Java and Spring Boot.

The backend will provide the central business logic and REST API consumed by the Next.js frontend.

The backend is responsible for:

- authentication
- authorization
- user management
- hotel management
- branch management
- employee management
- guest management
- room management
- reservations
- check-in
- check-out
- housekeeping
- maintenance
- restaurant operations
- menu management
- restaurant orders
- kitchen operations
- inventory
- suppliers
- procurement
- billing
- payments
- taxes
- discounts
- loyalty
- notifications
- reporting
- audit logging
- system configuration

---

# 2. Backend Objectives

The backend must be:

- secure
- modular
- maintainable
- scalable
- testable
- transaction-safe
- API-driven
- cloud-ready
- multi-branch capable
- production-ready

The implementation must separate:

```text
HTTP Layer
     ↓
Controller
     ↓
DTO
     ↓
Service
     ↓
Repository
     ↓
Database
```

Controllers must not contain complex business logic.

Business rules belong in services.

Database operations belong in repositories.

---

# 3. Technology Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming language |
| Spring Boot | Application framework |
| Spring Web | REST API |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| Spring Data JPA | Data access |
| Hibernate | ORM |
| PostgreSQL | Relational database |
| Flyway | Database migrations |
| Bean Validation | Input validation |
| Lombok | Boilerplate reduction |
| Jackson | JSON serialization |
| OpenAPI | API documentation |
| JUnit 5 | Unit testing |
| Mockito | Mock testing |
| Testcontainers | Integration testing |
| Maven | Build management |
| SLF4J / Logback | Logging |

---

# 4. Backend Architecture

The backend follows a modular layered architecture.

```text
                    NEXT.JS FRONTEND
                           │
                           │ HTTPS
                           ▼
                  ┌──────────────────┐
                  │ Spring Security  │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │ REST Controllers │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │      DTOs        │
                  │   Validation     │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │     Services     │
                  │ Business Logic   │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │   Repositories   │
                  │   Spring Data    │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │   PostgreSQL     │
                  └──────────────────┘
```

---

# 5. Project Initialization

Create the project using Spring Initializr.

Recommended settings:

```text
Project:
Maven

Language:
Java

Spring Boot:
Current stable Spring Boot 3.x release

Packaging:
Jar

Java:
21
```

Recommended metadata:

```text
Group:
com.hrms

Artifact:
hrms-backend

Name:
hrms-backend

Description:
Hotel and Restaurant Management System Backend

Package:
com.hrms
```

---

# 6. Maven Dependencies

The project should include dependencies for:

```xml
<dependencies>

    <!-- REST -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Flyway -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>

    <!-- Flyway PostgreSQL support -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-database-postgresql</artifactId>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
    </dependency>

    <!-- OpenAPI -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Security testing -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>

</dependencies>
```

JWT implementation must also include compatible JJWT runtime components:

```xml
jjwt-api
jjwt-impl
jjwt-jackson
```

Versions should be managed centrally through Maven properties or dependency management.

---

# 7. Project Directory Structure

The backend should use the following structure:

```text
hrms-backend/
│
├── pom.xml
├── README.md
├── .gitignore
├── .env.example
├── Dockerfile
├── docker-compose.yml
│
└── src/
    │
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── hrms/
    │   │
    │   │           ├── HrmsApplication.java
    │   │
    │   │           ├── config/
    │   │           │   ├── SecurityConfig.java
    │   │           │   ├── CorsConfig.java
    │   │           │   ├── OpenApiConfig.java
    │   │           │   └── DatabaseConfig.java
    │   │
    │   │           ├── common/
    │   │           │   ├── exception/
    │   │           │   ├── response/
    │   │           │   ├── validation/
    │   │           │   ├── security/
    │   │           │   ├── audit/
    │   │           │   └── util/
    │   │
    │   │           ├── auth/
    │   │           ├── users/
    │   │           ├── roles/
    │   │           ├── permissions/
    │   │           ├── organization/
    │   │           ├── branches/
    │   │           ├── departments/
    │   │           ├── employees/
    │   │           ├── guests/
    │   │           ├── rooms/
    │   │           ├── roomtypes/
    │   │           ├── reservations/
    │   │           ├── stays/
    │   │           ├── housekeeping/
    │   │           ├── maintenance/
    │   │           ├── restaurants/
    │   │           ├── tables/
    │   │           ├── menu/
    │   │           ├── orders/
    │   │           ├── kitchen/
    │   │           ├── inventory/
    │   │           ├── suppliers/
    │   │           ├── procurement/
    │   │           ├── billing/
    │   │           ├── payments/
    │   │           ├── taxes/
    │   │           ├── discounts/
    │   │           ├── loyalty/
    │   │           ├── notifications/
    │   │           ├── reports/
    │   │           ├── dashboard/
    │   │           ├── audit/
    │   │           └── settings/
    │   │
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-test.yml
    │       ├── application-prod.yml
    │       │
    │       └── db/
    │           └── migration/
    │
    └── test/
        └── java/
            └── com/
                └── hrms/
```

---

# 8. Application Configuration

Base configuration:

```yaml
spring:
  application:
    name: hrms-backend

  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

  jackson:
    time-zone: Africa/Nairobi

server:
  port: ${SERVER_PORT:8080}

app:
  timezone: Africa/Nairobi
  currency: KES
  api:
    version: v1
```

---

# 9. Database Configuration

PostgreSQL configuration:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate

    open-in-view: false

    properties:
      hibernate:
        format_sql: true
        jdbc:
          time_zone: Africa/Nairobi
```

The application must not use:

```yaml
ddl-auto: create
```

or:

```yaml
ddl-auto: update
```

in production.

Flyway is responsible for schema changes.

---

# 10. Flyway Configuration

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    validate-on-migrate: true
    baseline-on-migrate: false
```

Migration files:

```text
V001__create_extensions.sql
V002__create_organizations.sql
...
V054__seed_initial_data.sql
```

The backend must start only after successful database migration.

---

# 11. Core Domain Model

Major entities:

```text
Organization
    │
    └── Branch
          │
          ├── Department
          ├── Employee
          ├── Room
          ├── Reservation
          ├── Restaurant
          └── Inventory

User
 ├── Role
 └── Permission

Guest
 ├── Reservation
 ├── Stay
 └── LoyaltyAccount

RoomType
 └── Room

Reservation
 ├── Guest
 ├── Room
 └── Stay

Restaurant
 ├── RestaurantTable
 ├── MenuCategory
 └── MenuItem

Order
 ├── OrderItem
 └── KitchenTicket

InventoryItem
 ├── InventoryTransaction
 └── SupplierItem

PurchaseOrder
 └── PurchaseOrderItem

Invoice
 ├── InvoiceItem
 └── Payment
```

---

# 12. Entity Implementation Standards

Every entity should follow consistent standards.

Example:

```java
@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String roomNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    @Version
    private Long version;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
```

Primary keys:

```java
UUID
```

Money:

```java
BigDecimal
```

Dates:

```java
LocalDate
```

Times:

```java
LocalTime
```

Timestamp:

```java
Instant
```

or:

```java
OffsetDateTime
```

depending on the domain requirement.

---

# 13. Organization Module

Package:

```text
organization/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Main entity:

```text
Organization
```

Responsibilities:

- create organization
- update organization
- retrieve organization
- manage organization status
- organization settings

Endpoints:

```text
POST   /api/v1/organizations
GET    /api/v1/organizations/{id}
PUT    /api/v1/organizations/{id}
DELETE /api/v1/organizations/{id}
```

---

# 14. Branch Module

Entity:

```text
Branch
```

Every operational record must be associated with a branch where appropriate.

Endpoints:

```text
POST   /api/v1/branches
GET    /api/v1/branches
GET    /api/v1/branches/{id}
PUT    /api/v1/branches/{id}
DELETE /api/v1/branches/{id}
```

Branch context should be resolved from:

- authenticated user's branch permissions
- request context
- explicit branch parameter where permitted

Users must never access branches for which they lack authorization.

---

# 15. User and Role Module

Entities:

```text
User
Role
Permission
RolePermission
UserBranch
```

User service responsibilities:

- create user
- update user
- disable user
- enable user
- assign roles
- assign branches
- reset password
- retrieve current user

Password must never be stored in plaintext.

User entity example:

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private boolean enabled;

    private boolean accountNonLocked;

    private Instant createdAt;

    private Instant updatedAt;
}
```

---

# 16. Employee Module

Employee management includes:

- employee records
- department
- job title
- employment status
- branch
- contact details
- user account association

Endpoints:

```text
POST   /api/v1/employees
GET    /api/v1/employees
GET    /api/v1/employees/{id}
PUT    /api/v1/employees/{id}
DELETE /api/v1/employees/{id}
```

---

# 17. Guest Module

Guest management includes:

- guest registration
- contact information
- identification details
- nationality
- guest history
- preferences
- reservation history

Endpoints:

```text
POST /api/v1/guests
GET  /api/v1/guests
GET  /api/v1/guests/{id}
PUT  /api/v1/guests/{id}
GET  /api/v1/guests/{id}/reservations
GET  /api/v1/guests/{id}/stays
```

Guest information must be handled according to applicable privacy and data-protection requirements.

---

# 18. Room Management Module

Components:

```text
RoomType
Room
Amenity
RoomTypeAmenity
```

Room statuses:

```text
AVAILABLE
OCCUPIED
DIRTY
CLEANING
OUT_OF_ORDER
MAINTENANCE
RESERVED
```

Room service responsibilities:

- create rooms
- update rooms
- assign room type
- change room status
- retrieve available rooms
- retrieve room history

---

# 19. Reservation Module

Reservation statuses:

```text
PENDING
CONFIRMED
CANCELLED
CHECKED_IN
COMPLETED
NO_SHOW
```

Reservation service responsibilities:

```text
check availability
create reservation
confirm reservation
modify reservation
cancel reservation
calculate accommodation cost
calculate taxes
apply discounts
assign room
```

Endpoints:

```text
POST /api/v1/reservations
GET  /api/v1/reservations
GET  /api/v1/reservations/{id}
PUT  /api/v1/reservations/{id}
POST /api/v1/reservations/{id}/confirm
POST /api/v1/reservations/{id}/cancel
GET  /api/v1/reservations/availability
```

---

# 20. Stay and Front Desk Module

A stay represents the actual hotel occupancy.

Check-in:

```text
Reservation
     ↓
Validate reservation
     ↓
Verify guest
     ↓
Verify room
     ↓
Create Stay
     ↓
Set room OCCUPIED
     ↓
Create audit record
```

Check-out:

```text
Stay
 ↓
Calculate charges
 ↓
Apply taxes
 ↓
Apply discounts
 ↓
Calculate balance
 ↓
Collect payment
 ↓
Generate invoice
 ↓
Close stay
 ↓
Room → DIRTY
```

Endpoints:

```text
POST /api/v1/stays/check-in
POST /api/v1/stays/{id}/check-out
GET  /api/v1/stays
GET  /api/v1/stays/{id}
```

---

# 21. Housekeeping Module

Housekeeping tasks include:

```text
ROOM_CLEANING
ROOM_INSPECTION
LINEN_CHANGE
AMENITY_REPLENISHMENT
DEEP_CLEANING
```

Task statuses:

```text
PENDING
ASSIGNED
IN_PROGRESS
COMPLETED
CANCELLED
```

Endpoints:

```text
POST /api/v1/housekeeping/tasks
GET  /api/v1/housekeeping/tasks
PUT  /api/v1/housekeeping/tasks/{id}
POST /api/v1/housekeeping/tasks/{id}/complete
```

Room status changes must be synchronized with housekeeping operations.

---

# 22. Maintenance Module

Maintenance request lifecycle:

```text
OPEN
ASSIGNED
IN_PROGRESS
RESOLVED
CLOSED
CANCELLED
```

The service should support:

- issue reporting
- priority
- assignment
- maintenance notes
- resolution
- room blocking

---

# 23. Restaurant Module

Entities:

```text
Restaurant
RestaurantTable
```

Restaurant service:

- create restaurant
- manage restaurant status
- create tables
- update table status
- assign orders to tables

Table statuses:

```text
AVAILABLE
OCCUPIED
RESERVED
CLEANING
OUT_OF_SERVICE
```

---

# 24. Menu Module

Entities:

```text
MenuCategory
MenuItem
MenuItemIngredient
```

Menu item fields include:

```text
name
description
price
category
availability
tax
preparation_time
```

Endpoints:

```text
POST /api/v1/menu/categories
GET  /api/v1/menu/categories

POST /api/v1/menu/items
GET  /api/v1/menu/items
GET  /api/v1/menu/items/{id}
PUT  /api/v1/menu/items/{id}
DELETE /api/v1/menu/items/{id}
```

---

# 25. Order and POS Module

Order lifecycle:

```text
DRAFT
PLACED
CONFIRMED
PREPARING
READY
SERVED
COMPLETED
CANCELLED
```

Order creation:

```text
Waiter
 ↓
Create Order
 ↓
Add Items
 ↓
Calculate Subtotal
 ↓
Calculate Tax
 ↓
Apply Discount
 ↓
Calculate Total
 ↓
Send Kitchen Ticket
```

Order totals must always be calculated server-side.

The frontend must never be trusted to provide the final amount.

---

# 26. Kitchen Module

Kitchen ticket statuses:

```text
PENDING
ACCEPTED
PREPARING
READY
SERVED
CANCELLED
```

Kitchen workflow:

```text
Order
 ↓
Kitchen Ticket
 ↓
Kitchen Queue
 ↓
Accept
 ↓
Prepare
 ↓
Ready
 ↓
Serve
```

Endpoints:

```text
GET  /api/v1/kitchen/tickets
POST /api/v1/kitchen/tickets/{id}/accept
POST /api/v1/kitchen/tickets/{id}/start
POST /api/v1/kitchen/tickets/{id}/ready
POST /api/v1/kitchen/tickets/{id}/serve
```

---

# 27. Inventory Module

Inventory responsibilities:

- item management
- stock levels
- stock receipts
- stock issues
- wastage
- adjustments
- stock transfers
- stock alerts

Inventory transaction types:

```text
OPENING_BALANCE
RECEIPT
ISSUE
SALE
WASTAGE
ADJUSTMENT
TRANSFER_IN
TRANSFER_OUT
RETURN
```

Stock should not be directly changed without an inventory transaction.

Example:

```text
Current Stock
=
Opening Stock
+
Receipts
+
Transfers In
+
Adjustments
-
Issues
-
Sales
-
Wastage
-
Transfers Out
```

---

# 28. Supplier and Procurement Module

Entities:

```text
Supplier
SupplierItem
PurchaseOrder
PurchaseOrderItem
GoodsReceipt
GoodsReceiptItem
```

Purchase order lifecycle:

```text
DRAFT
PENDING_APPROVAL
APPROVED
SENT
PARTIALLY_RECEIVED
RECEIVED
CANCELLED
```

Workflow:

```text
Inventory Requirement
       ↓
Purchase Order
       ↓
Approval
       ↓
Supplier
       ↓
Delivery
       ↓
Goods Receipt
       ↓
Inventory Transaction
```

---

# 29. Billing Module

Entities:

```text
Invoice
InvoiceItem
```

Invoice statuses:

```text
DRAFT
ISSUED
PARTIALLY_PAID
PAID
VOID
CANCELLED
```

Charges can originate from:

```text
ROOM
RESTAURANT
ROOM_SERVICE
LAUNDRY
OTHER
```

The billing service must calculate:

```text
Subtotal
+
Tax
-
Discount
=
Total
```

---

# 30. Payment Module

Entities:

```text
Payment
PaymentTransaction
Refund
```

Payment methods may include:

```text
CASH
CARD
MPESA
BANK_TRANSFER
OTHER
```

Payment statuses:

```text
PENDING
PROCESSING
COMPLETED
FAILED
CANCELLED
REFUNDED
```

Payment endpoints:

```text
POST /api/v1/payments
GET  /api/v1/payments
GET  /api/v1/payments/{id}
POST /api/v1/payments/{id}/refund
```

Payment processing must be transaction-safe.

---

# 31. Tax and Discount Module

Tax entity:

```text
Tax
```

Tax types:

```text
PERCENTAGE
FIXED
```

Discount types:

```text
PERCENTAGE
FIXED
```

Discount validation must occur server-side.

Example:

```java
BigDecimal discountAmount =
        subtotal.multiply(discountRate)
                .divide(BigDecimal.valueOf(100));
```

All financial calculations must use:

```java
BigDecimal
```

Never use:

```java
double
```

for monetary values.

---

# 32. Loyalty Module

Entities:

```text
LoyaltyAccount
LoyaltyTransaction
```

Operations:

```text
earn points
redeem points
adjust points
view balance
```

Example:

```text
Eligible Transaction
        ↓
Calculate Points
        ↓
Create Loyalty Transaction
        ↓
Update Balance
```

Every points change must be recorded.

---

# 33. Notification Module

Notification channels:

```text
IN_APP
EMAIL
SMS
```

Notification statuses:

```text
PENDING
SENT
FAILED
READ
```

Examples:

```text
Reservation confirmation
Booking cancellation
Payment receipt
Low-stock warning
Maintenance assignment
Password reset
```

Notifications should be asynchronous where possible.

---

# 34. Audit Module

Every important business operation should create an audit record.

Audit fields:

```text
user
action
entity
entity_id
old_value
new_value
ip_address
user_agent
timestamp
```

Examples:

```text
USER_CREATED
USER_DISABLED
RESERVATION_CREATED
RESERVATION_CANCELLED
CHECK_IN
CHECK_OUT
PAYMENT_CREATED
REFUND_CREATED
INVENTORY_ADJUSTED
PURCHASE_ORDER_APPROVED
```

Audit records must not be casually deleted.

---

# 35. Settings Module

System settings include:

```text
hotel name
currency
timezone
tax configuration
reservation policies
check-in time
check-out time
notification settings
receipt settings
```

Settings should be stored in the database where appropriate.

---

# 36. JWT Authentication

Authentication flow:

```text
Login
 ↓
Validate username/password
 ↓
Load user
 ↓
Verify password
 ↓
Generate JWT
 ↓
Return token
```

Request flow:

```text
Frontend
 ↓
Authorization: Bearer JWT
 ↓
JWT Filter
 ↓
Validate Token
 ↓
Load User
 ↓
Set SecurityContext
 ↓
Controller
```

JWT payload should contain minimal claims such as:

```json
{
  "sub": "user-id",
  "username": "admin",
  "roles": [
    "ADMIN"
  ],
  "iat": 1720000000,
  "exp": 1720003600
}
```

Do not put sensitive information in JWT claims.

---

# 37. Authorization

Authorization must operate at two levels:

```text
Role
Permission
```

Example permissions:

```text
USER_READ
USER_CREATE
USER_UPDATE
USER_DELETE

RESERVATION_READ
RESERVATION_CREATE
RESERVATION_UPDATE
RESERVATION_CANCEL

ROOM_READ
ROOM_CREATE
ROOM_UPDATE

PAYMENT_READ
PAYMENT_CREATE
PAYMENT_REFUND

INVENTORY_READ
INVENTORY_CREATE
INVENTORY_ADJUST
```

Controller example:

```java
@PreAuthorize("hasAuthority('RESERVATION_CREATE')")
@PostMapping
public ResponseEntity<?> createReservation(...) {
    ...
}
```

Authorization must also enforce branch-level access.

---

# 38. Password Security

Passwords must be hashed.

Recommended approach:

```text
Argon2id
```

or another modern password hashing mechanism supported by the selected Spring Security version.

Never:

```text
store plaintext passwords
log passwords
return passwords through API
store passwords in JWT
```

Password reset tokens must:

- expire
- be single-use
- be securely generated
- not expose the password

---

# 39. DTO Design

Entities should not be returned directly from controllers.

Use DTOs.

Example:

```java
public record CreateGuestRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email String email,
        String phoneNumber
) {}
```

Response:

```java
public record GuestResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {}
```

Benefits:

- prevents accidental data exposure
- separates API from database structure
- simplifies versioning
- improves validation

---

# 40. Mapper Layer

Mappings should be centralized.

Example:

```java
@Component
public class GuestMapper {

    public GuestResponse toResponse(Guest guest) {
        return new GuestResponse(
            guest.getId(),
            guest.getFirstName(),
            guest.getLastName(),
            guest.getEmail(),
            guest.getPhoneNumber()
        );
    }
}
```

MapStruct may be introduced for larger modules.

---

# 41. Repository Layer

Repositories extend Spring Data interfaces.

Example:

```java
@Repository
public interface GuestRepository
        extends JpaRepository<Guest, UUID> {

    Page<Guest> findByLastNameContainingIgnoreCase(
            String lastName,
            Pageable pageable
    );
}
```

Repositories must not contain business logic.

---

# 42. Service Layer

Services contain business rules.

Example:

```java
@Service
@RequiredArgsConstructor
@Transactional
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestResponse create(CreateGuestRequest request) {

        Guest guest = Guest.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .build();

        Guest saved = guestRepository.save(guest);

        return mapper.toResponse(saved);
    }
}
```

Services should:

- validate business rules
- coordinate repositories
- manage transactions
- create audit events
- publish domain events where appropriate

---

# 43. Controller Layer

Controllers expose HTTP endpoints.

Example:

```java
@RestController
@RequestMapping("/api/v1/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @PostMapping
    public ResponseEntity<GuestResponse> create(
            @Valid @RequestBody CreateGuestRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guestService.create(request));
    }
}
```

Controllers should remain thin.

Avoid:

```java
controller → repository
```

Use:

```text
controller → service → repository
```

---

# 44. Validation

Use Jakarta Bean Validation.

Examples:

```java
@NotBlank
@Email
@Size
@Pattern
@NotNull
@Positive
@PositiveOrZero
@Past
@Future
```

Example:

```java
public record CreateRoomRequest(

        @NotBlank
        String roomNumber,

        @NotNull
        UUID roomTypeId
) {}
```

Business validation must still happen inside services.

Example:

```text
Bean Validation
     ↓
Business Validation
     ↓
Database Constraints
```

---

# 45. Exception Handling

Create centralized exception handling.

Structure:

```text
common/
└── exception/
    ├── GlobalExceptionHandler.java
    ├── ResourceNotFoundException.java
    ├── BusinessException.java
    ├── UnauthorizedException.java
    ├── ForbiddenException.java
    └── ConflictException.java
```

Example:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(...);
    }
}
```

---

# 46. API Response Format

Successful response:

```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "name": "Example"
  }
}
```

Error response:

```json
{
  "success": false,
  "error": {
    "code": "ROOM_NOT_AVAILABLE",
    "message": "The selected room is unavailable.",
    "details": []
  }
}
```

Validation error:

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Request validation failed.",
    "details": [
      {
        "field": "email",
        "message": "Invalid email address."
      }
    ]
  }
}
```

---

# 47. Pagination and Filtering

List endpoints should support:

```text
page
size
sort
direction
search
```

Example:

```text
GET /api/v1/guests?page=0&size=20&sort=lastName,asc
```

Spring Data:

```java
Pageable pageable =
        PageRequest.of(
            page,
            size,
            Sort.by("lastName").ascending()
        );
```

Default:

```text
page = 0
size = 20
maximum size = 100
```

---

# 48. Transactions

Use Spring transactions.

Example:

```java
@Transactional
public Stay checkIn(...) {
    ...
}
```

Financial and inventory operations must be transactional.

Examples:

```text
Payment
 + Invoice update
 + Audit

Goods Receipt
 + Inventory transaction
 + Stock update

Check-in
 + Stay
 + Room status
 + Reservation status
 + Audit
```

If one required operation fails, the transaction should roll back.

---

# 49. Reservation Business Logic

Reservation creation:

```text
Receive request
       ↓
Validate dates
       ↓
Validate guest
       ↓
Validate room
       ↓
Check room availability
       ↓
Calculate rate
       ↓
Calculate taxes
       ↓
Apply discount
       ↓
Create reservation
       ↓
Create reservation guests
       ↓
Create audit
       ↓
Commit
```

Availability must consider overlapping reservations.

Conceptually:

```text
requested_check_in < existing_check_out
AND
requested_check_out > existing_check_in
```

Room availability must be checked again inside the transaction immediately before creating the reservation.

Database constraints and appropriate locking strategies should be used to reduce race conditions.

---

# 50. Check-In Business Logic

```text
Find reservation
       ↓
Verify status
       ↓
Verify guest
       ↓
Verify room
       ↓
Verify room status
       ↓
Create stay
       ↓
Reservation → CHECKED_IN
       ↓
Room → OCCUPIED
       ↓
Audit event
```

Reject check-in when:

```text
reservation cancelled
reservation already completed
room unavailable
guest information invalid
```

---

# 51. Check-Out Business Logic

```text
Find active stay
       ↓
Collect accommodation charges
       ↓
Collect restaurant charges
       ↓
Collect additional charges
       ↓
Calculate taxes
       ↓
Apply discounts
       ↓
Calculate balance
       ↓
Verify payment
       ↓
Generate invoice
       ↓
Close stay
       ↓
Room → DIRTY
       ↓
Create housekeeping task
       ↓
Audit
```

---

# 52. Restaurant Order Business Logic

Create order:

```text
Validate restaurant
       ↓
Validate table
       ↓
Validate menu items
       ↓
Check menu availability
       ↓
Calculate item totals
       ↓
Calculate subtotal
       ↓
Calculate tax
       ↓
Apply discount
       ↓
Calculate total
       ↓
Save order
       ↓
Create kitchen ticket
       ↓
Commit
```

Never trust frontend-calculated:

```text
subtotal
tax
discount
total
```

The backend recalculates all amounts.

---

# 53. Inventory Business Logic

Inventory operations must create transaction records.

Example stock receipt:

```text
Goods Receipt
      ↓
Validate item
      ↓
Validate quantity
      ↓
Create inventory transaction
      ↓
Update stock
      ↓
Audit
```

Stock adjustment:

```text
Current Stock
      ↓
Adjustment Request
      ↓
Authorization
      ↓
Create Transaction
      ↓
Update Stock
      ↓
Audit
```

Inventory adjustments require appropriate permission.

---

# 54. Procurement Business Logic

Purchase order:

```text
Create Draft
     ↓
Submit
     ↓
Approval
     ↓
Approved
     ↓
Send Supplier
     ↓
Receive Goods
     ↓
Goods Receipt
     ↓
Inventory Updated
```

Only authorized users may approve purchase orders.

---

# 55. Billing Business Logic

Billing engine:

```text
Charges
  ↓
Subtotal
  ↓
Tax Rules
  ↓
Discount Rules
  ↓
Total
```

Use:

```java
BigDecimal
```

with explicit rounding rules.

Example:

```java
amount.setScale(2, RoundingMode.HALF_UP);
```

The final rounding strategy should be defined centrally and applied consistently.

---

# 56. Payment Idempotency

Payment creation must support idempotency.

Client sends:

```text
Idempotency-Key: unique-request-id
```

Backend:

```text
Receive request
       ↓
Check key
       ↓
Already processed?
    ↙       ↘
  YES       NO
   ↓         ↓
Return      Process
existing       ↓
result      Save key
             ↓
           Payment
```

This prevents accidental duplicate payments caused by retries.

---

# 57. Audit Logging

Audit logging can be implemented using:

```text
Service calls
+
Spring application events
+
AOP where appropriate
```

Important actions must always produce audit events.

Example:

```java
auditService.record(
    AuditAction.RESERVATION_CREATED,
    reservation.getId(),
    currentUser
);
```

Audit failures should be handled carefully so that critical business transactions do not silently lose required audit records.

---

# 58. Notifications

Notification service interface:

```java
public interface NotificationService {

    void sendInApp(...);

    void sendEmail(...);

    void sendSms(...);
}
```

Implementations:

```text
InAppNotificationService
EmailNotificationService
SmsNotificationService
```

The system should support asynchronous delivery.

Example:

```text
Business Event
      ↓
Notification Event
      ↓
Queue / Async Executor
      ↓
Notification Provider
```

---

# 59. OpenAPI / Swagger

API documentation should be generated automatically.

Configuration example:

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hrmsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel & Restaurant Management System API")
                        .version("1.0")
                        .description(
                            "REST API for hotel and restaurant operations"
                        ));
    }
}
```

Swagger UI should be available in development environments.

Typical path:

```text
/swagger-ui/index.html
```

OpenAPI JSON:

```text
/v3/api-docs
```

Authentication should be documented using:

```text
Bearer JWT
```

---

# 60. CORS

Only trusted frontend origins should be allowed.

Development:

```text
http://localhost:3000
```

Production:

```text
https://your-production-domain
```

Never use unrestricted:

```text
*
```

for credentialed production authentication.

---

# 61. Security Configuration

Spring Security should configure:

```text
JWT authentication
CORS
CSRF strategy
session policy
endpoint authorization
password encoding
security headers
```

Stateless API:

```java
.sessionManagement(session ->
    session.sessionCreationPolicy(
        SessionCreationPolicy.STATELESS
    )
)
```

Public endpoints:

```text
POST /api/v1/auth/login
POST /api/v1/auth/refresh
POST /api/v1/auth/forgot-password
POST /api/v1/auth/reset-password
```

Protected endpoints:

```text
/api/v1/**
```

Health endpoints may be exposed selectively.

---

# 62. Logging

Use:

```text
SLF4J
Logback
```

Logging levels:

```text
ERROR
WARN
INFO
DEBUG
TRACE
```

Production default:

```text
INFO
```

Never log:

```text
passwords
JWT tokens
payment credentials
security secrets
full identification documents
```

Use structured logging where practical.

Example:

```text
timestamp
request_id
user_id
branch_id
method
endpoint
status
duration_ms
```

---

# 63. Environment Configuration

Use environment variables.

Example:

```env
SPRING_PROFILES_ACTIVE=dev

DATABASE_URL=jdbc:postgresql://localhost:5432/hrms
DATABASE_USERNAME=hrms_user
DATABASE_PASSWORD=change_me

JWT_SECRET=change_me
JWT_EXPIRATION=3600000

FRONTEND_URL=http://localhost:3000

MAIL_HOST=
MAIL_PORT=
MAIL_USERNAME=
MAIL_PASSWORD=
```

Secrets must not be committed to Git.

Provide:

```text
.env.example
```

but not:

```text
.env
```

in the repository.

---

# 64. Testing Strategy

Testing levels:

```text
Unit Tests
Integration Tests
Repository Tests
Controller Tests
Security Tests
End-to-End Tests
```

Target important business logic first.

Priority:

```text
Authentication
Reservations
Availability
Check-in
Check-out
Payments
Inventory
Procurement
Orders
Billing
Authorization
```

---

# 65. Integration Testing

Use:

```text
@SpringBootTest
```

for full application integration.

Controller tests:

```text
@WebMvcTest
```

Repository tests:

```text
@DataJpaTest
```

Security tests should verify:

```text
unauthenticated access
authenticated access
role access
permission access
branch restrictions
```

---

# 66. Testcontainers

Use PostgreSQL Testcontainers for integration tests.

Concept:

```text
JUnit
 ↓
Testcontainers
 ↓
PostgreSQL Container
 ↓
Flyway
 ↓
Spring Boot
 ↓
Tests
```

This ensures tests run against PostgreSQL rather than an unrelated in-memory database.

Example dependency:

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

---

# 67. Backend Error Codes

Standard business error codes:

```text
AUTH_INVALID_CREDENTIALS
AUTH_TOKEN_EXPIRED
AUTH_ACCESS_DENIED

RESOURCE_NOT_FOUND
VALIDATION_ERROR
DUPLICATE_RESOURCE
INVALID_REQUEST

ROOM_NOT_AVAILABLE
ROOM_ALREADY_OCCUPIED

RESERVATION_NOT_FOUND
RESERVATION_ALREADY_CANCELLED
RESERVATION_INVALID_STATUS
RESERVATION_DATE_CONFLICT

CHECK_IN_NOT_ALLOWED
CHECK_OUT_NOT_ALLOWED

ORDER_NOT_FOUND
ORDER_INVALID_STATUS

MENU_ITEM_UNAVAILABLE

INSUFFICIENT_STOCK
INVENTORY_ADJUSTMENT_NOT_ALLOWED

PAYMENT_FAILED
PAYMENT_ALREADY_PROCESSED
PAYMENT_DUPLICATE_REQUEST

INVOICE_NOT_FOUND
INVOICE_ALREADY_PAID

PURCHASE_ORDER_NOT_FOUND
PURCHASE_ORDER_NOT_APPROVED

INTERNAL_SERVER_ERROR
```

---

# 68. Health Checks

Use Spring Boot Actuator.

Endpoints:

```text
/actuator/health
```

Possible checks:

```text
database
application
disk
```

Production health information should not expose sensitive internals.

---

# 69. Performance Requirements

Initial performance targets:

```text
Simple API request:
< 500 ms

Typical database query:
< 200 ms

Authentication:
< 1 second

Standard list endpoint:
< 1 second
```

These are engineering targets rather than guarantees and should be validated under realistic load.

Optimization techniques:

```text
database indexes
pagination
query optimization
lazy loading
connection pooling
caching
async processing
DTO projections
```

Avoid unnecessary:

```text
N+1 queries
large SELECT *
queries
unbounded result sets
```

---

# 70. Deployment Requirements

Backend should be packaged as:

```text
hrms-backend.jar
```

Build:

```bash
./mvnw clean package
```

Run:

```bash
java -jar target/hrms-backend.jar
```

Docker image:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/hrms-backend.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Production architecture:

```text
Internet
   │
   ▼
Reverse Proxy / Load Balancer
   │
   ▼
Spring Boot
   │
   ├── PostgreSQL
   ├── Redis
   ├── Email Provider
   └── SMS Provider
```

---

# 71. CI/CD

Recommended pipeline:

```text
Git Push
   ↓
Build
   ↓
Unit Tests
   ↓
Integration Tests
   ↓
Static Analysis
   ↓
Package
   ↓
Build Docker Image
   ↓
Security Scan
   ↓
Deploy Staging
   ↓
Smoke Tests
   ↓
Production Approval
   ↓
Production Deployment
```

Pipeline must fail if:

```text
compilation fails
tests fail
migration validation fails
security checks fail
```

---

# 72. Implementation Phases

## Phase 1 — Project Foundation

Implement:

```text
Spring Boot
Maven
PostgreSQL
Flyway
configuration
logging
exception handling
API response
```

---

## Phase 2 — Security

Implement:

```text
Spring Security
JWT
password hashing
users
roles
permissions
branch authorization
```

---

## Phase 3 — Organization

Implement:

```text
organizations
branches
departments
employees
```

---

## Phase 4 — Hotel Operations

Implement:

```text
guests
room types
rooms
amenities
reservations
stays
```

---

## Phase 5 — Operations

Implement:

```text
housekeeping
maintenance
```

---

## Phase 6 — Restaurant

Implement:

```text
restaurants
tables
menu
orders
kitchen
```

---

## Phase 7 — Inventory

Implement:

```text
inventory
suppliers
procurement
goods receipts
```

---

## Phase 8 — Finance

Implement:

```text
taxes
discounts
invoices
payments
refunds
```

---

## Phase 9 — Supporting Services

Implement:

```text
loyalty
notifications
audit
settings
```

---

## Phase 10 — Reporting

Implement:

```text
dashboard
occupancy reports
revenue reports
restaurant reports
inventory reports
payment reports
```

---

## Phase 11 — Testing

Complete:

```text
unit testing
integration testing
security testing
API testing
performance testing
```

---

## Phase 12 — Deployment

Complete:

```text
Docker
CI/CD
environment configuration
production database
monitoring
backup
deployment
```

---

# 73. Definition of Done

A backend feature is considered complete when:

- entity exists
- database migration exists
- repository exists
- DTOs exist
- mapper exists
- service exists
- controller exists
- validation exists
- authorization exists
- error handling exists
- audit requirements are handled
- tests exist
- API documentation exists
- integration test passes

For financial functionality:

- transaction boundaries are verified
- rounding is tested
- duplicate processing is prevented
- audit records exist
- authorization is tested

For inventory:

- every stock-changing operation creates a transaction
- stock calculations are tested
- negative stock rules are enforced
- audit records are generated

For reservations:

- availability is checked
- date conflicts are handled
- concurrent booking behavior is tested
- status transitions are enforced

---

# 74. Backend Implementation Checklist

## Foundation

- [ ] Create Spring Boot project
- [ ] Configure Maven
- [ ] Configure Java 21
- [ ] Configure PostgreSQL
- [ ] Configure Flyway
- [ ] Configure application profiles
- [ ] Configure logging
- [ ] Configure Actuator
- [ ] Configure global exception handling

## Security

- [ ] Configure Spring Security
- [ ] Implement JWT
- [ ] Implement password hashing
- [ ] Implement login
- [ ] Implement refresh tokens
- [ ] Implement logout/revocation strategy
- [ ] Implement roles
- [ ] Implement permissions
- [ ] Implement branch authorization
- [ ] Configure CORS
- [ ] Configure security headers

## Organization

- [ ] Organizations
- [ ] Branches
- [ ] Departments
- [ ] Employees

## Hotel

- [ ] Guests
- [ ] Room types
- [ ] Amenities
- [ ] Rooms
- [ ] Reservations
- [ ] Stays

## Operations

- [ ] Housekeeping
- [ ] Maintenance

## Restaurant

- [ ] Restaurants
- [ ] Tables
- [ ] Menu categories
- [ ] Menu items
- [ ] Orders
- [ ] Kitchen tickets

## Inventory

- [ ] Inventory categories
- [ ] Units
- [ ] Inventory items
- [ ] Inventory transactions
- [ ] Suppliers
- [ ] Supplier items
- [ ] Purchase orders
- [ ] Goods receipts

## Finance

- [ ] Taxes
- [ ] Discounts
- [ ] Invoices
- [ ] Payments
- [ ] Payment transactions
- [ ] Refunds

## Supporting Services

- [ ] Loyalty
- [ ] Notifications
- [ ] Audit logs
- [ ] Settings

## API

- [ ] REST endpoints
- [ ] DTOs
- [ ] validation
- [ ] pagination
- [ ] filtering
- [ ] sorting
- [ ] error responses
- [ ] OpenAPI
- [ ] Swagger UI

## Testing

- [ ] Unit tests
- [ ] Repository tests
- [ ] Controller tests
- [ ] Security tests
- [ ] Integration tests
- [ ] Testcontainers
- [ ] Reservation concurrency tests
- [ ] Payment idempotency tests
- [ ] Inventory transaction tests

## Deployment

- [ ] Dockerfile
- [ ] Docker Compose
- [ ] Environment variables
- [ ] CI/CD
- [ ] Production database
- [ ] Monitoring
- [ ] Backup
- [ ] Health checks

---

# FINAL BACKEND ARCHITECTURE

```text
                         ┌───────────────────────┐
                         │     NEXT.JS WEB APP   │
                         └───────────┬───────────┘
                                     │
                                  HTTPS
                                     │
                                     ▼
                         ┌───────────────────────┐
                         │    SPRING SECURITY    │
                         │     JWT AUTHENTICATION │
                         └───────────┬───────────┘
                                     │
                                     ▼
                         ┌───────────────────────┐
                         │      CONTROLLERS      │
                         └───────────┬───────────┘
                                     │
                                     ▼
                         ┌───────────────────────┐
                         │        DTOs           │
                         │     VALIDATION        │
                         └───────────┬───────────┘
                                     │
                                     ▼
                         ┌───────────────────────┐
                         │       SERVICES        │
                         │   BUSINESS LOGIC      │
                         └───────────┬───────────┘
                                     │
                    ┌────────────────┼────────────────┐
                    │                │                │
                    ▼                ▼                ▼
              Repositories       Events          Audit
                    │                │
                    └────────┬───────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │    PostgreSQL    │
                    │                  │
                    │ Organizations   │
                    │ Users           │
                    │ Guests          │
                    │ Rooms           │
                    │ Reservations    │
                    │ Stays           │
                    │ Restaurant      │
                    │ Orders          │
                    │ Inventory       │
                    │ Procurement     │
                    │ Billing         │
                    │ Payments        │
                    │ Audit           │
                    └──────────────────┘
```

---

# PROJECT DOCUMENT STATUS

```text
DOCUMENT 1   ✓ Complete Requirements Specification
DOCUMENT 2   ✓ Database ERD + Complete Table Definitions
DOCUMENT 3   ✓ Complete API Specification
DOCUMENT 4   ✓ UI/UX Screen Map + Navigation
DOCUMENT 5   ✓ System Architecture Specification
DOCUMENT 6   ✓ Backend Project Structure
DOCUMENT 7   ✓ Frontend Project Structure
DOCUMENT 8   ✓ PostgreSQL Migration Scripts
DOCUMENT 9   ✓ Spring Boot Backend Implementation
DOCUMENT 10  → Next.js Frontend Implementation
DOCUMENT 11  → Testing Strategy
DOCUMENT 12  → Deployment Specification
```

---

# NEXT IMPLEMENTATION STEP

The next implementation document should be:

```text
NEXT_JS_FRONTEND_IMPLEMENTATION.md
```

It should define the actual Next.js implementation, including:

```text
Next.js App Router
TypeScript
Tailwind CSS
UI component architecture
authentication
JWT integration
API client
React Query / data fetching
state management
forms
validation
dashboard
reservations
front desk
rooms
housekeeping
restaurant POS
kitchen
inventory
procurement
billing
payments
reports
role-based UI
permission-based navigation
responsive layouts
loading states
error states
notifications
charts
tables
deployment
```

This will connect directly to the Spring Boot API defined in this document.