# HOTEL & RESTAURANT MANAGEMENT SYSTEM

# COMPLETE UI/UX SCREEN MAP & NAVIGATION SPECIFICATION

**Project:** Hotel & Restaurant Management System (HRMS)  
**Document:** UI/UX Screen Map & Navigation Specification  
**Version:** 1.0  
**Status:** Implementation-Ready  
**Frontend:** Next.js + TypeScript  
**Backend:** Java + Spring Boot  
**Database:** PostgreSQL  
**Design Approach:** Responsive, modular, role-based enterprise interface  
**Date:** September 2026

---

# TABLE OF CONTENTS

1. UI/UX Overview
2. Design Objectives
3. Target Users
4. User Roles
5. Design Principles
6. Application Structure
7. Public Website
8. Authentication Screens
9. Global Application Layout
10. Main Navigation
11. Dashboard
12. Reception Module
13. Reservation Module
14. Guest Management
15. Room Management
16. Check-In
17. Check-Out
18. Housekeeping
19. Maintenance
20. Restaurant Management
21. Restaurant Tables
22. Menu Management
23. Point of Sale
24. Kitchen Display System
25. Inventory
26. Suppliers
27. Procurement
28. Finance
29. Payments
30. Invoices
31. Loyalty
32. Employees
33. User Management
34. Roles & Permissions
35. Reports
36. Notifications
37. Audit Logs
38. System Settings
39. Profile
40. Search
41. Global Components
42. Forms
43. Tables
44. Modals
45. Status Indicators
46. Responsive Design
47. Accessibility
48. Error & Empty States
49. Loading States
50. Confirmation & Destructive Actions
51. Navigation Flows
52. Reservation Flow
53. Check-In Flow
54. Check-Out Flow
55. Restaurant Order Flow
56. Inventory Flow
57. Procurement Flow
58. Payment Flow
59. User Management Flow
60. Role-Based Navigation
61. Route Map
62. Next.js Application Structure
63. UI Component Architecture
64. Design Tokens
65. Frontend State Management
66. API Integration
67. Security UX
68. Performance Requirements
69. Testing Requirements
70. UX Acceptance Criteria
71. Final Screen Inventory
72. Final Navigation Architecture
73. Implementation Order

---

# 1. UI/UX OVERVIEW

The HRMS frontend shall provide a centralized interface for managing:

```text
Hotel
├── Reservations
├── Guests
├── Rooms
├── Check-In
├── Check-Out
├── Housekeeping
├── Maintenance
│
Restaurant
├── Tables
├── Menu
├── Orders
├── Kitchen
│
Finance
├── Invoices
├── Payments
├── Taxes
├── Discounts
│
Inventory
├── Items
├── Categories
├── Stock
├── Suppliers
├── Purchase Orders
│
Management
├── Employees
├── Users
├── Roles
├── Reports
├── Notifications
├── Audit Logs
└── Settings
```

The interface shall be role-based.

Users should only see modules and actions they are authorized to access.

---

# 2. DESIGN OBJECTIVES

The interface shall be:

- Professional
- Modern
- Responsive
- Fast
- Accessible
- Easy to learn
- Consistent
- Role-based
- Mobile-friendly
- Desktop-optimized
- Suitable for real hotel operations

The design shall prioritize operational efficiency.

Receptionists should be able to complete reservations and check-ins quickly.

Waiters should be able to create orders with minimal interaction.

Kitchen staff should see orders clearly.

Managers should understand business performance from the dashboard.

---

# 3. TARGET USERS

The primary users are:

| User | Primary Responsibilities |
|---|---|
| Super Administrator | Complete system administration |
| Hotel Manager | Overall hotel management |
| Receptionist | Reservations, guests, check-in/out |
| Front Office Manager | Reception operations |
| Housekeeper | Room cleaning |
| Housekeeping Manager | Housekeeping supervision |
| Maintenance Staff | Maintenance requests |
| Restaurant Manager | Restaurant operations |
| Waiter | Restaurant orders |
| Cashier | Payments and billing |
| Chef | Kitchen orders |
| Storekeeper | Inventory |
| Procurement Officer | Suppliers and purchasing |
| Accountant | Finance |
| HR/Admin | Employees and users |

---

# 4. USER ROLES

Recommended roles:

```text
SUPER_ADMIN
HOTEL_MANAGER
FRONT_OFFICE_MANAGER
RECEPTIONIST
HOUSEKEEPING_MANAGER
HOUSEKEEPER
MAINTENANCE_MANAGER
MAINTENANCE_STAFF
RESTAURANT_MANAGER
WAITER
CASHIER
CHEF
STOREKEEPER
PROCUREMENT_OFFICER
ACCOUNTANT
HR_MANAGER
AUDITOR
```

Roles shall be controlled by permissions.

---

# 5. DESIGN PRINCIPLES

## 5.1 Consistency

Buttons, forms, tables, icons and statuses shall behave consistently.

## 5.2 Visibility

Important operational information should be visible without excessive navigation.

## 5.3 Minimal Steps

Frequently performed tasks should require as few steps as practical.

## 5.4 Clear Feedback

Every important action should provide feedback.

Example:

```text
Reservation created successfully.
```

## 5.5 Error Prevention

The system should prevent invalid operations before submission.

