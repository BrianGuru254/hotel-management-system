# HOTEL & RESTAURANT MANAGEMENT SYSTEM

# SYSTEM ARCHITECTURE SPECIFICATION

**Project:** Hotel & Restaurant Management System (HRMS)  
**Document:** System Architecture Specification  
**Version:** 1.0  
**Status:** Implementation-Ready  
**Frontend:** Next.js + TypeScript  
**Backend:** Java + Spring Boot  
**Database:** PostgreSQL  
**API:** REST + JSON  
**Authentication:** JWT-based authentication  
**API Documentation:** OpenAPI 3 / Swagger  
**Architecture Style:** Layered Modular Architecture  
**Deployment:** Cloud-ready  
**Date:** September 2026

---

# TABLE OF CONTENTS

1. Architecture Overview
2. Architecture Goals
3. System Context
4. High-Level Architecture
5. Logical Architecture
6. Physical Architecture
7. Frontend Architecture
8. Backend Architecture
9. Database Architecture
10. API Architecture
11. Authentication Architecture
12. Authorization Architecture
13. Security Architecture
14. Hotel Management Architecture
15. Reservation Architecture
16. Check-In Architecture
17. Check-Out Architecture
18. Restaurant Architecture
19. Kitchen Architecture
20. Inventory Architecture
21. Procurement Architecture
22. Finance Architecture
23. Notification Architecture
24. Reporting Architecture
25. Audit Architecture
26. File Management Architecture
27. Integration Architecture
28. Data Flow Architecture
29. Reservation Data Flow
30. Restaurant Order Data Flow
31. Payment Data Flow
32. Inventory Data Flow
33. Check-In Data Flow
34. Check-Out Data Flow
35. Role-Based Architecture
36. Multi-Branch Architecture
37. Transaction Architecture
38. Concurrency Architecture
39. Caching Architecture
40. Error Handling Architecture
41. Logging Architecture
42. Monitoring Architecture
43. Backup Architecture
44. Disaster Recovery
45. Performance Architecture
46. Scalability Architecture
47. Deployment Architecture
48. Environment Architecture
49. CI/CD Architecture
50. Network Architecture
51. Security Controls
52. Technology Stack
53. Backend Package Architecture
54. Frontend Package Architecture
55. Database Schema Architecture
56. API Communication
57. External Services
58. Architecture Decision Records
59. Development Phases
60. Architecture Acceptance Criteria
61. Final Architecture Diagram

---

# 1. ARCHITECTURE OVERVIEW

The Hotel & Restaurant Management System shall use a modular three-tier architecture.

```text
┌───────────────────────────────────────────┐
│              PRESENTATION                 │
│                                           │
│        Next.js + TypeScript               │
│                                           │
│ Dashboard • POS • Reservations • Reports  │
└────────────────────┬──────────────────────┘
                     │
                  HTTPS
                     │
                     ▼
┌───────────────────────────────────────────┐
│              APPLICATION                  │
│                                           │
│          Spring Boot REST API             │
│                                           │
│ Authentication • Business Logic • APIs    │
└────────────────────┬──────────────────────┘
                     │
                  JDBC/JPA
                     │
                     ▼
┌───────────────────────────────────────────┐
│                 DATA                      │
│                                           │
│               PostgreSQL                  │
│                                           │
│ Hotel • Restaurant • Finance • Inventory  │
└───────────────────────────────────────────┘
```

The architecture separates:

```text
Presentation
Business Logic
Data Access
Database
External Integrations
```

---

# 2. ARCHITECTURE GOALS

The architecture shall provide:

- Maintainability
- Security
- Scalability
- Reliability
- Performance
- Testability
- Modularity
- Extensibility
- Clear separation of concerns
- Role-based access control
- Multi-branch support
- Financial transaction integrity
- Auditability

The architecture should allow additional hotel branches and modules to be introduced without redesigning the entire system.

---

# 3. SYSTEM CONTEXT

The HRMS interacts with several actors.

```text
                           ┌──────────────┐
                           │    GUEST     │
                           └──────┬───────┘
                                  │
                           Public Website
                                  │
                                  ▼
┌──────────────┐          ┌──────────────────┐
│ HOTEL STAFF  │─────────▶│                  │
└──────────────┘          │       HRMS       │
                          │                  │
┌──────────────┐          │ Next.js +        │
│   MANAGERS   │─────────▶│ Spring Boot      │
└──────────────┘          │ + PostgreSQL     │
                          │                  │
┌──────────────┐          │                  │
│   ADMINS     │─────────▶│                  │
└──────────────┘          └────────┬─────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
                    ▼              ▼              ▼
                 Payment         Email           SMS
                 Gateway        Service         Service
```

---

# 4. HIGH-LEVEL ARCHITECTURE

The system shall consist of:

```text
1. Public Web Application
2. Management Web Application
3. REST API
4. Authentication Service
5. Business Services
6. Data Access Layer
7. PostgreSQL Database
8. File Storage
9. Notification Services
10. Payment Integration
11. Reporting Engine
12. Audit System
13. Monitoring System
```

---

# 5. LOGICAL ARCHITECTURE

The logical architecture is divided into layers.

```text
┌─────────────────────────────────────┐
│         PRESENTATION LAYER          │
│                                     │
│ Next.js / React / TypeScript        │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│          API / CONTROLLER           │
│                                     │
│ REST Controllers                    │
│ Request Validation                  │
│ Response Mapping                    │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│          SERVICE LAYER              │
│                                     │
│ Business Rules                      │
│ Transactions                        │
│ Authorization Checks                │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│        DATA ACCESS LAYER             │
│                                     │
│ Spring Data JPA / Repositories      │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│             DATABASE                │
│                                     │
│ PostgreSQL                          │
└─────────────────────────────────────┘
```

---

# 6. PHYSICAL ARCHITECTURE

Production deployment:

```text
                       INTERNET
                           │
                           ▼
                  ┌─────────────────┐
                  │ DNS / CDN / TLS │
                  └────────┬────────┘
                           │
             ┌─────────────┴─────────────┐
             │                           │
             ▼                           ▼
     ┌───────────────┐          ┌────────────────┐
     │ Next.js       │          │ Spring Boot    │
     │ Frontend      │          │ API            │
     └───────────────┘          └───────┬────────┘
                                        │
                                        ▼
                                ┌───────────────┐
                                │ PostgreSQL    │
                                │ Database      │
                                └───────────────┘
```

Additional services:

```text
Object Storage
Email Provider
SMS Provider
Payment Gateway
Monitoring
Logging
```

---

# 7. FRONTEND ARCHITECTURE

Frontend technology:

```text
Next.js
TypeScript
React
CSS / Tailwind CSS or equivalent UI system
```

Architecture:

```text
Next.js Application
│
├── Pages
├── Layouts
├── Components
├── Hooks
├── API Client
├── Authentication
├── Permission System
├── State Management
├── Form Validation
└── Utilities
```

---

# 8. FRONTEND LAYERS

```text
UI Components
      ↓
Feature Components
      ↓
Pages
      ↓
API Client
      ↓
Spring Boot API
```

Example:

```text
ReservationPage
      ↓
ReservationForm
      ↓
reservationApi.create()
      ↓
POST /api/v1/reservations
```

---

# 9. FRONTEND RESPONSIBILITIES

The frontend shall handle:

- User interface
- Navigation
- Form input
- Client-side validation
- API communication
- Authentication state
- Permission-aware UI
- Loading states
- Error states
- Notifications
- Data visualization
- Responsive layouts

The frontend shall not:

- Connect directly to PostgreSQL
- Store database credentials
- Implement authoritative authorization
- Perform financial calculations that must be trusted
- Bypass backend validation

---

# 10. BACKEND ARCHITECTURE

The backend shall use Spring Boot.

```text
Spring Boot
│
├── REST API
├── Security
├── Services
├── Repositories
├── Entities
├── DTOs
├── Validation
├── Exception Handling
├── Transactions
├── Audit
└── Integrations
```

---

# 11. BACKEND LAYERS

## Controller

Responsible for:

```text
HTTP
Request parsing
Validation triggering
Response formatting
```

## Service

Responsible for:

```text
Business logic
Transactions
Business rules
Workflow management
```

## Repository

Responsible for:

```text
Database queries
Persistence
Data retrieval
```

## Entity

Represents:

```text
Database records
```

## DTO

Represents:

```text
API request/response objects
```

---

# 12. MODULAR BACKEND ARCHITECTURE

Modules:

```text
auth
users
roles
employees
organization
branches

guests
rooms
roomtypes
reservations
stays

housekeeping
maintenance

restaurants
tables
menu
orders
kitchen

inventory
suppliers
procurement

billing
payments
taxes
discounts

loyalty
notifications
reports
audit
settings
```

Each module should contain its own:

```text
controller/
service/
repository/
entity/
dto/
mapper/
```

---

# 13. DATABASE ARCHITECTURE

Database:

```text
PostgreSQL
```

The database shall contain logical groups.

```text
Identity
├── users
├── roles
├── permissions
└── employees

Hotel
├── guests
├── rooms
├── room_types
├── amenities
├── reservations
└── stays

Operations
├── housekeeping_tasks
└── maintenance_requests

Restaurant
├── restaurants
├── restaurant_tables
├── menu_categories
├── menu_items
├── orders
└── order_items

Finance
├── invoices
├── invoice_items
├── payments
├── taxes
└── discounts

Inventory
├── inventory_items
├── inventory_categories
├── inventory_transactions
├── suppliers
└── purchase_orders

System
├── notifications
├── audit_logs
└── settings
```

---

