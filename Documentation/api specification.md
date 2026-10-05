# HOTEL & RESTAURANT MANAGEMENT SYSTEM

# COMPLETE API SPECIFICATION

**Project:** Hotel & Restaurant Management System (HRMS)  
**Document:** REST API Specification  
**Version:** 1.0  
**Status:** Implementation-Ready  
**Backend:** Java + Spring Boot  
**Database:** PostgreSQL  
**Frontend:** Next.js + TypeScript  
**API Style:** RESTful JSON API  
**Base URL:** `/api/v1`  
**Date:** September 2026

---

# TABLE OF CONTENTS

1. API Overview
2. API Architecture
3. Base URL
4. HTTP Methods
5. Authentication
6. Authorization
7. Standard Headers
8. Response Format
9. Error Handling
10. Validation
11. Pagination
12. Filtering
13. Sorting
14. Authentication APIs
15. User APIs
16. Role & Permission APIs
17. Organization APIs
18. Branch APIs
19. Employee APIs
20. Guest APIs
21. Room Type APIs
22. Room APIs
23. Amenity APIs
24. Reservation APIs
25. Stay APIs
26. Check-In APIs
27. Check-Out APIs
28. Housekeeping APIs
29. Maintenance APIs
30. Restaurant APIs
31. Restaurant Table APIs
32. Menu Category APIs
33. Menu Item APIs
34. Order APIs
35. Kitchen APIs
36. Tax APIs
37. Discount APIs
38. Invoice APIs
39. Payment APIs
40. Inventory Category APIs
41. Inventory Item APIs
42. Inventory Transaction APIs
43. Supplier APIs
44. Purchase Order APIs
45. Loyalty APIs
46. Notification APIs
47. Audit APIs
48. System Settings APIs
49. Dashboard APIs
50. Reporting APIs
51. File APIs
52. Health APIs
53. API Security
54. API Rate Limiting
55. API Versioning
56. HTTP Status Codes
57. Complete Endpoint Map
58. API Implementation Rules

---

# 1. API OVERVIEW

The HRMS API provides communication between:

```text
Next.js Frontend
       │
       │ HTTPS / JSON
       ▼
Spring Boot REST API
       │
       ├── Authentication
       ├── Hotel Services
       ├── Restaurant Services
       ├── Inventory Services
       ├── Finance Services
       ├── Reporting
       └── Administration
       │
       ▼
PostgreSQL
```

The API shall use JSON for request and response bodies.

---

# 2. API ARCHITECTURE

The backend should follow a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Example:

```text
ReservationController
        ↓
ReservationService
        ↓
ReservationRepository
        ↓
PostgreSQL
```

Controllers shall handle HTTP concerns.

Services shall contain business logic.

Repositories shall handle database access.

---

# 3. BASE URL

All API endpoints shall use:

```text
/api/v1
```

Example:

```text
GET /api/v1/rooms
```

Production example:

```text
https://api.example.com/api/v1/rooms
```

The production domain shall be configured through environment variables.

---

# 4. HTTP METHODS

| Method | Purpose |
|---|---|
| GET | Retrieve data |
| POST | Create data |
| PUT | Replace/update data |
| PATCH | Partially update data |
| DELETE | Delete/deactivate data |

---

# 5. AUTHENTICATION

The API shall use token-based authentication.

Recommended implementation:

```text
JWT
```

Authentication flow:

```text
Login
  ↓
Validate credentials
  ↓
Generate access token
  ↓
Frontend stores token securely
  ↓
Frontend sends token
  ↓
API validates token
  ↓
Request authorized
```

Header:

```http
Authorization: Bearer <access-token>
```

---

# 6. AUTHORIZATION

Every protected endpoint shall verify:

1. User authentication.
2. User account status.
3. User role.
4. Required permission.

Example:

```text
GET /api/v1/reservations
```

Required permission:

```text
RESERVATION_VIEW
```

Creating:

```text
POST /api/v1/reservations
```

Required permission:

```text
RESERVATION_CREATE
```

---

# 7. STANDARD HEADERS

Requests should include:

```http
Content-Type: application/json
Accept: application/json
Authorization: Bearer <token>
```

For file uploads:

```http
Content-Type: multipart/form-data
```

---

# 8. STANDARD SUCCESS RESPONSE

A standard response shall use:

```json
{
  "success": true,
  "data": {},
  "message": "Operation completed successfully",
  "timestamp": "2026-09-23T13:00:00Z"
}
```

---

# 9. LIST RESPONSE

```json
{
  "success": true,
  "data": [],
  "pagination": {
    "page": 1,
    "limit": 20,
    "totalItems": 100,
    "totalPages": 5
  }
}
```

---