Example:

```text
Room 102 is unavailable for the selected dates.
```

## 5.6 Progressive Disclosure

Advanced options should appear only when necessary.

---

# 6. APPLICATION STRUCTURE

The application consists of two major interfaces.

```text
HRMS
│
├── Public Website
│
└── Management Application
    │
    ├── Dashboard
    ├── Reservations
    ├── Guests
    ├── Rooms
    ├── Front Office
    ├── Housekeeping
    ├── Maintenance
    ├── Restaurant
    ├── Kitchen
    ├── Inventory
    ├── Procurement
    ├── Finance
    ├── Employees
    ├── Reports
    └── Administration
```

---

# 7. PUBLIC WEBSITE

The public website is accessible without authentication.

## 7.1 Homepage

Route:

```text
/
```

Sections:

```text
Header
Hero
Hotel Introduction
Rooms
Amenities
Restaurant
Services
Gallery
Testimonials
Location
Contact
Footer
```

Primary actions:

```text
Book a Room
View Rooms
Contact Us
View Restaurant
```

---

# 7.2 Rooms Page

Route:

```text
/rooms
```

Displays:

- Room types
- Photos
- Capacity
- Amenities
- Price
- Availability
- Booking button

---

# 7.3 Room Details

Route:

```text
/rooms/{id}
```

Sections:

```text
Gallery
Room description
Amenities
Capacity
Price
Availability
Booking form
```

---

# 7.4 Restaurant Page

Route:

```text
/restaurant
```

Displays:

- Restaurant information
- Opening hours
- Menu categories
- Featured meals
- Contact information

---

# 7.5 Contact Page

Route:

```text
/contact
```

Includes:

```text
Contact information
Location
Phone
Email
Contact form
Map
```

---

# 7.6 Public Booking

Route:

```text
/book
```

Flow:

```text
Select dates
      ↓
Search availability
      ↓
Select room
      ↓
Guest information
      ↓
Review booking
      ↓
Payment/deposit
      ↓
Confirmation
```

---

# 8. AUTHENTICATION SCREENS

## 8.1 Login

Route:

```text
/login
```

Elements:

```text
Hotel Logo
Username/Email
Password
Remember Me
Login
Forgot Password
```

Optional:

```text
Show Password
```

---

# 8.2 Forgot Password

Route:

```text
/forgot-password
```

Fields:

```text
Email
```

Action:

```text
Send Reset Link
```

---

# 8.3 Reset Password

Route:

```text
/reset-password
```

Fields:

```text
New Password
Confirm Password
```

---

# 8.4 Authentication Flow

```text
/login
   │
   ├── Success
   │      ↓
   │   Dashboard
   │
   └── Forgot Password
          ↓
      /forgot-password
          ↓
      /reset-password
```

---

# 9. GLOBAL APPLICATION LAYOUT

Authenticated users shall use a common application shell.

```text
┌─────────────────────────────────────────────────────┐
│ Top Header                                          │
│ Logo | Search | Notifications | User               │
├──────────────┬──────────────────────────────────────┤
│              │                                      │
│ Sidebar      │ Main Content                         │
│              │                                      │
│ Dashboard    │                                      │
│ Reservations │                                      │
│ Guests       │                                      │
│ Rooms        │                                      │
│ Restaurant   │                                      │
│ Inventory    │                                      │
│ Finance      │                                      │
│ Reports      │                                      │
│              │                                      │
│ Settings     │                                      │
└──────────────┴──────────────────────────────────────┘
```

---

# 10. MAIN NAVIGATION

Recommended sidebar:

```text
Dashboard

FRONT OFFICE
├── Reservations
├── Guests
├── Check-In
├── Check-Out
└── Stays

ROOMS
├── Rooms
├── Room Types
├── Availability
├── Housekeeping
└── Maintenance

RESTAURANT
├── POS
├── Orders
├── Tables
├── Menu
└── Kitchen

INVENTORY
├── Items
├── Categories
├── Stock Transactions
├── Suppliers
└── Purchase Orders

FINANCE
├── Invoices
├── Payments
├── Taxes
└── Discounts

MANAGEMENT
├── Employees
├── Users
├── Roles & Permissions
├── Reports
└── Audit Logs

SYSTEM
├── Notifications
├── Settings
└── Profile
```

Navigation items shall dynamically depend on permissions.

---

# 11. DASHBOARD

Route:

```text
/dashboard
```

The dashboard shall provide a high-level business overview.

## 11.1 KPI Cards

```text
Today's Revenue
Occupied Rooms
Available Rooms
Check-Ins
Check-Outs
Restaurant Sales
Pending Orders
Low Stock Items
```

---

# 11.2 Dashboard Layout

```text
┌─────────────────────────────────────────────┐
│ Welcome / Date                              │
├────────┬────────┬────────┬──────────────────┤
│Revenue │Occupancy│Check-In│Check-Out        │
├────────┴────────┴────────┴──────────────────┤
│ Revenue Chart          │ Occupancy Chart    │
├────────────────────────┼────────────────────┤
│ Today's Reservations   │ Room Status        │
├────────────────────────┼────────────────────┤
│ Restaurant Orders     │ Low Stock Alerts   │
└────────────────────────┴────────────────────┘
```