# 14. DATABASE ACCESS

Spring Boot shall access PostgreSQL through:

```text
Spring Data JPA
Hibernate
JDBC
```

Preferred normal access:

```text
Service
   ↓
Repository
   ↓
JPA/Hibernate
   ↓
PostgreSQL
```

---

# 15. API ARCHITECTURE

The API shall use:

```text
REST
JSON
HTTPS
/api/v1
```

Example:

```text
Next.js
   │
   │ HTTPS
   ▼
POST /api/v1/reservations
   │
   ▼
ReservationController
   │
   ▼
ReservationService
   │
   ▼
ReservationRepository
   │
   ▼
PostgreSQL
```

---

# 16. API GATEWAY / ENTRY POINT

The API entry point is:

```text
/api/v1
```

All API requests pass through:

```text
Security
 ↓
Authentication
 ↓
Authorization
 ↓
Validation
 ↓
Controller
 ↓
Service
```

---

# 17. AUTHENTICATION ARCHITECTURE

Authentication flow:

```text
User
 ↓
Login Form
 ↓
POST /auth/login
 ↓
Spring Security
 ↓
Validate Credentials
 ↓
Generate JWT
 ↓
Return Authentication Result
```

Protected request:

```text
Frontend
 ↓
Authorization Header
 ↓
Spring Security Filter
 ↓
Validate JWT
 ↓
Identify User
 ↓
Authorization
 ↓
Controller
```

---

# 18. AUTHORIZATION ARCHITECTURE

Authorization shall be permission-based.

Example:

```text
USER
 ↓
ROLE
 ↓
PERMISSIONS
```

Example:

```text
RECEPTIONIST
│
├── RESERVATION_VIEW
├── RESERVATION_CREATE
├── RESERVATION_UPDATE
├── GUEST_VIEW
├── GUEST_CREATE
├── CHECKIN_CREATE
└── CHECKOUT_CREATE
```

---

# 19. SECURITY ARCHITECTURE

Security layers:

```text
Internet
   ↓
HTTPS
   ↓
Reverse Proxy / CDN
   ↓
Spring Security
   ↓
JWT Validation
   ↓
Authorization
   ↓
Input Validation
   ↓
Business Rules
   ↓
Database
```

---

# 20. SECURITY REQUIREMENTS

The system shall implement:

```text
HTTPS
JWT authentication
Role-based authorization
Permission checks
Password hashing
Input validation
SQL injection protection
Rate limiting
Audit logging
Secure headers
CORS policy
Session/token expiration
```

Passwords shall never be stored in plain text.

Recommended password hashing:

```text
Argon2id
```

or an appropriately configured strong password-hashing algorithm supported by the chosen security stack.

---

# 21. HOTEL MANAGEMENT ARCHITECTURE

Hotel operations:

```text
Guest
 ↓
Reservation
 ↓
Room Assignment
 ↓
Check-In
 ↓
Stay
 ↓
Charges
 ↓
Check-Out
 ↓
Invoice
 ↓
Payment
```

Related services:

```text
GuestService
ReservationService
RoomService
StayService
BillingService
PaymentService
```

---

# 22. RESERVATION ARCHITECTURE

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
PENDING
   ↓
CANCELLED
```

or:

```text
CONFIRMED
   ↓
NO_SHOW
```

The reservation service shall enforce valid transitions.

---

# 23. RESERVATION CONCURRENCY

The system must prevent double booking.

```text
User A ─────┐
            │
            ▼
        Availability
            │
User B ─────┘
```

The backend shall use:

```text
Database constraints
Transactions
Locking where required
Conflict detection
```

Availability must be checked on the server immediately before final booking.

---

# 24. CHECK-IN ARCHITECTURE

```text
Reservation
      ↓
Validation
      ↓
Guest Verification
      ↓
Room Availability
      ↓
Create Stay
      ↓
Update Room
      ↓
Record Deposit
      ↓
Audit
```

Room state:

```text
RESERVED
   ↓
OCCUPIED
```

---

# 25. CHECK-OUT ARCHITECTURE

```text
Active Stay
     ↓
Collect Charges
     ↓
Accommodation
Restaurant
Room Service
Other Charges
     ↓
Calculate Taxes
     ↓
Apply Discounts
     ↓
Calculate Balance
     ↓
Payment
     ↓
Invoice
     ↓
Close Stay
     ↓
Room → DIRTY
```

Check-out should be transactional.

---

# 26. RESTAURANT ARCHITECTURE

Restaurant modules:

```text
Restaurant
│
├── Tables
├── Menu
├── POS
├── Orders
├── Kitchen
└── Billing
```

Flow:

```text
Waiter
 ↓
POS
 ↓
Order
 ↓
Kitchen
 ↓
Preparation
 ↓
Ready
 ↓
Serve
 ↓
Payment
```

---

# 27. KITCHEN ARCHITECTURE

Kitchen receives submitted orders.

```text
POS
 ↓