# 10. ERROR RESPONSE

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid request",
    "details": [
      {
        "field": "checkOutDate",
        "message": "Check-out date must be after check-in date"
      }
    ]
  },
  "timestamp": "2026-09-23T13:00:00Z"
}
```

---

# 11. VALIDATION

The API shall validate all incoming data.

Examples:

```text
Required fields
Maximum length
Minimum length
Email format
Phone format
Numeric values
Date ranges
UUID format
Enum values
Business rules
```

Validation shall happen on the backend even if the frontend also validates the request.

---

# 12. PAGINATION

List endpoints shall support:

```text
?page=1&limit=20
```

Default:

```text
page = 1
limit = 20
```

Maximum:

```text
limit = 100
```

---

# 13. FILTERING

Example:

```text
GET /api/v1/reservations?status=CONFIRMED
```

Multiple filters:

```text
GET /api/v1/reservations?status=CONFIRMED&roomId=<uuid>
```

---

# 14. SORTING

Example:

```text
GET /api/v1/guests?sort=lastName&direction=asc
```

---

# 15. AUTHENTICATION APIs

## 15.1 Login

```http
POST /api/v1/auth/login
```

Request:

```json
{
  "username": "admin",
  "password": "password"
}
```

Response:

```json
{
  "success": true,
  "data": {
    "accessToken": "JWT_TOKEN",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": "UUID",
      "username": "admin",
      "role": "SUPER_ADMIN"
    }
  }
}
```

---

## 15.2 Refresh Token

```http
POST /api/v1/auth/refresh
```

---

## 15.3 Logout

```http
POST /api/v1/auth/logout
```

---

## 15.4 Current User

```http
GET /api/v1/auth/me
```

---

## 15.5 Forgot Password

```http
POST /api/v1/auth/forgot-password
```

Request:

```json
{
  "email": "user@example.com"
}
```

---

## 15.6 Reset Password

```http
POST /api/v1/auth/reset-password
```

Request:

```json
{
  "token": "RESET_TOKEN",
  "newPassword": "NewPassword123!"
}
```

---

# 16. USER APIs

## List Users

```http
GET /api/v1/users
```

Permission:

```text
USER_VIEW
```

## Get User

```http
GET /api/v1/users/{id}
```

## Create User

```http
POST /api/v1/users
```

Request:

```json
{
  "employeeId": "UUID",
  "roleId": "UUID",
  "username": "reception01",
  "email": "reception@example.com",
  "password": "Password123!"
}
```

## Update User

```http
PUT /api/v1/users/{id}
```

## Disable User

```http
PATCH /api/v1/users/{id}/status
```

Request:

```json
{
  "status": "INACTIVE"
}
```

---

# 17. ROLE APIs

## List Roles

```http
GET /api/v1/roles
```

## Get Role

```http
GET /api/v1/roles/{id}
```

## Create Role

```http
POST /api/v1/roles
```

## Update Role

```http
PUT /api/v1/roles/{id}
```

## Delete Role

```http
DELETE /api/v1/roles/{id}
```

---

# 18. PERMISSION APIs

## List Permissions

```http
GET /api/v1/permissions
```

## Get Permissions for Role

```http
GET /api/v1/roles/{id}/permissions
```

## Assign Permissions

```http
PUT /api/v1/roles/{id}/permissions
```

Request:

```json
{
  "permissionIds": [
    "UUID",
    "UUID"
  ]
}
```

---

# 19. ORGANIZATION APIs

## Get Organization

```http
GET /api/v1/organization
```

## Update Organization

```http
PUT /api/v1/organization
```

Request:

```json
{
  "name": "Example Hotel",
  "legalName": "Example Hotel Limited",
  "phone": "+254700000000",
  "email": "info@example.com",
  "currencyCode": "KES"
}
```

---

# 20. BRANCH APIs

## List Branches

```http
GET /api/v1/branches
```

## Get Branch

```http
GET /api/v1/branches/{id}
```

## Create Branch

```http
POST /api/v1/branches
```

## Update Branch

```http
PUT /api/v1/branches/{id}
```

## Disable Branch

```http
PATCH /api/v1/branches/{id}/status
```

---

# 21. EMPLOYEE APIs

## List Employees

```http
GET /api/v1/employees
```

## Get Employee

```http
GET /api/v1/employees/{id}
```

## Create Employee

```http
POST /api/v1/employees
```

Request:

```json
{
  "branchId": "UUID",
  "departmentId": "UUID",
  "userId": "UUID",
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+254700000000",
  "email": "john@example.com",
  "jobTitle": "Receptionist",
  "employmentStatus": "ACTIVE",
  "hireDate": "2026-10-05"
}
```

`departmentId` and `userId` are optional. List filters include `organizationId`, `branchId`, `departmentId`, `status`, and `search`, with pagination and sorting. Delete archives the employee by marking the record INACTIVE; it does not remove the record.

## Update Employee

```http
PUT /api/v1/employees/{id}
```

## Delete Employee

```http
DELETE /api/v1/employees/{id}
```

---

# 22. GUEST APIs

## List Guests

```http
GET /api/v1/guests
```

Supported filters:

```text
name
phone
email
guestNumber
identificationNumber
```

Guest lists are restricted to properties visible to the authenticated user. The endpoint supports page (zero-based, default 0), size (default 20, maximum 100), sort, and direction.

## Get Guest

```http
GET /api/v1/guests/{id}
```

## Create Guest

```http
POST /api/v1/guests
```

Request:

```json
{
  "propertyId": "UUID",
  "firstName": "Brian",
  "lastName": "Kiprop",
  "phone": "+254700000000",
  "email": "guest@example.com",
  "nationality": "Kenyan",
  "identificationType": "NATIONAL_ID",
  "identificationNumber": "12345678"
}
```

Guest profiles are stored separately per property. The server assigns guestNumber; identification numbers must be unique within that property and may be reused at a different property.

## Update Guest

```http
PUT /api/v1/guests/{id}
```

## Guest History

```http
GET /api/v1/guests/{id}/history
```

Response should include:

```text
Reservations
Stays
Orders
Invoices
Payments
Loyalty
```

The current backend returns the first 100 reservations and stays for the guest at their owning property, ordered newest first. Stay history includes both the primary guest and additional guests assigned to the stay. `availableSections` lists `reservations` and `stays`; `unavailableSections` explicitly reports `orders`, `invoices`, `payments`, and `loyalty` until those modules are implemented. The endpoint requires `GUEST_READ` capability and access to the guest's property.

---

# 23. ROOM TYPE APIs

Implemented. All requests require authentication and a matching `ROOM_TYPE_*` capability plus an applicable resource scope. A Room Type belongs to one Property; users cannot list or modify records outside properties visible to their assigned scope. Lists are paged (zero-based `page`, default 0; `size`, default 20, maximum 100) and accept `propertyId`, `name`, `code`, and `status` filters.

## List Room Types

```http
GET /api/v1/room-types
```

## Get Room Type

```http
GET /api/v1/room-types/{id}
```

## Create Room Type

```http
POST /api/v1/room-types
```

Request:

```json
{
  "propertyId": "UUID",
  "name": "Deluxe",
  "code": "DLX",
  "description": "Deluxe room",
  "basePrice": 8500,
  "maxOccupancy": 2
}
```

## Update Room Type

```http
PUT /api/v1/room-types/{id}
```

## Delete Room Type

```http
DELETE /api/v1/room-types/{id}
```

---

# 24. ROOM APIs

Implemented. All requests require authentication and a matching `ROOM_*` capability plus a resource scope. Rooms and their room types must belong to the same Property. Room lists are paged (zero-based `page`, default 0; `size`, default 20, maximum 100) and accept `propertyId`, `roomTypeId`, `roomNumber`, `status`, and `floorNumber` filters.

## List Rooms

```http
GET /api/v1/rooms
```

Filters:

```text
propertyId
roomTypeId
status
floorNumber
roomNumber
page
size
sort
direction
```

## Get Room

```http
GET /api/v1/rooms/{id}
```

## Create Room

```http
POST /api/v1/rooms
```

Request:

```json
{
  "propertyId": "UUID",
  "roomTypeId": "UUID",
  "roomNumber": "101",
  "floorNumber": 1,
  "capacity": 2,
  "description": "Corner room"
}
```

## Update Room

```http
PUT /api/v1/rooms/{id}
```

## Change Room Status

Housekeeping scopes may set `DIRTY` or `CLEANING`, and may return a dirty/cleaning room to `AVAILABLE`. Maintenance scopes may set `MAINTENANCE` or `OUT_OF_ORDER` and return those rooms to `AVAILABLE`. Broader room-management capabilities may set any defined status. A reason is required for `MAINTENANCE` and `OUT_OF_ORDER`.

```http
PATCH /api/v1/rooms/{id}/status
```

Request:

```json
{
  "status": "MAINTENANCE",
  "reason": "Air conditioner failure"
}
```

## Delete Room

```http
DELETE /api/v1/rooms/{id}
```

Rooms in `RESERVED` or `OCCUPIED` status cannot be edited or deleted.

## Assign Amenities

Replaces the room's complete amenity set. Every amenity must belong to the same property as the room. An empty `amenityIds` array clears assignments.

```http
PUT /api/v1/rooms/{id}/amenities
```

```json
{
  "amenityIds": ["UUID", "UUID"]
}
```

## Room Availability

Reservation-aware availability is deferred to the Reservations module.

```http
GET /api/v1/rooms/availability
```

Parameters:

```text
checkInDate
checkOutDate
roomTypeId
```

Example:

```text
GET /api/v1/rooms/availability?checkInDate=2026-10-01&checkOutDate=2026-10-05
```

---

# 25. AMENITY APIs

Implemented as a property-owned catalog. All requests require authentication and a matching `AMENITY_*` capability plus an applicable resource scope. Names are unique without regard to case within a property. Lists are paged (zero-based `page`, default 0; `size`, default 20, maximum 100) and accept `propertyId` and `name` filters.

## List Amenities

```http
GET /api/v1/amenities
```

## Get Amenity

```http
GET /api/v1/amenities/{id}
```

## Create Amenity

```http
POST /api/v1/amenities
```

Request:

```json
{
  "propertyId": "UUID",
  "name": "Wi-Fi",
  "description": "High-speed wireless internet"
}
```

## Update Amenity

```http
PUT /api/v1/amenities/{id}
```

## Delete Amenity

```http
DELETE /api/v1/amenities/{id}
```

## Assign Amenities

Room amenity assignment is provided by `PUT /api/v1/rooms/{id}/amenities` in the Room API. The service and database enforce same-property ownership.

---

# 26. RESERVATION APIs

## List Reservations

```http
GET /api/v1/reservations
```

Filters:

```text
status
guestId
roomId
roomTypeId
checkInDate
checkOutDate
bookingSource
```

## Get Reservation

```http
GET /api/v1/reservations/{id}
```

## Create Reservation

```http
POST /api/v1/reservations
```

Request:

```json
{
  "branchId": "UUID",
  "guestId": "UUID",
  "roomTypeId": "UUID",
  "roomId": "UUID",
  "checkInDate": "2026-10-01",
  "checkOutDate": "2026-10-05",
  "adults": 2,
  "children": 1,
  "nightlyRate": 8500,
  "depositAmount": 5000,
  "bookingSource": "DIRECT",
  "specialRequests": "Late arrival"
}
```

## Confirm Reservation

```http
PATCH /api/v1/reservations/{id}/confirm
```

## Cancel Reservation

```http
POST /api/v1/reservations/{id}/cancel
```

Request:

```json
{
  "reason": "Guest requested cancellation"
}
```

## Modify Reservation

```http
PUT /api/v1/reservations/{id}
```

## Reservation Availability

```http
POST /api/v1/reservations/check-availability
```

Request:

```json
{
  "roomTypeId": "UUID",
  "checkInDate": "2026-10-01",
  "checkOutDate": "2026-10-05"
}
```

---

# 27. STAY APIs

## List Stays

```http
GET /api/v1/stays
```

Optional filters: `propertyId`, `status`, `guestId`, `roomId`, `checkOutFrom`, `checkOutTo`, `page`, `size`, `sort`, and `direction`. The response is paginated and restricted to the caller's live property scope.

## Get Stay

```http
GET /api/v1/stays/{id}
```

## Active Stays

```http
GET /api/v1/stays/active
```

Returns active stays only, within the requested property filter and the caller's live scope.

## Add Guest to Stay

```http
POST /api/v1/stays/{id}/guests
```

Request:

```json
{
  "guestId": "UUID",
  "isPrimaryGuest": false
}
```

Each guest profile must belong to the Stay's Property. A Stay can have one primary guest; an existing assigned guest can be promoted by repeating the request with `isPrimaryGuest: true`. Guest assignments cannot exceed the room capacity.

## Stay History

```http
GET /api/v1/stays/{id}/history
```

Returns the Stay's status transition history.

---

# 28. CHECK-IN APIs

## Check In

```http
POST /api/v1/check-ins
```

Request:

```json
{
  "reservationId": "UUID",
  "roomId": "UUID",
  "adults": 2,
  "children": 0,
  "notes": "Guest arrived early"
}
```

`reservationId` must identify a CONFIRMED reservation or a partially checked-in reservation at a Property in the caller's scope. The request selects one physical room; a multi-room reservation is checked in one room per request. If the reservation has an assigned room, that room must be used. Guest, dates, nightly rate, and deposit are taken from the reservation.

Walk-in check-in omits `reservationId` and supplies `propertyId`, `guestId`, `roomId`, `expectedCheckOutDate`, and `nightlyRate`; `adults`, `children`, and `notes` are optional. The guest and room must both belong to the supplied Property.

The backend shall:

1. Validate the reservation or walk-in request and all Property relationships.
2. Lock and verify the selected room is available, has no overlapping reservation, and can hold the party.
3. Create the active Stay and its primary guest assignment.
4. Mark the room OCCUPIED and record the acting user and status history.
5. For reservation check-in, transition the reservation to CHECKED_IN.

---

# 29. CHECK-OUT APIs

## Check-Out Preview

```http
GET /api/v1/check-outs/{stayId}/preview
```

Returns:

```text
stayId
stayNumber
nights
nightlyRate
accommodationEstimate
depositEstimate
estimatedBalanceDue
currencyCode
```

This is an accommodation estimate only. Restaurant charges, taxes, payments, folios, and invoices are not part of the current Stay module.

## Check-Out

```http
POST /api/v1/check-outs/{stayId}
```

Request:

```json
{
  "notes": "Key returned",
  "completeReservation": false
}
```

The operation closes an active Stay, records the check-out user, and marks the room DIRTY for housekeeping. A reservation is completed when all its checked-in rooms are checked out. Set `completeReservation: true` only when intentionally releasing any reserved rooms that never checked in; finalization is rejected while another room has an active Stay. Payment is not taken by this endpoint.

---

# 30. HOUSEKEEPING APIs

## List Tasks

```http
GET /api/v1/housekeeping/tasks
```

Filters:

```text
propertyId
roomId
status
page
size
sort
direction
```

## Create Task

```http
POST /api/v1/housekeeping/tasks
```

Request fields: `propertyId`, `roomId`, optional `taskType`, `priority`, and `notes`.

## Update Task

```http
PUT /api/v1/housekeeping/tasks/{id}
```

The update accepts optional `assignedEmployeeId`, `status`, `priority`, and `notes`. Assignment requires the scoped `HOUSEKEEPING_ASSIGN` capability and an active employee assigned to the same property or branch.

## Complete Task

```http
POST /api/v1/housekeeping/tasks/{id}/complete
```

Starting a task is requested by updating its status to `IN_PROGRESS`; completion releases a cleaned room to `AVAILABLE`.

---

# 31. MAINTENANCE APIs

## List Requests

```http
GET /api/v1/maintenance
```

Supports `propertyId`, `roomId`, `status`, `priority`, `page`, `size`, `sort`, and `direction` filters. Results are scoped to the caller's live maintenance capability and property grants.

## Create Request

```http
POST /api/v1/maintenance
```

Request fields: required `propertyId` and `title`, optional `roomId`, `description`, and `priority`. A room-linked request blocks that room from new bookings until resolution or cancellation. The room and request must belong to the same property.

## Assign Technician

```http
PATCH /api/v1/maintenance/{id}/assign
```

Request: `{ "employeeId": "UUID" }`. The technician must be an active employee with a linked account holding scoped maintenance-update capability for the request property.

## Start Maintenance

```http
PATCH /api/v1/maintenance/{id}/start
```

## Resolve

```http
PATCH /api/v1/maintenance/{id}/resolve
```

Request:

```json
{
  "resolution": "Air conditioner repaired"
}
```

`GET /api/v1/maintenance/{id}` returns a request and `GET /api/v1/maintenance/{id}/history` returns its status audit trail. Resolved requests can be closed at `PATCH /api/v1/maintenance/{id}/close`; open or assigned requests can be cancelled at `PATCH /api/v1/maintenance/{id}/cancel`. Resolving or cancelling restores a previously available room to AVAILABLE; other cleanable room states return to DIRTY.

---

# 32. RESTAURANT APIs

## List Restaurants

```http
GET /api/v1/restaurants
```

## Get Restaurant

```http
GET /api/v1/restaurants/{id}
```

## Create Restaurant

```http
POST /api/v1/restaurants
```

Request fields: `branchId`, `name`, optional `description`, `openingTime`, `closingTime`, and `active`. Restaurant and table APIs require matching branch-scoped capabilities. Names are unique without regard to case within a branch.

## Update Restaurant

```http
PUT /api/v1/restaurants/{id}
```

---

# 33. RESTAURANT TABLE APIs

## List Tables

```http
GET /api/v1/restaurants/{restaurantId}/tables
```

## Create Table

```http
POST /api/v1/restaurants/{restaurantId}/tables
```

Table requests require a unique `tableNumber` and positive `capacity`; `location` is optional. Tables begin as AVAILABLE. Status transitions are constrained by the current table status; occupied tables must move through CLEANING before returning to AVAILABLE.

Request:

```json
{
  "tableNumber": "T01",
  "capacity": 4,
  "location": "Main Hall"
}
```

## Update Table

```http
PUT /api/v1/tables/{id}
```

## Change Table Status

```http
PATCH /api/v1/tables/{id}/status
```

---

# 34. MENU CATEGORY APIs

## List Categories

```http
GET /api/v1/restaurants/{restaurantId}/menu/categories
```

## Create Category

```http
POST /api/v1/restaurants/{restaurantId}/menu/categories
```

## Update Category

```http
PUT /api/v1/menu/categories/{id}
```

## Delete Category

```http
DELETE /api/v1/menu/categories/{id}
```

---

# 35. MENU ITEM APIs

## List Menu Items

```http
GET /api/v1/menu/items
```

Filters:

```text
restaurantId
categoryId
available
```

## Get Item

```http
GET /api/v1/menu/items/{id}
```

## Create Item

```http
POST /api/v1/menu/items
```

Request:

```json
{
  "categoryId": "UUID",
  "name": "Chicken Pilau",
  "sku": "FOOD001",
  "description": "Spiced rice with chicken",
  "sellingPrice": 650,
  "preparationTimeMinutes": 20
}
```

## Update Item

```http
PUT /api/v1/menu/items/{id}
```

## Change Availability

```http
PATCH /api/v1/menu/items/{id}/availability
```

Request:

```json
{
  "available": false
}
```

---

# 36. ORDER APIs

## List Orders

```http
GET /api/v1/orders
```

Filters:

```text
restaurantId
tableId
guestId
stayId
waiterId
status
orderType
date
```

## Get Order

```http
GET /api/v1/orders/{id}
```

## Create Order

```http
POST /api/v1/orders
```

Request:

```json
{
  "restaurantId": "UUID",
  "tableId": "UUID",
  "guestId": "UUID",
  "orderType": "DINE_IN",
  "items": [
    {
      "menuItemId": "UUID",
      "quantity": 2,
      "specialInstructions": "No onions"
    }
  ],
  "notes": "Customer order"
}
```

## Add Item

```http
POST /api/v1/orders/{id}/items
```

## Update Item

```http
PUT /api/v1/orders/{id}/items/{itemId}
```

## Remove Item

```http
DELETE /api/v1/orders/{id}/items/{itemId}
```

## Submit Order

```http
POST /api/v1/orders/{id}/submit
```

## Cancel Order

```http
POST /api/v1/orders/{id}/cancel
```

---

# 37. KITCHEN APIs

## Kitchen Queue

```http
GET /api/v1/kitchen/orders
```

Filters:

```text
status
priority
restaurantId
```

## Accept Order

```http
PATCH /api/v1/kitchen/orders/{id}/accept
```

## Start Preparation

```http
PATCH /api/v1/kitchen/orders/{id}/start
```

## Mark Ready

```http
PATCH /api/v1/kitchen/orders/{id}/ready
```

## Complete

```http
PATCH /api/v1/kitchen/orders/{id}/complete
```

---

# 38. TAX APIs

## List Taxes

```http
GET /api/v1/taxes
```

## Create Tax

```http
POST /api/v1/taxes
```

## Update Tax

```http
PUT /api/v1/taxes/{id}
```

## Disable Tax

```http
PATCH /api/v1/taxes/{id}/status
```

---

# 39. DISCOUNT APIs

## List Discounts

```http
GET /api/v1/discounts
```

## Create Discount

```http
POST /api/v1/discounts
```

## Update Discount

```http
PUT /api/v1/discounts/{id}
```

## Apply Discount

Discount application may be performed through invoice/order operations.

```http
POST /api/v1/orders/{id}/discount
```

---

# 40. INVOICE APIs

## List Invoices

```http
GET /api/v1/invoices
```

Filters:

```text
guestId
stayId
orderId
status
date
```

## Get Invoice

```http
GET /api/v1/invoices/{id}
```

## Generate Invoice

```http
POST /api/v1/invoices
```

## Void Invoice

```http
POST /api/v1/invoices/{id}/void
```

Request:

```json
{
  "reason": "Duplicate invoice"
}
```

## Download Invoice

```http
GET /api/v1/invoices/{id}/pdf
```

---

# 41. PAYMENT APIs

## List Payments

```http
GET /api/v1/payments
```

## Get Payment

```http
GET /api/v1/payments/{id}
```

## Record Payment

```http
POST /api/v1/payments
```

Request:

```json
{
  "invoiceId": "UUID",
  "amount": 15000,
  "paymentMethod": "MOBILE_MONEY",
  "transactionReference": "ABC123"
}
```

## Payment Status

```http
GET /api/v1/payments/{id}/status
```

## Refund

```http
POST /api/v1/payments/{id}/refund
```

Request:

```json
{
  "amount": 5000,
  "reason": "Customer refund"
}
```

Refund permissions shall be restricted.

---

# 42. INVENTORY CATEGORY APIs

## List

```http
GET /api/v1/inventory/categories
```

## Create

```http
POST /api/v1/inventory/categories
```

## Update

```http
PUT /api/v1/inventory/categories/{id}
```

---

# 43. INVENTORY ITEM APIs

## List

```http
GET /api/v1/inventory/items
```

Filters:

```text
branchId
categoryId
supplierId
lowStock
status
```

## Get Item

```http
GET /api/v1/inventory/items/{id}
```

## Create Item

```http
POST /api/v1/inventory/items
```

Request:

```json
{
  "branchId": "UUID",
  "categoryId": "UUID",
  "sku": "INV001",
  "name": "Cooking Oil",
  "unit": "LITRE",
  "quantity": 50,
  "reorderLevel": 15,
  "unitCost": 250
}
```

## Update Item

```http
PUT /api/v1/inventory/items/{id}
```

## Low Stock

```http
GET /api/v1/inventory/items/low-stock
```

---

# 44. INVENTORY TRANSACTION APIs

## List Transactions

```http
GET /api/v1/inventory/transactions
```

## Issue Stock

```http
POST /api/v1/inventory/transactions/issue
```

Request:

```json
{
  "inventoryItemId": "UUID",
  "quantity": 5,
  "notes": "Issued to kitchen"
}
```

## Receive Stock

```http
POST /api/v1/inventory/transactions/receive
```

Request:

```json
{
  "inventoryItemId": "UUID",
  "quantity": 20,
  "unitCost": 250,
  "referenceId": "UUID"
}
```

## Record Wastage

```http
POST /api/v1/inventory/transactions/wastage
```

---

# 45. SUPPLIER APIs

## List Suppliers

```http
GET /api/v1/suppliers
```

## Get Supplier

```http
GET /api/v1/suppliers/{id}
```

## Create Supplier

```http
POST /api/v1/suppliers
```

## Update Supplier

```http
PUT /api/v1/suppliers/{id}
```

## Disable Supplier

```http
PATCH /api/v1/suppliers/{id}/status
```

---

# 46. PURCHASE ORDER APIs

## List Purchase Orders

```http
GET /api/v1/purchase-orders
```

## Get Purchase Order

```http
GET /api/v1/purchase-orders/{id}
```

## Create Purchase Order

```http
POST /api/v1/purchase-orders
```

Request:

```json
{
  "branchId": "UUID",
  "supplierId": "UUID",
  "items": [
    {
      "inventoryItemId": "UUID",
      "quantity": 20,
      "unitCost": 250
    }
  ]
}
```

## Submit

```http
POST /api/v1/purchase-orders/{id}/submit
```

## Approve

```http
POST /api/v1/purchase-orders/{id}/approve
```

## Receive

```http
POST /api/v1/purchase-orders/{id}/receive
```

Request:

```json
{
  "items": [
    {
      "itemId": "UUID",
      "receivedQuantity": 20
    }
  ]
}
```

## Cancel

```http
POST /api/v1/purchase-orders/{id}/cancel
```

---

# 47. LOYALTY APIs

## Get Loyalty Account

```http
GET /api/v1/guests/{guestId}/loyalty
```

## Create Loyalty Account

```http
POST /api/v1/guests/{guestId}/loyalty
```

## Loyalty Transactions

```http
GET /api/v1/loyalty/accounts/{id}/transactions
```

## Redeem Points

```http
POST /api/v1/loyalty/accounts/{id}/redeem
```

Request:

```json
{
  "points": 500,
  "description": "Restaurant reward"
}
```

---

# 48. NOTIFICATION APIs

## List Notifications

```http
GET /api/v1/notifications
```

## Mark Read

```http
PATCH /api/v1/notifications/{id}/read
```

## Mark All Read

```http
PATCH /api/v1/notifications/read-all
```

## Send Notification

```http
POST /api/v1/notifications
```

Request:

```json
{
  "userId": "UUID",
  "title": "Low Stock",
  "message": "Cooking oil is below reorder level",
  "channel": "IN_APP"
}
```

---

# 49. AUDIT APIs

Audit logs shall normally be read-only.

## List Audit Logs

```http
GET /api/v1/audit-logs
```

Filters:

```text
userId
action
entityType
entityId
startDate
endDate
```

## Get Audit Log

```http
GET /api/v1/audit-logs/{id}
```

Only authorized administrators should access these endpoints.

---

# 50. SYSTEM SETTINGS APIs

## Get Settings

```http
GET /api/v1/settings
```

## Get Setting

```http
GET /api/v1/settings/{key}
```

## Update Setting

```http
PUT /api/v1/settings/{key}
```

Request:

```json
{
  "value": "10:00"
}
```

---

# 51. DASHBOARD APIs

## Management Dashboard

```http
GET /api/v1/dashboard/management
```

Response:

```json
{
  "success": true,
  "data": {
    "todayRevenue": 125000,
    "occupiedRooms": 32,
    "availableRooms": 18,
    "checkInsToday": 12,
    "checkOutsToday": 9,
    "restaurantSales": 42000,
    "pendingOrders": 7,
    "lowStockItems": 5
  }
}
```

---

## Reception Dashboard

```http
GET /api/v1/dashboard/reception
```

---

## Restaurant Dashboard

```http
GET /api/v1/dashboard/restaurant
```

---

## Inventory Dashboard

```http
GET /api/v1/dashboard/inventory
```

---

# 52. REPORTING APIs

## Revenue Report

```http
GET /api/v1/reports/revenue
```

Parameters:

```text
startDate
endDate
branchId
```

## Occupancy Report

```http
GET /api/v1/reports/occupancy
```

## Reservation Report

```http
GET /api/v1/reports/reservations
```

## Restaurant Sales

```http
GET /api/v1/reports/restaurant-sales
```

## Inventory Report

```http
GET /api/v1/reports/inventory
```

## Payment Report

```http
GET /api/v1/reports/payments
```

## Tax Report

```http
GET /api/v1/reports/taxes
```

## Export Report

```http
GET /api/v1/reports/{reportType}/export
```

Supported formats:

```text
PDF
CSV
XLSX
```

---

# 53. FILE APIs

## Upload File

```http
POST /api/v1/files
```

Content type:

```text
multipart/form-data
```

## Get File

```http
GET /api/v1/files/{id}
```

## Delete File

```http
DELETE /api/v1/files/{id}
```

File access must be authorized.

---

# 54. HEALTH APIs

Health endpoints should be publicly restricted to appropriate infrastructure access.

## Application Health

```http
GET /api/health
```

## Readiness

```http
GET /api/health/ready
```

## Liveness

```http
GET /api/health/live
```

Example:

```json
{
  "status": "UP"
}
```

---

# 55. API SECURITY

The API shall implement:

## Authentication

JWT or equivalent secure authentication.

## Authorization

Role and permission checks.

## Input Validation

All request bodies must be validated.

## SQL Injection Protection

Use parameterized queries/JPA.

## XSS Protection

Sanitize and encode user-generated content where rendered.

## CSRF

CSRF protection strategy shall be appropriate to the authentication mechanism.

## Rate Limiting

Limit excessive requests.

## HTTPS

Production API traffic must use HTTPS.

---

# 56. RATE LIMITING

Suggested initial limits:

```text
Login:
5 requests/minute/IP