---

# 12. RECEPTION MODULE

The reception module is one of the most important operational areas.

```text
Reception
├── Dashboard
├── Reservations
├── Guests
├── Check-In
├── Check-Out
└── Active Stays
```

---

# 13. RESERVATION MODULE

Route:

```text
/reservations
```

## 13.1 Reservation List

Columns:

```text
Reservation #
Guest
Room
Room Type
Check-In
Check-Out
Guests
Status
Payment
Actions
```

Actions:

```text
View
Edit
Confirm
Cancel
Check-In
Print
```

---

# 13.2 Reservation Detail

Route:

```text
/reservations/{id}
```

Sections:

```text
Reservation Information
Guest Information
Room Information
Stay Dates
Pricing
Payments
Special Requests
Activity Timeline
```

---

# 13.3 Create Reservation

Route:

```text
/reservations/new
```

Wizard:

```text
Step 1
Dates & Guests
        ↓
Step 2
Room Selection
        ↓
Step 3
Guest Information
        ↓
Step 4
Pricing
        ↓
Step 5
Confirmation
```

---

# 14. GUEST MANAGEMENT

Route:

```text
/guests
```

## Guest List

Columns:

```text
Guest #
Name
Phone
Email
Nationality
Total Visits
Outstanding Balance
Actions
```

Actions:

```text
View
Edit
Reservation
History
```

---

# 14.1 Guest Profile

Route:

```text
/guests/{id}
```

Sections:

```text
Profile
Contact Information
Identification
Reservations
Stay History
Restaurant Orders
Invoices
Payments
Loyalty
Notes
```

---

# 15. ROOM MANAGEMENT

Route:

```text
/rooms
```

The primary room interface should support both:

```text
List View
Grid View
```

---

# 15.1 Room Grid

Example:

```text
┌─────────┐ ┌─────────┐ ┌─────────┐
│ Room101 │ │ Room102 │ │ Room103 │
│ Deluxe  │ │ Standard│ │ Suite   │
│         │ │         │ │         │
│ OCCUPIED│ │ AVAILABLE││ CLEANING│
└─────────┘ └─────────┘ └─────────┘
```

Status examples:

```text
AVAILABLE
OCCUPIED
RESERVED
CLEANING
DIRTY
MAINTENANCE
OUT_OF_ORDER
```

---

# 15.2 Room Detail

Route:

```text
/rooms/{id}
```

Displays:

```text
Room Number
Room Type
Floor
Capacity
Current Status
Current Guest
Amenities
Reservation
Housekeeping Tasks
Maintenance Requests
History
```

---

# 16. CHECK-IN

Route:

```text
/check-in
```

Primary interface:

```text
Search Reservation
       ↓
Select Reservation
       ↓
Verify Guest
       ↓
Confirm Room
       ↓
Record Deposit
       ↓
Check In
```

Confirmation:

```text
Guest checked in successfully.
```

---

# 17. CHECK-OUT

Route:

```text
/check-out
```

Flow:

```text
Search Active Stay
        ↓
Review Charges
        ↓
Review Invoice
        ↓
Payment
        ↓
Confirm Check-Out
        ↓
Generate Receipt
        ↓
Room → Dirty/Cleaning
```

---

# 18. HOUSEKEEPING

Route:

```text
/housekeeping
```

Dashboard:

```text
Rooms to Clean
Cleaning In Progress
Completed
Priority Tasks
```

---

# 18.1 Housekeeping Board

Recommended Kanban:

```text
┌──────────┬────────────┬───────────┐
│ TO CLEAN │ IN PROGRESS│ COMPLETED │
├──────────┼────────────┼───────────┤
│ Room 101 │ Room 204   │ Room 301  │
│ Room 102 │ Room 205   │ Room 302  │
└──────────┴────────────┴───────────┘
```

---

# 18.2 Housekeeping Task

Route:

```text
/housekeeping/tasks/{id}
```

Information:

```text
Room
Assigned Employee
Priority
Task Type
Start Time
Completion Time
Notes
Status
```

---

# 19. MAINTENANCE

Route:

```text
/maintenance
```

Dashboard:

```text
Open Requests
High Priority
In Progress
Resolved
```

---

# 19.1 Maintenance Request

Fields:

```text
Room
Issue
Description
Priority
Assigned Technician
Status
Attachments
```

---

# 20. RESTAURANT MANAGEMENT

Route:

```text
/restaurant
```

Restaurant navigation:

```text
Restaurant
├── Dashboard
├── POS
├── Orders
├── Tables
├── Menu
└── Kitchen
```

---

# 21. RESTAURANT TABLES

Route:

```text
/restaurant/tables
```

The interface should provide a visual floor-plan style view.

Example:

```text
┌──────────────────────────────────┐
│          RESTAURANT              │
│                                  │
│  [T01]      [T02]      [T03]    │
│                                  │
│        [T04]  [T05]              │
│                                  │
│  [T06]      [T07]      [T08]    │
└──────────────────────────────────┘
```

Table statuses:

```text
AVAILABLE
OCCUPIED
RESERVED
CLEANING
OUT_OF_SERVICE
```

---

# 22. MENU MANAGEMENT

Route:

```text
/restaurant/menu
```

Navigation:

```text
Menu
├── Categories
└── Items
```

