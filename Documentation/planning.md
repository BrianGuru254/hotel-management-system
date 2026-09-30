# Hotel Management System — Master Project Plan

**Project Type:** Hospitality Management System
**Architecture Reference:** Panari-style hotel/property operations
**Architecture Pattern:** Modular Monolith
**Primary Backend:** NestJS + TypeScript
**Primary Database:** PostgreSQL
**Frontend:** React + TypeScript
**Public Website:** Next.js + TypeScript
**Cache / Queue:** Redis + BullMQ
**ORM:** Prisma
**Version:** 1.0
**Status:** Planning & Architecture

---

# 1. Project Vision

Build a complete, modern Hotel Management System (HMS) designed around the operational structure of a large hotel such as Panari Hotels.

The system will manage the complete hotel lifecycle:

* Guest acquisition
* Reservations
* Rooms
* Front office
* Check-in/check-out
* Guest folios
* Billing
* Payments
* Restaurants
* Bars
* POS
* Kitchen
* Events
* Conferences
* Weddings
* Housekeeping
* Maintenance
* Spa and wellness
* Activities
* Inventory
* Procurement
* Suppliers
* Staff
* Finance
* Reporting
* Notifications
* Loyalty
* Administration

The system should initially support a single large hotel property while being architected to support multiple properties in the future.

---

# 2. Architectural Philosophy

The system will not be designed as a simple room-booking application.

It will be designed as a:

> **Hospitality Management Platform**

The core business model is:

```text
Organization
    |
    +-- Property
          |
          +-- Accommodation
          |
          +-- Business Units
          |      |
          |      +-- Restaurant
          |      +-- Bar
          |      +-- Cafe
          |      +-- Spa
          |      +-- Events
          |
          +-- Facilities
          |
          +-- Experiences
          |
          +-- Operations
```

The architecture must be:

* Modular
* Secure
* Scalable
* Maintainable
* Configuration-driven
* API-first
* Multi-property capable
* Integration-ready

---

# 3. Business Architecture

## 3.1 Organization

The highest-level entity.

```text
Organization
    |
    +-- Properties
```

Example:

```text
Hotel Group
    |
    +-- Nairobi Property
    +-- Mombasa Property
    +-- Kisumu Property
```

The initial implementation may operate with one property, but all major business entities should support `property_id`.

---

# 4. Property Management

A property represents a physical hotel.

```text
Property
|
+-- Property Information
+-- Buildings
+-- Floors
+-- Rooms
+-- Facilities
+-- Business Units
+-- Venues
+-- Experiences
+-- Policies
+-- Operating Hours
+-- Configuration
```

## Property Types

The database should support configurable property types:

* City Hotel
* Resort
* Safari Lodge
* Safari Camp
* Beach Resort
* Heritage Hotel
* Boutique Hotel
* Villa
* Other

---

# 5. Core System Modules

The initial system will contain the following modules.

```text
01. Authentication
02. Users & Roles
03. Property Management
04. Room Management
05. Room Types
06. Rate Management
07. Guest Management
08. Reservations
09. Availability
10. Front Office
11. Check-in
12. Check-out
13. Guest Folios
14. Billing
15. Payments
16. Housekeeping
17. Maintenance
18. Restaurants
19. POS
20. Kitchen
21. Events
22. Venues
23. Event Packages
24. Spa & Wellness
25. Activities & Experiences
26. Inventory
27. Procurement
28. Suppliers
29. Finance
30. Staff Management
31. Notifications
32. Loyalty
33. Reports
34. Audit & Administration
```

---

# 6. Accommodation Management

## 6.1 Room Types

Room types define categories of accommodation.

Examples:

* Standard Room
* Deluxe Room
* Executive Room
* Junior Suite
* Executive Suite
* Presidential Suite

A room type contains:

```text
Room Type
|
+-- Name
+-- Description
+-- Maximum Occupancy
+-- Bed Configuration
+-- Amenities
+-- Images
+-- Base Rate
+-- Status
```

## 6.2 Rooms

Physical rooms belong to a room type.

```text
Room
|
+-- Room Number
+-- Room Type
+-- Building
+-- Floor
+-- Status
+-- Housekeeping Status
+-- Maintenance Status
+-- Features
```

Room statuses:

```text
AVAILABLE
RESERVED
OCCUPIED
DIRTY
CLEANING
CLEAN
INSPECTED
OUT_OF_ORDER
MAINTENANCE
```