Password reset:
5 requests/hour/account

General API:
100 requests/minute/user

Public API:
60 requests/minute/IP
```

These values should be configurable.

---

# 57. API VERSIONING

The API shall use URL versioning:

```text
/api/v1
```

Future version:

```text
/api/v2
```

Breaking changes should use a new API version.

---

# 58. HTTP STATUS CODES

| Code | Meaning |
|---|---|
| 200 | Success |
| 201 | Created |
| 202 | Accepted |
| 204 | No Content |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 422 | Validation Error |
| 429 | Too Many Requests |
| 500 | Internal Server Error |
| 503 | Service Unavailable |

---

# 59. BUSINESS ERROR CODES

Examples:

```text
AUTH_INVALID_CREDENTIALS
AUTH_ACCOUNT_LOCKED
AUTH_TOKEN_EXPIRED

ROOM_NOT_AVAILABLE
ROOM_ALREADY_OCCUPIED
ROOM_UNDER_MAINTENANCE

RESERVATION_CONFLICT
RESERVATION_NOT_FOUND
INVALID_RESERVATION_DATES

STAY_ALREADY_CHECKED_OUT
CHECK_IN_NOT_ALLOWED
CHECK_OUT_NOT_ALLOWED

ORDER_NOT_FOUND
ORDER_ALREADY_COMPLETED
MENU_ITEM_UNAVAILABLE