---

# 22.1 Menu Item List

Columns:

```text
Image
Name
Category
Price
Preparation Time
Availability
Actions
```

Actions:

```text
Edit
Disable
Delete
View
```

---

# 23. POINT OF SALE

Route:

```text
/restaurant/pos
```

The POS should be optimized for speed.

Layout:

```text
┌──────────────────────────┬────────────────────────┐
│ Categories               │ Current Order          │
│                          │                        │
│ Breakfast               │ Chicken x2             │
│ Main Course             │ Chips x1               │
│ Drinks                  │ Soda x2                │
│ Desserts                │                        │
│                          │ ---------------------- │
│ [Food Item] [Food Item] │ Subtotal               │
│ [Food Item] [Food Item] │ Tax                    │
│ [Food Item] [Food Item] │ Total                  │
│                          │                        │
│                          │ [PAY] [SAVE] [CANCEL] │
└──────────────────────────┴────────────────────────┘
```

---

# 23.1 POS Flow

```text
Select Table
     ↓
Select Menu Items
     ↓
Adjust Quantity
     ↓
Add Notes
     ↓
Submit Order
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

# 24. KITCHEN DISPLAY SYSTEM

Route:

```text
/restaurant/kitchen
```

The kitchen interface should prioritize visibility.

Example:

```text
┌────────────┬────────────┬────────────┐
│ NEW        │ PREPARING  │ READY      │
├────────────┼────────────┼────────────┤
│ Order #101 │ Order #98  │ Order #95  │
│ Table T01  │ Table T04  │ Table T02  │
│ Chicken    │ Beef       │ Burger     │
│ Chips      │ Rice       │ Fries      │
└────────────┴────────────┴────────────┘
```

---

# 25. INVENTORY

Route:

```text
/inventory
```

Navigation:

```text
Inventory
├── Dashboard
├── Items
├── Categories
├── Stock Transactions
├── Low Stock
└── Suppliers
```

---

# 25.1 Inventory Dashboard

KPIs:

```text
Total Items
Inventory Value
Low Stock
Out of Stock
Stock Received
Stock Issued
Wastage
```

---

# 25.2 Inventory List

Columns:

```text
SKU
Item
Category
Current Quantity
Unit
Reorder Level
Unit Cost
Stock Status
Actions
```

---

# 25.3 Inventory Item Detail

Sections:

```text
Basic Information
Current Stock
Stock Movement
Suppliers
Purchase History
Wastage
Adjustments
```

---

# 26. SUPPLIERS

Route:

```text
/suppliers
```

Columns:

```text
Supplier
Contact Person
Phone
Email
Items Supplied
Status
Actions
```

---

# 27. PROCUREMENT

Route:

```text
/purchase-orders
```

Flow:

```text
Create Purchase Order
        ↓
Add Items
        ↓
Submit
        ↓
Approval
        ↓
Supplier
        ↓
Receive Stock
        ↓
Inventory Updated
```

---

# 27.1 Purchase Order Detail

Sections:

```text
Supplier
Order Number
Order Date
Expected Delivery
Items
Subtotal
Tax
Total
Status
Approval
Receiving
```

---

# 28. FINANCE

Route:

```text
/finance
```

Navigation:

```text
Finance
├── Dashboard
├── Invoices
├── Payments
├── Taxes
└── Discounts
```

---

# 29. PAYMENTS

Route:

```text
/finance/payments
```

Columns:

```text
Payment #
Invoice
Guest
Amount
Method
Reference
Date
Status
Recorded By
```

Payment methods may include:

```text
CASH
CARD
BANK_TRANSFER
MOBILE_MONEY
OTHER
```

---

# 30. INVOICES

Route:

```text
/finance/invoices
```

Columns:

```text
Invoice #
Guest
Date
Subtotal
Tax
Discount
Total
Paid
Balance
Status
Actions
```

---

# 30.1 Invoice Detail

Route:

```text
/finance/invoices/{id}
```

Actions:

```text
Print
Download PDF
Send
Record Payment
Void
```

---

# 31. LOYALTY

Route:

```text
/loyalty
```

Guest loyalty profile:

```text
Current Points
Tier
Points Earned
Points Redeemed
Transaction History
Rewards
```

---

# 32. EMPLOYEES

Route:

```text
/employees
```

Columns:

```text
Employee #
Name
Department
Position
Phone
Email
Status
Actions
```

---

# 32.1 Employee Profile

Route:

```text
/employees/{id}
```

Sections:

```text
Personal Information
Employment Information
Department
Position
User Account
Activity
Status
```

---

# 33. USER MANAGEMENT

Route:

```text
/users
```

Columns:

```text
Username
Employee
Email
Role
Status
Last Login
Actions
```

Actions:

```text
Edit
Disable
Reset Password
View Activity
```

---

# 34. ROLES & PERMISSIONS

Route:

```text
/settings/roles
```

Interface:

```text
Role
    ↓
Permissions
```

Example:

```text
RECEPTIONIST

