# README

Spring Boot inventory management application for health and beauty products, with role-based access control, audit logging, PostgreSQL persistence, and Docker support.

## Features

- Product management (`create`, `read`, `update`, `delete`)
- Supplier management
- Role-based authorization (`ADMIN`, `STAFF`, `USER`)
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

- Public:
  - `GET /api/products`
  - `GET /api/products/low-stock`
- `ADMIN`:
  - full product and supplier management
  - can register new users from UI
  - can view audit logs
- `STAFF`:
  - can edit products
  - read suppliers
- `USER`:
  - authenticated-only endpoints where allowed

## Default Seed Data (Local Development)

On startup, the app seeds:

- Users:
  - `admin / admin123` (`ROLE_ADMIN`)
  - `staff / staff123` (`ROLE_STAFF`)
- Suppliers (only if `suppliers` table is empty):
  - `L'Oreal Distributor`
  - `Nivea Wholesale`

`DataInitializer` avoids duplicates by checking existing records first.

## Run with Docker (Recommended)

```bash
docker compose up -d --build
```

Services:

- App: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

Useful commands:

```bash
docker compose ps
docker compose logs -f app
docker compose down
```

## Run Locally (Without Docker)

Prerequisites:

- Java 21
- Maven 3.9+
- PostgreSQL running locally
- Database created: `health_beauty_inventory`

Default local configuration is in `src/main/resources/application.yml`.

Run:

```bash
mvn spring-boot:run
```

Or package and run:

```bash
mvn clean package
java -jar target/health-beauty-inventory-0.0.1-SNAPSHOT.jar
```

## API Documentation

- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/v3/api-docs`

## Testing

Run all tests:

```bash
mvn test
```

Includes integration tests for product/supplier authorization behavior and request validation.

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
docs/
  part2-auth-flow.md
docker-compose.yml
Dockerfile
```

## Production Notes

- Change all default credentials before deployment.
- Use environment variables for database and external API keys.
- Prefer HTTPS and a stronger authentication model (for example JWT/OAuth2) for internet-facing deployments.