Order Submitted
 ↓
Kitchen Queue
 ↓
Accept
 ↓
Preparing
 ↓
Ready
 ↓
Served
```

The kitchen interface should update rapidly.

---

# 28. INVENTORY ARCHITECTURE

Inventory follows transaction-based stock management.

```text
Opening Stock
      ↓
Receipts
      ↓
Current Stock
      ↓
Issues
      ↓
Wastage
      ↓
Adjustments
```

The current quantity should be derived from controlled inventory operations.

Every stock-changing operation should create an inventory transaction.

---

# 29. PROCUREMENT ARCHITECTURE

```text
Inventory Need
      ↓
Purchase Order
      ↓
Approval
      ↓
Supplier
      ↓
Delivery
      ↓
Receive Stock
      ↓
Inventory Transaction
      ↓
Stock Updated
```

---

# 30. FINANCE ARCHITECTURE

Financial flow:

```text
Charges
   ↓
Invoice
   ↓
Payment
   ↓
Receipt
```

Financial records shall be immutable or carefully controlled after posting.

Corrections should use:

```text
Void
Refund
Adjustment
Reversal
```

rather than silently modifying historical transactions.

---

# 31. PAYMENT ARCHITECTURE

Payment flow:

```text
Invoice
   ↓
Payment Request
   ↓
Payment Method
   ↓
Payment Provider
   ↓
Payment Result
   ↓
Payment Record
   ↓
Invoice Balance
```

Supported methods may include:

```text
CASH
CARD
BANK_TRANSFER
MOBILE_MONEY
OTHER
```

---

# 32. PAYMENT IDEMPOTENCY

Every external or potentially retryable payment operation should support idempotency.

```text
Payment Request
      ↓
Idempotency Key
      ↓
Check Existing Transaction
      ↓
If Exists → Return Existing Result
      ↓
If New → Process
```

This protects against duplicate payments.

---

# 33. NOTIFICATION ARCHITECTURE

Notification channels:

```text
In-App
Email
SMS
```

Architecture:

```text
Business Event
      ↓
Notification Service
      ↓
Select Channel
      ↓
Provider
      ↓
Delivery
```

Examples:

```text
Reservation Created
Payment Received
Low Stock
Maintenance Request
Password Reset
```

---

# 34. REPORTING ARCHITECTURE

Reports shall use dedicated service classes.

```text
Report Controller
       ↓
Report Service
       ↓
Reporting Queries
       ↓
PostgreSQL
       ↓
Report Formatter
       ↓
PDF / CSV / XLSX / JSON
```

Reports should not unnecessarily load entire transactional tables into application memory.

---

# 35. AUDIT ARCHITECTURE

Important actions shall generate audit records.

Examples:

```text
LOGIN
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

Audit flow:

```text
User Action
    ↓
Business Operation
    ↓
Audit Service
    ↓
Audit Log
    ↓
PostgreSQL
```

---

# 36. FILE MANAGEMENT ARCHITECTURE

Files may include:

```text
Guest documents
Room images
Menu images
Invoices
Receipts
Reports
Attachments
```

Recommended architecture:

```text
Frontend
   ↓
Spring Boot
   ↓
Object Storage
```

The database should store:

```text
File ID
File name
Content type
Size
Storage key
Owner
Created date
```

The database should not normally store large binary files directly.

---

# 37. INTEGRATION ARCHITECTURE

External integrations may include:

```text
Payment Gateway
Email Provider
SMS Provider
Object Storage
Monitoring Service
```

Integration layer:

```text
Spring Boot
     │
     ├── PaymentClient
     ├── EmailClient
     ├── SmsClient
     └── StorageClient
```

External APIs shall be isolated behind service interfaces.

---

# 38. DATA FLOW ARCHITECTURE

General data flow:

```text
User
 ↓
Next.js
 ↓
HTTPS
 ↓
Spring Security
 ↓
Controller
 ↓
DTO Validation
 ↓
Service
 ↓
Repository
 ↓
PostgreSQL
 ↓
Repository
 ↓
Service
 ↓
Response DTO
 ↓
Next.js
 ↓
User
```

---

# 39. RESERVATION DATA FLOW

```text
Guest
 ↓
Public Website
 ↓
Select Dates
 ↓
Availability API
 ↓
Room Service
 ↓
PostgreSQL
 ↓
Available Rooms
 ↓
Guest Selects Room
 ↓
Reservation API
 ↓
Reservation Service
 ↓
Transaction
 ↓
Reservation Database
 ↓
Confirmation
 ↓
Email/SMS
```

---

# 40. RESTAURANT ORDER DATA FLOW

```text
Waiter
 ↓
POS
 ↓
Order API
 ↓
Order Service
 ↓
Database
 ↓
Kitchen Queue
 ↓
Chef
 ↓
Order Status
 ↓
POS
 ↓
Serve
 ↓
Billing
```