☑ View Reservations
☑ Create Reservations
☑ Edit Reservations
☑ Check-In
☑ Check-Out
☑ View Guests
☐ Manage Users
☐ Manage Roles
☐ View Audit Logs
```

---

# 35. REPORTS

Route:

```text
/reports
```

Reports:

```text
Revenue
Occupancy
Reservations
Restaurant Sales
Payments
Taxes
Inventory
Purchases
Housekeeping
Maintenance
Guest Statistics
```

---

# 35.1 Report Screen

Layout:

```text
Report Type
Date Range
Branch
Filters
       ↓
Generate Report
       ↓
Chart + Table
       ↓
Export
```

Export:

```text
PDF
CSV
XLSX
```

---

# 36. NOTIFICATIONS

Route:

```text
/notifications
```

Types:

```text
Reservation
Payment
Inventory
Housekeeping
Maintenance
System
```

Notification examples:

```text
Room 204 requires cleaning.

Cooking oil has reached reorder level.

New reservation received.

Payment successfully recorded.
```

---

# 37. AUDIT LOGS

Route:

```text
/audit-logs
```

Columns:

```text
Date
User
Action
Entity
Entity ID
IP Address
Description
```

Example:

```text
23 Sep 2026
admin
UPDATE
Room
ROOM-102
Changed status to MAINTENANCE
```

---

# 38. SYSTEM SETTINGS

Route:

```text
/settings
```

Navigation:

```text
Settings
├── General
├── Organization
├── Branches
├── Users
├── Roles
├── Taxes
├── Discounts
├── Notifications
├── Payment Methods
└── System Preferences
```

---

# 39. PROFILE

Route:

```text
/profile
```

Sections:

```text
Profile Information
Password
Preferences
Notifications
Login Activity
```

---

# 40. GLOBAL SEARCH

The application shall provide global search.

Search examples:

```text
Guest name
Reservation number
Room number
Invoice number
Order number
Employee
Inventory SKU
```

Example:

```text
Search: "101"

Results:

Room 101
Reservation #RES-101
Invoice #INV-101
Order #ORD-101
```

---

# 41. GLOBAL COMPONENTS

The frontend should contain reusable components.

```text
Button
Input
Select
DatePicker
DateRangePicker
Modal
Dialog
Dropdown
Tooltip
Badge
Avatar
Card
Table
Pagination
Tabs
Accordion
Toast
Alert
Drawer
Sidebar
Navbar
Breadcrumb
Search
FileUpload
```

---

# 42. FORMS

All forms shall have:

```text
Labels
Validation
Required indicators
Help text where necessary
Error messages
Submit button
Cancel button
Loading state
Success feedback
```

Example:

```text
Room Number *
[____________]

Room Type *
[Select Room Type]

Floor
[____________]

Capacity *
[____________]

[Cancel] [Save Room]
```

---

# 43. TABLES

Tables should support:

```text
Search
Filtering
Sorting
Pagination
Column visibility
Row actions
Bulk actions
Export
```

Example:

```text
Reservations

[Search...] [Filter] [Export] [+ New Reservation]

Reservation | Guest | Room | Dates | Status | Actions
```

---

# 44. MODALS

Use modals for small operations.

Examples:

```text
Confirm Cancellation
Record Payment
Change Room Status
Assign Housekeeper
Assign Technician
Delete Item
Disable User
```

Large forms should use dedicated pages or drawers instead of oversized modals.

---

# 45. STATUS INDICATORS

The system shall use consistent visual status indicators.

Reservation:

```text
PENDING
CONFIRMED
CHECKED_IN
COMPLETED
CANCELLED
NO_SHOW
```

Room:

```text
AVAILABLE
RESERVED
OCCUPIED
CLEANING
DIRTY
MAINTENANCE
OUT_OF_ORDER
```

Payment:

```text
PENDING
COMPLETED
FAILED
REFUNDED
PARTIALLY_REFUNDED
```

Order:

```text
DRAFT
SUBMITTED
ACCEPTED
PREPARING
READY
SERVED
COMPLETED
CANCELLED
```

---

# 46. RESPONSIVE DESIGN

The application shall support:

```text
Desktop
Laptop
Tablet
Mobile
```

Recommended breakpoints:

```text
Mobile
< 640px

Tablet
640px – 1024px

Desktop
> 1024px
```

---

# 46.1 Mobile Navigation

On mobile:

```text
┌─────────────────────────┐
│ ☰ Logo       🔔 👤     │
├─────────────────────────┤
│                         │
│ Main Content            │
│                         │
└─────────────────────────┘
```

Sidebar becomes a drawer.

---

# 47. ACCESSIBILITY

The application should follow accessible design practices.

Requirements:

```text
Keyboard navigation
Visible focus states
Accessible labels
ARIA where necessary
Sufficient contrast
Readable typography
Alternative text
Accessible dialogs
Screen-reader support
```

Forms should associate labels with inputs.

---

# 48. ERROR STATES

Example:

```text
Something went wrong.

We couldn't load reservations.

[Try Again]
```

Network error:

```text
Unable to connect to the server.

Check your internet connection and try again.
```

---

# 49. EMPTY STATES

Example:

```text
No Reservations Found

There are no reservations matching your search.

[Create Reservation]
```

Inventory:

```text
No Low Stock Items