INSUFFICIENT_STOCK
INVENTORY_ITEM_NOT_FOUND

PAYMENT_FAILED
PAYMENT_ALREADY_COMPLETED
INVALID_PAYMENT_AMOUNT

INVOICE_NOT_FOUND
INVOICE_ALREADY_PAID

ACCESS_DENIED
RESOURCE_NOT_FOUND
VALIDATION_ERROR
```

---

# 60. COMPLETE ENDPOINT MAP

```text
AUTH
├── POST   /auth/login
├── POST   /auth/refresh
├── POST   /auth/logout
├── GET    /auth/me
├── POST   /auth/forgot-password
└── POST   /auth/reset-password

USERS
├── GET    /users
├── GET    /users/{id}
├── POST   /users
├── PUT    /users/{id}
└── PATCH  /users/{id}/status

ROLES
├── GET    /roles
├── GET    /roles/{id}
├── POST   /roles
├── PUT    /roles/{id}
└── DELETE /roles/{id}

PERMISSIONS
├── GET    /permissions
└── PUT    /roles/{id}/permissions

ORGANIZATION
├── GET    /organization
└── PUT    /organization

BRANCHES
├── GET    /branches
├── GET    /branches/{id}
├── POST   /branches
├── PUT    /branches/{id}
└── PATCH  /branches/{id}/status