---

# 41. PAYMENT DATA FLOW

```text
Cashier
 ↓
Invoice
 ↓
Payment Screen
 ↓
Payment API
 ↓
Payment Service
 ↓
Payment Provider
 ↓
Provider Response
 ↓
Payment Record
 ↓
Invoice Balance
 ↓
Receipt
```

---

# 42. INVENTORY DATA FLOW

```text
Storekeeper
 ↓
Inventory Screen
 ↓
Stock Transaction
 ↓
Inventory Service
 ↓
Validate Quantity
 ↓
Database Transaction
 ↓
Inventory Transaction
 ↓
Update Stock
 ↓
Audit Log
```

---

# 43. CHECK-IN DATA FLOW

```text
Receptionist
 ↓
Reservation
 ↓
Check-In Screen
 ↓
Check-In API
 ↓
Validate Reservation
 ↓
Validate Room
 ↓
Create Stay
 ↓
Update Room
 ↓
Audit
 ↓
Success
```

---

# 44. CHECK-OUT DATA FLOW

```text
Receptionist
 ↓
Active Stay
 ↓
Check-Out Preview
 ↓
Billing Service
 ↓
Calculate Charges
 ↓
Payment
 ↓
Invoice
 ↓
Close Stay
 ↓
Room Status Update
 ↓
Audit
```

---

# 45. ROLE-BASED ARCHITECTURE

Every request should be evaluated against:

```text
User
 ↓
Account Status
 ↓
Role
 ↓
Permission
 ↓
Branch Access
 ↓
Resource
 ↓
Action
```

Example:

```text
Receptionist
 ↓
Reservation
 ↓
CREATE
 ↓
Allowed
```

But:

```text
Receptionist
 ↓
Role Management
 ↓
DELETE
 ↓
Denied
```

---

# 46. MULTI-BRANCH ARCHITECTURE

The system shall support multiple branches.

```text
Organization
│
├── Branch A
│   ├── Rooms
│   ├── Restaurant
│   └── Inventory
│
├── Branch B
│   ├── Rooms
│   ├── Restaurant
│   └── Inventory
│
└── Branch C
    ├── Rooms
    ├── Restaurant
    └── Inventory
```

Branch-aware records shall include:

```text
branch_id
```

where appropriate.

Users may be assigned access to one or multiple branches depending on their role.

---

# 47. TRANSACTION ARCHITECTURE

Transactions shall be used for operations that change multiple related records.

Examples:

```text
Reservation creation
Check-in
Check-out
Payment
Stock receiving
Stock issuing
Order completion
Purchase receiving
Refund
```

Example:

```text
@Transactional
CheckOut
├── Calculate charges
├── Record payment
├── Generate invoice
├── Close stay
├── Update room
└── Create audit
```

If one critical operation fails, the transaction should roll back where appropriate.

---

# 48. CONCURRENCY ARCHITECTURE

The system must protect shared resources.

Critical resources:

```text
Rooms
Payments
Inventory
Orders
Purchase Orders
Invoices
```

Recommended mechanisms:

```text
Database constraints
Optimistic locking
Transactions
Unique indexes
Row locking where necessary
Idempotency keys
```

---

# 49. CACHING ARCHITECTURE

Caching may be introduced for relatively stable data.

Potential cached data:

```text
Room Types
Amenities
Menu Categories
Menu Items
Tax Configuration
System Settings
```

Avoid caching rapidly changing transactional data without a clear invalidation strategy.

Potential technology:

```text
Redis
```

Redis should be introduced when actual performance requirements justify it.

---

# 50. ERROR HANDLING ARCHITECTURE

Backend errors should pass through a centralized exception handler.

```text
Exception
    ↓
GlobalExceptionHandler
    ↓
Error DTO
    ↓
HTTP Response
```

Example:

```json
{
  "success": false,
  "error": {
    "code": "ROOM_NOT_AVAILABLE",
    "message": "The selected room is unavailable."
  }
}
```

---

# 51. LOGGING ARCHITECTURE

Application logs should include:

```text
Timestamp
Log Level
Service
Request ID
User ID where appropriate
Operation
Duration
Error
```

Levels:

```text
TRACE
DEBUG
INFO
WARN
ERROR
```

Production logging should avoid sensitive data.

Passwords and authentication secrets must never be logged.

---

# 52. MONITORING ARCHITECTURE

Monitor:

```text
Application health
API response times
HTTP errors
Database performance
CPU
Memory
Disk
Request volume
Authentication failures
Payment failures
```

Health endpoints:

```text
/api/health
/api/health/live
/api/health/ready
```

---

# 53. BACKUP ARCHITECTURE

PostgreSQL backups should include:

```text
Daily backups
Point-in-time recovery where supported
Backup retention policy
Encrypted backups
Off-site backup storage
Backup verification
```