All inventory items are currently above their reorder levels.
```

---

# 50. LOADING STATES

Use:

```text
Skeleton loaders
Spinners
Progress indicators
Disabled submit buttons
```

Example:

```text
Saving reservation...
```

Avoid blank screens while data is loading.

---

# 51. CONFIRMATION & DESTRUCTIVE ACTIONS

Destructive operations must require confirmation.

Example:

```text
Cancel Reservation?

This action will cancel reservation #RES-1024.

[Keep Reservation] [Cancel Reservation]
```

For critical financial actions:

```text
Void Invoice?

Invoice: INV-00123
Amount: KES 25,000

Reason:
[________________]

[Cancel] [Void Invoice]
```

---

# 52. RESERVATION FLOW

Complete flow:

```text
Dashboard
   ↓
Reservations
   ↓
New Reservation
   ↓
Select Dates
   ↓
Check Availability
   ↓
Select Room
   ↓
Select/Create Guest
   ↓
Review Pricing
   ↓
Deposit
   ↓
Confirm
   ↓
Reservation Created
```

---

# 53. CHECK-IN FLOW

```text
Reservations
      ↓
Select Confirmed Reservation
      ↓
Check-In
      ↓
Verify Guest
      ↓
Verify Room
      ↓
Record Deposit
      ↓
Confirm Check-In
      ↓
Stay Created
      ↓
Room → OCCUPIED
```

---

# 54. CHECK-OUT FLOW

```text
Active Stays
      ↓
Select Guest
      ↓
Check-Out
      ↓
Billing Preview
      ↓
Review Charges
      ↓
Apply Discount
      ↓
Calculate Balance
      ↓
Record Payment
      ↓
Confirm Check-Out
      ↓
Invoice
      ↓
Receipt
      ↓
Room → DIRTY/CLEANING
```

---

# 55. RESTAURANT ORDER FLOW

```text
POS
 ↓
Select Table
 ↓
Select Items
 ↓
Review Order
 ↓
Submit
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
 ↓
Payment
 ↓
Complete
```

---

# 56. INVENTORY FLOW

## Stock Issue

```text
Inventory
   ↓
Stock Transactions
   ↓
Issue Stock
   ↓
Select Item
   ↓
Quantity
   ↓
Reason
   ↓
Confirm
   ↓
Stock Updated
```

---

# 57. PROCUREMENT FLOW

```text
Purchase Orders
       ↓
Create PO
       ↓
Select Supplier
       ↓
Add Items
       ↓
Submit
       ↓
Approve
       ↓
Receive
       ↓
Inventory Updated
```

---

# 58. PAYMENT FLOW

```text
Invoice
   ↓
Record Payment
   ↓
Select Method
   ↓
Enter Amount
   ↓
Transaction Reference
   ↓
Confirm
   ↓
Payment Recorded
   ↓
Invoice Balance Updated
```

---

# 59. USER MANAGEMENT FLOW

```text
Users
 ↓
Create User
 ↓
Select Employee
 ↓
Assign Role
 ↓
Configure Permissions
 ↓
Create Account
 ↓
User Receives Credentials
 ↓
First Login
```

---

# 60. ROLE-BASED NAVIGATION

## SUPER ADMIN

```text
Dashboard
All Modules
Users
Roles
Settings
Audit Logs
Reports
```

## HOTEL MANAGER

```text
Dashboard
Reservations
Guests
Rooms
Housekeeping
Maintenance
Restaurant
Inventory
Finance
Reports
```

## RECEPTIONIST

```text
Dashboard
Reservations
Guests
Check-In
Check-Out
Stays
Rooms
```

## HOUSEKEEPER

```text
Dashboard
My Tasks
Rooms
Housekeeping
```

## RESTAURANT MANAGER

```text
Dashboard
POS
Orders
Tables
Menu
Kitchen
Inventory
Reports
```

## WAITER

```text
Dashboard
POS
Tables
Orders
```

## CHEF

```text
Dashboard
Kitchen
Orders
```

## STOREKEEPER

```text
Dashboard
Inventory
Stock Transactions
Suppliers
Purchase Orders
```

## ACCOUNTANT

```text
Dashboard
Invoices
Payments
Taxes
Reports
```

## HR MANAGER

```text
Dashboard
Employees
Users
Reports
```

---

# 61. COMPLETE ROUTE MAP

```text
PUBLIC
/
├── /rooms
├── /rooms/{id}
├── /restaurant
├── /contact
└── /book

AUTH
├── /login
├── /forgot-password
└── /reset-password

