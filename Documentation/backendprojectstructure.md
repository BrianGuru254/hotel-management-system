# HOTEL & RESTAURANT MANAGEMENT SYSTEM

## Backend Project Structure Specification

**Project:** Hotel & Restaurant Management System (HRMS)  
**Document:** Backend Project Structure  
**Version:** 1.0  
**Status:** Implementation-Ready  
**Date:** September 2026  
**Backend:** Java + Spring Boot  
**Database:** PostgreSQL  
**API:** REST + JSON  
**Authentication:** JWT  
**Build Tool:** Maven  

---

# Table of Contents

1. [Document Purpose](#1-document-purpose)
2. [Backend Objectives](#2-backend-objectives)
3. [Backend Technology Stack](#3-backend-technology-stack)
4. [Backend Architecture](#4-backend-architecture)
5. [Complete Backend Directory Structure](#5-complete-backend-directory-structure)
6. [Root Project Files](#6-root-project-files)
7. [Java Package Structure](#7-java-package-structure)
8. [Configuration Package](#8-configuration-package)
9. [Common Package](#9-common-package)
10. [Authentication Module](#10-authentication-module)
11. [User Management Module](#11-user-management-module)
12. [Roles and Permissions](#12-roles-and-permissions)
13. [Organization and Branches](#13-organization-and-branches)
14. [Employee Management](#14-employee-management)
15. [Guest Management](#15-guest-management)
16. [Room Management](#16-room-management)
17. [Reservation Management](#17-reservation-management)
18. [Stay Management](#18-stay-management)
19. [Housekeeping](#19-housekeeping)
20. [Maintenance](#20-maintenance)
21. [Restaurant Management](#21-restaurant-management)
22. [Menu Management](#22-menu-management)
23. [Restaurant Orders](#23-restaurant-orders)
24. [Kitchen Management](#24-kitchen-management)
25. [Inventory Management](#25-inventory-management)
26. [Supplier Management](#26-supplier-management)
27. [Procurement](#27-procurement)
28. [Billing](#28-billing)
29. [Payments](#29-payments)
30. [Taxes and Discounts](#30-taxes-and-discounts)
31. [Loyalty Management](#31-loyalty-management)
32. [Notifications](#32-notifications)
33. [Reporting](#33-reporting)
34. [Dashboard](#34-dashboard)
35. [Audit Logging](#35-audit-logging)
36. [Settings](#36-settings)
37. [Database Migration Structure](#37-database-migration-structure)
38. [DTO Architecture](#38-dto-architecture)
39. [Entity Architecture](#39-entity-architecture)
40. [Repository Architecture](#40-repository-architecture)
41. [Service Architecture](#41-service-architecture)
42. [Controller Architecture](#42-controller-architecture)
43. [Security Architecture](#43-security-architecture)
44. [Exception Handling](#44-exception-handling)
45. [API Response Architecture](#45-api-response-architecture)
46. [Validation](#46-validation)
47. [Transaction Management](#47-transaction-management)
48. [Logging](#48-logging)
49. [Testing Structure](#49-testing-structure)
50. [Environment Configuration](#50-environment-configuration)
51. [Docker Structure](#51-docker-structure)
52. [API Documentation](#52-api-documentation)
53. [Coding Standards](#53-coding-standards)
54. [Module Dependency Rules](#54-module-dependency-rules)
55. [Backend Development Order](#55-backend-development-order)
56. [Definition of Done](#56-definition-of-done)

---

# 1. Document Purpose

This document defines the complete backend project structure for the Hotel & Restaurant Management System.

The purpose is to provide a clear and scalable structure before implementation begins.

The backend will provide:

- REST APIs
- Authentication
- Authorization
- Hotel operations
- Reservation management
- Guest management
- Room management
- Restaurant management
- Kitchen management
- Inventory management
- Procurement
- Billing
- Payments
- Reporting
- Notifications
- Auditing
- Administration
- Multi-branch support

The backend will serve the Next.js frontend through a versioned REST API.

---

# 2. Backend Objectives

The backend must be:

- Secure
- Modular
- Maintainable
- Testable
- Scalable
- Transaction-safe
- API-first
- Cloud-ready
- Database-driven
- Multi-branch capable

The backend must separate:

```text
HTTP Layer
     ↓
Business Logic
     ↓
Data Access
     ↓
Database
```

---

# 3. Backend Technology Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming language |
| Spring Boot | Application framework |
| Spring Web | REST APIs |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| PostgreSQL | Relational database |
| Flyway | Database migrations |
| Bean Validation | Input validation |
| Maven | Dependency/build management |
| OpenAPI | API documentation |
| JUnit | Unit testing |
| Mockito | Mocking |
| Spring Boot Test | Integration testing |
| Docker | Containerization |
| Git | Version control |

---

# 4. Backend Architecture

The backend follows a modular layered architecture.

```text
                         CLIENT
                           │
                           ▼
                    ┌──────────────┐
                    │   REST API   │
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
                    │ Controllers  │
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
                    │   Services   │
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
                    │ Repositories │
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
                    │  PostgreSQL  │
                    └──────────────┘
```

Cross-cutting services operate across modules:

```text
Security
Validation
Exception Handling
Logging
Auditing
Transactions
Notifications
File Management
Caching
```

---

# 5. Complete Backend Directory Structure

The recommended project structure is:

```text
hrms-backend/
│
├── pom.xml
├── README.md
├── .gitignore
├── .env.example
├── Dockerfile
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
│
├── docs/
│   ├── api/
│   ├── architecture/
│   └── database/
│
└── src/
    │
    ├── main/
    │   │
    │   ├── java/
    │   │   └── com/
    │       └── hrms/
    │           │
    │           ├── HrmsApplication.java
    │           │
    │           ├── config/
    │           │
    │           ├── common/
    │           │
    │           ├── auth/
    │           │
    │           ├── users/
    │           │
    │           ├── roles/
    │           │
    │           ├── permissions/
    │           │
    │           ├── organization/
    │           │
    │           ├── branches/
    │           │
    │           ├── employees/
    │           │
    │           ├── guests/
    │           │
    │           ├── roomtypes/
    │           │
    │           ├── rooms/
    │           │
    │           ├── reservations/
    │           │
    │           ├── stays/
    │           │
    │           ├── housekeeping/
    │           │
    │           ├── maintenance/
    │           │
    │           ├── restaurants/
    │           │
    │           ├── tables/
    │           │
    │           ├── menu/
    │           │
    │           ├── orders/
    │           │
    │           ├── kitchen/
    │           │
    │           ├── inventory/
    │           │
    │           ├── suppliers/
    │           │
    │           ├── procurement/
    │           │
    │           ├── billing/
    │           │
    │           ├── payments/
    │           │
    │           ├── taxes/
    │           │
    │           ├── discounts/
    │           │
    │           ├── loyalty/
    │           │
    │           ├── notifications/
    │           │
    │           ├── reports/
    │           │
    │           ├── dashboard/
    │           │
    │           ├── audit/
    │           │
    │           ├── files/
    │           │
    │           └── settings/
    │           │
    │           └── resources/
    │               │
    │               ├── application.yml
    │               ├── application-dev.yml
    │               ├── application-test.yml
    │               ├── application-prod.yml
    │               │
    │               └── db/
    │                   └── migration/
    │
    └── test/
        │
        └── java/
            └── com/
                └── hrms/
```

---

# 6. Root Project Files

## 6.1 `pom.xml`

The Maven configuration defines:

- Java version
- Spring Boot version
- dependencies
- plugins
- build configuration
- testing dependencies

Major dependencies include:

```text
spring-boot-starter-web
spring-boot-starter-security
spring-boot-starter-data-jpa
spring-boot-starter-validation
postgresql
flyway
jjwt
springdoc-openapi
lombok
junit
mockito
```

---

## 6.2 `README.md`

The README should contain:

- project description
- architecture
- requirements
- setup instructions
- environment configuration
- database setup
- development commands
- testing
- API documentation
- deployment instructions

---

# 7. Java Package Structure

The root Java package is:

```text
com.hrms
```

Main application:

```text
com.hrms.HrmsApplication
```

All modules exist below this package.

---

# 8. Configuration Package

Location:

```text
config/
```

Structure:

```text
config/
├── SecurityConfig.java
├── CorsConfig.java
├── DatabaseConfig.java
├── OpenApiConfig.java
├── JacksonConfig.java
├── WebConfig.java
├── JpaConfig.java
└── AuditConfig.java
```

Responsibilities:

### `SecurityConfig`

Controls:

- authentication
- authorization
- JWT filters
- protected routes
- password encoder

### `CorsConfig`

Controls allowed frontend origins.

### `DatabaseConfig`

Controls database-related configuration.

### `OpenApiConfig`

Configures Swagger/OpenAPI.

---

# 9. Common Package

The common package contains functionality shared across modules.

```text
common/
├── exception/
├── response/
├── validation/
├── security/
├── audit/
├── pagination/
├── mapper/
├── enums/
├── util/
└── constants/
```

Example:

```text
common/exception/
├── GlobalExceptionHandler.java
├── ResourceNotFoundException.java
├── BusinessException.java
├── UnauthorizedException.java
├── ForbiddenException.java
└── ConflictException.java
```

---

# 10. Authentication Module

Location:

```text
auth/
```

Structure:

```text
auth/
├── controller/
│   └── AuthController.java
│
├── service/
│   ├── AuthService.java
│   └── JwtService.java
│
├── repository/
│   └── RefreshTokenRepository.java
│
├── entity/
│   └── RefreshToken.java
│
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RefreshTokenRequest.java
│   ├── ForgotPasswordRequest.java
│   └── ResetPasswordRequest.java
│
└── security/
    ├── JwtAuthenticationFilter.java
    ├── CustomUserDetailsService.java
    └── AuthenticatedUser.java
```

Responsibilities:

- Login
- Logout
- JWT generation
- Refresh tokens
- Password recovery
- Current-user information
- Authentication validation

---

# 11. User Management Module

```text
users/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Create users
- Update users
- Activate/deactivate users
- Password management
- User profile
- User branch assignments

---

# 12. Roles and Permissions

## Roles

```text
roles/
├── controller/
├── service/
├── repository/
├── entity/
└── dto/
```

## Permissions

```text
permissions/
├── controller/
├── service/
├── repository/
├── entity/
└── dto/
```

Example roles:

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
HR_MANAGER
MAINTENANCE
AUDITOR
```

Authorization must be permission-based where appropriate rather than relying only on role names.

---

# 13. Organization and Branches

## Organization

```text
organization/
├── controller/
├── service/
├── repository/
├── entity/
└── dto/
```

## Branches

```text
branches/
├── controller/
├── service/
├── repository/
├── entity/
└── dto/
```

The structure supports multiple hotel branches.

```text
Organization
    │
    ├── Branch A
    ├── Branch B
    └── Branch C
```

---

# 14. Employee Management

```text
employees/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Employee records
- Departments
- Positions
- Employment status
- Staff profiles
- Branch assignments

---

# 15. Guest Management

```text
guests/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Guest registration
- Guest profiles
- Identification information
- Contact information
- Guest history
- Preferences
- Reservation history

---

# 16. Room Management

## Room Types

```text
roomtypes/
├── controller/
├── service/
├── repository/
├── entity/
└── dto/
```

## Rooms

```text
rooms/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Room types
- Room numbers
- Room status
- Amenities
- Pricing
- Availability
- Room assignments

Possible room statuses:

```text
AVAILABLE
OCCUPIED
RESERVED
DIRTY
CLEAN
OUT_OF_ORDER
MAINTENANCE
```

---

# 17. Reservation Management

```text
reservations/
├── controller/
│   └── ReservationController.java
│
├── service/
│   ├── ReservationService.java
│   └── AvailabilityService.java
│
├── repository/
│   └── ReservationRepository.java
│
├── entity/
│   └── Reservation.java
│
├── dto/
│   ├── CreateReservationRequest.java
│   ├── UpdateReservationRequest.java
│   ├── ReservationResponse.java
│   └── AvailabilityResponse.java
│
└── mapper/
    └── ReservationMapper.java
```

Responsibilities:

- Create reservations
- Modify reservations
- Cancel reservations
- Confirm reservations
- Availability
- No-shows
- Booking references

Reservation lifecycle:

```text
PENDING
   ↓
CONFIRMED
   ↓
CHECKED_IN
   ↓
COMPLETED
```

Alternative:

```text
PENDING → CANCELLED
CONFIRMED → NO_SHOW
```

Double-booking protection must be implemented at the service/database level.

---

# 18. Stay Management

```text
stays/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Check-in
- Active stays
- Room assignment
- Guest stay information
- Check-out
- Stay history

Check-in must be transactional.

---

# 19. Housekeeping

```text
housekeeping/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Cleaning tasks
- Room cleaning status
- Staff assignment
- Inspection
- Task completion
- Housekeeping schedules

Workflow:

```text
Room becomes DIRTY
        ↓
Create Cleaning Task
        ↓
Assign Staff
        ↓
Cleaning
        ↓
Inspection
        ↓
Room CLEAN
```

---

# 20. Maintenance

```text
maintenance/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Maintenance requests
- Equipment problems
- Room maintenance
- Work orders
- Staff assignment
- Completion tracking

---

# 21. Restaurant Management

```text
restaurants/
├── controller/
├── service/
├── repository/
├── entity/
└── dto/
```

Responsibilities:

- Restaurants
- Restaurant branches
- Restaurant settings
- Operating hours
- Restaurant configuration

---

# 22. Menu Management

```text
menu/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Menu categories
- Menu items
- Prices
- Availability
- Modifiers
- Ingredients
- Item status

Example:

```text
Menu
 ├── Breakfast
 ├── Lunch
 ├── Dinner
 ├── Drinks
 └── Desserts
```

---

# 23. Restaurant Orders

```text
orders/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Create orders
- Order items
- Table orders
- Room service orders
- Order status
- Discounts
- Taxes
- Payment

Order lifecycle:

```text
DRAFT
 ↓
SUBMITTED
 ↓
PREPARING
 ↓
READY
 ↓
SERVED
 ↓
PAID
 ↓
COMPLETED
```

---

# 24. Kitchen Management

```text
kitchen/
├── controller/
├── service/
├── repository/
├── entity/
└── dto/
```

Responsibilities:

- Kitchen tickets
- Food preparation
- Kitchen queues
- Item status
- Ready notifications

Workflow:

```text
Order
 ↓
Kitchen Ticket
 ↓
Preparing
 ↓
Ready
 ↓
Waiter Notification
```

---

# 25. Inventory Management

```text
inventory/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Products
- Categories
- Units
- Stock levels
- Stock receipts
- Stock issues
- Wastage
- Adjustments
- Stock transactions

Every stock-changing operation must create an inventory transaction.

---

# 26. Supplier Management

```text
suppliers/
├── controller/
├── service/
├── repository/
├── entity/
└── dto/
```

Responsibilities:

- Supplier profiles
- Contacts
- Supplier status
- Supplier products
- Supplier history

---

# 27. Procurement

```text
procurement/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Purchase requests
- Purchase orders
- Approval
- Supplier selection
- Goods receiving
- Purchase history

Workflow:

```text
Inventory Need
     ↓
Purchase Request
     ↓
Approval
     ↓
Purchase Order
     ↓
Supplier
     ↓
Delivery
     ↓
Receive Goods
     ↓
Inventory Update
```

---

# 28. Billing

```text
billing/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Invoices
- Invoice items
- Charges
- Balances
- Receipts
- Credit notes
- Adjustments

Billing sources include:

```text
Accommodation
Restaurant
Room Service
Laundry
Other Services
Taxes
Discounts
```

---

# 29. Payments

```text
payments/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── provider/
└── mapper/
```

Possible payment methods:

```text
CASH
CARD
MOBILE_MONEY
BANK_TRANSFER
OTHER
```

Payment providers should be abstracted behind interfaces.

Example:

```text
PaymentProvider
      │
      ├── MobileMoneyProvider
      ├── CardProvider
      └── BankProvider
```

Payment operations must support idempotency.

---

# 30. Taxes and Discounts

## Taxes

```text
taxes/
├── controller/
├── service/
├── repository/
├── entity/
└── dto/
```

## Discounts

```text
discounts/
├── controller/
├── service/
├── repository/
├── entity/
└── dto/
```

Discount calculations must be performed server-side.

---

# 31. Loyalty Management

```text
loyalty/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/
```

Responsibilities:

- Loyalty accounts
- Points
- Rewards
- Membership levels
- Transactions

---

# 32. Notifications

```text
notifications/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── channel/
│   ├── EmailNotificationChannel.java
│   ├── SmsNotificationChannel.java
│   └── InAppNotificationChannel.java
└── template/
```

Supported channels:

```text
EMAIL
SMS
IN_APP
```

Business events can trigger notifications.

Examples:

```text
Reservation Confirmed
Payment Received
Room Ready
Order Ready
Password Reset
Invoice Generated
```

---

# 33. Reporting

```text
reports/
├── controller/
├── service/
├── repository/
├── dto/
├── generator/
│   ├── PdfReportGenerator.java
│   ├── CsvReportGenerator.java
│   └── ExcelReportGenerator.java
└── query/
```

Reports may include:

- Occupancy
- Revenue
- Reservations
- Payments
- Restaurant sales
- Inventory
- Procurement
- Expenses
- Guest statistics
- Staff activity

---

# 34. Dashboard

```text
dashboard/
├── controller/
├── service/
├── dto/
└── query/
```

Dashboard data may include:

```text
Today's Revenue
Today's Check-ins
Today's Check-outs
Current Occupancy
Available Rooms
Pending Reservations
Restaurant Sales
Low Stock Items
Pending Payments
```

Dashboard queries should be optimized for performance.

---

# 35. Audit Logging

```text
audit/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── listener/
```

Important events:

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

Audit records should include:

- user
- action
- entity
- entity ID
- timestamp
- IP address where appropriate
- relevant metadata

---

# 36. Settings

```text
settings/
├── controller/
├── service/
├── repository/
├── entity/
└── dto/
```

Settings include:

- Hotel information
- Currency
- Tax configuration
- Invoice settings
- Notification settings
- Reservation settings
- Restaurant settings
- System preferences

---

# 37. Database Migration Structure

Flyway migrations will be stored in:

```text
src/main/resources/db/migration/
```

Recommended structure:

```text
db/migration/
├── V001__create_organizations.sql
├── V002__create_branches.sql
├── V003__create_roles.sql
├── V004__create_permissions.sql
├── V005__create_users.sql
├── V006__create_employees.sql
├── V007__create_guests.sql
├── V008__create_room_types.sql
├── V009__create_rooms.sql
├── V010__create_reservations.sql
├── V011__create_stays.sql
├── V012__create_housekeeping.sql
├── V013__create_maintenance.sql
├── V014__create_restaurants.sql
├── V015__create_tables.sql
├── V016__create_menu.sql
├── V017__create_orders.sql
├── V018__create_kitchen.sql
├── V019__create_inventory.sql
├── V020__create_suppliers.sql
├── V021__create_procurement.sql
├── V022__create_billing.sql
├── V023__create_payments.sql
├── V024__create_taxes.sql
├── V025__create_discounts.sql
├── V026__create_loyalty.sql
├── V027__create_notifications.sql
├── V028__create_audit_logs.sql
└── V029__create_settings.sql
```

The exact migration sequence will be aligned with the final database ERD.

---

# 38. DTO Architecture

Entities must not be exposed directly through REST APIs.

Use DTOs.

Example:

```text
CreateGuestRequest
UpdateGuestRequest
GuestResponse
GuestSummaryResponse
```

Example:

```java
public record CreateGuestRequest(
    String firstName,
    String lastName,
    String email,
    String phone
) {}
```

The DTO layer protects internal database structures.

---

# 39. Entity Architecture

Entities represent database tables.

Example:

```text
Guest.java
Reservation.java
Room.java
Invoice.java
Payment.java
Order.java
InventoryItem.java
```

Entities should contain:

- IDs
- relationships
- persistence configuration
- timestamps
- version fields where required

Business logic should generally remain in services rather than controllers.

---

# 40. Repository Architecture

Repositories handle database access.

Example:

```java
public interface GuestRepository
        extends JpaRepository<Guest, UUID> {
}
```

Repositories should be responsible for:

- queries
- persistence
- database filtering
- pagination
- custom query methods

Repositories should not contain business workflows.

---

# 41. Service Architecture

Services contain business logic.

Example:

```text
ReservationService
AvailabilityService
CheckInService
PaymentService
InventoryService
BillingService
```

A service may coordinate multiple repositories.

Example:

```text
ReservationService
       │
       ├── GuestRepository
       ├── RoomRepository
       ├── ReservationRepository
       └── NotificationService
```

---

# 42. Controller Architecture

Controllers expose REST endpoints.

Example:

```java
@RestController
@RequestMapping("/api/v1/guests")
public class GuestController {
}
```

Controllers should:

1. Receive request
2. Validate request
3. Call service
4. Return response

Controllers should not contain complex business logic.

---

# 43. Security Architecture

Security structure:

```text
security/
├── JwtAuthenticationFilter.java
├── JwtService.java
├── CustomUserDetailsService.java
├── SecurityUser.java
├── PermissionEvaluator.java
└── SecurityUtils.java
```

Authentication:

```text
Login
 ↓
Validate Credentials
 ↓
Generate Access Token
 ↓
Generate Refresh Token
 ↓
Return Tokens
```

Request:

```text
HTTP Request
 ↓
JWT Filter
 ↓
Validate Token
 ↓
Load User
 ↓
Check Permission
 ↓
Controller
```

Security requirements:

- Password hashing
- JWT expiration
- Refresh-token handling
- Permission checks
- CORS
- Secure headers
- Rate limiting
- Input validation
- Audit logging

---

# 44. Exception Handling

All API exceptions should be handled centrally.

```text
common/exception/
├── GlobalExceptionHandler.java
├── ResourceNotFoundException.java
├── BusinessException.java
├── ConflictException.java
├── UnauthorizedException.java
└── ForbiddenException.java
```

Example response:

```json
{
  "success": false,
  "error": {
    "code": "ROOM_NOT_AVAILABLE",
    "message": "The selected room is unavailable."
  }
}
```

Validation errors should provide field-level information.

---

# 45. API Response Architecture

Standard success response:

```json
{
  "success": true,
  "data": {},
  "message": "Operation completed successfully."
}
```

Paginated response:

```json
{
  "success": true,
  "data": [],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

Error response:

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid request.",
    "fields": {}
  }
}
```

---

# 46. Validation

Use Jakarta Bean Validation.

Examples:

```java
@NotBlank
@Email
@Size
@Positive
@NotNull
@Past
@Future
```

Validation must occur at API boundaries.

Business validation belongs in the service layer.

Example:

```text
Request Validation
       ↓
Business Validation
       ↓
Database Constraints
```

---

# 47. Transaction Management

Critical operations must be transactional.

Use:

```java
@Transactional
```

Examples:

### Reservation

```text
Check Availability
 ↓
Create Reservation
 ↓
Update Availability
 ↓
Create Audit
```

### Check-in

```text
Validate Reservation
 ↓
Create Stay
 ↓
Update Room
 ↓
Record Deposit
 ↓
Audit
```

### Check-out

```text
Calculate Charges
 ↓
Calculate Taxes
 ↓
Apply Discounts
 ↓
Process Payment
 ↓
Generate Invoice
 ↓
Close Stay
 ↓
Update Room
```

### Inventory

```text
Receive Stock
 ↓
Update Stock
 ↓
Create Inventory Transaction
```

---

# 48. Logging

Use structured application logging.

Log levels:

```text
TRACE
DEBUG
INFO
WARN
ERROR
```

Important events:

```text
Application Startup
Authentication Failure
Payment Failure
Database Failure
External API Failure
Critical Business Exception
```

Never log:

- passwords
- JWT tokens
- payment secrets
- sensitive credentials

---

# 49. Testing Structure

Tests should mirror the production package structure.

Example:

```text
src/test/java/com/hrms/

├── auth/
├── users/
├── reservations/
├── guests/
├── rooms/
├── billing/
├── payments/
├── inventory/
└── orders/
```

Testing levels:

```text
Unit Tests
     ↓
Integration Tests
     ↓
API Tests
     ↓
End-to-End Tests
```

Recommended tools:

```text
JUnit
Mockito
Spring Boot Test
Testcontainers
```

Critical workflows requiring tests:

- Login
- Authorization
- Reservation creation
- Double-booking prevention
- Check-in
- Check-out
- Payment
- Refund
- Inventory receiving
- Inventory adjustment
- Restaurant order
- Invoice calculation

---

# 50. Environment Configuration

Configuration files:

```text
application.yml
application-dev.yml
application-test.yml
application-prod.yml
```

Example:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
```

Environment variables should contain secrets.

Never commit:

```text
DATABASE_PASSWORD
JWT_SECRET
PAYMENT_API_KEY
EMAIL_PASSWORD
SMS_API_KEY
```

---

# 51. Docker Structure

Recommended structure:

```text
hrms-backend/
├── Dockerfile
└── docker-compose.yml
```

Development environment:

```text
Docker
 ├── Spring Boot
 ├── PostgreSQL
 └── Redis
```

Production services may be deployed separately depending on infrastructure.

---

# 52. API Documentation

OpenAPI documentation should be available through Swagger UI.

Expected endpoints:

```text
/api/v1/...
```

Documentation should describe:

- endpoints
- request bodies
- response bodies
- authentication
- parameters
- validation
- status codes
- error codes

---

# 53. Coding Standards

## Naming

Classes:

```text
GuestService
ReservationController
RoomRepository
PaymentResponse
```

Methods:

```text
createReservation()
findGuestById()
calculateInvoice()
processPayment()
```

Variables:

```text
guestId
reservationId
totalAmount
paymentStatus
```

## General Rules

1. Use meaningful names.
2. Avoid unnecessary abbreviations.
3. Keep methods focused.
4. Keep controllers thin.
5. Keep business logic in services.
6. Avoid duplicate logic.
7. Use DTOs for API communication.
8. Validate external input.
9. Write tests for critical business logic.
10. Document complex business rules.

---

# 54. Module Dependency Rules

Modules should follow controlled dependencies.

Example:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Entity
```

A controller must not directly access a repository for business operations.

Bad:

```text
Controller → Repository
```

Preferred:

```text
Controller → Service → Repository
```

Cross-module dependencies should also be controlled.

Example:

```text
ReservationService
      ↓
GuestService
      ↓
GuestRepository
```

Avoid circular dependencies such as:

```text
ReservationService
      ↓
GuestService
      ↓
ReservationService
```

Shared functionality belongs in `common/`.

---

# 55. Backend Development Order

The backend should be implemented in phases.

## Phase 1 — Project Initialization

```text
Spring Boot
Maven
PostgreSQL
Flyway
Docker
Environment configuration
```

---

## Phase 2 — Common Infrastructure

```text
Exception handling
Response wrapper
Validation
Logging
Utilities
Pagination
```

---

## Phase 3 — Security

```text
Users
Roles
Permissions
JWT
Authentication
Authorization
```

---

## Phase 4 — Organization

```text
Organization
Branches
Employees
Departments
```

---

## Phase 5 — Hotel Core

```text
Guests
Room Types
Rooms
Reservations
Availability
Stays
```

---

## Phase 6 — Hotel Operations

```text
Housekeeping
Maintenance
Room assignments
```

---

## Phase 7 — Restaurant

```text
Restaurants
Tables
Menu
Orders
Kitchen
```

---

## Phase 8 — Inventory

```text
Inventory
Suppliers
Procurement
Stock transactions
```

---

## Phase 9 — Finance

```text
Taxes
Discounts
Billing
Invoices
Payments
Receipts
Refunds
```

---

## Phase 10 — Management

```text
Dashboard
Reports
Notifications
Loyalty
Audit
Settings
```

---

## Phase 11 — Testing

```text
Unit tests
Integration tests
API tests
Security tests
Performance tests
End-to-end tests
```

---

## Phase 12 — Production

```text
Docker
CI/CD
Cloud deployment
Monitoring
Backups
Security hardening
```

---

# 56. Definition of Done

A backend module is considered complete when:

- [ ] Entity implemented
- [ ] Database migration implemented
- [ ] Repository implemented
- [ ] Service implemented
- [ ] DTOs implemented
- [ ] Mapper implemented
- [ ] Controller implemented
- [ ] Validation implemented
- [ ] Authorization implemented
- [ ] Exception handling implemented
- [ ] Audit requirements implemented
- [ ] Unit tests implemented
- [ ] Integration tests implemented
- [ ] API documentation completed
- [ ] Logging implemented
- [ ] Security reviewed

---

# Final Backend Architecture

The final backend should follow:

```text
                        NEXT.JS FRONTEND
                               │
                               │ HTTPS / JSON
                               ▼
                    ┌─────────────────────┐
                    │    SPRING BOOT      │
                    │      REST API       │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │      SECURITY       │
                    │ JWT + Permissions   │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │    CONTROLLERS      │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │      SERVICES       │
                    │   Business Logic    │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │    REPOSITORIES     │
                    │    Data Access      │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │     POSTGRESQL      │
                    │      DATABASE       │
                    └─────────────────────┘

       ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
       │ Notifications│ │    Redis     │ │ Object Store │
       └──────────────┘ └──────────────┘ └──────────────┘
```

---

# Backend Project Completion Checklist

```text
[✓] Architecture defined
[✓] Package structure defined
[✓] Module boundaries defined
[✓] Controller layer defined
[✓] Service layer defined
[✓] Repository layer defined
[✓] Entity layer defined
[✓] DTO layer defined
[✓] Security structure defined
[✓] Exception architecture defined
[✓] Validation architecture defined
[✓] Transaction strategy defined
[✓] Testing structure defined
[✓] Configuration structure defined
[✓] Migration structure defined
[✓] Docker structure defined
[✓] Development sequence defined
```

---

# Next Document

After this document, the next design artifact should be:

```text
FRONTEND_PROJECT_STRUCTURE.md
```

This will define the complete Next.js architecture, including:

```text
app/
components/
features/
hooks/
lib/
stores/
types/
services/
forms/
layouts/
dashboard/
authentication/
reservations/
guests/
rooms/
restaurant/
inventory/
finance/
reports/
```

After the frontend structure is finalized, we can move from **architecture documentation into actual implementation**, beginning with the PostgreSQL migration scripts and Spring Boot project initialization.

---

**Document Status:** COMPLETE  
**Document Version:** 1.0  
**Implementation Status:** Ready for Frontend Project Structure Design