Backups should be tested through periodic restoration procedures.

---

# 54. DISASTER RECOVERY

Disaster recovery should define:

```text
RPO
Recovery Point Objective

RTO
Recovery Time Objective
```

Recovery procedure:

```text
Infrastructure Failure
        ↓
Provision Replacement
        ↓
Restore Database
        ↓
Restore Files
        ↓
Deploy Application
        ↓
Configure Environment
        ↓
Verify Health
        ↓
Restore Traffic
```

---

# 55. PERFORMANCE ARCHITECTURE

The system should use:

```text
Pagination
Database indexes
Efficient queries
Lazy loading
Caching where justified
Connection pooling
Async processing for non-critical work
Optimized frontend bundles
Image optimization
```

Large reports should use optimized queries or asynchronous generation where necessary.

---

# 56. SCALABILITY ARCHITECTURE

Initial architecture:

```text
One Frontend
One API Instance
One PostgreSQL Database
```

Future architecture:

```text
                  Load Balancer
                       │
          ┌────────────┼────────────┐
          ▼            ▼            ▼
       API #1        API #2       API #3
          │            │            │
          └────────────┼────────────┘
                       ▼
                  PostgreSQL
```

The backend should remain stateless where possible so additional instances can be added.

---

# 57. DEPLOYMENT ARCHITECTURE

Recommended production architecture:

```text
                    INTERNET
                       │
                       ▼
                ┌──────────────┐
                │ DNS / CDN    │
                │ TLS          │
                └──────┬───────┘
                       │
              ┌────────┴────────┐
              │                 │
              ▼                 ▼
        Next.js Frontend   Spring Boot API
              │                 │
              │                 ▼
              │           PostgreSQL
              │
              ├───────────────► Object Storage
              │
              └───────────────► External Services
```

---

# 58. ENVIRONMENT ARCHITECTURE

Three primary environments:

```text
Development
Staging
Production
```

## Development

```text
Local Next.js
Local Spring Boot
Local PostgreSQL
```

## Staging

```text
Staging Frontend
Staging API
Staging Database
```

## Production

```text
Production Frontend
Production API
Production Database
Production Storage
```

Production secrets must never be copied into source code.

---

# 59. CI/CD ARCHITECTURE

Recommended workflow:

```text
Developer
   ↓
Git
   ↓
Feature Branch
   ↓
Pull Request
   ↓
Automated Tests
   ↓
Build
   ↓
Code Quality Checks
   ↓
Staging
   ↓
Approval
   ↓
Production
```

Pipeline stages:

```text
Install
 ↓
Lint
 ↓
Unit Tests
 ↓
Integration Tests
 ↓
Build
 ↓
Security Checks
 ↓
Deploy
```

---

# 60. NETWORK ARCHITECTURE

Production traffic:

```text
Client
 ↓
HTTPS :443
 ↓
Frontend
```

API:

```text
Frontend
 ↓
HTTPS :443
 ↓
API
```

Database:

```text
API
 ↓
Private Network
 ↓
PostgreSQL
```

The PostgreSQL database should not be publicly exposed.

---

# 61. SECURITY CONTROLS

Security controls shall include:

```text
TLS
JWT
Password hashing
RBAC
Permission checks
CORS restrictions
Input validation
Parameterized queries
Rate limiting
Audit logs
Secret management
Database access controls
Backup encryption
File access controls
```

---

# 62. TECHNOLOGY STACK

## Frontend

```text
Next.js
React
TypeScript
Tailwind CSS or equivalent
React Hook Form
Zod
TanStack Query or equivalent
```

## Backend

```text
Java
Spring Boot
Spring Security
Spring Data JPA
Hibernate
Bean Validation
OpenAPI
```

## Database

```text
PostgreSQL
```

## Testing

```text
JUnit
Mockito
Spring Boot Test
Playwright
Jest/Vitest where appropriate
```

## Infrastructure

```text
Git
GitHub
Docker
CI/CD
Cloud Hosting
Object Storage
```

---

# 63. BACKEND PACKAGE ARCHITECTURE

Recommended structure:

```text
src/main/java/com/hrms/

├── HrmsApplication.java
│
├── config/
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   ├── DatabaseConfig.java
│   └── OpenApiConfig.java
│
├── common/
│   ├── exception/
│   ├── response/
│   ├── validation/
│   ├── security/
│   ├── audit/
│   └── util/
│
├── auth/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
│
├── users/
├── roles/
├── permissions/
├── employees/
├── organization/
├── branches/
│
├── guests/
├── rooms/
├── roomtypes/
├── reservations/
├── stays/
│
├── housekeeping/
├── maintenance/
│
├── restaurants/
├── tables/
├── menu/
├── orders/
├── kitchen/
│
├── inventory/
├── suppliers/
├── procurement/
│
├── billing/
├── payments/
├── taxes/
├── discounts/
│
├── loyalty/
├── notifications/
├── reports/
├── dashboard/
├── audit/
└── settings/
```

