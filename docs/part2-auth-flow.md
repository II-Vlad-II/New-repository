# Part 2 - Authentication and Authorization Flow

## 1. Security model used in this project

- Authentication type: `HTTP Basic`
- User source: PostgreSQL (`users`, `roles`, `user_roles`)
- Password storage: `BCrypt` hash (never plain text)
- Authorization type: Role-Based Access Control (RBAC) with `ROLE_ADMIN`, `ROLE_STAFF`, `ROLE_USER`
- Method-level protection: `@PreAuthorize` plus route rules in `SecurityConfig`

This project does **not** use JWT or session login endpoint.  
Credentials are sent in the `Authorization: Basic ...` header on protected requests.

## 2. Access rules currently implemented

### Products
- `GET /api/products` -> public
- `GET /api/products/{id}` -> authenticated
- `POST /api/products` -> `ADMIN` only
- `PUT /api/products/{id}` -> `ADMIN` or `STAFF`
- `DELETE /api/products/{id}` -> `ADMIN` only

### Suppliers
- `GET /api/suppliers` -> `ADMIN` or `STAFF`
- `GET /api/suppliers/{id}` -> `ADMIN` or `STAFF`
- `POST /api/suppliers` -> `ADMIN` only
- `PUT /api/suppliers/{id}` -> `ADMIN` only
- `DELETE /api/suppliers/{id}` -> `ADMIN` only

## 3. Request/Response evidence (role behavior)

Base URL:

```bash
http://localhost:8080
```

### A) Admin can create product (expected success)

```bash
curl -i -X POST "http://localhost:8080/api/products" \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d "{\"name\":\"Niacinamide Serum\",\"brand\":\"The Ordinary\",\"category\":\"Skin Care\",\"price\":49.99,\"quantity\":30}"
```

Expected response:
- Status: `201 Created`
- Body contains created product JSON

### B) Staff can edit product but cannot delete (expected partial access)

Edit product:

```bash
curl -i -X PUT "http://localhost:8080/api/products/1" \
  -H "Content-Type: application/json" \
  -u staff:staff123 \
  -d "{\"name\":\"Niacinamide Serum Updated\",\"price\":44.99,\"quantity\":25}"
```

Expected response:
- Status: `200 OK`

Delete product with same user:

```bash
curl -i -X DELETE "http://localhost:8080/api/products/1" \
  -u staff:staff123
```

Expected response:
- Status: `403 Forbidden`

### C) User role cannot perform protected supplier operations

Example below assumes you registered `viewer` first via `POST /api/auth/register`.

```bash
curl -i -X GET "http://localhost:8080/api/suppliers" \
  -u viewer:viewer123
```

Expected response:
- Status: `403 Forbidden`

## 4. Validation and test evidence

Integration tests added and passing:
- `ProductControllerIntegrationTest`
- `SupplierControllerIntegrationTest`

What tests prove:
- `401` when no credentials are provided on protected routes
- `403` when role is insufficient
- `201/200/204` for valid role-based operations
- `400` on invalid request payloads

Run command:

```bash
mvn test
```

## 5. API documentation (Swagger / OpenAPI)

Swagger UI is available at:

```bash
http://localhost:8080/api/swagger-ui.html
```

OpenAPI JSON is available at:

```bash
http://localhost:8080/api/v3/api-docs
```

In Swagger descriptions, each endpoint includes required access level (Public, ROLE_ADMIN, ROLE_STAFF, etc.).

## 6. Conclusion for Part 2

The application enforces RBAC correctly with database-backed users and BCrypt passwords.  
Admin has full inventory management rights, Staff has edit-only product rights, and basic users are blocked from privileged actions.

## 7. Runtime defaults in current app version

- Default seeded users at startup:
  - `admin / admin123` with `ROLE_ADMIN`
  - `staff / staff123` with `ROLE_STAFF`
- Suppliers are auto-seeded only when the `suppliers` table is empty:
  - `L'Oreal Distributor`
  - `Nivea Wholesale`
