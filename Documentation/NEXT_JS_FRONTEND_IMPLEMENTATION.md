# HOTEL & RESTAURANT MANAGEMENT SYSTEM

# NEXT.JS FRONTEND IMPLEMENTATION

**Version:** 1.0  
**Status:** Implementation-Ready  
**Date:** September 2026  
**Frontend:** Next.js + TypeScript  
**UI:** Tailwind CSS  
**API:** Spring Boot REST API  
**Database:** PostgreSQL  
**Authentication:** JWT  
**API Documentation:** OpenAPI 3  
**Default Currency:** KES  
**Default Timezone:** Africa/Nairobi  

---

# Table of Contents

1. [Document Purpose](#1-document-purpose)
2. [Frontend Objectives](#2-frontend-objectives)
3. [Technology Stack](#3-technology-stack)
4. [Frontend Architecture](#4-frontend-architecture)
5. [Next.js Configuration](#5-nextjs-configuration)
6. [Project Initialization](#6-project-initialization)
7. [Frontend Directory Structure](#7-frontend-directory-structure)
8. [Application Routes](#8-application-routes)
9. [Layout Architecture](#9-layout-architecture)
10. [Design System](#10-design-system)
11. [Responsive Design](#11-responsive-design)
12. [Authentication Architecture](#12-authentication-architecture)
13. [JWT Handling](#13-jwt-handling)
14. [Authorization and Permissions](#14-authorization-and-permissions)
15. [API Client](#15-api-client)
16. [API Request Lifecycle](#16-api-request-lifecycle)
17. [Data Fetching](#17-data-fetching)
18. [State Management](#18-state-management)
19. [Form Architecture](#19-form-architecture)
20. [Validation](#20-validation)
21. [Reusable UI Components](#21-reusable-ui-components)
22. [Dashboard](#22-dashboard)
23. [Reservation Frontend](#23-reservation-frontend)
24. [Guest Management](#24-guest-management)
25. [Room Management](#25-room-management)
26. [Front Desk](#26-front-desk)
27. [Housekeeping](#27-housekeeping)
28. [Maintenance](#28-maintenance)
29. [Restaurant Management](#29-restaurant-management)
30. [Restaurant POS](#30-restaurant-pos)
31. [Kitchen Display System](#31-kitchen-display-system)
32. [Inventory](#32-inventory)
33. [Procurement](#33-procurement)
34. [Billing](#34-billing)
35. [Payments](#35-payments)
36. [Loyalty](#36-loyalty)
37. [Employees](#37-employees)
38. [Reports](#38-reports)
39. [Notifications](#39-notifications)
40. [Settings](#40-settings)
41. [Audit Logs](#41-audit-logs)
42. [Tables and Data Grids](#42-tables-and-data-grids)
43. [Charts and Analytics](#43-charts-and-analytics)
44. [Loading States](#44-loading-states)
45. [Error States](#45-error-states)
46. [Empty States](#46-empty-states)
47. [Toast Notifications](#47-toast-notifications)
48. [Modal Architecture](#48-modal-architecture)
49. [Search](#49-search)
50. [Pagination](#50-pagination)
51. [File Uploads](#51-file-uploads)
52. [Security Requirements](#52-security-requirements)
53. [Accessibility](#53-accessibility)
54. [Performance Optimization](#54-performance-optimization)
55. [SEO](#55-seo)
56. [Frontend Environment Variables](#56-frontend-environment-variables)
57. [Error Monitoring](#57-error-monitoring)
58. [Testing](#58-testing)
59. [End-to-End Testing](#59-end-to-end-testing)
60. [Production Build](#60-production-build)
61. [Deployment](#61-deployment)
62. [CI/CD](#62-cicd)
63. [Implementation Phases](#63-implementation-phases)
64. [Definition of Done](#64-definition-of-done)
65. [Frontend Implementation Checklist](#65-frontend-implementation-checklist)

---

# 1. Document Purpose

This document defines the complete implementation strategy for the Hotel & Restaurant Management System frontend.

The frontend will provide a professional web-based interface for:

- administrators
- managers
- receptionists
- front-desk staff
- housekeeping staff
- maintenance staff
- restaurant staff
- waiters
- kitchen staff
- inventory staff
- procurement staff
- finance staff
- reporting users

The application communicates with the Spring Boot backend through REST APIs.

Architecture:

```text
Next.js
   ↓
React Components
   ↓
Feature Modules
   ↓
API Client
   ↓
Spring Boot REST API
   ↓
PostgreSQL
```

---

# 2. Frontend Objectives

The frontend must be:

- professional
- responsive
- accessible
- fast
- secure
- modular
- maintainable
- scalable
- mobile-friendly
- desktop-friendly
- role-aware
- permission-aware

The UI should feel like a professional hotel management platform rather than a basic CRUD application.

---

# 3. Technology Stack

| Technology | Purpose |
|---|---|
| Next.js | React application framework |
| React | UI |
| TypeScript | Type safety |
| Tailwind CSS | Styling |
| React Hook Form | Form management |
| Zod | Validation |
| TanStack Query | Server state |
| Zustand | Client state |
| Axios / Fetch | HTTP communication |
| Recharts | Charts |
| Lucide React | Icons |
| date-fns | Date handling |
| Sonner | Toast notifications |
| Playwright | E2E testing |
| Vitest/Jest | Unit testing |

---

# 4. Frontend Architecture

The frontend uses a feature-based architecture.

```text
                    Next.js
                       │
          ┌────────────┴────────────┐
          │                         │
       Server                    Client
    Components                 Components
          │                         │
          └────────────┬────────────┘
                       │
                 Feature Modules
                       │
                 API / Query Layer
                       │
                       ▼
                Spring Boot API
```

Feature modules:

```text
auth
dashboard
reservations
guests
rooms
front-desk
housekeeping
maintenance
restaurant
kitchen
inventory
procurement
finance
employees
reports
settings
```

---

# 5. Next.js Configuration

The project should use the App Router.

Example:

```text
app/
├── layout.tsx
├── page.tsx
├── login/
│   └── page.tsx
└── dashboard/
    └── page.tsx
```

Use TypeScript throughout the project.

Strict TypeScript should be enabled:

```json
{
  "compilerOptions": {
    "strict": true
  }
}
```

---

# 6. Project Initialization

Create the application:

```bash
npx create-next-app@latest hrms-frontend
```

Recommended options:

```text
TypeScript: Yes
ESLint: Yes
Tailwind CSS: Yes
src/: Yes
App Router: Yes
Turbopack: Yes
Import alias: @/*
```

Then:

```bash
cd hrms-frontend
npm install
```

Install frontend libraries:

```bash
npm install @tanstack/react-query
npm install zustand
npm install react-hook-form
npm install zod
npm install @hookform/resolvers
npm install lucide-react
npm install recharts
npm install date-fns
npm install sonner
```

---

# 7. Frontend Directory Structure

Recommended structure:

```text
hrms-frontend/
│
├── public/
│   ├── images/
│   ├── icons/
│   └── logos/
│
├── src/
│   │
│   ├── app/
│   │   ├── layout.tsx
│   │   ├── page.tsx
│   │   ├── globals.css
│   │   │
│   │   ├── login/
│   │   │   └── page.tsx
│   │   │
│   │   ├── dashboard/
│   │   │   └── page.tsx
│   │   │
│   │   ├── reservations/
│   │   │   ├── page.tsx
│   │   │   ├── new/
│   │   │   └── [id]/
│   │   │
│   │   ├── guests/
│   │   ├── rooms/
│   │   ├── front-desk/
│   │   ├── housekeeping/
│   │   ├── maintenance/
│   │   ├── restaurant/
│   │   ├── kitchen/
│   │   ├── inventory/
│   │   ├── procurement/
│   │   ├── finance/
│   │   ├── employees/
│   │   ├── reports/
│   │   ├── settings/
│   │   └── audit/
│   │
│   ├── components/
│   │   ├── ui/
│   │   ├── layout/
│   │   ├── forms/
│   │   ├── tables/
│   │   ├── charts/
│   │   ├── modals/
│   │   └── feedback/
│   │
│   ├── features/
│   │   ├── auth/
│   │   ├── dashboard/
│   │   ├── reservations/
│   │   ├── guests/
│   │   ├── rooms/
│   │   ├── front-desk/
│   │   ├── housekeeping/
│   │   ├── maintenance/
│   │   ├── restaurant/
│   │   ├── kitchen/
│   │   ├── inventory/
│   │   ├── procurement/
│   │   ├── finance/
│   │   ├── employees/
│   │   ├── reports/
│   │   └── settings/
│   │
│   ├── lib/
│   │   ├── api/
│   │   ├── auth/
│   │   ├── permissions/
│   │   ├── validation/
│   │   ├── constants/
│   │   └── utils/
│   │
│   ├── hooks/
│   ├── stores/
│   ├── types/
│   └── styles/
│
├── .env.local
├── .env.example
├── next.config.ts
├── package.json
├── tsconfig.json
└── README.md
```

---

# 8. Application Routes

Public routes:

```text
/login
/forgot-password
/reset-password
```

Protected routes:

```text
/dashboard

/reservations
/reservations/new
/reservations/[id]

/guests
/guests/[id]

/rooms
/room-types

/front-desk
/front-desk/check-in
/front-desk/check-out

/housekeeping
/maintenance

/restaurant
/restaurant/tables
/restaurant/menu
/restaurant/orders

/kitchen

/inventory
/inventory/items
/inventory/transactions

/procurement
/procurement/suppliers
/procurement/purchase-orders
/procurement/goods-receipts

/finance
/finance/invoices
/finance/payments
/finance/refunds

/employees

/reports

/settings

/audit
```

---

# 9. Layout Architecture

Main layout:

```text
┌─────────────────────────────────────────────┐
│                  Top Bar                    │
├───────────────┬─────────────────────────────┤
│               │                             │
│   Sidebar     │       Main Content          │
│               │                             │
│               │                             │
│               │                             │
└───────────────┴─────────────────────────────┘
```

Components:

```text
AppShell
├── Sidebar
├── Topbar
├── Breadcrumbs
├── PageHeader
├── MainContent
└── NotificationCenter
```

---

# 10. Design System

The design should communicate:

- hospitality
- professionalism
- cleanliness
- trust
- efficiency

Use a consistent design system.

Components:

```text
Button
Input
Select
Textarea
Checkbox
Radio
Switch
Badge
Card
Dialog
Dropdown
Tabs
Tooltip
Table
Pagination
DatePicker
Calendar
Toast
Alert
Skeleton
```

Typography should be consistent across the entire system.

Spacing should follow a predictable scale.

---

# 11. Responsive Design

Desktop layout:

```text
Sidebar + Main Content
```

Tablet:

```text
Collapsible Sidebar
```

Mobile:

```text
Top Navigation
+
Drawer
+
Full-width Content
```

The system should support:

```text
320px+
640px+
768px+
1024px+
1280px+
1536px+
```

Important operational screens such as POS and kitchen should receive special responsive treatment.

---

# 12. Authentication Architecture

Login flow:

```text
Login Form
     ↓
POST /auth/login
     ↓
Spring Boot
     ↓
Validate Credentials
     ↓
JWT
     ↓
Frontend Auth State
     ↓
Dashboard
```

Authentication state should contain:

```text
user
roles
permissions
branches
token state
authentication status
```

---

# 13. JWT Handling

The frontend must treat access tokens as sensitive credentials.

Recommended architecture:

```text
Login
 ↓
Authentication mechanism
 ↓
Secure token handling
 ↓
API requests
```

Avoid exposing tokens unnecessarily to client-side JavaScript.

Where the authentication architecture supports it, use secure, HttpOnly cookies for session/token storage rather than persistent browser storage.

Do not store authentication secrets in:

```text
localStorage
sessionStorage
URLs
logs
```

unless the chosen authentication design explicitly requires it and the associated risks have been addressed.

---

# 14. Authorization and Permissions

Frontend authorization improves user experience but is not the security boundary.

The backend remains authoritative.

Permission helper:

```typescript
export function hasPermission(
  permission: string,
  permissions: string[]
): boolean {
  return permissions.includes(permission);
}
```

Example:

```tsx
{hasPermission("RESERVATION_CREATE", permissions) && (
  <Button>
    New Reservation
  </Button>
)}
```

Route protection:

```text
User
 ↓
Authenticated?
 ↓
Permission?
 ↓
Render page
```

Users without permission should receive an appropriate unauthorized/forbidden state.

---

# 15. API Client

Centralize API communication.

Structure:

```text
src/lib/api/
├── client.ts
├── auth.ts
├── guests.ts
├── rooms.ts
├── reservations.ts
├── stays.ts
├── restaurant.ts
├── inventory.ts
├── procurement.ts
├── finance.ts
└── reports.ts
```

Example:

```typescript
export async function getGuests(params?: {
  page?: number;
  size?: number;
  search?: string;
}) {
  return apiClient.get("/guests", {
    params,
  });
}
```

Never duplicate raw API URLs throughout components.

---

# 16. API Request Lifecycle

```text
Component
   ↓
Custom Hook
   ↓
TanStack Query
   ↓
API Service
   ↓
HTTP Client
   ↓
Spring Boot
```

Response:

```text
Spring Boot
   ↓
HTTP Response
   ↓
API Client
   ↓
Query Cache
   ↓
Component
```

---

# 17. Data Fetching

Use TanStack Query for server state.

Example:

```typescript
const {
  data,
  isLoading,
  isError,
  refetch,
} = useQuery({
  queryKey: ["guests", filters],
  queryFn: () => getGuests(filters),
});
```

Mutation:

```typescript
const mutation = useMutation({
  mutationFn: createGuest,
  onSuccess: () => {
    queryClient.invalidateQueries({
      queryKey: ["guests"],
    });
  },
});
```

Use caching carefully.

Do not cache rapidly changing information indefinitely.

---

# 18. State Management

Use Zustand for client-side state.

Good candidates:

```text
sidebar state
selected branch
theme
POS cart
temporary UI preferences
modal state
```

Use TanStack Query for:

```text
guests
rooms
reservations
orders
inventory
invoices
reports
```

Avoid duplicating server state inside Zustand.

---

# 19. Form Architecture

Use:

```text
React Hook Form
+
Zod
```

Example:

```typescript
const guestSchema = z.object({
  firstName: z.string().min(1),
  lastName: z.string().min(1),
  email: z.string().email().optional(),
  phoneNumber: z.string().optional(),
});
```

Forms must support:

```text
initial values
validation
submission
loading
server errors
success state
reset
```

---

# 20. Validation

Validation occurs at three levels:

```text
Frontend validation
        ↓
Backend validation
        ↓
Database constraints
```

Frontend validation improves usability.

Backend validation remains authoritative.

Examples:

```text
required fields
email format
phone format
date ranges
positive quantities
valid prices
```

---

# 21. Reusable UI Components

Core components:

```text
src/components/ui/

Button
Input
Label
Select
Textarea
Card
Badge
Dialog
Dropdown
Tabs
Table
Pagination
Calendar
DatePicker
Spinner
Skeleton
Alert
Toast
```

Business components:

```text
ReservationCard
RoomStatusBadge
GuestCard
PaymentSummary
InvoiceSummary
OrderSummary
InventoryStatus
KpiCard
```

---

# 22. Dashboard

Dashboard route:

```text
/dashboard
```

Main KPIs:

```text
Occupancy
Today's Arrivals
Today's Departures
Available Rooms
Revenue
Outstanding Balance
Restaurant Sales
Low Stock Items
```

Layout:

```text
┌───────────┬───────────┬───────────┬───────────┐
│ Occupancy │ Revenue   │ Arrivals  │ Departures│
└───────────┴───────────┴───────────┴───────────┘

┌─────────────────────────┬───────────────────────┐
│ Revenue Chart           │ Occupancy Chart       │
└─────────────────────────┴───────────────────────┘

┌─────────────────────────┬───────────────────────┐
│ Today's Reservations    │ Low Stock             │
└─────────────────────────┴───────────────────────┘
```

Dashboard data should come from dedicated backend endpoints.

---

# 23. Reservation Frontend

Reservation screen:

```text
Reservation List
```

Filters:

```text
date
status
guest
room
branch
```

Actions:

```text
New Reservation
View
Edit
Confirm
Cancel
Check In
```

Reservation creation wizard:

```text
Step 1
Guest

Step 2
Dates

Step 3
Room

Step 4
Rate

Step 5
Guest Details

Step 6
Confirmation
```

Availability display:

```text
Available
Reserved
Occupied
Maintenance
```

---

# 24. Guest Management

Guest list:

```text
Name
Phone
Email
Reservations
Last Stay
Status
Actions
```

Guest profile:

```text
Personal Details
Reservation History
Stay History
Payments
Preferences
Loyalty
Notes
```

Actions:

```text
Edit Guest
New Reservation
View Stay History
```

---

# 25. Room Management

Room dashboard should provide a visual room map.

Example:

```text
┌────────┐ ┌────────┐ ┌────────┐
│ 101    │ │ 102    │ │ 103    │
│ Clean  │ │Occupied│ │ Dirty  │
└────────┘ └────────┘ └────────┘

┌────────┐ ┌────────┐ ┌────────┐
│ 104    │ │ 105    │ │ 106    │
│Available││Cleaning││Maintenance│
└────────┘ └────────┘ └────────┘
```

Room filters:

```text
floor
room type
status
branch
```

---

# 26. Front Desk

Front desk dashboard:

```text
Today's Arrivals
Today's Departures
In-House Guests
Available Rooms
Outstanding Balances
```

Check-in interface:

```text
Reservation
 ↓
Guest verification
 ↓
Room
 ↓
Payment/deposit
 ↓
Confirm
```

Check-out:

```text
Stay
 ↓
Charges
 ↓
Invoice
 ↓
Payment
 ↓
Checkout
```

---

# 27. Housekeeping

Housekeeping dashboard:

```text
Pending
Assigned
In Progress
Completed
```

Room cards should display:

```text
Room Number
Room Type
Task
Priority
Assigned Employee
Status
```

Quick actions:

```text
Start
Complete
Inspect
Report Issue
```

---

# 28. Maintenance

Maintenance screen:

```text
Issue
Room
Priority
Assigned To
Status
Created
```

Priority:

```text
LOW
MEDIUM
HIGH
URGENT
```

Actions:

```text
Assign
Start
Resolve
Close
```

---

# 29. Restaurant Management

Restaurant module:

```text
Restaurants
Tables
Menu
Orders
Kitchen
```

Table view:

```text
┌──────┐ ┌──────┐ ┌──────┐
│ T01  │ │ T02  │ │ T03  │
│ Free │ │ Busy │ │Reserved│
└──────┘ └──────┘ └──────┘
```

Clicking a table should allow authorized users to:

```text
view order
create order
add items
transfer table
close table
```

---

# 30. Restaurant POS

POS should prioritize speed.

Layout:

```text
┌──────────────────┬──────────────────────────┐
│ Categories        │ Current Order            │
│                   │                          │
│ Breakfast         │ 2 × Coffee               │
│ Lunch             │ 1 × Burger               │
│ Drinks            │ 1 × Juice                │
│ Desserts          │                          │
│                   │ Subtotal                 │
│ Menu Items        │ Tax                       │
│                   │ Discount                  │
│                   │ Total                     │
└──────────────────┴──────────────────────────┘
```

POS features:

```text
table selection
menu categories
item search
quantity controls
notes
discount
tax
order submission
payment
receipt
```

The server calculates final totals.

---

# 31. Kitchen Display System

Kitchen interface should be optimized for visibility.

Ticket:

```text
┌────────────────────────────┐
│ ORDER #1024                 │
│ TABLE 12                    │
├────────────────────────────┤
│ 2 × Chicken Burger          │
│ 1 × Fries                   │
│ 2 × Soda                    │
├────────────────────────────┤
│ [ACCEPT] [PREPARING]        │
└────────────────────────────┘
```

Ticket states:

```text
PENDING
ACCEPTED
PREPARING
READY
SERVED
```

Kitchen screen should support automatic refresh or real-time updates.

---

# 32. Inventory

Inventory dashboard:

```text
Total Items
Low Stock
Out of Stock
Inventory Value
Recent Transactions
```

Item page:

```text
Item
Category
Unit
Current Stock
Minimum Level
Maximum Level
Cost
Supplier
```

Transactions:

```text
Receipt
Issue
Wastage
Adjustment
Transfer
Return
```

---

# 33. Procurement

Purchase order screen:

```text
Supplier
Items
Quantity
Unit Cost
Tax
Total
Status
```

Purchase order workflow:

```text
Draft
 ↓
Submit
 ↓
Approval
 ↓
Approved
 ↓
Sent
 ↓
Received
```

Goods receipt:

```text
Purchase Order
 ↓
Delivered Items
 ↓
Quantities
 ↓
Accept
 ↓
Inventory Update
```

---

# 34. Billing

Billing dashboard:

```text
Today's Revenue
Outstanding
Paid
Partially Paid
Refunds
```

Invoice page:

```text
Invoice Number
Guest
Stay
Items
Subtotal
Tax
Discount
Total
Paid
Balance
```

Actions:

```text
View
Print
Download
Send
Void
```

---

# 35. Payments

Payment screen:

```text
Invoice
 ↓
Amount Due
 ↓
Payment Method
 ↓
Amount
 ↓
Confirm Payment
 ↓
Receipt
```

Payment methods:

```text
Cash
Card
M-Pesa
Bank Transfer
Other
```

Frontend must prevent obvious duplicate submissions.

The backend remains responsible for idempotency.

---

# 36. Loyalty

Guest loyalty page:

```text
Current Points
Points Earned
Points Redeemed
Transaction History
```

Actions:

```text
Redeem
Adjust
View History
```

---

# 37. Employees

Employee management:

```text
Employee
Department
Position
Branch
Status
User Account
```

Employee profile:

```text
Personal Information
Employment Information
Assigned Branch
Permissions
Activity
```

---

# 38. Reports

Reports dashboard:

```text
Revenue
Occupancy
Reservations
Guests
Restaurant
Inventory
Procurement
Payments
Taxes
```

Date filters:

```text
Today
Yesterday
This Week
This Month
Previous Month
Custom Range
```

Export:

```text
CSV
PDF
```

Exports should preferably be generated by the backend for large or sensitive reports.

---

# 39. Notifications

Notification center:

```text
Notifications
Unread
Read
```

Examples:

```text
New reservation
Payment received
Low stock
Maintenance issue
Purchase order approved
```

UI:

```text
🔔 Notifications
------------------------
New reservation received
5 minutes ago

Low stock: Cooking Oil
20 minutes ago
```

---

# 40. Settings

Settings categories:

```text
Organization
Branches
Users
Roles
Permissions
Hotel
Rooms
Restaurant
Taxes
Discounts
Notifications
System
```

Only authorized administrators should access sensitive settings.

---

# 41. Audit Logs

Audit screen:

```text
Timestamp
User
Action
Entity
Branch
IP
```

Filters:

```text
date
user
action
entity
branch
```

Audit records should be read-only from the normal UI.

---

# 42. Tables and Data Grids

All large datasets should use reusable data tables.

Features:

```text
sorting
pagination
filtering
search
column visibility
row actions
loading
empty state
```

Example:

```text
┌──────┬────────────┬────────────┬────────┐
│ ID   │ Name       │ Status     │ Action │
├──────┼────────────┼────────────┼────────┤
│ 001  │ John Doe   │ Active     │ View   │
│ 002  │ Jane Doe   │ Active     │ View   │
└──────┴────────────┴────────────┴────────┘
```

---

# 43. Charts and Analytics

Use charts for:

```text
Revenue
Occupancy
Reservations
Restaurant Sales
Inventory
Payments
```

Recommended chart types:

```text
Line
Bar
Area
Pie
Donut
```

Charts should have:

```text
title
date range
legend
tooltip
empty state
loading state
```

---

# 44. Loading States

Every asynchronous operation must have a visual loading state.

Examples:

```text
Skeleton
Spinner
Progress indicator
Disabled button
Loading table
```

Avoid blank screens while data is loading.

---

# 45. Error States

Errors should be understandable.

Example:

```text
Unable to load reservations.

Please check your connection and try again.

[Retry]
```

API errors should map backend error codes to user-friendly messages.

Do not expose stack traces.

---

# 46. Empty States

Example:

```text
No reservations found.

Try changing your filters or create a new reservation.

[New Reservation]
```

Empty states should provide a useful next action where appropriate.

---

# 47. Toast Notifications

Use toast notifications for:

```text
success
warning
error
info
```

Examples:

```text
Reservation created successfully.
Payment completed successfully.
Room updated successfully.
Unable to complete checkout.
```

Avoid showing a toast for every minor UI interaction.

---

# 48. Modal Architecture

Reusable modal:

```text
Dialog
 ├── Header
 ├── Content
 ├── Form
 └── Actions
```

Use dialogs for:

```text
confirm cancellation
delete
refund
stock adjustment
approval
status change
```

Destructive actions should require confirmation.

---

# 49. Search

Global search may support:

```text
Guests
Reservations
Rooms
Orders
Invoices
Employees
```

Search interface:

```text
Search...
```

Results should indicate entity type:

```text
John Kamau
Guest

RES-1023
Reservation

Room 204
Room
```

---

# 50. Pagination

API-driven pagination:

```text
Page 1
Page 2
Page 3
...
```

Default:

```text
20 records
```

Options:

```text
20
50
100
```

Frontend must respect the backend's maximum page size.

---

# 51. File Uploads

Potential uploads:

```text
guest documents
employee documents
organization logo
menu images
attachments
```

Upload flow:

```text
Select File
 ↓
Validate Type
 ↓
Validate Size
 ↓
Upload API
 ↓
Server Validation
 ↓
Storage
 ↓
Return URL / Identifier
```

Allowed types must be explicitly configured.

Never trust client-side MIME checks alone.

---

# 52. Security Requirements

Frontend security requirements:

- HTTPS in production
- secure authentication handling
- route protection
- permission-aware UI
- input validation
- output escaping
- safe file uploads
- no secrets in client bundles
- no passwords in logs
- no tokens in URLs
- CSP where appropriate
- secure API communication

Never place:

```text
DATABASE_PASSWORD
JWT_SECRET
API_PRIVATE_KEY
```

in `NEXT_PUBLIC_*` variables.

---

# 53. Accessibility

The frontend should target WCAG 2.2 AA practices.

Requirements:

```text
keyboard navigation
visible focus
semantic HTML
ARIA where needed
screen-reader labels
sufficient contrast
accessible forms
accessible dialogs
accessible tables
```

Buttons must have meaningful labels.

Images require appropriate alt text.

---

# 54. Performance Optimization

Use:

```text
Server Components
dynamic imports
lazy loading
image optimization
pagination
query caching
memoization where justified
code splitting
```

Avoid:

```text
huge client components
unnecessary re-renders
unbounded queries
large initial JavaScript bundles
```

Use Next.js image optimization for appropriate image assets.

---

# 55. SEO

Public pages should have metadata.

Example:

```typescript
export const metadata = {
  title: "Hotel & Restaurant Management System",
  description:
    "Hotel and restaurant management platform",
};
```

Authenticated operational pages generally do not need public SEO optimization.

---

# 56. Frontend Environment Variables

Example:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

Production:

```env
NEXT_PUBLIC_API_URL=https://api.example.com/api/v1
```

Only public configuration should use:

```text
NEXT_PUBLIC_
```

Sensitive values must remain server-side.

---

# 57. Error Monitoring

Production should support frontend error monitoring.

Capture:

```text
JavaScript errors
API failures
route failures
performance problems
```

Do not capture sensitive data unnecessarily.

Before sending telemetry, remove:

```text
passwords
tokens
payment credentials
identification documents
```

---

# 58. Testing

Testing levels:

```text
Unit Tests
Component Tests
Integration Tests
E2E Tests
Accessibility Tests
```

Priority components:

```text
Login
Reservation Form
Check-in
Check-out
POS
Payment
Inventory Adjustment
Purchase Order
```

Example unit test:

```text
Given valid reservation data
When form is submitted
Then API request should be created
```

---

# 59. End-to-End Testing

Use Playwright.

Critical flows:

## Login

```text
Open login
 ↓
Enter credentials
 ↓
Submit
 ↓
Dashboard
```

## Reservation

```text
Login
 ↓
Reservations
 ↓
New Reservation
 ↓
Select guest
 ↓
Select dates
 ↓
Select room
 ↓
Confirm
 ↓
Reservation created
```

## Check-In

```text
Reservation
 ↓
Check-in
 ↓
Verify
 ↓
Confirm
 ↓
Stay created
```

## Restaurant

```text
Restaurant
 ↓
Select table
 ↓
Select menu
 ↓
Create order
 ↓
Send kitchen
 ↓
Kitchen updates status
 ↓
Serve
```

## Payment

```text
Invoice
 ↓
Payment
 ↓
Select method
 ↓
Confirm
 ↓
Receipt
```

---

# 60. Production Build

Run:

```bash
npm run lint
npm run build
npm start
```

The build must complete without:

```text
TypeScript errors
ESLint errors that fail CI
missing environment variables
broken imports
```

---

# 61. Deployment

Recommended architecture:

```text
                 Internet
                    │
                    ▼
              Next.js Frontend
                    │
                  HTTPS
                    │
                    ▼
             Spring Boot API
                    │
                    ▼
                PostgreSQL
```

The frontend can be deployed to a Next.js-compatible cloud platform.

Production environment must configure:

```text
NEXT_PUBLIC_API_URL
```

The backend must allow the production frontend origin through CORS.

---

# 62. CI/CD

Frontend pipeline:

```text
Git Push
   ↓
Install Dependencies
   ↓
Lint
   ↓
Type Check
   ↓
Unit Tests
   ↓
Build
   ↓
E2E Tests
   ↓
Deploy Preview
   ↓
Production Deployment
```

Production deployment must only occur after required checks pass.

---

# 63. Implementation Phases

## Phase 1 — Foundation

Implement:

```text
Next.js
TypeScript
Tailwind
layout
UI components
environment configuration
API client
```

---

## Phase 2 — Authentication

Implement:

```text
login
logout
session handling
JWT/session integration
route protection
permissions
```

---

## Phase 3 — Dashboard

Implement:

```text
KPIs
charts
recent activity
notifications
```

---

## Phase 4 — Hotel Operations

Implement:

```text
guests
rooms
room types
reservations
stays
front desk
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
POS
orders
kitchen
```

---

## Phase 7 — Inventory

Implement:

```text
inventory
stock transactions
suppliers
procurement
purchase orders
goods receipts
```

---

## Phase 8 — Finance

Implement:

```text
invoices
taxes
discounts
payments
refunds
```

---

## Phase 9 — Supporting Features

Implement:

```text
loyalty
notifications
employees
settings
audit
```

---

## Phase 10 — Reporting

Implement:

```text
dashboard reports
revenue reports
occupancy reports
restaurant reports
inventory reports
financial reports
```

---

## Phase 11 — Testing

Complete:

```text
unit tests
component tests
integration tests
E2E tests
accessibility tests
```

---

## Phase 12 — Production

Complete:

```text
production configuration
CI/CD
deployment
monitoring
performance optimization
security review
```

---

# 64. Definition of Done

A frontend feature is complete when:

- page exists
- route exists
- API integration exists
- TypeScript types exist
- loading state exists
- error state exists
- empty state exists
- validation exists
- authorization exists
- responsive layout exists
- accessibility is considered
- tests exist
- API errors are handled
- success feedback exists

For financial screens:

- amounts are formatted consistently
- currency is KES
- backend totals are displayed
- duplicate submissions are prevented at UI level
- payment status is clear

For reservation screens:

- availability is retrieved from backend
- date validation exists
- status is displayed
- conflicting reservations are handled gracefully

For POS:

- interface is fast
- cart state is reliable
- totals are server-authoritative
- duplicate order submissions are handled
- kitchen status is visible

---

# 65. Frontend Implementation Checklist

## Project

- [ ] Create Next.js application
- [ ] Configure TypeScript
- [ ] Configure Tailwind
- [ ] Configure ESLint
- [ ] Install TanStack Query
- [ ] Install Zustand
- [ ] Install React Hook Form
- [ ] Install Zod
- [ ] Install Recharts
- [ ] Install Lucide
- [ ] Configure environment variables

## Architecture

- [ ] Create App Router
- [ ] Create layouts
- [ ] Create feature modules
- [ ] Create UI library
- [ ] Create API client
- [ ] Create shared types
- [ ] Create validation schemas

## Authentication

- [ ] Login
- [ ] Logout
- [ ] Session management
- [ ] JWT integration
- [ ] Route protection
- [ ] Permission checks
- [ ] Branch selection

## Dashboard

- [ ] KPI cards
- [ ] Revenue chart
- [ ] Occupancy chart
- [ ] Reservation summary
- [ ] Low-stock summary
- [ ] Notifications

## Hotel

- [ ] Guests
- [ ] Rooms
- [ ] Room types
- [ ] Reservations
- [ ] Stays
- [ ] Check-in
- [ ] Check-out

## Operations

- [ ] Housekeeping
- [ ] Maintenance

## Restaurant

- [ ] Restaurants
- [ ] Tables
- [ ] Menu
- [ ] POS
- [ ] Orders
- [ ] Kitchen

## Inventory

- [ ] Categories
- [ ] Units
- [ ] Items
- [ ] Transactions
- [ ] Stock adjustments

## Procurement

- [ ] Suppliers
- [ ] Purchase orders
- [ ] Approval
- [ ] Goods receipts

## Finance

- [ ] Taxes
- [ ] Discounts
- [ ] Invoices
- [ ] Payments
- [ ] Refunds

## Supporting

- [ ] Loyalty
- [ ] Notifications
- [ ] Employees
- [ ] Settings
- [ ] Audit logs

## Reports

- [ ] Revenue
- [ ] Occupancy
- [ ] Reservations
- [ ] Restaurant
- [ ] Inventory
- [ ] Procurement
- [ ] Payments
- [ ] Taxes

## UX

- [ ] Loading states
- [ ] Error states
- [ ] Empty states
- [ ] Toasts
- [ ] Dialogs
- [ ] Responsive layout
- [ ] Accessibility

## Testing

- [ ] Unit tests
- [ ] Component tests
- [ ] Integration tests
- [ ] E2E tests
- [ ] Accessibility tests

## Production

- [ ] Environment configuration
- [ ] Build
- [ ] CI/CD
- [ ] Deployment
- [ ] Monitoring
- [ ] Performance optimization
- [ ] Security review

---

# COMPLETE FRONTEND ARCHITECTURE

```text
                         ┌─────────────────────┐
                         │       USERS         │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │      NEXT.JS        │
                         │    APP ROUTER       │
                         └──────────┬──────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
                    ▼               ▼               ▼
                 Layouts         Pages         Components
                    │               │               │
                    └───────────────┼───────────────┘
                                    │
                                    ▼
                           Feature Modules
                                    │
              ┌─────────────────────┼────────────────────┐
              │                     │                    │
              ▼                     ▼                    ▼
          Forms/UI              State                Queries
              │                     │                    │
              └─────────────────────┼────────────────────┘
                                    │
                                    ▼
                              API Client
                                    │
                                  HTTPS
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   SPRING BOOT API   │
                         └──────────┬──────────┘
                                    │
                                    ▼
                              PostgreSQL
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
DOCUMENT 10  ✓ Next.js Frontend Implementation
DOCUMENT 11  → Testing Strategy
DOCUMENT 12  → Deployment Specification
```

---

# NEXT IMPLEMENTATION STEP

The next document should be:

```text
TESTING_STRATEGY.md
```

It should define the complete testing architecture across:

```text
Backend Unit Tests
Backend Integration Tests
PostgreSQL Testcontainers
Spring Security Tests
API Tests
Frontend Unit Tests
Frontend Component Tests
Frontend Integration Tests
Playwright E2E Tests
Reservation Concurrency Tests
Payment Idempotency Tests
Inventory Consistency Tests
POS Tests
Performance Tests
Security Tests
Accessibility Tests
CI/CD Test Gates
```

After that:

```text
DEPLOYMENT_SPECIFICATION.md
```

should define:

```text
Docker
PostgreSQL production
Next.js deployment
Spring Boot deployment
Environment variables
HTTPS
Reverse proxy
CI/CD
Database migrations
Backups
Monitoring
Logging
Scaling
Disaster recovery
Production security
```

The twelve-document architecture will then provide a complete implementation blueprint for the HRMS.