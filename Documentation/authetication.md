# HOTEL & RESTAURANT MANAGEMENT SYSTEM

# AUTHENTICATION & AUTHORIZATION SPECIFICATION

**Version:** 1.0  
**Status:** Implementation-Ready  
**Date:** September 2026  
**Frontend:** Next.js + TypeScript  
**Backend:** Java + Spring Boot  
**Database:** PostgreSQL  
**Authentication:** JWT + Secure Session Handling  
**Authorization:** RBAC + Permissions + Branch Access Control  
**Default Timezone:** Africa/Nairobi  
**Default Currency:** KES  

---

# Table of Contents

1. [Purpose](#1-purpose)
2. [Authentication Objectives](#2-authentication-objectives)
3. [Security Architecture](#3-security-architecture)
4. [Authentication Model](#4-authentication-model)
5. [User Account Model](#5-user-account-model)
6. [Roles and Permissions](#6-roles-and-permissions)
7. [Authentication Flow](#7-authentication-flow)
8. [Login](#8-login)
9. [JWT Access Tokens](#9-jwt-access-tokens)
10. [Refresh Tokens](#10-refresh-tokens)
11. [Token Rotation](#11-token-rotation)
12. [Logout](#12-logout)
13. [Session Management](#13-session-management)
14. [Password Security](#14-password-security)
15. [Password Change](#15-password-change)
16. [Forgot Password](#16-forgot-password)
17. [Password Reset](#17-password-reset)
18. [Account Locking](#18-account-locking)
19. [Email Verification](#19-email-verification)
20. [Multi-Factor Authentication](#20-multi-factor-authentication)
21. [Role-Based Access Control](#21-role-based-access-control)
22. [Permission-Based Access Control](#22-permission-based-access-control)
23. [Branch-Level Access](#23-branch-level-access)
24. [Frontend Authentication](#24-frontend-authentication)
25. [Backend Authentication](#25-backend-authentication)
26. [Spring Security Configuration](#26-spring-security-configuration)
27. [JWT Filter](#27-jwt-filter)
28. [Authentication DTOs](#28-authentication-dtos)
29. [Authentication API](#29-authentication-api)
30. [Authentication Responses](#30-authentication-responses)
31. [Authentication Error Codes](#31-authentication-error-codes)
32. [Security Headers](#32-security-headers)
33. [CORS](#33-cors)
34. [Rate Limiting](#34-rate-limiting)
35. [Brute Force Protection](#35-brute-force-protection)
36. [Audit Logging](#36-audit-logging)
37. [Security Events](#37-security-events)
38. [Sensitive Data Handling](#38-sensitive-data-handling)
39. [Cookie Security](#39-cookie-security)
40. [CSRF Considerations](#40-csrf-considerations)
41. [Session Revocation](#41-session-revocation)
42. [Concurrent Sessions](#42-concurrent-sessions)
43. [API Authorization](#43-api-authorization)
44. [Frontend Route Protection](#44-frontend-route-protection)
45. [Permission-Aware UI](#45-permission-aware-ui)
46. [Branch Switching](#46-branch-switching)
47. [Authentication State](#47-authentication-state)
48. [Security Monitoring](#48-security-monitoring)
49. [Testing](#49-testing)
50. [Security Acceptance Criteria](#50-security-acceptance-criteria)
51. [Implementation Checklist](#51-implementation-checklist)

---

# 1. Purpose

This document defines the authentication and authorization architecture for the Hotel & Restaurant Management System.

The authentication system protects access to:

- hotel administration
- reservations
- guests
- rooms
- front desk
- housekeeping
- maintenance
- restaurants
- POS
- kitchen
- inventory
- procurement
- billing
- payments
- employees
- reports
- settings
- audit logs

The system must ensure that users can access only the resources and operations they are authorized to use.

---

# 2. Authentication Objectives

The authentication system must provide:

- secure login
- secure logout
- access-token authentication
- refresh-token management
- password hashing
- password reset
- account locking
- session management
- role-based authorization
- permission-based authorization
- branch-level authorization
- audit logging
- rate limiting
- security monitoring
- optional multi-factor authentication

---

# 3. Security Architecture

Overall architecture:

```text
┌──────────────────────────────┐
│          Next.js             │
│          Frontend            │
└──────────────┬───────────────┘
               │
               │ HTTPS
               ▼
┌──────────────────────────────┐
│       Spring Security        │
│                              │
│ JWT Authentication           │
│ Authorization                │
│ Rate Limiting                │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│       Authentication         │
│          Service             │
└──────────────┬───────────────┘
               │
       ┌───────┴────────┐
       ▼                ▼
┌──────────────┐ ┌──────────────┐
│ PostgreSQL   │ │ Token Store  │
│ Users/Roles  │ │ / Sessions   │
└──────────────┘ └──────────────┘
```

---

# 4. Authentication Model

The system uses:

```text
Authentication:
JWT-based

Authorization:
RBAC + permissions

Tenant/Organization:
Organization

Operational scope:
Branch

Session:
Access Token + Refresh Token
```

Authentication answers:

> Who are you?

Authorization answers:

> What are you allowed to do?

Branch access answers:

> Which hotel branch are you allowed to operate in?

---

# 5. User Account Model

The `users` table should contain information such as:

```text
id
organization_id
username
email
password_hash
first_name
last_name
phone_number
enabled
account_non_locked
created_at
updated_at
deleted_at
version
```

Additional authentication-related data may include:

```text
failed_login_attempts
locked_until
last_login_at
password_changed_at
email_verified
```

Sensitive authentication data must never be returned through normal user APIs.

---

# 6. Roles and Permissions

The system should support predefined roles such as:

```text
SUPER_ADMIN
ADMIN
MANAGER
RECEPTIONIST
HOUSEKEEPING
MAINTENANCE
WAITER
KITCHEN
INVENTORY_MANAGER
PROCUREMENT_OFFICER
ACCOUNTANT
HR_MANAGER
REPORTING_USER
```

Example permissions:

```text
USER_READ
USER_CREATE
USER_UPDATE
USER_DELETE

ROLE_READ
ROLE_CREATE
ROLE_UPDATE

GUEST_READ
GUEST_CREATE
GUEST_UPDATE

ROOM_READ
ROOM_CREATE
ROOM_UPDATE
ROOM_STATUS_UPDATE

RESERVATION_READ
RESERVATION_CREATE
RESERVATION_UPDATE
RESERVATION_CANCEL

CHECKIN_CREATE
CHECKOUT_CREATE

HOUSEKEEPING_READ
HOUSEKEEPING_UPDATE

MAINTENANCE_READ
MAINTENANCE_UPDATE

MENU_READ
MENU_CREATE
MENU_UPDATE

ORDER_READ
ORDER_CREATE
ORDER_UPDATE

KITCHEN_READ
KITCHEN_UPDATE

INVENTORY_READ
INVENTORY_CREATE
INVENTORY_ADJUST

SUPPLIER_READ
SUPPLIER_CREATE
SUPPLIER_UPDATE

PURCHASE_ORDER_READ
PURCHASE_ORDER_CREATE
PURCHASE_ORDER_APPROVE

INVOICE_READ
INVOICE_CREATE

PAYMENT_READ
PAYMENT_CREATE
PAYMENT_REFUND

REPORT_READ

SETTINGS_READ
SETTINGS_UPDATE

AUDIT_READ
```

---

# 7. Authentication Flow

Complete login flow:

```text
             USER
               │
               ▼
        ┌─────────────┐
        │ Login Form  │
        └──────┬──────┘
               │
               ▼
        POST /auth/login
               │
               ▼
        ┌─────────────┐
        │ Spring      │
        │ Security    │
        └──────┬──────┘
               │
               ▼
        Find User
               │
               ▼
        Verify Password
               │
        ┌──────┴──────┐
        │             │
      Failed        Success
        │             │
        ▼             ▼
    Audit Event   Generate Tokens
                      │
                      ▼
               Authenticated
                  Session
```

---

# 8. Login

Endpoint:

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

Production systems must use HTTPS.

Login response should contain the authenticated user/session information and the access-token mechanism defined by the deployment architecture.

Example conceptual response:

```json
{
  "success": true,
  "data": {
    "user": {
      "id": "uuid",
      "username": "admin",
      "email": "admin@example.com",
      "roles": [
        "ADMIN"
      ],
      "permissions": [
        "USER_READ",
        "ROOM_READ",
        "RESERVATION_CREATE"
      ]
    },
    "expiresIn": 900
  }
}
```

Authentication credentials must never be returned in the response.

---

# 9. JWT Access Tokens

Access tokens should be short-lived.

Recommended initial lifetime:

```text
10–15 minutes
```

The exact lifetime should be configurable.

JWT claims should remain minimal.

Example:

```json
{
  "sub": "user-uuid",
  "iat": 1750000000,
  "exp": 1750000900
}
```

Optional claims:

```text
jti
issuer
audience
session_id
```

Do not put sensitive information into the JWT.

Avoid placing:

```text
password
password_hash
payment information
personal identification documents
```

inside tokens.

---

# 10. Refresh Tokens

Refresh tokens allow the frontend to obtain a new access token without requiring the user to log in again.

Endpoint:

```http
POST /api/v1/auth/refresh
```

Recommended design:

```text
Access Token
Short Lifetime

Refresh Token
Longer Lifetime
```

Example refresh lifetime:

```text
7–30 days
```

The exact value should be configurable according to operational security requirements.

Refresh tokens should be:

- cryptographically random
- revocable
- associated with a session
- rotated
- protected from client-side exposure where possible
- stored securely

Prefer storing refresh tokens in secure HttpOnly cookies when the deployment architecture permits it.

---

# 11. Token Rotation

Refresh-token rotation:

```text
Refresh Token A
       │
       ▼
Validate
       │
       ▼
Revoke A
       │
       ▼
Create B
       │
       ▼
Return New Session
```

If a previously revoked refresh token is reused, the associated session/token family should be treated as potentially compromised and revoked according to the application's security policy.

---

# 12. Logout

Endpoint:

```http
POST /api/v1/auth/logout
```

Logout should:

```text
invalidate/revoke refresh session
clear authentication cookie where applicable
clear frontend authentication state
```

The frontend must redirect the user to:

```text
/login
```

Logout should be safe to repeat.

---

# 13. Session Management

Each authenticated session may contain:

```text
session_id
user_id
refresh_token_hash
created_at
last_used_at
expires_at
revoked_at
ip_address
user_agent
```

Store a hash of a refresh token rather than a plaintext refresh token whenever possible.

Sessions can be revoked individually.

Example:

```text
Settings
 → Security
 → Active Sessions
 → Revoke
```

---

# 14. Password Security

Passwords must be hashed using a password-specific, adaptive hashing algorithm.

Preferred:

```text
Argon2id
```

An appropriately configured BCrypt implementation is also acceptable where Argon2id is unavailable or operational constraints require it.

Never store:

```text
plaintext password
reversible encrypted password
```

Password requirements should include:

```text
minimum length
```

and should avoid unnecessarily restrictive composition rules that encourage predictable passwords.

Passwords must not be logged.

---

# 15. Password Change

Endpoint:

```http
POST /api/v1/auth/change-password
```

Request:

```json
{
  "currentPassword": "old-password",
  "newPassword": "new-password"
}
```

Process:

```text
Verify current password
       ↓
Validate new password
       ↓
Hash new password
       ↓
Update password
       ↓
Update password_changed_at
       ↓
Revoke appropriate existing sessions
       ↓
Audit event
```

Password changes should invalidate existing sessions according to the configured security policy.

---

# 16. Forgot Password

Endpoint:

```http
POST /api/v1/auth/forgot-password
```

Request:

```json
{
  "email": "user@example.com"
}
```

The response should not reveal whether the account exists.

Example:

```json
{
  "success": true,
  "message": "If an account exists for that address, reset instructions have been sent."
}
```

This prevents account enumeration.

---

# 17. Password Reset

Endpoint:

```http
POST /api/v1/auth/reset-password
```

Request:

```json
{
  "token": "secure-reset-token",
  "newPassword": "new-password"
}
```

Reset token requirements:

```text
cryptographically random
short-lived
single-use
revocable
```

Process:

```text
Reset Request
      ↓
Generate Token
      ↓
Store Token Hash
      ↓
Send Reset Message
      ↓
User Opens Link
      ↓
Submit New Password
      ↓
Validate Token
      ↓
Change Password
      ↓
Invalidate Token
      ↓
Revoke Sessions
```

---

# 18. Account Locking

Repeated failed authentication attempts should trigger throttling and/or temporary account protection.

Example policy:

```text
Failed attempts:
1–4 → continue with rate limiting

5+ → temporary lock/throttle
```

Lock duration should be configurable.

The system should avoid permanently locking an account based solely on a small number of failed attempts.

Administrative unlock:

```text
ADMIN
 ↓
User Management
 ↓
Locked Account
 ↓
Unlock
```

All unlock actions must be audited.

---

# 19. Email Verification

If email-based account verification is enabled:

```text
Create Account
      ↓
Verification Email
      ↓
User Clicks Link
      ↓
Validate Token
      ↓
email_verified = true
```

Verification tokens must:

- expire
- be single-use
- be securely generated

---

# 20. Multi-Factor Authentication

MFA should be supported as an optional or policy-controlled security feature.

Possible methods:

```text
TOTP authenticator application
Email verification
SMS OTP
Security keys / passkeys
```

For privileged administrator accounts, stronger authentication should be considered.

TOTP flow:

```text
Username
   ↓
Password
   ↓
MFA Challenge
   ↓
Authenticator Code
   ↓
Verify
   ↓
Create Session
```

MFA recovery codes should be generated securely and stored/handled so that they cannot be recovered in plaintext from the database.

---

# 21. Role-Based Access Control

RBAC structure:

```text
User
  │
  └── Role
        │
        └── Permissions
```

Example:

```text
RECEPTIONIST
    │
    ├── GUEST_READ
    ├── GUEST_CREATE
    ├── RESERVATION_READ
    ├── RESERVATION_CREATE
    ├── CHECKIN_CREATE
    └── CHECKOUT_CREATE
```

Roles should represent job responsibilities rather than individual UI pages.

---

# 22. Permission-Based Access Control

Backend authorization example:

```java
@PreAuthorize("hasAuthority('RESERVATION_CREATE')")
@PostMapping
public ResponseEntity<?> createReservation(...) {
    ...
}
```

Multiple permissions:

```java
@PreAuthorize("""
    hasAuthority('PAYMENT_REFUND')
    or hasRole('SUPER_ADMIN')
""")
```

The backend must always perform authorization checks.

Frontend permission checks are only for UI behavior.

---

# 23. Branch-Level Access

The system supports multiple branches.

Example:

```text
Organization
│
├── Branch A
├── Branch B
└── Branch C
```

A user may have:

```text
User A
 ├── Branch A
 └── Branch B
```

User A must not access Branch C.

Branch access must be enforced server-side.

A malicious user must not gain access simply by changing:

```text
branchId
```

in a URL or API request.

---

# 24. Frontend Authentication

Frontend structure:

```text
src/
├── features/
│   └── auth/
│       ├── components/
│       │   ├── LoginForm.tsx
│       │   ├── ForgotPasswordForm.tsx
│       │   └── ResetPasswordForm.tsx
│       ├── hooks/
│       ├── services/
│       ├── schemas/
│       └── types/
│
└── lib/
    └── auth/
        ├── auth-client.ts
        ├── permissions.ts
        └── session.ts
```

Login page:

```text
/login
```

---

# 25. Backend Authentication

Spring Boot packages:

```text
auth/
├── controller/
│   └── AuthController.java
│
├── service/
│   ├── AuthService.java
│   ├── TokenService.java
│   ├── PasswordService.java
│   └── SessionService.java
│
├── repository/
│   └── SessionRepository.java
│
├── entity/
│   └── UserSession.java
│
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RefreshRequest.java
│   ├── ChangePasswordRequest.java
│   ├── ForgotPasswordRequest.java
│   └── ResetPasswordRequest.java
│
└── security/
    ├── JwtAuthenticationFilter.java
    ├── JwtService.java
    └── CustomUserDetailsService.java
```

---

# 26. Spring Security Configuration

Example structure:

```java
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/refresh",
                    "/api/v1/auth/forgot-password",
                    "/api/v1/auth/reset-password"
                ).permitAll()
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
```

The exact CSRF strategy must match the final browser authentication mechanism. If authentication relies on cookies, CSRF protection must be addressed rather than disabled blindly.

---

# 27. JWT Filter

JWT filter responsibilities:

```text
Read authentication credential
       ↓
Extract token
       ↓
Validate signature
       ↓
Validate issuer/audience where configured
       ↓
Validate expiration
       ↓
Load user/session context
       ↓
Set SecurityContext
```

Invalid token:

```text
401 Unauthorized
```

Valid token without sufficient permission:

```text
403 Forbidden
```

---

# 28. Authentication DTOs

## LoginRequest

```java
public record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {}
```

## RefreshRequest

If refresh tokens are cookie-based, a refresh request body may not be required.

```java
public record RefreshRequest() {}
```

## ChangePasswordRequest

```java
public record ChangePasswordRequest(
    @NotBlank String currentPassword,
    @NotBlank String newPassword
) {}
```

## ForgotPasswordRequest

```java
public record ForgotPasswordRequest(
    @Email @NotBlank String email
) {}
```

---

# 29. Authentication API

## Login

```http
POST /api/v1/auth/login
```

## Refresh

```http
POST /api/v1/auth/refresh
```

## Logout

```http
POST /api/v1/auth/logout
```

## Current User

```http
GET /api/v1/auth/me
```

## Change Password

```http
POST /api/v1/auth/change-password
```

## Forgot Password

```http
POST /api/v1/auth/forgot-password
```

## Reset Password

```http
POST /api/v1/auth/reset-password
```

## Verify Email

```http
POST /api/v1/auth/verify-email
```

## MFA

```http
POST /api/v1/auth/mfa/setup
POST /api/v1/auth/mfa/verify
POST /api/v1/auth/mfa/disable
```

---

# 30. Authentication Responses

Successful authentication:

```json
{
  "success": true,
  "data": {
    "user": {
      "id": "uuid",
      "username": "admin",
      "email": "admin@example.com",
      "roles": [
        "ADMIN"
      ],
      "permissions": [
        "ROOM_READ",
        "RESERVATION_READ"
      ]
    },
    "expiresIn": 900
  }
}
```

The access-token transport must follow the final security architecture.

---

# 31. Authentication Error Codes

```text
AUTH_INVALID_CREDENTIALS
AUTH_ACCOUNT_DISABLED
AUTH_ACCOUNT_LOCKED
AUTH_TOKEN_INVALID
AUTH_TOKEN_EXPIRED
AUTH_REFRESH_TOKEN_INVALID
AUTH_SESSION_REVOKED
AUTH_ACCESS_DENIED
AUTH_MFA_REQUIRED
AUTH_MFA_INVALID
AUTH_PASSWORD_INVALID
AUTH_PASSWORD_TOO_WEAK
AUTH_RESET_TOKEN_INVALID
AUTH_RESET_TOKEN_EXPIRED
AUTH_EMAIL_NOT_VERIFIED
AUTH_RATE_LIMITED
```

---

# 32. Security Headers

The application should configure appropriate security headers, including where applicable:

```text
Content-Security-Policy
Strict-Transport-Security
X-Content-Type-Options
Referrer-Policy
Permissions-Policy
```

Clickjacking protection should be enabled using the appropriate modern header configuration.

---

# 33. CORS

Production CORS must explicitly allow the frontend origin.

Example:

```text
https://app.example.com
```

Do not use unrestricted origins for authenticated production APIs.

Allowed methods:

```text
GET
POST
PUT
PATCH
DELETE
OPTIONS
```

Allowed headers should be limited to those actually required.

---

# 34. Rate Limiting

Rate limiting should protect:

```text
/login
/refresh
/forgot-password
/reset-password
```

Example conceptual policy:

```text
Authentication endpoint
        ↓
Rate Limiter
        ↓
Normal request → Process
Excessive → 429
```

Rate limits should consider:

- IP address
- account identifier
- endpoint
- time window

Distributed deployments should use a shared mechanism such as Redis rather than only in-memory counters.

---

# 35. Brute Force Protection

Protection mechanisms:

```text
rate limiting
temporary account throttling
progressive delays
monitoring
audit logs
```

Do not reveal whether:

```text
username exists
email exists
```

during password-reset and similar account-discovery operations.

---

# 36. Audit Logging

Authentication events must be logged.

Examples:

```text
LOGIN_SUCCESS
LOGIN_FAILURE
LOGOUT
PASSWORD_CHANGED
PASSWORD_RESET_REQUESTED
PASSWORD_RESET_COMPLETED
ACCOUNT_LOCKED
ACCOUNT_UNLOCKED
MFA_ENABLED
MFA_DISABLED
SESSION_CREATED
SESSION_REVOKED
TOKEN_REUSE_DETECTED
```

Audit logs must not contain:

```text
password
JWT
refresh token
OTP
reset token
```

---

# 37. Security Events

Security events should be available for monitoring.

Example:

```text
Multiple failed logins
        ↓
Security Event
        ↓
Audit Log
        ↓
Monitoring
```

Potential alerts:

```text
unusual login volume
repeated failures
refresh-token reuse
administrator login anomalies
large numbers of account lockouts
```

---

# 38. Sensitive Data Handling

Never expose:

```text
password_hash
refresh_token
reset_token
MFA secret
MFA recovery codes
JWT signing secret
database credentials
```

API responses should use dedicated DTOs.

Do not serialize authentication entities directly.

---

# 39. Cookie Security

If cookies are used:

```text
HttpOnly
Secure
SameSite
```

must be configured appropriately.

Production cookies should normally be:

```text
Secure = true
```

HttpOnly cookies cannot be read by JavaScript, reducing exposure to token theft through certain client-side attacks.

Cookie domain and SameSite settings must match the deployment topology.

---

# 40. CSRF Considerations

The correct CSRF strategy depends on how authentication credentials are transported.

If credentials are automatically attached through cookies:

```text
CSRF protection must be considered.
```

If authentication uses a non-cookie bearer-token architecture:

```text
CSRF risk is different,
```

but XSS and token theft remain important concerns.

Do not disable CSRF protection without considering the chosen authentication architecture.

---

# 41. Session Revocation

Sessions should be revocable.

Administrative interface:

```text
User
 ↓
Security
 ↓
Active Sessions
 ↓
Select Session
 ↓
Revoke
```

Revoking a session should prevent further use of its refresh capability.

---

# 42. Concurrent Sessions

The system should support a configurable policy.

Possible options:

```text
ALLOW_UNLIMITED
LIMIT_SESSIONS
SINGLE_SESSION
```

Recommended implementation:

```text
Maximum active sessions configurable per organization
```

Administrators may revoke individual sessions.

---

# 43. API Authorization

Every protected API request follows:

```text
HTTP Request
     ↓
Authentication
     ↓
User
     ↓
Role
     ↓
Permission
     ↓
Branch Scope
     ↓
Resource Access
```

Example:

```text
POST /api/v1/payments
        ↓
Authenticated?
        ↓
PAYMENT_CREATE?
        ↓
Correct branch?
        ↓
Invoice accessible?
        ↓
Create payment
```

---

# 44. Frontend Route Protection

Protected route:

```text
/dashboard
```

Flow:

```text
Open Page
   ↓
Check Session
   ↓
Authenticated?
 ┌─┴─┐
No  Yes
│    │
▼    ▼
Login Permission
      │
      ▼
    Page
```

Protected routes must not rely solely on client-side checks.

The backend must independently authorize API requests.

---

# 45. Permission-Aware UI

Navigation should adapt to permissions.

Example:

```text
Dashboard
Reservations
Guests
Rooms
Front Desk
Restaurant
Kitchen
Inventory
Procurement
Finance
Employees
Reports
Settings
Audit
```

A user without:

```text
PAYMENT_READ
```

should not see the corresponding finance/payment navigation where hiding it improves usability.

However, hiding UI elements is not authorization.

---

# 46. Branch Switching

If a user has access to multiple branches:

```text
Topbar
 ↓
Branch Selector
```

Example:

```text
Current Branch:
Main Hotel ▼

Options:
Main Hotel
West Wing
Airport Branch
```

When branch changes:

```text
Selected Branch
      ↓
Update Application Context
      ↓
Invalidate branch-dependent queries
      ↓
Reload Data
```

The backend must verify that the authenticated user is authorized for the selected branch.

---

# 47. Authentication State

Frontend auth state:

```typescript
type AuthState = {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  permissions: string[];
  branches: Branch[];
  currentBranch: Branch | null;
};
```

Authentication state should not contain unnecessary secrets.

---

# 48. Security Monitoring

Monitor:

```text
authentication failures
authentication successes
account locks
password resets
MFA events
session creation
session revocation
permission changes
role changes
branch assignment changes
```

Security monitoring should be available to authorized administrators.

---

# 49. Testing

## Authentication Tests

Test:

```text
valid login
invalid password
unknown user
disabled account
locked account
expired access token
invalid access token
expired refresh token
revoked refresh token
refresh-token rotation
logout
password change
password reset
```

---

## Authorization Tests

Test:

```text
authorized role
unauthorized role
authorized permission
missing permission
branch access
cross-branch access
```

---

## Security Tests

Test:

```text
rate limiting
brute-force protection
CSRF behavior where applicable
CORS
security headers
session revocation
token reuse detection
```

---

## Frontend Tests

Test:

```text
login form
validation
loading state
invalid credentials
successful login
logout
route protection
permission-based navigation
branch switching
expired session handling
```

---

# 50. Security Acceptance Criteria

Authentication is complete when:

- [ ] Login works
- [ ] Logout works
- [ ] Access tokens are short-lived
- [ ] Refresh sessions are securely managed
- [ ] Refresh token rotation is implemented
- [ ] Passwords are securely hashed
- [ ] Password reset works
- [ ] Password reset tokens expire
- [ ] Password reset tokens are single-use
- [ ] Account protection exists
- [ ] Rate limiting exists
- [ ] RBAC works
- [ ] Permissions work
- [ ] Branch authorization works
- [ ] Protected API routes reject unauthenticated requests
- [ ] Unauthorized users receive 403
- [ ] Invalid authentication receives 401
- [ ] Authentication events are audited
- [ ] Sensitive data is not logged
- [ ] Security headers are configured
- [ ] CORS is restricted
- [ ] Session revocation works
- [ ] Security tests pass
- [ ] E2E authentication tests pass

---

# 51. Implementation Checklist

## Backend

- [ ] Create authentication module
- [ ] Configure Spring Security
- [ ] Configure password hashing
- [ ] Create UserDetailsService
- [ ] Create JWT service
- [ ] Create JWT authentication filter
- [ ] Create authentication controller
- [ ] Create authentication service
- [ ] Create session service
- [ ] Create refresh-token mechanism
- [ ] Create token rotation
- [ ] Create logout
- [ ] Create password change
- [ ] Create password reset
- [ ] Create account protection
- [ ] Create authorization system
- [ ] Create branch authorization
- [ ] Create authentication audit events

## Frontend

- [ ] Create login page
- [ ] Create login form
- [ ] Create forgot-password page
- [ ] Create reset-password page
- [ ] Create authentication client
- [ ] Create session handling
- [ ] Create protected layouts
- [ ] Create permission helpers
- [ ] Create branch selector
- [ ] Handle expired sessions
- [ ] Handle unauthorized responses
- [ ] Implement logout
- [ ] Implement loading states
- [ ] Implement authentication errors

## Security

- [ ] HTTPS
- [ ] Secure cookies where applicable
- [ ] HttpOnly cookies where applicable
- [ ] SameSite configuration
- [ ] CORS restrictions
- [ ] Security headers
- [ ] Rate limiting
- [ ] Brute-force protection
- [ ] Audit logging
- [ ] Secret management
- [ ] Sensitive-data filtering

## Testing

- [ ] Login tests
- [ ] Logout tests
- [ ] JWT tests
- [ ] Refresh-token tests
- [ ] Password tests
- [ ] Authorization tests
- [ ] Branch-access tests
- [ ] Rate-limit tests
- [ ] Session-revocation tests
- [ ] Frontend auth tests
- [ ] Playwright authentication tests

---

# COMPLETE AUTHENTICATION ARCHITECTURE

```text
                         USER
                           │
                           ▼
                  ┌─────────────────┐
                  │    Next.js      │
                  │    Login UI     │
                  └────────┬────────┘
                           │
                         HTTPS
                           │
                           ▼
                  ┌─────────────────┐
                  │ Spring Security │
                  └────────┬────────┘
                           │
                           ▼
                  ┌─────────────────┐
                  │  Auth Service   │
                  └────────┬────────┘
                           │
                ┌──────────┴──────────┐
                │                     │
                ▼                     ▼
        ┌──────────────┐      ┌───────────────┐
        │ PostgreSQL   │      │ Session/Token │
        │              │      │ Management    │
        │ Users        │      └───────┬───────┘
        │ Roles        │              │
        │ Permissions  │              ▼
        │ Branches     │       Refresh Session
        └──────────────┘              │
                                      ▼
                               Access Token
                                      │
                                      ▼
                              Protected API
                                      │
                                      ▼
                              Permission Check
                                      │
                                      ▼
                               Branch Check
                                      │
                                      ▼
                              Business Service
```

---

# AUTHENTICATION SECURITY MODEL

```text
                    AUTHENTICATION
                         │
              ┌──────────┴──────────┐
              │                     │
          Identity              Session
              │                     │
              ▼                     ▼
          Password              Access Token
          / MFA                 Refresh Token
              │                     │
              └──────────┬──────────┘
                         │
                         ▼
                   AUTHORIZATION
                         │
              ┌──────────┼──────────┐
              │          │          │
             Role     Permission   Branch
              │          │          │
              └──────────┼──────────┘
                         │
                         ▼
                    API RESOURCE
                         │
                         ▼
                  BUSINESS OPERATION
                         │
                         ▼
                    AUDIT LOG
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
DOCUMENT 11  ✓ Authentication & Authorization Specification
DOCUMENT 12  → Testing Strategy
DOCUMENT 13  → Deployment Specification
```

---

# NEXT IMPLEMENTATION DOCUMENT

The next document should be:

```text
TESTING_STRATEGY.md
```

It will connect all previous specifications and define:

```text
Backend Unit Testing
Repository Testing
Service Testing
Controller Testing
Spring Security Testing
JWT Testing
Authorization Testing
Branch Access Testing
PostgreSQL Integration Testing
Testcontainers
Frontend Unit Testing
Component Testing
API Integration Testing
Playwright E2E
Reservation Concurrency Testing
Payment Idempotency Testing
Inventory Consistency Testing
POS Testing
Performance Testing
Security Testing
Accessibility Testing
CI/CD Quality Gates
```

After testing:

```text
DEPLOYMENT_SPECIFICATION.md
```

will define the production infrastructure, Docker deployment, database deployment, environment management, HTTPS, CI/CD, backups, monitoring, scaling, disaster recovery, and production security.