APPLICATION
│
├── /dashboard
│
├── /reservations
│   ├── /new
│   └── /{id}
│
├── /guests
│   ├── /new
│   └── /{id}
│
├── /check-in
├── /check-out
├── /stays
│   └── /{id}
│
├── /rooms
│   ├── /new
│   ├── /availability
│   ├── /room-types
│   └── /{id}
│
├── /housekeeping
│   └── /tasks/{id}
│
├── /maintenance
│   └── /{id}
│
├── /restaurant
│   ├── /dashboard
│   ├── /pos
│   ├── /orders
│   ├── /orders/{id}
│   ├── /tables
│   ├── /menu
│   └── /kitchen
│
├── /inventory
│   ├── /dashboard
│   ├── /items
│   ├── /items/{id}
│   ├── /categories
│   └── /transactions
│
├── /suppliers
│   └── /{id}
│
├── /purchase-orders
│   ├── /new
│   └── /{id}
│
├── /finance
│   ├── /invoices
│   ├── /invoices/{id}
│   ├── /payments
│   ├── /taxes
│   └── /discounts
│
├── /loyalty
│
├── /employees
│   ├── /new
│   └── /{id}
│
├── /users
│   └── /{id}
│
├── /reports
│   ├── /revenue
│   ├── /occupancy
│   ├── /reservations
│   ├── /restaurant-sales
│   ├── /inventory
│   └── /payments
│
├── /notifications
├── /audit-logs
│
├── /settings
│   ├── /general
│   ├── /organization
│   ├── /branches
│   ├── /roles
│   ├── /taxes
│   ├── /discounts
│   └── /notifications
│
└── /profile
```

---

# 62. NEXT.JS APPLICATION STRUCTURE

Recommended App Router structure:

```text
src/
│
├── app/
│   │
│   ├── (public)/
│   │   ├── page.tsx
│   │   ├── rooms/
│   │   ├── restaurant/
│   │   ├── contact/
│   │   └── book/
│   │
│   ├── (auth)/
│   │   ├── login/
│   │   ├── forgot-password/
│   │   └── reset-password/
│   │
│   └── (dashboard)/
│       ├── layout.tsx
│       ├── dashboard/
│       ├── reservations/
│       ├── guests/
│       ├── rooms/
│       ├── check-in/
│       ├── check-out/
│       ├── stays/
│       ├── housekeeping/
│       ├── maintenance/
│       ├── restaurant/
│       ├── inventory/
│       ├── suppliers/
│       ├── purchase-orders/
│       ├── finance/
│       ├── loyalty/
│       ├── employees/
│       ├── users/
│       ├── reports/
│       ├── notifications/
│       ├── audit-logs/
│       ├── settings/
│       └── profile/
│
├── components/
│   ├── ui/
│   ├── layout/
│   ├── dashboard/
│   ├── reservations/
│   ├── guests/
│   ├── rooms/
│   ├── restaurant/
│   ├── inventory/
│   ├── finance/
│   └── reports/
│
├── lib/
│   ├── api/
│   ├── auth/
│   ├── permissions/
│   ├── validation/
│   └── utils/
│
├── hooks/
│
├── stores/
│
├── types/
│
└── styles/
```

---

# 63. UI COMPONENT ARCHITECTURE

The component hierarchy should follow:

```text
Page
 ↓
Section
 ↓
Feature Component
 ↓
Reusable UI Component
```

Example:

```text
ReservationPage
    ↓
ReservationTable
    ↓
DataTable
    ↓
Button
Badge
Dropdown
Pagination
```

---

# 64. DESIGN TOKENS

The interface shall centralize design tokens.

Example:

```text
Typography
Spacing
Border Radius
Shadows
Breakpoints
Component Sizes
Transitions
Z-Index
```

Example conceptual structure:

```text
Design Tokens
├── Colors
├── Typography
├── Spacing
├── Radius
├── Shadows
├── Breakpoints
└── Animation
```

The actual visual theme can be finalized during the visual design phase.

---

# 65. FRONTEND STATE MANAGEMENT

Application state should be separated into:

```text
Server State
UI State
Authentication State
Form State
```

Example:

```text
Server State
├── Reservations
├── Guests
├── Rooms
└── Orders

UI State
├── Sidebar
├── Modals
├── Filters
└── Theme

Auth State
├── User
├── Role
└── Permissions

Form State
├── Reservation Form
├── Guest Form
└── Payment Form
```

---

# 66. API INTEGRATION

Frontend screens shall communicate with the Spring Boot API.

Example:

```text
ReservationPage
       ↓
reservationApi
       ↓
GET /api/v1/reservations
       ↓
Spring Boot
       ↓