---

# 7. Rate Management

Rates must be independent from physical rooms.

## Rate Plans

Examples:

* Standard Rate
* Corporate Rate
* Government Rate
* Resident Rate
* Non-Resident Rate
* Weekend Rate
* Promotional Rate
* Package Rate

A rate plan may contain:

```text
Rate Plan
|
+-- Property
+-- Room Type
+-- Season
+-- Occupancy
+-- Meal Plan
+-- Price
+-- Cancellation Policy
+-- Valid From
+-- Valid Until
```

---

# 8. Guest Management

Guests have a central profile.

```text
Guest
|
+-- Personal Information
+-- Contact Information
+-- Identification
+-- Preferences
+-- Reservations
+-- Visits
+-- Folios
+-- Payments
+-- Complaints
+-- Loyalty Account
```

The system should prevent unnecessary duplicate guest records.

---

# 9. Reservation Management

Reservations are central to the accommodation system.

## Reservation Lifecycle

```text
Inquiry
   |
   v
Reservation
   |
   v
Confirmed
   |
   v
Checked In
   |
   v
In House
   |
   v
Checked Out
   |
   v
Completed
```

Alternative states:

```text
Cancelled
No Show
Rejected
```

## Reservation Features

* New reservation
* Modify reservation
* Cancel reservation
* Room assignment
* Room transfer
* Availability search
* Walk-in reservation
* Group reservation
* Corporate reservation
* Package reservation
* Early check-in
* Late checkout
* No-show management

---

# 10. Front Office

The front office provides reception staff with a central operational interface.

Features:

* Arrival list
* Departure list
* In-house guests
* Room status
* Reservation lookup
* Guest lookup
* Check-in
* Check-out
* Walk-in
* Room change
* Guest messaging
* Folio access
* Payment collection

---

# 11. Check-in

Standard workflow:

```text
Reservation
    |
    v
Guest verification
    |
    v
Room assignment
    |
    v
Registration
    |
    v
Payment / Guarantee
    |
    v
Check-in
    |
    v
Room becomes OCCUPIED
```

---

# 12. Check-out

```text
Guest
   |
   v
Review Folio
   |
   v
Add Outstanding Charges
   |
   v
Payment
   |
   v
Invoice
   |
   v
Check-out
   |
   v
Room = DIRTY
   |
   v
Housekeeping
```

---

# 13. Guest Folio

The folio is one of the central financial objects.

A guest should have one consolidated folio for their stay.

Possible charges:

* Room
* Breakfast
* Restaurant
* Bar
* Room service
* Laundry
* Spa
* Activities
* Transport
* Events
* Other services

Example:

```text
Guest Folio

Room Charge              25,000
Breakfast                 3,000
Restaurant                4,500
Spa                       5,000
Laundry                     800
--------------------------------
Total                    38,300

Payment                  20,000
--------------------------------
Balance                  18,300
```

---

# 14. Billing

Billing must support:

* Charges
* Discounts
* Taxes
* Service charges
* Deposits
* Payments
* Refunds
* Credit
* Invoices
* Receipts

All revenue-generating departments should be able to post charges to a guest folio where appropriate.

---

# 15. Payments

Supported payment methods:

```text
CASH
CARD
M_PESA
BANK_TRANSFER
MOBILE_MONEY
VOUCHER
CREDIT
OTHER
```

Payment integrations should be implemented behind a common payment abstraction.

```text
Payment Service
|
+-- Cash
+-- Card
+-- M-Pesa
+-- Bank Transfer
+-- Other
```

This allows new payment providers to be added without redesigning the billing system.

---

# 16. Food & Beverage

F&B is a complete business module.

```text
Food & Beverage
|
+-- Outlets
|     |
|     +-- Restaurant
|     +-- Cafe
|     +-- Bar
|     +-- Lounge
|     +-- Room Service
|
+-- Menus
+-- Menu Categories
+-- Menu Items
+-- Tables
+-- Orders
+-- Kitchen
+-- KOT
+-- Bills
+-- Payments
+-- Inventory
```

---

# 17. Restaurant POS

Order workflow:

```text
Waiter
   |
   v
Table
   |
   v
Order
   |
   v
Kitchen
   |
   v
KOT
   |
   v
Preparation
   |
   v
Serve
   |
   v
Bill
   |
   v
Payment
```

Room service:

```text
Room Service Order
        |
        v
Guest Room
        |
        v
Guest Folio
```

---

# 18. Kitchen

Kitchen operations should support:

* Kitchen orders
* KOT
* Order status
* Preparation
* Completed orders
* Multiple kitchen stations
* Menu item routing
* Priority
* Cancelled items
* Kitchen performance

Order statuses:

```text
NEW
ACCEPTED
PREPARING
READY
SERVED
CANCELLED
```

---

# 19. Events & Conferences

Events are a first-class business module.

```text
Events
|
+-- Event Enquiries
+-- Customers
+-- Venues
+-- Venue Configurations
+-- Quotations
+-- Packages
+-- Catering
+-- Equipment
+-- Staff
+-- Bookings
+-- Invoices
+-- Payments
```

---

# 20. Venues

A venue can support different layouts.

Example:

```text
Conference Hall

Theatre       500
Classroom     250
Banquet       300
Cocktail      400
Boardroom      40
```

Venue configuration must be stored as data rather than hard-coded.

---

# 21. Event Booking Workflow

```text
Event Enquiry
      |
      v
Customer Requirements
      |
      v
Venue Selection
      |
      v
Package Selection
      |
      v
Catering Selection
      |
      v
Equipment Selection
      |
      v
Quotation
      |
      v
Customer Approval
      |
      v
Event Booking
      |
      v
Invoice
      |
      v
Payment
```

---

# 22. Housekeeping

Housekeeping manages room cleanliness and related operations.

Features:

* Room status
* Cleaning tasks
* Task assignment
* Inspections
* Linen
* Lost & found
* Cleaning reports
* Maintenance requests

Room workflow:

```text
Checkout
   |
   v
DIRTY
   |
   v
Cleaning
   |
   v
Inspection
   |
   v
CLEAN / INSPECTED
   |
   v
Available
```

---

# 23. Maintenance

Maintenance is separate from housekeeping.

Features:

* Work orders
* Equipment
* Assets
* Preventive maintenance
* Repairs
* Technicians
* Maintenance history
* Room maintenance
* Property maintenance

Example:

```text
Room 405
   |
   v
AC Failure
   |
   v
Maintenance Request
   |
   v
Work Order
   |
   v
Technician
   |
   v
Repair
   |
   v
Completed
```

---

# 24. Spa & Wellness

```text
Spa
|
+-- Services
+-- Therapists
+-- Treatment Rooms
+-- Appointments
+-- Packages
+-- Products
+-- Payments
```

Spa charges can be posted to the guest folio.

---

# 25. Activities & Experiences

The platform should support activities offered by the property.

Examples:

* Tours
* Entertainment
* Wellness activities
* Recreational activities
* Cultural experiences
* Family activities
* Adventure activities

Activity structure:

```text
Experience
|
+-- Name
+-- Category
+-- Description
+-- Duration
+-- Capacity
+-- Price
+-- Schedule
+-- Availability
+-- Booking
```

---

# 26. Inventory

Central inventory serves hotel departments.

```text
Central Store
|
+-- F&B
+-- Housekeeping
+-- Spa
+-- Maintenance
+-- Other Departments
```

Inventory features:

* Products
* Categories
* Units
* Warehouses
* Stores
* Suppliers
* Stock
* Stock movements
* Stock counts
* Stock adjustments
* Transfers
* Minimum stock
* Reordering

---

# 27. Procurement

Procurement workflow:

```text
Department Request
       |
       v
Purchase Request
       |
       v
Approval
       |
       v
Purchase Order
       |
       v
Supplier
       |
       v
Goods Received
       |
       v
Inventory
       |
       v
Supplier Invoice
       |
       v
Payment
```

---

# 28. Finance

Hotel finance features:

* Guest folios
* Invoices
* Receipts
* Payments
* Refunds
* Taxes
* Discounts
* Revenue
* Expenses
* Cashier sessions
* Cash reconciliation
* Accounts receivable
* Supplier payments
* Financial reports

---

# 29. Staff Management

```text
Staff
|
+-- Employees
+-- Departments
+-- Positions
+-- Shifts
+-- Attendance
+-- User Accounts
+-- Roles
+-- Permissions
```

The employee record and system user account should be separate concepts.

---

# 30. Authentication & Authorization

Authentication:

* Login
* Logout
* Password reset
* Session management
* Optional 2FA

Authorization:

```text
Role
   |
   +-- Permissions
```

Example permissions:

```text
reservation.create
reservation.edit
reservation.cancel

guest.view
guest.edit

payment.create
payment.refund

report.view
user.manage
inventory.adjust
```

---

# 31. Audit Logging

Important actions must be recorded.

Examples:

```text
User
Action
Entity
Entity ID
Timestamp
IP Address
Old Value
New Value
```

Audit events include:

* Login
* Reservation changes
* Room changes
* Price changes
* Payment creation
* Payment refund
* Invoice changes
* Inventory adjustments
* User permission changes

---

# 32. Reporting

Management reports will include:

## Accommodation

* Occupancy
* ADR
* RevPAR
* Room revenue
* Arrivals
* Departures
* Cancellations
* No-shows
* Room utilization

## F&B

* Daily sales
* Outlet sales
* Menu item sales
* Average order value
* Payment methods
* Void/discount reports

## Events

* Event revenue
* Venue utilization
* Event bookings
* Outstanding quotations
* Event profitability

## Finance

* Revenue
* Payments
* Outstanding balances
* Refunds
* Expenses
* Cashier reconciliation

## Inventory

* Stock levels
* Stock valuation
* Stock movement
* Low-stock items
* Purchases
* Supplier balances

---

# 33. Dashboard

The management dashboard will provide:

```text
Occupancy
Revenue
ADR
RevPAR
Arrivals
Departures
In-House Guests
Outstanding Balances
F&B Revenue
Event Revenue
Spa Revenue
Inventory Alerts
Maintenance Alerts
Housekeeping Alerts
```

---

# 34. Notifications

Notification channels:

```text
Email
SMS
WhatsApp
In-App
```

Events that trigger notifications:

* Booking confirmation
* Booking cancellation
* Check-in reminder
* Payment receipt
* Invoice
* Checkout reminder
* Event confirmation
* Staff notification
* Low inventory
* Maintenance alert

Notifications should be asynchronous.

```text
Application
    |
    v
Notification Queue
    |
    +-- Email
    +-- SMS
    +-- WhatsApp
```

---

# 35. Loyalty

Future loyalty module:

```text
Loyalty
|
+-- Members
+-- Membership Levels
+-- Points
+-- Transactions
+-- Rewards
+-- Benefits
+-- Promotions
```

---

# 36. Technology Stack

## Frontend — HMS

```text
React
TypeScript
Vite
Tailwind CSS
shadcn/ui
TanStack Query
TanStack Table
React Hook Form
Zod
Recharts
```

## Public Website

```text
Next.js
TypeScript
Tailwind CSS
```

## Backend

```text
Node.js
NestJS
TypeScript
REST API
WebSockets
```

## Database

```text
PostgreSQL
Prisma ORM
```

## Cache & Queues

```text
Redis
BullMQ
```

## Testing

```text
Vitest
Jest
Playwright
```

## Development

```text
Git
GitHub
VS Code
Docker
Docker Compose
pnpm
ESLint
Prettier
Husky
```

---

# 37. System Architecture

```text
                         INTERNET
                            |
                            v
                     CDN / LOAD BALANCER
                            |
             +--------------+--------------+
             |                             |
             v                             v
       PUBLIC WEBSITE                 HMS FRONTEND
         Next.js                         React
             |                             |
             +--------------+--------------+
                            |
                            v
                       API LAYER
                         NestJS
                            |
       +--------------------+--------------------+
       |                    |                    |
       v                    v                    v
  PostgreSQL              Redis               BullMQ
       |                    |                    |
       |                    |                    |
       +--------------------+--------------------+
                            |
                            v
                     Object Storage
```

---

# 38. Backend Architecture

The backend will use a modular monolith.

```text
backend/
|
+-- auth/
+-- users/
+-- roles/
+-- properties/
|
+-- guests/
+-- reservations/
+-- availability/
+-- rooms/
+-- room-types/
+-- rates/
|
+-- front-office/
+-- check-in/
+-- check-out/
+-- folios/
+-- billing/
+-- payments/
|
+-- housekeeping/
+-- maintenance/
|
+-- restaurants/
+-- menus/
+-- pos/
+-- kitchen/
|
+-- events/
+-- venues/
+-- event-packages/
|
+-- spa/
+-- activities/
|
+-- inventory/
+-- suppliers/
+-- procurement/
|
+-- finance/
+-- reports/
+-- notifications/
+-- loyalty/
|
+-- audit/
+-- common/
```