---

# 64. FRONTEND PACKAGE ARCHITECTURE

```text
src/

├── app/
│
├── components/
│   ├── ui/
│   ├── layout/
│   ├── forms/
│   ├── tables/
│   └── charts/
│
├── features/
│   ├── auth/
│   ├── dashboard/
│   ├── reservations/
│   ├── guests/
│   ├── rooms/
│   ├── housekeeping/
│   ├── maintenance/
│   ├── restaurant/
│   ├── inventory/
│   ├── procurement/
│   ├── finance/
│   ├── employees/
│   ├── reports/
│   └── settings/
│
├── lib/
│   ├── api/
│   ├── auth/
│   ├── permissions/
│   ├── validation/
│   └── utils/
│
├── hooks/
├── stores/
├── types/
└── styles/
```

---

# 65. DATABASE SCHEMA ARCHITECTURE

Logical relationship:

```text
Organization
     │
     └── Branch
           │
     ┌─────┼───────────┐
     │     │           │
   Rooms Restaurant Inventory
     │     │           │
     │     │           └── Suppliers
     │     │
     │     └── Tables
     │         Menu
     │         Orders
     │
     └── Reservations
             │
           Guests
             │
            Stays
             │
          Invoices
             │
          Payments
```

Identity:

```text
Employee
   │
   ▼
User
   │
   ▼
Role
   │
   ▼
Permissions
```

---

# 66. API COMMUNICATION

The frontend should use a centralized API client.

Example:

```text
src/lib/api/client.ts
```

Responsibilities:

```text
Base URL
Authentication headers
Request handling
Response parsing
Error handling
Token management
Retry strategy where appropriate
```

Feature API modules:

```text
authApi
guestApi
roomApi
reservationApi
restaurantApi
inventoryApi
financeApi
reportApi
```

---

# 67. EXTERNAL SERVICES

Potential external services:

```text
Payment
├── Mobile Money
├── Card
└── Bank

Communication
├── Email
└── SMS

Storage
└── Object Storage

Monitoring
└── Application Monitoring
```

Each provider should be accessed through an abstraction.

Example:

```text
PaymentService
      ↓
PaymentGateway
      ↓
Selected Provider
```

This makes providers replaceable.

---

# 68. ARCHITECTURE DECISION RECORDS

Important architecture decisions shall be documented.

## ADR-001

```text
Decision:
Use Next.js for frontend.

Reason:
Modern React framework, routing, performance and full-stack frontend capabilities.
```

## ADR-002

```text
Decision:
Use Spring Boot for backend.

Reason:
Strong Java ecosystem, security, enterprise architecture and database integration.
```

## ADR-003

```text
Decision:
Use PostgreSQL.

Reason:
Relational integrity, transactions, indexing and mature SQL capabilities.
```

## ADR-004

```text
Decision:
Use REST API.

Reason:
Simple separation between frontend and backend and broad client compatibility.
```

## ADR-005

```text
Decision:
Use modular layered backend architecture.

Reason:
Maintainability, testability and separation of responsibilities.
```

---

# 69. DEVELOPMENT PHASES

## Phase 1 — Infrastructure

```text
Git repository
Next.js project
Spring Boot project
PostgreSQL
Environment configuration
Docker
```

## Phase 2 — Security

```text
Authentication
JWT
Users
Roles
Permissions
```

## Phase 3 — Hotel Core

```text
Guests
Rooms
Room Types
Reservations
Stays
Check-In
Check-Out
```

## Phase 4 — Operations

```text
Housekeeping
Maintenance
```

## Phase 5 — Restaurant

```text
Restaurant
Tables
Menu
POS
Orders
Kitchen
```

## Phase 6 — Inventory

```text
Inventory
Stock
Suppliers
Procurement
```

## Phase 7 — Finance

```text
Invoices
Payments
Taxes
Discounts
```

## Phase 8 — Management

```text
Reports
Dashboard
Employees
Notifications
Audit
Loyalty
Settings
```

## Phase 9 — Production

```text
Testing
Security
Monitoring
Backups
CI/CD
Deployment
```

---

# 70. ARCHITECTURE ACCEPTANCE CRITERIA

The architecture shall be considered ready for implementation when:

- Frontend and backend are clearly separated.
- PostgreSQL is isolated behind the backend.
- REST API architecture is defined.
- Authentication architecture is defined.
- Authorization architecture is defined.
- All major business modules are identified.
- Database access follows a repository pattern.
- Business logic resides in services.
- DTOs separate API contracts from entities.
- Critical operations use transactions.
- Double-booking protection is defined.
- Payment idempotency is defined.
- Inventory transactions are auditable.
- Audit logging is defined.
- External integrations are abstracted.
- Multi-branch support is defined.
- Production deployment is defined.
- Backup and recovery are defined.
- Monitoring is defined.
- CI/CD is defined.
- Security controls are defined.
- Scalability strategy is defined.