PostgreSQL
```

The frontend should not communicate directly with PostgreSQL.

---

# 67. SECURITY UX

The frontend shall:

- Protect authenticated routes.
- Hide unauthorized navigation items.
- Prevent unauthorized actions.
- Automatically handle expired sessions.
- Provide logout.
- Avoid exposing sensitive information.
- Validate permissions before sensitive operations.

Important:

```text
Frontend authorization
≠
Backend authorization
```

The backend remains the final authority.

---

# 68. PERFORMANCE REQUIREMENTS

The frontend should:

- Lazy-load heavy modules.
- Paginate large datasets.
- Avoid unnecessary API requests.
- Cache appropriate server data.
- Optimize images.
- Use skeleton loading.
- Minimize large client-side bundles.
- Debounce global search.
- Avoid rendering thousands of records simultaneously.

---

# 69. TESTING REQUIREMENTS

UI testing shall cover:

```text
Authentication
Navigation
Permissions
Forms
Validation
Reservations
Check-In
Check-Out
Restaurant POS
Kitchen
Inventory
Payments
Reports
Responsive layouts
Accessibility
```

Critical workflows must have end-to-end tests.

---

# 70. UX ACCEPTANCE CRITERIA

The UI/UX design shall be considered complete when:

- Every major module has a defined screen.
- Every major user role has defined navigation.
- Routes are defined.
- Major workflows are documented.
- Forms are documented.
- Tables are documented.
- Statuses are documented.
- Responsive behavior is documented.
- Error states are documented.
- Loading states are documented.
- Empty states are documented.
- Permission-based navigation is defined.
- Critical business workflows are mapped.
- Next.js application structure is defined.

---

# 71. FINAL SCREEN INVENTORY

The system should contain approximately the following major screens.

## Public

```text
1. Homepage
2. Rooms
3. Room Details
4. Restaurant
5. Contact
6. Public Booking
```

## Authentication

```text
7. Login
8. Forgot Password
9. Reset Password
```

## Core

```text
10. Dashboard
11. Reservations
12. Reservation Details
13. New Reservation
14. Guests
15. Guest Details
16. Check-In
17. Check-Out
18. Active Stays
```

## Rooms

```text
19. Rooms
20. Room Details
21. Room Types
22. Room Availability
23. Housekeeping
24. Housekeeping Task
25. Maintenance
26. Maintenance Request
```

## Restaurant

```text
27. Restaurant Dashboard
28. POS
29. Orders
30. Order Details
31. Tables
32. Menu
33. Menu Categories
34. Menu Items
35. Kitchen
```

## Inventory

```text
36. Inventory Dashboard
37. Inventory Items
38. Inventory Item Details
39. Inventory Categories
40. Stock Transactions
41. Low Stock
42. Suppliers
43. Supplier Details
44. Purchase Orders
45. Purchase Order Details
```

## Finance

```text
46. Finance Dashboard
47. Invoices
48. Invoice Details
49. Payments
50. Payment Details
51. Taxes
52. Discounts
```

## Management

```text
53. Employees
54. Employee Details
55. Users
56. User Details
57. Roles
58. Permissions
59. Reports
60. Audit Logs
61. Notifications
62. Loyalty
```

## System

```text
63. Settings
64. Organization
65. Branches
66. Profile
67. System Preferences
```

---

# 72. FINAL NAVIGATION ARCHITECTURE

The complete application navigation is:

```text
                           HRMS
                            │
          ┌─────────────────┴─────────────────┐
          │                                   │
      PUBLIC SITE                         AUTHENTICATION
          │                                   │
     ┌────┼────┐                         Login / Reset
     │    │    │                               │
  Rooms Restaurant Contact                    │
     │                                          
     └────────────── Booking                    │
                                               │
                                               ▼
                                         APPLICATION
                                               │
       ┌───────────────────────────────────────┼──────────────────────┐
       │                                       │                      │
       ▼                                       ▼                      ▼
   FRONT OFFICE                             ROOMS                RESTAURANT
       │                                       │                      │
 Reservations                              Rooms                    POS
 Guests                                  Availability              Orders
 Check-In                               Housekeeping                Tables
 Check-Out                              Maintenance                  Menu
 Stays                                                               Kitchen
       │                                       │                      │
       └───────────────────────┬───────────────┴──────────────────────┘
                               │
                               ▼
                         OPERATIONS
                               │
                    ┌──────────┼──────────┐
                    │          │          │
                    ▼          ▼          ▼
                INVENTORY   PROCUREMENT  FINANCE
                    │          │          │
                 Items       Suppliers   Invoices
                 Stock       Purchase    Payments
                 Wastage     Orders      Taxes
                                         Discounts
                    │          │          │
                    └──────────┼──────────┘
                               │
                               ▼
                          MANAGEMENT
                               │
                    ┌──────────┼───────────┐
                    │          │           │
                    ▼          ▼           ▼
                Employees    Reports     Audit
                Users        Dashboard   Logs
                Roles
                    │
                    └──────────────┐
                                   ▼
                              SYSTEM
                                   │
                         Settings / Profile
                         Notifications
                         Organization
                         Branches
```

---

# 73. IMPLEMENTATION ORDER

The frontend should be implemented in the following order.

## Phase 1 — Foundation

```text
Next.js project
TypeScript
Global layout
Authentication
Routing
API client
UI component library
Error handling
Loading states
```

## Phase 2 — Core Hotel Operations

```text
Dashboard
Guests
Rooms
Room Types
Reservations
Check-In
Check-Out
Stays
```

## Phase 3 — Hotel Operations

```text
Housekeeping
Maintenance
```

## Phase 4 — Restaurant

```text
Restaurant
Tables
Menu
POS
Orders
Kitchen
```

## Phase 5 — Inventory

```text
Inventory
Stock Transactions
Suppliers
Purchase Orders
```

## Phase 6 — Finance

```text
Invoices
Payments
Taxes
Discounts
```

## Phase 7 — Management

```text
Employees
Users
Roles
Permissions
Reports
Audit Logs
Notifications
Loyalty
```

## Phase 8 — Public Website

```text
Homepage
Rooms
Restaurant
Contact
Public Booking
```

## Phase 9 — Polish

```text
Responsive design
Accessibility
Performance
Animations
Loading states
Empty states
Error states
Security UX
End-to-end testing
```

---

# FINAL UI/UX DESIGN STATUS

The HRMS design documentation now consists of:

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

# END OF UI/UX SCREEN MAP & NAVIGATION SPECIFICATION

**Version:** 1.0  
**Frontend:** Next.js + TypeScript  
**Backend:** Spring Boot + Java  
**Database:** PostgreSQL  
**API:** REST + JSON  
**Status:** Ready for System Architecture Design