---

# 39. Frontend Architecture

```text
frontend/
|
+-- src/
    |
    +-- components/
    +-- layouts/
    +-- pages/
    |
    +-- features/
    |   |
    |   +-- auth/
    |   +-- dashboard/
    |   +-- reservations/
    |   +-- rooms/
    |   +-- guests/
    |   +-- front-office/
    |   +-- housekeeping/
    |   +-- maintenance/
    |   +-- restaurants/
    |   +-- pos/
    |   +-- events/
    |   +-- spa/
    |   +-- inventory/
    |   +-- procurement/
    |   +-- finance/
    |   +-- reports/
    |
    +-- hooks/
    +-- services/
    +-- types/
    +-- utils/
    +-- validation/
```

---

# 40. Public Website Architecture

The public website should be separate from the internal HMS.

```text
Website
|
+-- Home
+-- Hotel
+-- Rooms
+-- Dining
+-- Events
+-- Spa
+-- Activities
+-- Offers
+-- Facilities
+-- Gallery
+-- Contact
+-- Booking
```

The public website communicates with the booking API rather than directly accessing the database.

---

# 41. API Architecture

REST API version:

```text
/api/v1
```

Examples:

```text
GET    /api/v1/rooms
GET    /api/v1/rooms/:id

GET    /api/v1/guests
POST   /api/v1/guests

GET    /api/v1/reservations
POST   /api/v1/reservations
PATCH  /api/v1/reservations/:id
DELETE /api/v1/reservations/:id

POST   /api/v1/check-ins
POST   /api/v1/check-outs

GET    /api/v1/folios/:id
POST   /api/v1/folios/:id/charges

POST   /api/v1/payments

GET    /api/v1/restaurants
POST   /api/v1/orders

GET    /api/v1/events
POST   /api/v1/events

GET    /api/v1/inventory
POST   /api/v1/purchase-orders

GET    /api/v1/reports
```

---

# 42. Database Architecture

Core entities:

```text
organizations
properties
buildings
floors

room_types
rooms
room_amenities

rate_plans
rate_prices
seasons

guests
guest_documents
guest_preferences

reservations
reservation_rooms
reservation_guests

check_ins
check_outs

folios
folio_items
invoices
invoice_items
payments
refunds

business_units

restaurants
outlets
menus
menu_categories
menu_items
tables
orders
order_items
kitchen_orders

venues
venue_configurations
event_enquiries
event_quotations
event_bookings
event_packages

spa_services
spa_therapists
spa_appointments

activities
activity_bookings

housekeeping_tasks
maintenance_requests
maintenance_work_orders

products
categories
units
warehouses
stock
stock_movements

suppliers
purchase_requests
purchase_orders
purchase_order_items
goods_receipts

employees
departments
positions
shifts

users
roles
permissions

notifications
audit_logs

loyalty_accounts
loyalty_transactions
rewards
```

---

# 43. Database Principles

The database must:

* Use PostgreSQL
* Use UUIDs where appropriate
* Enforce foreign keys
* Use database constraints
* Use transactions for financial operations
* Track created/updated timestamps
* Support soft deletion where appropriate
* Maintain audit records for critical operations
* Avoid unnecessary duplication
* Use indexes for high-volume queries
* Preserve financial history

Financial records should generally not be physically deleted.

---

# 44. Security Architecture

Security requirements:

* HTTPS
* Secure authentication
* Password hashing
* Role-based authorization
* Permission checks
* Input validation
* Rate limiting
* Secure cookies
* Session management
* Audit logging
* Database backups
* Encryption of sensitive data
* Least-privilege access

---

# 45. Concurrency & Double Booking

Room availability must be transaction-safe.

Example:

```text
Guest A                     Guest B
   |                           |
   |--- Book Room 405 -------->|
   |                           |
   |                     Book Room 405
   |                           |
   +---------- DATABASE TRANSACTION
                         |
                         v
                  Only one succeeds
```

PostgreSQL transactions and appropriate locking/constraints will be used to prevent double booking.

---

# 46. Background Processing

Long-running operations should use queues.

Examples:

```text
Generate Invoice
Send Email
Send SMS
Send WhatsApp
Generate Report
Process Payment Callback
Update Analytics
Generate PDF
```

Architecture:

```text
Application
    |
    v
BullMQ
    |
    v
Worker
    |
    +-- Email
    +-- SMS
    +-- Reports
    +-- Documents
```

---

# 47. Integrations

Initial integrations:

```text
M-Pesa
Email
SMS
```

Future integrations:

```text
WhatsApp
Payment Gateways
Accounting Systems
Channel Managers
Online Travel Agencies
Booking Platforms
Identity Verification
Maps
Analytics
Cloud Storage
```

All external services should be isolated behind integration interfaces.

---

# 48. Development Phases

## Phase 0 — Architecture

Deliverables:

* Requirements specification
* Architecture
* ERD
* API specification
* UI specification
* Security model

---

## Phase 1 — Foundation

Build:

* Project setup
* Authentication
* Users
* Roles
* Permissions
* Property
* Departments
* Audit logging
* Database foundation

---

## Phase 2 — Accommodation

Build:

* Room types
* Rooms
* Rates
* Availability
* Guests
* Reservations
* Check-in
* Check-out
* Front desk

---

## Phase 3 — Billing

Build:

* Folios
* Charges
* Invoices
* Payments
* Receipts
* Refunds
* Taxes
* Discounts

---

## Phase 4 — Housekeeping & Maintenance

Build:

* Housekeeping dashboard
* Cleaning tasks
* Room inspection
* Maintenance requests
* Work orders
* Assets

---

## Phase 5 — F&B

Build:

* Restaurants
* Menus
* Tables
* POS
* Kitchen
* KOT
* Room service
* Folio integration

---

## Phase 6 — Events

Build:

* Venues
* Configurations
* Event enquiries
* Quotations
* Packages
* Catering
* Event bookings
* Event billing

---

## Phase 7 — Inventory & Procurement

Build:

* Products
* Stores
* Stock
* Suppliers
* Purchase requests
* Purchase orders
* Goods receiving
* Stock movements

---

## Phase 8 — Spa & Activities

Build:

* Spa services
* Therapists
* Appointments
* Activities
* Experience bookings
* Folio integration

---

## Phase 9 — Reporting

Build:

* Management dashboard
* Revenue reports
* Occupancy reports
* F&B reports
* Event reports
* Inventory reports
* Financial reports

---

## Phase 10 — Integrations

Build:

* M-Pesa
* SMS
* Email
* WhatsApp
* Online booking
* External booking channels

---

## Phase 11 — Advanced Platform

Future:

* Multi-property
* Loyalty
* Mobile applications
* Advanced analytics
* Revenue management
* AI-assisted reporting
* Channel management

---

# 49. Testing Strategy

## Unit Tests

Examples:

```text
calculateRoomRate()
calculateTax()
calculateInvoice()
calculateBalance()
checkAvailability()
calculateDiscount()
```

## Integration Tests

Examples:

```text
Reservation -> Room
Payment -> Folio
Checkout -> Room Status
POS -> Inventory
Restaurant -> Folio
Spa -> Folio
Event -> Invoice
```

## End-to-End Tests

Example complete guest journey:

```text
Login
  ↓
Create Guest
  ↓
Create Reservation
  ↓
Assign Room
  ↓
Check In
  ↓
Restaurant Charge
  ↓
Spa Charge
  ↓
Payment
  ↓
Generate Invoice
  ↓
Check Out
```

---

# 50. Deployment

Environments:

```text
Development
     ↓
Staging
     ↓
Production
```

Docker services:

```text
frontend
backend
postgres
redis
worker
```

Production architecture:

```text
Internet
   |
   v
CDN / Load Balancer
   |
   +-- Frontend
   |
   +-- API
         |
         +-- PostgreSQL
         +-- Redis
         +-- Workers
         +-- Object Storage
```

---

# 51. Backup Strategy

PostgreSQL backups must be automated.

Requirements:

* Daily backups
* Point-in-time recovery where supported
* Backup verification
* Off-site backup
* Retention policy
* Disaster recovery procedure

---

# 52. UI Design Principles

The HMS interface should prioritize hotel staff productivity.

Design principles:

* Fast navigation
* Minimal clicks
* Clear room status
* Search everywhere
* Keyboard-friendly workflows
* Responsive design
* Role-specific dashboards
* Clear alerts
* Consistent tables/forms
* Confirmation for destructive actions

Primary navigation:

```text
Dashboard

Front Office
  Reservations
  Check-in
  Check-out
  Guests
  Rooms

Operations
  Housekeeping
  Maintenance

Food & Beverage
  Restaurants
  POS
  Kitchen

Events
  Venues
  Bookings
  Quotations

Wellness
  Spa
  Activities

Inventory
  Stock
  Purchasing
  Suppliers

Finance
  Folios
  Invoices
  Payments

Reports

Administration
  Users
  Roles
  Settings
  Audit Logs
```

---

# 53. Key Business Rules

Important rules will be formally documented before implementation.

Examples:

### Room Availability

A room cannot have overlapping confirmed reservations.

### Check-in

A guest cannot be checked into an unavailable or out-of-order room.

### Check-out

A guest cannot complete checkout with unresolved financial requirements unless an authorized user overrides the rule.

### Payment

Payments must be immutable financial records.

### Refund

Refunds require appropriate permission and must reference the original payment.

### Inventory

Stock movements must be recorded rather than simply changing stock quantities without history.

### Audit

Critical changes must create audit records.

---

# 54. Success Criteria

The first production version should allow hotel staff to perform:

```text
Create Guest
       ↓
Create Reservation
       ↓
Assign Room
       ↓
Check In
       ↓
Manage Stay
       ↓
Post Restaurant Charge
       ↓
Post Other Charges
       ↓
Receive Payment
       ↓
Generate Invoice
       ↓
Check Out
       ↓
Room Cleaning
       ↓
Room Available Again
```

Additionally:

```text
Create Event
       ↓
Select Venue
       ↓
Create Quotation
       ↓
Confirm Booking
       ↓
Add Catering
       ↓
Generate Invoice
       ↓
Receive Payment
```

And:

```text
Restaurant
   ↓
Order
   ↓
Kitchen
   ↓
Serve
   ↓
Bill
   ↓
Payment / Folio
   ↓
Inventory
```

---

# 55. Long-Term Vision

The system should eventually evolve from a single-property HMS into a complete hospitality platform.

```text
                    HOSPITALITY PLATFORM
                            |
        +-------------------+-------------------+
        |                   |                   |
   PROPERTY OPS       CENTRAL SERVICES      CUSTOMER
        |                   |                   |
   Hotels              Reservations          Website
   Resorts             CRM                   Mobile
   Lodges              Loyalty               Portal
   Camps               Marketing             Booking
        |
        +-- Rooms
        +-- F&B
        +-- Events
        +-- Spa
        +-- Activities
        +-- Inventory
        +-- Procurement
        +-- Finance
```

The initial implementation will focus on a **Panari-style single large hotel**, while preserving the architectural capability to become a multi-property platform.

---

# 56. Immediate Next Steps

The planning sequence is:

```text
STEP 1
Requirements
    ↓
STEP 2
System Architecture
    ↓
STEP 3
Database ERD
    ↓
STEP 4
Database Schema
    ↓
STEP 5
API Specification
    ↓
STEP 6
UI/UX Specification
    ↓
STEP 7
Project Setup
    ↓
STEP 8
Authentication
    ↓
STEP 9
Property + Rooms
    ↓
STEP 10
Reservations
    ↓
STEP 11
Front Office
    ↓
STEP 12
Billing + Payments
    ↓
STEP 13
Operations
    ↓
STEP 14
F&B + POS
    ↓
STEP 15
Events
    ↓
STEP 16
Inventory + Procurement
    ↓
STEP 17
Reports
    ↓
STEP 18
Integrations
    ↓
STEP 19
Testing
    ↓
STEP 20
Production
```

---

# 57. Current Architecture Decision

The project will use:

```text
Architecture:
Modular Monolith

Frontend:
React + TypeScript

Public Website:
Next.js + TypeScript

Backend:
NestJS + TypeScript

Database:
PostgreSQL

ORM:
Prisma

Cache:
Redis

Queues:
BullMQ

Containerization:
Docker

API:
REST + WebSockets

Testing:
Vitest + Playwright

Version Control:
Git + GitHub
```

The business architecture is based on a **Panari-style full-service hotel**, with accommodation, food & beverage, events, wellness, activities, housekeeping, maintenance, inventory, procurement, finance and guest services.

---

# 58. Document Status

**Status:** Architecture Planning

**Completed:**

* Product vision
* Business architecture
* Core modules
* Technology stack
* System architecture
* Development roadmap
* Initial database entity plan
* Security direction
* Testing strategy
* Deployment direction