---

# 71. FINAL ARCHITECTURE DIAGRAM

```text
                                      INTERNET
                                          │
                                          ▼
                              ┌─────────────────────┐
                              │     DNS / CDN       │
                              │       HTTPS         │
                              └──────────┬──────────┘
                                         │
                     ┌───────────────────┴───────────────────┐
                     │                                       │
                     ▼                                       ▼
          ┌─────────────────────┐                 ┌─────────────────────┐
          │   PUBLIC WEBSITE    │                 │ MANAGEMENT FRONTEND │
          │                     │                 │                     │
          │ Next.js             │                 │ Next.js             │
          │ TypeScript          │                 │ TypeScript          │
          └──────────┬──────────┘                 └──────────┬──────────┘
                     │                                       │
                     └──────────────────┬────────────────────┘
                                        │
                                      HTTPS
                                        │
                                        ▼
                         ┌──────────────────────────┐
                         │     SPRING BOOT API      │
                         │                          │
                         │     REST / JSON          │
                         ├──────────────────────────┤
                         │ Spring Security          │
                         │ JWT Authentication       │
                         │ Authorization            │
                         │ Validation               │
                         ├──────────────────────────┤
                         │ Controllers              │
                         │ Services                 │
                         │ Repositories             │
                         ├──────────────────────────┤
                         │ Hotel                    │
                         │ Restaurant               │
                         │ Inventory                │
                         │ Finance                  │
                         │ Procurement              │
                         │ Reports                  │
                         │ Notifications            │
                         │ Audit                    │
                         └────────────┬─────────────┘
                                      │
                              JPA / Hibernate
                                      │
                                      ▼
                         ┌──────────────────────────┐
                         │       POSTGRESQL         │
                         │                          │
                         │ Identity                 │
                         │ Guests                   │
                         │ Rooms                    │
                         │ Reservations             │
                         │ Stays                    │
                         │ Restaurant               │
                         │ Orders                   │
                         │ Inventory                │
                         │ Finance                  │
                         │ Audit                    │
                         └──────────────────────────┘

                 ┌──────────────────────────────────────────┐
                 │            EXTERNAL SERVICES             │
                 ├──────────────────────────────────────────┤
                 │ Payment Gateway                          │
                 │ Email Provider                            │
                 │ SMS Provider                              │
                 │ Object Storage                            │
                 │ Monitoring                                │
                 └──────────────────────────────────────────┘
```

---

# 72. COMPLETE SYSTEM ARCHITECTURE

The complete architectural relationship is:

```text
USER
 │
 ▼
NEXT.JS
 │
 ├── Authentication UI
 ├── Dashboard
 ├── Reservations
 ├── Guests
 ├── Rooms
 ├── Check-In
 ├── Check-Out
 ├── Housekeeping
 ├── Maintenance
 ├── Restaurant POS
 ├── Kitchen
 ├── Inventory
 ├── Procurement
 ├── Finance
 ├── Reports
 └── Administration
 │
 ▼
HTTPS / REST / JSON
 │
 ▼
SPRING BOOT
 │
 ├── Security
 ├── Controllers
 ├── Services
 ├── Validation
 ├── Transactions
 ├── Repositories
 ├── Notifications
 ├── Reporting
 └── Integrations
 │
 ▼
POSTGRESQL
 │
 ├── Identity
 ├── Hotel
 ├── Restaurant
 ├── Inventory
 ├── Procurement
 ├── Finance
 ├── Loyalty
 └── Audit
```

---

# 73. FINAL PROJECT DESIGN STATUS

The HRMS architecture documentation now consists of:

```text
DOCUMENT 1
✓ Complete System Requirements Specification

DOCUMENT 2
✓ Database ERD + Complete Table Definitions

DOCUMENT 3
✓ Complete API Specification

DOCUMENT 4
✓ Complete UI/UX Screen Map + Navigation

DOCUMENT 5
✓ Complete System Architecture Specification

DOCUMENT 6
→ Backend Project Structure

DOCUMENT 7
→ Frontend Project Structure

DOCUMENT 8
→ PostgreSQL Migration Scripts

DOCUMENT 9
→ Spring Boot Implementation

DOCUMENT 10
→ Next.js Implementation

DOCUMENT 11
→ Testing Strategy

DOCUMENT 12
→ Deployment Specification
```

---

# END OF SYSTEM ARCHITECTURE SPECIFICATION

**Version:** 1.0  
**Frontend:** Next.js + TypeScript  
**Backend:** Java + Spring Boot  
**Database:** PostgreSQL  
**API:** REST + JSON  
**Authentication:** JWT  
**Architecture:** Modular Layered Architecture  
**Status:** Ready for Backend and Frontend Project Structure Design