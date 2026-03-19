# Instrucțiuni Pentru Alt Calculator

Acest ghid explică cum faci deploy/rulare pentru proiect pe alt PC.

## 1. Cerințe minime

- Java 21 instalat
- Maven 3.9+ instalat
- PostgreSQL instalat (sau Docker pentru DB)
- Git instalat

Verificare rapidă:

```bash
java -version
mvn -version
git --version
```

## 2. Clone proiectul

```bash
git clone <URL_REPO_GITHUB>
cd health-beauty-inventory
```

## 3. Configurează baza de date PostgreSQL

1. Creează DB:
```sql
CREATE DATABASE health_beauty_inventory;
```

2. Verifică user/parolă DB în `src/main/resources/application.yml`:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

Exemplu actual:
- DB: `health_beauty_inventory`
- user: `postgres`
- password: `1234`

## 4. Environment Variables

Proiectul folosește:
- `UNSPLASH_ACCESS_KEY` (opțional, pentru imagini Unsplash)

Dacă nu setezi această variabilă, aplicația folosește fallback image provider (picsum) și funcționează în continuare.

Windows PowerShell (temporar, sesiunea curentă):

```powershell
$env:UNSPLASH_ACCESS_KEY="cheia_ta"
```

Linux/macOS:

```bash
export UNSPLASH_ACCESS_KEY="cheia_ta"
```

## 5. Rulează aplicația (mod development)

```bash
mvn spring-boot:run
```

URL-uri utile:
- App: `http://localhost:8080/`
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/v3/api-docs`

## 6. Build + run JAR (deploy simplu)

Build:

```bash
mvn clean package
```

Run:

```bash
java -jar target/health-beauty-inventory-0.0.1-SNAPSHOT.jar
```

## 7. Date inițiale / conturi

La pornire, `DataInitializer` creeaza automat:
- cont `admin` cu parola `admin123` (`ROLE_ADMIN`)
- cont `staff` cu parola `staff123` (`ROLE_STAFF`)
- 2 suppliers default (`L'Oreal Distributor`, `Nivea Wholesale`) doar daca tabela `suppliers` este goala


Poți verifica în DB:

```sql
SELECT u.username, r.name
FROM users u
JOIN user_roles ur ON ur.user_id = u.id
JOIN roles r ON r.id = ur.role_id;
```

## 8. Docker (opțional, dar acum disponibil în proiect)

În proiect există deja:
- `Dockerfile`
- `docker-compose.yml`

Ce pornește:
1. container PostgreSQL
2. container aplicație Spring Boot

### 8.1 Pornește cu Docker Compose

Din folderul proiectului:

```bash
docker compose up --build
```

După pornire:
- App: `http://localhost:8080/`
- Swagger: `http://localhost:8080/api/swagger-ui.html`

### 8.2 Rulează în background

```bash
docker compose up --build -d
```

### 8.3 Vezi loguri

```bash
docker compose logs -f app
docker compose logs -f postgres
```

### 8.4 Oprește containerele

```bash
docker compose down
```

### 8.5 Oprește + șterge și volumul DB (atenție: pierzi datele locale din container)

```bash
docker compose down -v
```

### 8.6 Variabile utile pentru Docker

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `UNSPLASH_ACCESS_KEY` (opțional)

## 9. Troubleshooting rapid

- Eroare DB connection:
  - verifică dacă PostgreSQL rulează
  - verifică user/parolă/port/dbname în `application.yml`

- Swagger dă 401:
  - folosește `http://localhost:8080/api/swagger-ui.html`
  - verifică `SecurityConfig` căile whitelist

- UI nu arată modificările:
  - restart aplicația
  - hard refresh browser (`Ctrl+F5`)

- Build nu merge:
  - rulează `mvn -q test` și vezi prima eroare reală

## 10. Deploy checklist

- repo clonat
- Java/Maven ok
- PostgreSQL ok + DB creată
- environment variables setate (opțional Unsplash)
- aplicația pornește pe port 8080
- Swagger/OpenAPI accesibile
- login testat (`admin` / `staff`)
