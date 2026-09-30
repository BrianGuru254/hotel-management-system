# HOTEL & RESTAURANT MANAGEMENT SYSTEM

# Frontend Project Structure Specification

**Project:** Hotel & Restaurant Management System (HRMS)  
**Document:** Frontend Project Structure  
**Version:** 1.0  
**Status:** Implementation-Ready  
**Date:** September 2026  
**Frontend:** Next.js + TypeScript  
**UI:** Responsive Web Application  
**API:** REST + JSON  
**Authentication:** JWT  
**Backend:** Spring Boot  
**Database:** PostgreSQL  

---

# Table of Contents

1. [Document Purpose](#1-document-purpose)
2. [Frontend Objectives](#2-frontend-objectives)
3. [Technology Stack](#3-technology-stack)
4. [Frontend Architecture](#4-frontend-architecture)
5. [Complete Project Directory](#5-complete-project-directory)
6. [Root Project Files](#6-root-project-files)
7. [Next.js App Router](#7-nextjs-app-router)
8. [Application Layout](#8-application-layout)
9. [Authentication Architecture](#9-authentication-architecture)
10. [Authorization and Permissions](#10-authorization-and-permissions)
11. [Dashboard Architecture](#11-dashboard-architecture)
12. [Guest Management](#12-guest-management)
13. [Room Management](#13-room-management)
14. [Reservation Management](#14-reservation-management)
15. [Check-in and Check-out](#15-check-in-and-check-out)
16. [Housekeeping](#16-housekeeping)
17. [Maintenance](#17-maintenance)
18. [Restaurant Management](#18-restaurant-management)
19. [Menu Management](#19-menu-management)
20. [Restaurant Orders](#20-restaurant-orders)
21. [Kitchen Management](#21-kitchen-management)
22. [Inventory Management](#22-inventory-management)
23. [Supplier Management](#23-supplier-management)
24. [Procurement](#24-procurement)
25. [Billing and Invoices](#25-billing-and-invoices)
26. [Payments](#26-payments)
27. [Employees](#27-employees)
28. [Reports](#28-reports)
29. [Notifications](#29-notifications)
30. [Settings](#30-settings)
31. [Reusable Components](#31-reusable-components)
32. [Forms Architecture](#32-forms-architecture)
33. [Tables Architecture](#33-tables-architecture)
34. [API Client Architecture](#34-api-client-architecture)
35. [State Management](#35-state-management)
36. [Server State Management](#36-server-state-management)
37. [Validation](#37-validation)
38. [TypeScript Architecture](#38-typescript-architecture)
39. [Error Handling](#39-error-handling)
40. [Loading and Empty States](#40-loading-and-empty-states)
41. [Responsive Design](#41-responsive-design)
42. [Accessibility](#42-accessibility)
43. [Security](#43-security)
44. [Performance](#44-performance)
45. [Testing Structure](#45-testing-structure)
46. [Environment Configuration](#46-environment-configuration)
47. [Frontend-Backend Communication](#47-frontend-backend-communication)
48. [Navigation Structure](#48-navigation-structure)
49. [Role-Based Navigation](#49-role-based-navigation)
50. [Frontend Development Order](#50-frontend-development-order)
51. [Definition of Done](#51-definition-of-done)
52. [Final Frontend Architecture](#52-final-frontend-architecture)

---

# 1. Document Purpose

This document defines the complete frontend architecture and project structure for the Hotel & Restaurant Management System.

The frontend will provide the user-facing interface for:

- Hotel administration
- Reservations
- Guest management
- Room management
- Check-in
- Check-out
- Housekeeping
- Maintenance
- Restaurant operations
- Kitchen operations
- Inventory
- Procurement
- Billing
- Payments
- Employees
- Reports
- Notifications
- System settings

The frontend communicates with the Spring Boot backend through a versioned REST API.

---

# 2. Frontend Objectives

The frontend must be:

- Responsive
- Accessible
- Secure
- Fast
- Modular
- Maintainable
- Type-safe
- User-friendly
- Role-aware
- Mobile-compatible
- Desktop-optimized
- API-driven

The interface should support:

```text
Desktop
Tablet
Mobile
```

The primary operational interface is expected to be desktop/tablet oriented, while selected functions should remain usable on mobile devices.

---

# 3. Technology Stack

| Technology | Purpose |
|---|---|
| Next.js | React framework |
| React | UI framework |
| TypeScript | Type safety |
| Tailwind CSS | Styling |
| React Hook Form | Form management |
| Zod | Validation |
| TanStack Query | Server state |
| Zustand | Client state where required |
| Axios / Fetch | API communication |
| Recharts | Charts |
| Lucide React | Icons |
| date-fns | Date handling |
| Playwright | E2E testing |
| Vitest/Jest | Unit/component testing |

The exact libraries may be adjusted during implementation, but the architectural responsibilities should remain unchanged.

---

# 4. Frontend Architecture

The frontend follows a feature-oriented architecture.

```text
Browser
   │
   ▼
Next.js App Router
   │
   ├── Pages
   ├── Layouts
   └── Route Protection
          │
          ▼
     Feature Modules
          │
          ├── Components
          ├── Forms
          ├── Hooks
          ├── API Functions
          └── Types
          │
          ▼
       API Client
          │
          ▼
   Spring Boot REST API
```

The frontend should avoid placing all business functionality into one large `components` directory.

Instead, domain-specific functionality belongs inside `features`.

---

# 5. Complete Project Directory

Recommended structure:

```text
hrms-frontend/
│
├── package.json
├── package-lock.json
├── tsconfig.json
├── next.config.ts
├── postcss.config.mjs
├── eslint.config.mjs
├── components.json
├── README.md
├── .gitignore
├── .env.example
├── Dockerfile
├── docker-compose.yml
│
├── public/
│   ├── images/
│   ├── icons/
│   ├── logos/
│   └── fonts/
│
└── src/
    │
    ├── app/
    │   │
    │   ├── layout.tsx
    │   ├── page.tsx
    │   ├── globals.css
    │   │
    │   ├── (auth)/
    │   │   ├── layout.tsx
    │   │   ├── login/
    │   │   │   └── page.tsx
    │   │   ├── forgot-password/
    │   │   │   └── page.tsx
    │   │   └── reset-password/
    │   │       └── page.tsx
    │   │
    │   └── (dashboard)/
    │       ├── layout.tsx
    │       │
    │       ├── dashboard/
    │       │   └── page.tsx
    │       │
    │       ├── reservations/
    │       │   ├── page.tsx
    │       │   ├── new/
    │       │   │   └── page.tsx
    │       │   └── [id]/
    │       │       ├── page.tsx
    │       │       └── edit/
    │       │           └── page.tsx
    │       │
    │       ├── guests/
    │       │   ├── page.tsx
    │       │   ├── new/
    │       │   └── [id]/
    │       │
    │       ├── rooms/
    │       │   ├── page.tsx
    │       │   ├── types/
    │       │   └── [id]/
    │       │
    │       ├── stays/
    │       │   ├── page.tsx
    │       │   ├── check-in/
    │       │   └── check-out/
    │       │
    │       ├── housekeeping/
    │       │   └── page.tsx
    │       │
    │       ├── maintenance/
    │       │   └── page.tsx
    │       │
    │       ├── restaurant/
    │       │   ├── page.tsx
    │       │   ├── tables/
    │       │   ├── menu/
    │       │   ├── orders/
    │       │   └── kitchen/
    │       │
    │       ├── inventory/
    │       │   ├── page.tsx
    │       │   ├── products/
    │       │   ├── stock/
    │       │   └── adjustments/
    │       │
    │       ├── procurement/
    │       │   ├── page.tsx
    │       │   ├── suppliers/
    │       │   └── purchase-orders/
    │       │
    │       ├── billing/
    │       │   ├── page.tsx
    │       │   ├── invoices/
    │       │   └── receipts/
    │       │
    │       ├── payments/
    │       │   └── page.tsx
    │       │
    │       ├── employees/
    │       │   └── page.tsx
    │       │
    │       ├── reports/
    │       │   ├── page.tsx
    │       │   ├── revenue/
    │       │   ├── occupancy/
    │       │   ├── inventory/
    │       │   └── restaurant/
    │       │
    │       ├── notifications/
    │       │   └── page.tsx
    │       │
    │       └── settings/
    │           ├── page.tsx
    │           ├── users/
    │           ├── roles/
    │           ├── branches/
    │           └── system/
    │
    ├── components/
    │   ├── ui/
    │   ├── layout/
    │   ├── forms/
    │   ├── tables/
    │   ├── charts/
    │   ├── feedback/
    │   ├── dialogs/
    │   └── navigation/
    │
    ├── features/
    │   ├── auth/
    │   ├── dashboard/
    │   ├── guests/
    │   ├── rooms/
    │   ├── reservations/
    │   ├── stays/
    │   ├── housekeeping/
    │   ├── maintenance/
    │   ├── restaurant/
    │   ├── menu/
    │   ├── orders/
    │   ├── kitchen/
    │   ├── inventory/
    │   ├── procurement/
    │   ├── billing/
    │   ├── payments/
    │   ├── employees/
    │   ├── reports/
    │   ├── notifications/
    │   └── settings/
    │
    ├── lib/
    │   ├── api/
    │   ├── auth/
    │   ├── permissions/
    │   ├── validation/
    │   ├── formatting/
    │   ├── constants/
    │   └── utils/
    │
    ├── hooks/
    │
    ├── stores/
    │
    ├── types/
    │
    ├── config/
    │
    └── styles/
```

---

# 6. Root Project Files

## `package.json`

Defines:

- dependencies
- development dependencies
- scripts
- project metadata

Recommended scripts:

```json
{
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint",
    "test": "vitest",
    "test:e2e": "playwright test"
  }
}
```

---

## `.env.example`

Example:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
NEXT_PUBLIC_APP_NAME=HRMS
```

Production secrets must not be committed to Git.

---

# 7. Next.js App Router

The project uses the Next.js App Router.

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
/guests
/rooms
/stays
/housekeeping
/restaurant
/inventory
/procurement
/billing
/payments
/employees
/reports
/settings
```

Route groups organize application sections without affecting URLs.

Example:

```text
app/
├── (auth)/
└── (dashboard)/
```

---

# 8. Application Layout

The main dashboard layout:

```text
┌──────────────────────────────────────────────┐
│ Top Header                                   │
├─────────────┬────────────────────────────────┤
│             │                                │
│ Sidebar     │ Main Content                   │
│             │                                │
│ Navigation  │ Page                           │
│             │                                │
│             │                                │
└─────────────┴────────────────────────────────┘
```

Layout components:

```text
components/layout/
├── AppShell.tsx
├── Sidebar.tsx
├── Header.tsx
├── UserMenu.tsx
├── Breadcrumbs.tsx
├── MobileNavigation.tsx
└── PageContainer.tsx
```

---

# 9. Authentication Architecture

Authentication flow:

```text
Login Page
    ↓
Login Form
    ↓
API Client
    ↓
Spring Boot
    ↓
JWT
    ↓
Authenticated Session
    ↓
Dashboard
```

Auth feature:

```text
features/auth/
├── components/
│   ├── LoginForm.tsx
│   ├── ForgotPasswordForm.tsx
│   └── ResetPasswordForm.tsx
├── hooks/
│   └── useAuth.ts
├── api/
│   └── authApi.ts
├── schemas/
│   └── authSchemas.ts
├── types/
│   └── authTypes.ts
└── index.ts
```

Authentication must support:

- Login
- Logout
- Current user
- Refresh token
- Password reset
- Session expiration

---

# 10. Authorization and Permissions

The frontend should not rely solely on hidden navigation.

The backend remains the final authority for authorization.

Frontend permissions are used to:

- hide unavailable navigation items
- disable actions
- protect pages
- improve user experience

Example:

```text
permissions:
  reservation:create
  reservation:update
  reservation:cancel
  payment:create
  payment:refund
  inventory:adjust
  report:view
  user:manage
```

Permission utilities:

```text
lib/permissions/
├── can.ts
├── permissions.ts
└── rolePermissions.ts
```

Example:

```text
can("reservation:create")
```

---

# 11. Dashboard Architecture

Dashboard feature:

```text
features/dashboard/
├── components/
│   ├── RevenueCard.tsx
│   ├── OccupancyCard.tsx
│   ├── CheckInsCard.tsx
│   ├── CheckOutsCard.tsx
│   ├── RoomStatusCard.tsx
│   ├── RevenueChart.tsx
│   ├── OccupancyChart.tsx
│   └── RecentReservations.tsx
├── api/
├── hooks/
├── types/
└── index.ts
```

Dashboard information:

```text
Today's Revenue
Current Occupancy
Check-ins
Check-outs
Available Rooms
Pending Reservations
Restaurant Revenue
Low Stock Alerts
Outstanding Payments
```

---

# 12. Guest Management

Feature structure:

```text
features/guests/
├── components/
│   ├── GuestTable.tsx
│   ├── GuestForm.tsx
│   ├── GuestProfile.tsx
│   ├── GuestHistory.tsx
│   └── GuestFilters.tsx
├── api/
│   └── guestsApi.ts
├── hooks/
├── schemas/
├── types/
└── index.ts
```

Pages:

```text
/guests
/guests/new
/guests/[id]
```

---

# 13. Room Management

Feature structure:

```text
features/rooms/
├── components/
│   ├── RoomGrid.tsx
│   ├── RoomCard.tsx
│   ├── RoomForm.tsx
│   ├── RoomStatusBadge.tsx
│   ├── RoomTypeTable.tsx
│   └── AvailabilityCalendar.tsx
├── api/
├── hooks/
├── schemas/
├── types/
└── index.ts
```

Pages:

```text
/rooms
/rooms/types
/rooms/[id]
```

Room statuses should have clear visual indicators.

---

# 14. Reservation Management

Feature:

```text
features/reservations/
├── components/
│   ├── ReservationTable.tsx
│   ├── ReservationForm.tsx
│   ├── ReservationDetails.tsx
│   ├── ReservationStatusBadge.tsx
│   ├── AvailabilitySearch.tsx
│   ├── BookingCalendar.tsx
│   └── ReservationFilters.tsx
├── api/
│   └── reservationsApi.ts
├── hooks/
├── schemas/
├── types/
└── index.ts
```

Pages:

```text
/reservations
/reservations/new
/reservations/[id]
/reservations/[id]/edit
```

Reservation creation workflow:

```text
Guest
 ↓
Dates
 ↓
Availability
 ↓
Room Selection
 ↓
Rate
 ↓
Guest Details
 ↓
Payment/Deposit
 ↓
Confirmation
```

Availability must always be confirmed by the backend.

---

# 15. Check-in and Check-out

Stay feature:

```text
features/stays/
├── components/
│   ├── CheckInForm.tsx
│   ├── CheckOutForm.tsx
│   ├── StayDetails.tsx
│   ├── StayCharges.tsx
│   ├── GuestVerification.tsx
│   └── RoomAssignment.tsx
├── api/
├── hooks/
├── schemas/
└── types/
```

Pages:

```text
/stays
/stays/check-in
/stays/check-out
```

Check-in:

```text
Reservation
 ↓
Verify Guest
 ↓
Verify Room
 ↓
Assign Room
 ↓
Create Stay
```

Check-out:

```text
Stay
 ↓
Calculate Charges
 ↓
Review Invoice
 ↓
Payment
 ↓
Receipt
 ↓
Complete Stay
```

---

# 16. Housekeeping

```text
features/housekeeping/
├── components/
│   ├── HousekeepingBoard.tsx
│   ├── CleaningTaskCard.tsx
│   ├── TaskAssignment.tsx
│   ├── RoomCleaningStatus.tsx
│   └── InspectionDialog.tsx
├── api/
├── hooks/
├── types/
└── index.ts
```

Primary interface:

```text
Room
 ↓
Cleaning Status
 ↓
Assigned Staff
 ↓
Task
 ↓
Completion
```

---

# 17. Maintenance

```text
features/maintenance/
├── components/
│   ├── MaintenanceBoard.tsx
│   ├── MaintenanceForm.tsx
│   ├── MaintenanceDetails.tsx
│   └── MaintenanceStatusBadge.tsx
├── api/
├── hooks/
└── types/
```

---

# 18. Restaurant Management

```text
features/restaurant/
├── components/
│   ├── RestaurantSelector.tsx
│   ├── TableMap.tsx
│   ├── TableCard.tsx
│   └── RestaurantDashboard.tsx
├── api/
├── hooks/
└── types/
```

Pages:

```text
/restaurant
/restaurant/tables
```

---

# 19. Menu Management

```text
features/menu/
├── components/
│   ├── MenuTable.tsx
│   ├── MenuItemForm.tsx
│   ├── MenuCategoryForm.tsx
│   ├── MenuItemCard.tsx
│   └── MenuAvailabilityToggle.tsx
├── api/
├── hooks/
├── schemas/
└── types/
```

---

# 20. Restaurant Orders

```text
features/orders/
├── components/
│   ├── OrderTable.tsx
│   ├── OrderForm.tsx
│   ├── OrderDetails.tsx
│   ├── OrderItemSelector.tsx
│   ├── OrderStatusBadge.tsx
│   └── POSInterface.tsx
├── api/
├── hooks/
├── schemas/
└── types/
```

Order workflow:

```text
Create Order
     ↓
Submit
     ↓
Kitchen
     ↓
Preparing
     ↓
Ready
     ↓
Served
     ↓
Payment
     ↓
Completed
```

---

# 21. Kitchen Management

```text
features/kitchen/
├── components/
│   ├── KitchenDisplay.tsx
│   ├── KitchenTicket.tsx
│   ├── PreparationQueue.tsx
│   └── OrderStatusControls.tsx
├── api/
├── hooks/
└── types/
```

The kitchen interface should prioritize:

- readability
- large status controls
- order timing
- urgency
- minimal navigation

---

# 22. Inventory Management

```text
features/inventory/
├── components/
│   ├── InventoryTable.tsx
│   ├── ProductForm.tsx
│   ├── StockLevelCard.tsx
│   ├── StockTransactionTable.tsx
│   ├── StockAdjustmentForm.tsx
│   └── LowStockAlert.tsx
├── api/
├── hooks/
├── schemas/
└── types/
```

Pages:

```text
/inventory
/inventory/products
/inventory/stock
/inventory/adjustments
```

---

# 23. Supplier Management

```text
features/procurement/
├── components/
│   ├── SupplierTable.tsx
│   ├── SupplierForm.tsx
│   ├── SupplierDetails.tsx
│   └── SupplierFilters.tsx
├── api/
├── hooks/
└── types/
```

---

# 24. Procurement

Purchase order interface:

```text
features/procurement/
├── components/
│   ├── PurchaseOrderTable.tsx
│   ├── PurchaseOrderForm.tsx
│   ├── PurchaseOrderDetails.tsx
│   ├── ApprovalDialog.tsx
│   └── GoodsReceivingForm.tsx
├── api/
├── hooks/
├── schemas/
└── types/
```

Workflow:

```text
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
Inventory
```

---

# 25. Billing and Invoices

```text
features/billing/
├── components/
│   ├── InvoiceTable.tsx
│   ├── InvoiceDetails.tsx
│   ├── InvoiceForm.tsx
│   ├── InvoiceItemTable.tsx
│   ├── ChargeSummary.tsx
│   └── ReceiptPreview.tsx
├── api/
├── hooks/
└── types/
```

Pages:

```text
/billing
/billing/invoices
/billing/receipts
```

---

# 26. Payments

```text
features/payments/
├── components/
│   ├── PaymentForm.tsx
│   ├── PaymentMethodSelector.tsx
│   ├── PaymentTable.tsx
│   ├── PaymentStatusBadge.tsx
│   └── RefundDialog.tsx
├── api/
├── hooks/
├── schemas/
└── types/
```

Payment methods:

```text
Cash
Card
Mobile Money
Bank Transfer
Other
```

The frontend must never directly trust payment success based only on client-side state.

Payment status must be confirmed by the backend.

---

# 27. Employees

```text
features/employees/
├── components/
│   ├── EmployeeTable.tsx
│   ├── EmployeeForm.tsx
│   ├── EmployeeProfile.tsx
│   └── EmployeeFilters.tsx
├── api/
├── hooks/
├── schemas/
└── types/
```

---

# 28. Reports

```text
features/reports/
├── components/
│   ├── ReportFilters.tsx
│   ├── ReportTable.tsx
│   ├── RevenueChart.tsx
│   ├── OccupancyChart.tsx
│   └── ReportExportButtons.tsx
├── api/
├── hooks/
├── types/
└── utils/
```

Reports:

```text
Revenue
Occupancy
Reservations
Payments
Restaurant Sales
Inventory
Procurement
Guests
Employees
```

Export formats:

```text
PDF
CSV
Excel
```

---

# 29. Notifications

```text
features/notifications/
├── components/
│   ├── NotificationBell.tsx
│   ├── NotificationList.tsx
│   ├── NotificationItem.tsx
│   └── NotificationPanel.tsx
├── api/
├── hooks/
└── types/
```

Notification states:

```text
Unread
Read
Archived
```

---

# 30. Settings

```text
features/settings/
├── components/
│   ├── GeneralSettings.tsx
│   ├── UserSettings.tsx
│   ├── RoleSettings.tsx
│   ├── BranchSettings.tsx
│   ├── SystemSettings.tsx
│   └── SecuritySettings.tsx
├── api/
├── hooks/
├── schemas/
└── types/
```

Pages:

```text
/settings
/settings/users
/settings/roles
/settings/branches
/settings/system
```

---

# 31. Reusable Components

Global reusable components live under:

```text
components/
```

Structure:

```text
components/
├── ui/
├── layout/
├── forms/
├── tables/
├── charts/
├── feedback/
├── dialogs/
└── navigation/
```

---

## UI Components

Examples:

```text
Button
Input
Select
Checkbox
Radio
Switch
Badge
Card
Tabs
Tooltip
Dropdown
Modal
Drawer
Calendar
DatePicker
```

---

## Feedback Components

```text
LoadingSpinner
Skeleton
ErrorMessage
EmptyState
SuccessMessage
Toast
Alert
ConfirmationDialog
```

---

# 32. Forms Architecture

Forms should use:

```text
React Hook Form
+
Zod
```

Example:

```text
features/reservations/schemas/
└── reservationSchema.ts
```

A form should have:

```text
Schema
 ↓
Form
 ↓
Validation
 ↓
API Request
 ↓
Response
```

Client-side validation improves user experience.

Server-side validation remains authoritative.

---

# 33. Tables Architecture

Reusable table components should support:

- Pagination
- Sorting
- Filtering
- Searching
- Column visibility
- Row actions
- Bulk actions where appropriate
- Loading states
- Empty states

Example:

```text
components/tables/
├── DataTable.tsx
├── DataTablePagination.tsx
├── DataTableFilters.tsx
└── DataTableToolbar.tsx
```

---

# 34. API Client Architecture

API communication should be centralized.

```text
lib/api/
├── client.ts
├── config.ts
├── errors.ts
├── auth.ts
└── endpoints.ts
```

Feature-specific API calls remain inside feature modules.

Example:

```text
features/guests/api/guestsApi.ts
features/rooms/api/roomsApi.ts
features/reservations/api/reservationsApi.ts
```

Architecture:

```text
Component
   ↓
Hook
   ↓
Feature API
   ↓
API Client
   ↓
Spring Boot
```

---

# 35. State Management

Not all state should be stored globally.

Use local component state for:

```text
Modal open/close
Temporary form values
UI toggles
Selected tabs
```

Use global client state only where needed.

Possible global stores:

```text
stores/
├── authStore.ts
├── uiStore.ts
├── branchStore.ts
└── notificationStore.ts
```

Zustand may be used for client-side state.

---

# 36. Server State Management

Server data should be managed separately from client UI state.

TanStack Query can manage:

- fetching
- caching
- mutations
- invalidation
- loading states
- errors
- refetching

Example:

```text
useGuests()
useGuest(id)
useCreateGuest()
useUpdateGuest()
useDeleteGuest()
```

This avoids manually duplicating server-state logic throughout components.

---

# 37. Validation

Validation should exist at three levels:

```text
Frontend Validation
        ↓
Backend Validation
        ↓
Database Constraints
```

Frontend:

```text
Zod
React Hook Form
```

Backend:

```text
Jakarta Bean Validation
```

Database:

```text
NOT NULL
UNIQUE
FOREIGN KEY
CHECK
```

The frontend should never assume that client-side validation is sufficient.

---

# 38. TypeScript Architecture

Shared application types:

```text
types/
├── api.ts
├── auth.ts
├── user.ts
├── guest.ts
├── room.ts
├── reservation.ts
├── stay.ts
├── restaurant.ts
├── order.ts
├── inventory.ts
├── billing.ts
├── payment.ts
└── common.ts
```

Feature-specific types may remain inside the feature.

Example:

```text
features/reservations/types/
├── reservation.ts
└── availability.ts
```

Avoid using:

```typescript
any
```

unless there is a documented reason.

---

# 39. Error Handling

API errors should be normalized.

Example:

```typescript
interface ApiError {
  code: string;
  message: string;
  fields?: Record<string, string>;
}
```

The application should provide user-friendly messages.

Example:

Backend:

```text
ROOM_NOT_AVAILABLE
```

Frontend:

```text
"The selected room is no longer available. Please choose another room."
```

Technical errors should be logged appropriately without exposing sensitive information.

---

# 40. Loading and Empty States

Every data-driven page should handle:

```text
Loading
Success
Empty
Error
```

Example:

```text
Loading:
[ Skeleton ]

Empty:
"No reservations found."

Error:
"Unable to load reservations."

Success:
Reservation table
```

This should be standardized across the application.

---

# 41. Responsive Design

Breakpoints should support:

```text
Mobile
Tablet
Desktop
Large Desktop
```

Primary dashboard:

```text
Desktop → Full sidebar
Tablet → Collapsible sidebar
Mobile → Mobile navigation
```

Tables should become responsive using:

- horizontal scrolling
- responsive cards
- prioritized columns
- mobile action menus

---

# 42. Accessibility

The frontend should follow accessible design principles.

Requirements:

- semantic HTML
- keyboard navigation
- visible focus states
- accessible labels
- sufficient contrast
- screen-reader-friendly controls
- meaningful error messages
- accessible dialogs
- accessible tables

Interactive elements must not rely solely on color.

---

# 43. Security

Frontend security requirements include:

- secure authentication handling
- protected routes
- permission-aware UI
- HTTPS in production
- input validation
- safe rendering
- CSRF considerations where applicable
- secure token handling
- no secrets in client-side code

The frontend must never contain:

```text
Database credentials
Private API keys
Payment secrets
JWT signing secrets
Server credentials
```

---

# 44. Performance

Performance strategies:

### Next.js Server Components

Use Server Components where suitable for server-rendered content.

### Client Components

Use `"use client"` only when interactivity requires it.

### Code Splitting

Load large features only when needed.

### Image Optimization

Use Next.js image optimization.

### Data Fetching

Use caching and request deduplication.

### Tables

Paginate large datasets rather than loading thousands of records.

### Charts

Load complex visualization components efficiently.

---

# 45. Testing Structure

Recommended:

```text
tests/
├── unit/
├── component/
├── integration/
└── e2e/
```

Feature tests may also live beside their components.

Example:

```text
features/reservations/
├── components/
│   ├── ReservationForm.tsx
│   └── ReservationForm.test.tsx
```

E2E tests:

```text
e2e/
├── auth.spec.ts
├── reservations.spec.ts
├── check-in.spec.ts
├── check-out.spec.ts
├── restaurant.spec.ts
├── inventory.spec.ts
└── payments.spec.ts
```

Critical user journeys must be covered.

---

# 46. Environment Configuration

Development:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

Staging:

```env
NEXT_PUBLIC_API_URL=https://staging-api.example.com/api/v1
```

Production:

```env
NEXT_PUBLIC_API_URL=https://api.example.com/api/v1
```

Environment-specific configuration must be managed outside source control.

---

# 47. Frontend-Backend Communication

Communication flow:

```text
Next.js
   │
   │ HTTPS
   ▼
Spring Boot
   │
   ▼
PostgreSQL
```

Example request:

```text
GET /api/v1/rooms
```

Example response:

```json
{
  "success": true,
  "data": []
}
```

Authentication information is included according to the selected JWT/session architecture.

The frontend should not directly communicate with PostgreSQL.

---

# 48. Navigation Structure

Main navigation:

```text
Dashboard

Front Office
├── Reservations
├── Guests
├── Rooms
├── Check-in
└── Check-out

Hotel Operations
├── Housekeeping
└── Maintenance

Restaurant
├── Overview
├── Tables
├── Menu
├── Orders
└── Kitchen

Inventory
├── Products
├── Stock
└── Adjustments

Procurement
├── Suppliers
├── Purchase Orders
└── Receiving

Finance
├── Billing
├── Invoices
├── Payments
└── Receipts

Management
├── Employees
├── Reports
└── Notifications

Administration
├── Users
├── Roles
├── Branches
└── Settings
```

---

# 49. Role-Based Navigation

The navigation should adapt to permissions.

Example:

### Receptionist

```text
Dashboard
Reservations
Guests
Rooms
Check-in
Check-out
```

### Waiter

```text
Dashboard
Restaurant
Orders
Tables
```

### Chef

```text
Dashboard
Kitchen
Orders
```

### Inventory Manager

```text
Dashboard
Inventory
Suppliers
Procurement
```

### Accountant

```text
Dashboard
Billing
Invoices
Payments
Reports
```

### Administrator

Access to administrative functionality based on assigned permissions.

The backend remains the authoritative authorization layer.

---

# 50. Frontend Development Order

The frontend should be developed in phases.

## Phase 1 — Project Setup

```text
Next.js
TypeScript
Tailwind
ESLint
Testing
Environment configuration
```

## Phase 2 — Design System

```text
Colors
Typography
Spacing
Buttons
Forms
Tables
Cards
Dialogs
Navigation
```

## Phase 3 — Application Shell

```text
Sidebar
Header
Dashboard layout
Responsive navigation
Breadcrumbs
User menu
```

## Phase 4 — Authentication

```text
Login
Logout
Password reset
Session handling
Protected routes
Permissions
```

## Phase 5 — Dashboard

```text
Statistics
Charts
Recent activity
Alerts
Quick actions
```

## Phase 6 — Hotel Operations

```text
Guests
Rooms
Reservations
Availability
Check-in
Check-out
Housekeeping
Maintenance
```

## Phase 7 — Restaurant

```text
Restaurants
Tables
Menu
Orders
Kitchen
POS
```

## Phase 8 — Inventory

```text
Products
Stock
Stock transactions
Adjustments
Low stock alerts
```

## Phase 9 — Procurement

```text
Suppliers
Purchase orders
Approvals
Receiving
```

## Phase 10 — Finance

```text
Billing
Invoices
Payments
Receipts
Refunds
```

## Phase 11 — Management

```text
Employees
Reports
Notifications
Settings
```

## Phase 12 — Testing and Optimization

```text
Unit tests
Component tests
E2E tests
Accessibility
Performance
Security
Responsive testing
```

## Phase 13 — Production

```text
Build
Docker
CI/CD
Deployment
Monitoring
```

---

# 51. Definition of Done

A frontend feature is considered complete when:

- [ ] Route implemented
- [ ] UI implemented
- [ ] Responsive layout implemented
- [ ] API integration completed
- [ ] TypeScript types implemented
- [ ] Form validation implemented
- [ ] Loading state implemented
- [ ] Empty state implemented
- [ ] Error state implemented
- [ ] Permission checks implemented
- [ ] Accessibility reviewed
- [ ] Unit/component tests implemented
- [ ] E2E test implemented where appropriate
- [ ] API errors handled
- [ ] Mobile layout tested
- [ ] Desktop layout tested
- [ ] Documentation updated

---

# 52. Final Frontend Architecture

The final frontend architecture is:

```text
                         USER
                           │
                           ▼
                    ┌───────────────┐
                    │    Next.js    │
                    │  App Router   │
                    └───────┬───────┘
                            │
             ┌──────────────┼──────────────┐
             │              │              │
             ▼              ▼              ▼
          Layouts        Features      Components
             │              │              │
             │              ├── Auth        │
             │              ├── Guests      │
             │              ├── Rooms       │
             │              ├── Reservations│
             │              ├── Restaurant  │
             │              ├── Inventory   │
             │              └── Finance     │
             │                             │
             └──────────────┬──────────────┘
                            │
                            ▼
                     React Hooks
                            │
                            ▼
                    TanStack Query
                            │
                            ▼
                       API Client
                            │
                            ▼
                     HTTPS / JSON
                            │
                            ▼
                   Spring Boot API
```

---

# Complete System Relationship

The frontend and backend should align as follows:

```text
                    HRMS SYSTEM
                         │
          ┌──────────────┴──────────────┐
          │                             │
          ▼                             ▼
     FRONTEND                       BACKEND
     Next.js                     Spring Boot
          │                             │
          │ REST API                    │
          └──────────────┬──────────────┘
                         │
                         ▼
                    PostgreSQL
```

Feature alignment:

| Frontend Feature | Backend Module |
|---|---|
| Authentication | `auth` |
| Users | `users` |
| Roles | `roles` |
| Permissions | `permissions` |
| Organization | `organization` |
| Branches | `branches` |
| Employees | `employees` |
| Guests | `guests` |
| Rooms | `rooms` |
| Reservations | `reservations` |
| Stays | `stays` |
| Housekeeping | `housekeeping` |
| Maintenance | `maintenance` |
| Restaurant | `restaurants` |
| Tables | `tables` |
| Menu | `menu` |
| Orders | `orders` |
| Kitchen | `kitchen` |
| Inventory | `inventory` |
| Suppliers | `suppliers` |
| Procurement | `procurement` |
| Billing | `billing` |
| Payments | `payments` |
| Taxes | `taxes` |
| Discounts | `discounts` |
| Loyalty | `loyalty` |
| Notifications | `notifications` |
| Reports | `reports` |
| Dashboard | `dashboard` |
| Audit | `audit` |
| Settings | `settings` |

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
DOCUMENT 8  → PostgreSQL Migration Scripts
DOCUMENT 9  → Spring Boot Implementation
DOCUMENT 10 → Next.js Implementation
DOCUMENT 11 → Testing Strategy
DOCUMENT 12 → Deployment Specification
```

---

# Next Step

The design phase has now established both the backend and frontend structures.

The next technical document should be:

**`POSTGRESQL_MIGRATION_SCRIPTS.md`**

This will translate the database design into actual PostgreSQL implementation, including:

- database extensions
- UUID strategy
- organizations
- branches
- users
- roles
- permissions
- employees
- guests
- room types
- rooms
- reservations
- stays
- housekeeping
- maintenance
- restaurants
- tables
- menu
- orders
- kitchen
- inventory
- suppliers
- procurement
- billing
- payments
- taxes
- discounts
- loyalty
- notifications
- audit logs
- indexes
- constraints
- foreign keys
- unique constraints
- check constraints
- timestamps
- soft deletion where appropriate
- migration ordering
- seed data

After that, we can start building the actual **Spring Boot backend** against the finalized database structure.

---

**Document Status:** COMPLETE  
**Version:** 1.0  
**Implementation Status:** Ready for PostgreSQL Migration Design