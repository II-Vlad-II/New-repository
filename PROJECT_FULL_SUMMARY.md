# Health & Beauty Inventory - Project Summary

Document date: 18 March 2026  
Project: `health-beauty-inventory`  
Tech stack: Spring Boot 3.2.5, Java 21, Spring Security, Spring Data JPA, PostgreSQL, H2 (test)

## 1. Project Goal

Build a Health & Beauty inventory system with:
- product and supplier management
- user authentication
- role-based authorization (`ADMIN`, `STAFF`, `USER`)
- secure database-backed accounts

## 2. Main Issues Identified and Solved

### 2.1 Register failed because `enabled` was null
- Added `enabled` field in `User` entity with default `true`
- Set `enabled` during user creation flow

### 2.2 Access control was too permissive
- Implemented role restrictions for product actions:
  - `POST /api/products/**` -> `ADMIN`
  - `PUT /api/products/**` -> `ADMIN` or `STAFF`
  - `DELETE /api/products/**` -> `ADMIN`

### 2.3 Security config duplication / inconsistency
- Kept a single security configuration class:
  - [SecurityConfig.java](/C:/Users/PREDATOR/Desktop/Inventory%20Vlad/health-beauty-inventory/src/main/java/com/vlad/healthbeauty/config/SecurityConfig.java)
- Removed duplicate config class to avoid ambiguous behavior

### 2.4 Compilation issue in `CustomUserDetailsService`
- Fixed malformed file content that caused:
  - `'{` expected`
  - cascading parser errors

### 2.5 Supplier endpoints were not protected
- Added explicit RBAC restrictions in:
  - [SupplierController.java](/C:/Users/PREDATOR/Desktop/Inventory%20Vlad/health-beauty-inventory/src/main/java/com/vlad/healthbeauty/controller/SupplierController.java)
- Also fixed update behavior to bind path ID (`supplier.setId(id)`)

## 3. Authentication and Authorization Model

- Authentication method: `HTTP Basic`
- Password algorithm: `BCrypt`
- Users and roles loaded from database (`users`, `roles`, `user_roles`)
- Authorities resolved by:
  - [CustomUserDetailsService.java](/C:/Users/PREDATOR/Desktop/Inventory%20Vlad/health-beauty-inventory/src/main/java/com/vlad/healthbeauty/service/CustomUserDetailsService.java)

Current expected role behavior:
- `admin` -> full management rights
- `staff` -> product edits, no product delete/create
- `viewer` / regular user -> denied on protected routes

Detailed flow + curl evidence is documented in:
- [part2-auth-flow.md](/C:/Users/PREDATOR/Desktop/Inventory%20Vlad/health-beauty-inventory/docs/part2-auth-flow.md)

## 4. Database Account Setup Completed

Using pgAdmin SQL, the following were achieved:
- cleared old accounts safely
- created `admin` account with `ROLE_ADMIN`
- created `staff` account with `ROLE_STAFF`
- verified role assignment via join query

Known practical note from this setup:
- database relation table name differences (`user_roles` vs `users_roles`) can trigger FK errors if wrong table is used in scripts

## 5. Testing Improvements (Part 2 - Real Testing)

### 5.1 Added test infrastructure
- Added H2 dependency (test scope) in:
  - [pom.xml](/C:/Users/PREDATOR/Desktop/Inventory%20Vlad/health-beauty-inventory/pom.xml)
- Added test profile config:
  - [application-test.yml](/C:/Users/PREDATOR/Desktop/Inventory%20Vlad/health-beauty-inventory/src/test/resources/application-test.yml)

### 5.2 Product integration tests
- File:
  - [ProductControllerIntegrationTest.java](/C:/Users/PREDATOR/Desktop/Inventory%20Vlad/health-beauty-inventory/src/test/java/com/vlad/healthbeauty/controller/ProductControllerIntegrationTest.java)
- Validates:
  - public GET access
  - `401` for missing credentials
  - `403` for insufficient role
  - `201/200/204` for allowed role operations
  - `400` for invalid payload

### 5.3 Supplier integration tests
- File:
  - [SupplierControllerIntegrationTest.java](/C:/Users/PREDATOR/Desktop/Inventory%20Vlad/health-beauty-inventory/src/test/java/com/vlad/healthbeauty/controller/SupplierControllerIntegrationTest.java)
- Validates:
  - `401` without auth
  - `200` for staff read
  - `403` for viewer
  - admin-only create/update/delete

### 5.4 Baseline test profile consistency
- Updated context test to use test profile (`H2`):
  - [HealthBeautyInventoryApplicationTests.java](/C:/Users/PREDATOR/Desktop/Inventory%20Vlad/health-beauty-inventory/src/test/java/com/vlad/healthbeauty/HealthBeautyInventoryApplicationTests.java)

### 5.5 Test run result
- Command: `mvn -q test`
- Status: passed

## 6. API Documentation (Swagger / OpenAPI)

- Swagger UI page:
  - `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON:
  - `http://localhost:8080/api/v3/api-docs`

Implementation details:
- Added `springdoc-openapi-starter-webmvc-ui` dependency
- Added `OpenApiConfig` with API metadata + Basic Auth scheme
- Exposed swagger paths in security config (`permitAll`)
- Added endpoint descriptions that include role/access requirement

## 7. Current Functional State

- App compiles successfully
- Security rules are consistent and centralized
- Role behavior works for products and suppliers
- Accounts (`admin`, `staff`) are operational in DB
- Integration tests are in place and passing

## 8. Suggested Portfolio Evidence to Attach

- Screenshot of users + roles query result in pgAdmin
- Screenshot or logs showing:
  - admin create/delete allowed
  - staff edit allowed
  - staff delete denied (`403`)
- Test execution screenshot (`mvn test` successful)
- Reference to this summary + auth flow doc

## 9. Next Recommended Steps

1. Add API documentation (OpenAPI/Swagger) for endpoints and role requirements  
2. Add DTO validation annotations and more negative tests  
3. Add audit logs for critical actions (delete/update)  
4. Create a short deployment section (run instructions + environment variables)
