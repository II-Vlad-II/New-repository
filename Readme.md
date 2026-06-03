# README

Spring Boot inventory management application for health and beauty products, with role-based access control, audit logging, PostgreSQL persistence, and Docker support.

## Features

- Product management (`create`, `read`, `update`, `delete`)
- Supplier management
- Role-based authorization (`ADMIN`, `MANAGER`, `SUPERVISOR`, `SALES_ASSISTANT`)
- HTTP Basic authentication with BCrypt password hashing
- Audit logs for critical actions
- OpenAPI/Swagger documentation
- Dockerized app + PostgreSQL setup

## Tech Stack

- Java 21
- Spring Boot 3.2.5
- Spring Web, Spring Data JPA, Spring Security, Validation
- PostgreSQL 16
- Maven
- Docker / Docker Compose
- H2 (tests)

## Access Control Summary

- `ADMIN` (Vlad):
  - full product and supplier management
  - can register and manage users
  - can view audit logs
- `MANAGER` (Gladys):
  - can manage products and suppliers
  - can view audit logs
- `SUPERVISOR` (Heyden):
  - can view and edit products
  - read suppliers
- `SALES_ASSISTANT` (Sam):
  - can view products only

## Default Seed Data

On startup, the app automatically creates:

- Users:
  - `admin / admin123` — Vlad (`ROLE_ADMIN`)
  - `manager / manager123` — Gladys (`ROLE_MANAGER`)
  - `supervisor / supervisor123` — Heyden (`ROLE_SUPERVISOR`)
  - `sales / sales123` — Sam (`ROLE_SALES_ASSISTANT`)
- Suppliers:
  - `L'Oreal Distributor`
  - `Nivea Wholesale`
- Products:
  - `Hydrating Face Serum`
  - `Vitamin C Day Cream`
  - `Repair Hair Mask`

## Run with Docker (Recommended)

```bash
docker compose up --build
```

Services:

- App: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

Useful commands:

```bash
docker compose down -v
docker compose up --build
```

## API Documentation

- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/v3/api-docs`

## Project Structure

```text
src/main/java/com/vlad/healthbeauty
  config/        # Security and OpenAPI configuration
  controller/    # REST endpoints
  service/       # Business logic
  repository/    # Spring Data repositories
  model/         # JPA entities
  dto/           # Request/response DTOs
src/main/resources
  application.yml
  static/index.html
docker-compose.yml
Dockerfile
```