EMPLOYEES
├── GET    /employees
├── GET    /employees/{id}
├── POST   /employees
├── PUT    /employees/{id}
└── DELETE /employees/{id}

GUESTS
├── GET    /guests
├── GET    /guests/{id}
├── POST   /guests
├── PUT    /guests/{id}
└── GET    /guests/{id}/history

ROOM TYPES
├── GET    /room-types
├── GET    /room-types/{id}
├── POST   /room-types
├── PUT    /room-types/{id}
└── DELETE /room-types/{id}

ROOMS
├── GET    /rooms
├── GET    /rooms/{id}
├── POST   /rooms
├── PUT    /rooms/{id}
├── PATCH  /rooms/{id}/status
└── GET    /rooms/availability

AMENITIES
├── GET    /amenities
├── POST   /amenities
├── PUT    /amenities/{id}
└── DELETE /amenities/{id}

RESERVATIONS
├── GET    /reservations
├── GET    /reservations/{id}
├── POST   /reservations
├── PUT    /reservations/{id}
├── PATCH  /reservations/{id}/confirm
├── POST   /reservations/{id}/cancel
└── POST   /reservations/check-availability

STAYS
├── GET    /stays
├── GET    /stays/{id}
├── GET    /stays/active
└── POST   /stays/{id}/guests

