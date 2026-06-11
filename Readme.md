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

### Step 1 — Install Docker Desktop

Download and install Docker Desktop from: https://www.docker.com/products/docker-desktop

- Available for Windows and Mac
- Free to download
- No other software needed

### Step 2 — Download the Project

Download this repository as a ZIP from GitHub and extract it to your computer.

### Step 3 — Start the Application

Open a terminal in the project folder and run:

```bash
docker compose up --build
```

This command builds the application and starts everything automatically (the app and the database). Wait 1-2 minutes for it to finish. The app is ready when you see "Started HealthBeautyInventoryApplication".

Once running, open your browser and go to: `http://localhost:8080`

## Useful Commands

```bash
docker compose up --build
```
Starts the application. Use this every time you want to run the project.

```bash
docker compose down
```
Stops the application and removes the containers properly. Use this every time you want to stop the project.

```bash
Ctrl + C
```
Only use this in an emergency to force stop. Note: this does NOT shut down the containers properly and may cause a "container already in use" error next time you start the app. If this happens run `docker compose down -v` to fix it.

```bash
docker compose down -v
```
Use this ONLY if you get errors when starting the app. Warning: this also deletes all the database data so everything resets back to the default seed data.

```bash
docker rm -f health_beauty_postgres
docker rm -f health_beauty_app
```
Force removes stuck containers. Use this if you get a "container name already in use" error.

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