CHECK-IN
└── POST   /check-ins

CHECK-OUT
├── GET    /check-outs/{stayId}/preview
└── POST   /check-outs/{stayId}

HOUSEKEEPING
├── GET    /housekeeping/tasks
├── POST   /housekeeping/tasks
├── PATCH  /housekeeping/tasks/{id}/assign
├── PATCH  /housekeeping/tasks/{id}/start
└── PATCH  /housekeeping/tasks/{id}/complete

MAINTENANCE
├── GET    /maintenance
├── POST   /maintenance
├── PATCH  /maintenance/{id}/assign
├── PATCH  /maintenance/{id}/start
└── PATCH  /maintenance/{id}/resolve

RESTAURANTS
├── GET    /restaurants
├── GET    /restaurants/{id}
├── POST   /restaurants
└── PUT    /restaurants/{id}

TABLES
├── GET    /restaurants/{id}/tables
├── POST   /restaurants/{id}/tables
├── PUT    /tables/{id}
└── PATCH  /tables/{id}/status

MENU
├── GET    /restaurants/{id}/menu/categories
├── POST   /restaurants/{id}/menu/categories
├── PUT    /menu/categories/{id}
├── DELETE /menu/categories/{id}
├── GET    /menu/items
├── GET    /menu/items/{id}
├── POST   /menu/items
├── PUT    /menu/items/{id}
└── PATCH  /menu/items/{id}/availability

ORDERS
├── GET    /orders
├── GET    /orders/{id}
├── POST   /orders
├── POST   /orders/{id}/items
├── PUT    /orders/{id}/items/{itemId}
├── DELETE /orders/{id}/items/{itemId}
├── POST   /orders/{id}/submit
└── POST   /orders/{id}/cancel

KITCHEN
├── GET    /kitchen/orders
├── PATCH  /kitchen/orders/{id}/accept
├── PATCH  /kitchen/orders/{id}/start
├── PATCH  /kitchen/orders/{id}/ready
└── PATCH  /kitchen/orders/{id}/complete

TAX
├── GET    /taxes
├── POST   /taxes
├── PUT    /taxes/{id}
└── PATCH  /taxes/{id}/status

DISCOUNTS
├── GET    /discounts
├── POST   /discounts
├── PUT    /discounts/{id}
└── POST   /orders/{id}/discount

INVOICES
├── GET    /invoices
├── GET    /invoices/{id}
├── POST   /invoices
├── POST   /invoices/{id}/void
└── GET    /invoices/{id}/pdf

PAYMENTS
├── GET    /payments
├── GET    /payments/{id}
├── POST   /payments
├── GET    /payments/{id}/status
└── POST   /payments/{id}/refund

INVENTORY
├── GET    /inventory/categories
├── POST   /inventory/categories
├── PUT    /inventory/categories/{id}
├── GET    /inventory/items
├── GET    /inventory/items/{id}
├── POST   /inventory/items
├── PUT    /inventory/items/{id}
├── GET    /inventory/items/low-stock
├── GET    /inventory/transactions
├── POST   /inventory/transactions/issue
├── POST   /inventory/transactions/receive
└── POST   /inventory/transactions/wastage

SUPPLIERS
├── GET    /suppliers
├── GET    /suppliers/{id}
├── POST   /suppliers
├── PUT    /suppliers/{id}
└── PATCH  /suppliers/{id}/status

PURCHASE ORDERS
├── GET    /purchase-orders
├── GET    /purchase-orders/{id}
├── POST   /purchase-orders
├── POST   /purchase-orders/{id}/submit
├── POST   /purchase-orders/{id}/approve
├── POST   /purchase-orders/{id}/receive
└── POST   /purchase-orders/{id}/cancel

LOYALTY
├── GET    /guests/{guestId}/loyalty
├── POST   /guests/{guestId}/loyalty
├── GET    /loyalty/accounts/{id}/transactions
└── POST   /loyalty/accounts/{id}/redeem

NOTIFICATIONS
├── GET    /notifications
├── PATCH  /notifications/{id}/read
├── PATCH  /notifications/read-all
└── POST   /notifications

AUDIT
├── GET    /audit-logs
└── GET    /audit-logs/{id}

SETTINGS
├── GET    /settings
├── GET    /settings/{key}
└── PUT    /settings/{key}

DASHBOARD
├── GET    /dashboard/management
├── GET    /dashboard/reception
├── GET    /dashboard/restaurant
└── GET    /dashboard/inventory

REPORTS
├── GET    /reports/revenue
├── GET    /reports/occupancy
├── GET    /reports/reservations
├── GET    /reports/restaurant-sales
├── GET    /reports/inventory
├── GET    /reports/payments
├── GET    /reports/taxes
└── GET    /reports/{reportType}/export

FILES
├── POST   /files
├── GET    /files/{id}
└── DELETE /files/{id}

HEALTH
├── GET    /api/health
├── GET    /api/health/ready
└── GET    /api/health/live
```

---

# 61. SPRING BOOT PACKAGE STRUCTURE

Recommended backend structure:

```text
src/main/java/com/hrms/
│
├── HrmsApplication.java
│
├── config/
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   └── CorsConfig.java
│
├── auth/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
│
├── users/
├── employees/
├── guests/
├── rooms/
├── reservations/
├── stays/
├── housekeeping/
├── maintenance/
├── restaurants/
├── menu/
├── orders/
├── kitchen/
├── billing/
├── payments/
├── inventory/
├── suppliers/
├── procurement/
├── loyalty/
├── notifications/
├── reports/
├── dashboard/
│
├── audit/
│
└── common/
    ├── exception/
    ├── response/
    ├── security/
    ├── validation/
    └── util/
```

---

# 62. DTO REQUIREMENTS

The API should not expose JPA entities directly.

Use DTOs.

Example:

```text
ReservationEntity
       ↓
ReservationResponseDTO
       ↓
JSON
```

For creation:

```text
JSON
 ↓
CreateReservationRequest
 ↓
ReservationService
 ↓
ReservationEntity
```

This prevents unwanted database fields from being exposed.

---

# 63. SERVICE LAYER RULES

Business logic shall live in services.

Example:

```java
ReservationService
```

shall handle:

- Availability
- Date validation
- Room assignment
- Reservation creation
- Cancellation
- Pricing
- Business rules

The controller should not contain complex business logic.

---

# 64. DATABASE TRANSACTIONS

Critical endpoints shall use transactions.

Example:

```java
@Transactional
public CheckInResponse checkIn(CheckInRequest request) {
    // validate reservation
    // validate room
    // create stay
    // update room
    // create charges
}
```

Transactions are especially important for:

- Check-in
- Check-out
- Payments
- Inventory
- Purchase receiving
- Order completion

---

# 65. CONCURRENCY CONTROL

The API shall protect against simultaneous updates.

Important examples:

```text
Two users attempting to book the same room
Two cashiers recording the same payment
Two storekeepers issuing the same stock
Two users modifying the same order
```

Possible mechanisms:

```text
Database constraints
Optimistic locking
Pessimistic locking
Transactions
Idempotency keys
```

---

# 66. IDEMPOTENCY

Financial operations should support idempotency.

Example:

```http
POST /api/v1/payments
Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000
```

If the same payment request is accidentally submitted twice, the system should not create two payments.

---

# 67. API DOCUMENTATION

The API shall be documented using:

```text
OpenAPI 3
Swagger UI
```

Recommended endpoint:

```text
/api/docs
```

and:

```text
/api/swagger-ui
```

The exact paths can be configured during implementation.

---

# 68. FRONTEND API CLIENT

The Next.js application should use a centralized API client.

Example conceptual structure:

```text
src/
├── lib/
│   └── api/
│       ├── client.ts
│       ├── auth.ts
│       ├── guests.ts
│       ├── rooms.ts
│       ├── reservations.ts
│       ├── orders.ts
│       ├── inventory.ts
│       └── reports.ts
```

This avoids scattering raw HTTP calls throughout UI components.

---

# 69. API ENVIRONMENT CONFIGURATION

Frontend:

```text
NEXT_PUBLIC_API_URL
```

Backend:

```text
DATABASE_URL
JWT_SECRET
JWT_EXPIRATION
CORS_ALLOWED_ORIGINS
PAYMENT_API_KEY
SMS_API_KEY
EMAIL_API_KEY
```

Secrets shall never be committed to Git.

---

# 70. API IMPLEMENTATION PRIORITY

## Phase 1 — Core

```text
Authentication
Users
Roles
Guests
Rooms
Room Types
Reservations
Check-In
Check-Out
```

## Phase 2 — Operations

```text
Housekeeping
Maintenance
Restaurants
Tables
Menu
Orders
Kitchen
```

## Phase 3 — Finance

```text
Taxes
Discounts
Invoices
Payments
```

## Phase 4 — Inventory

```text
Inventory
Suppliers
Purchase Orders
Stock Transactions
```

## Phase 5 — Management

```text
Dashboard
Reports
Notifications
Audit Logs
Loyalty
Settings
```

---

# 71. API ACCEPTANCE CRITERIA

The API shall be considered complete when:

- All core endpoints are implemented.
- Authentication works.
- Authorization works.
- Validation works.
- Errors follow a consistent format.
- Pagination works.
- Filtering works.
- Sorting works.
- Database transactions work.
- Double-booking protection works.
- Payment idempotency works.
- Inventory transactions are auditable.
- Audit logs are generated.
- API documentation is available.
- Integration tests pass.
- Security tests pass.
- Production configuration is documented.

---

# 72. FINAL API ARCHITECTURE

The final communication architecture shall be:

```text
┌───────────────────────────────┐
│       NEXT.JS FRONTEND        │
│                               │
│ Dashboard                     │
│ Reception                     │
│ Reservations                  │
│ Restaurant                    │
│ Kitchen                       │
│ Inventory                     │
│ Finance                       │
│ Reports                       │
└───────────────┬───────────────┘
                │
             HTTPS
                │
                ▼
┌───────────────────────────────┐
│       SPRING BOOT API         │
│                               │
│ Controllers                   │
│       ↓                       │
│ Services                      │
│       ↓                       │
│ Repositories                  │
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────────────┐
│          POSTGRESQL           │
│                               │
│ Guests                        │
│ Reservations                  │
│ Rooms                         │
│ Orders                        │
│ Payments                      │
│ Inventory                     │
│ Employees                     │
│ Audit Logs                    │
└───────────────────────────────┘
```

---

# 73. COMPLETE PROJECT DESIGN STATUS

The project design documents are now:

```text
DOCUMENT 1
✓ Complete System Requirements Specification

DOCUMENT 2
✓ Database ERD + Complete Table Definitions

DOCUMENT 3
✓ Complete API Specification

DOCUMENT 4
→ UI/UX Screen Map + Navigation

DOCUMENT 5
→ System Architecture Specification

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

# END OF COMPLETE API SPECIFICATION

**Version:** 1.0  
**API Base:** `/api/v1`  
**Backend:** Spring Boot / Java  
**Database:** PostgreSQL  
**Frontend:** Next.js / TypeScript  
**API Format:** REST + JSON  
**Documentation:** OpenAPI 3 / Swagger  
**Status:** Ready for UI/UX and System Architecture Design
