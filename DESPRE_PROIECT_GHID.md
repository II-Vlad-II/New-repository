# Despre Proiect - Ghid Simplu de Prezentare

Acest document te ajută să explici proiectul ușor, la interviu sau la evaluare.

## 1. Ce este proiectul (pe scurt)

`health-beauty-inventory` este o aplicație web pentru management de stocuri (produse de health & beauty).

Ce face:
- vezi lista de produse
- adaugi / editezi / ștergi produse (în funcție de rol)
- administrezi furnizori
- înregistrezi utilizatori
- controlezi accesul pe roluri (`ADMIN`, `STAFF`, `USER`)
- salvezi audit logs (cine a modificat ce și când)

Tehnologii:
- Java + Spring Boot
- Spring Security (Basic Auth)
- PostgreSQL (pgAdmin pentru administrare DB)
- Swagger/OpenAPI pentru documentație API

## 2. Roluri și permisiuni (foarte important la prezentare)

### Guest (vizitator, nelogat)
- poate vedea pagina
- poate vedea produse publice
- nu poate modifica nimic

### STAFF
- poate edita produse (`PUT`)
- nu poate adăuga produse
- nu poate șterge produse
- nu poate vedea butonul de register

### ADMIN
- poate adăuga, edita, șterge produse
- poate gestiona suppliers
- poate crea useri (register button)
- poate vedea secțiunea Audit Logs

## 3. Cum pornesc proiectul

1. Asigură-te că PostgreSQL este pornit  
2. Creează DB: `health_beauty_inventory`  
3. Verifică în `application.yml` user/parolă DB  
4. Rulează aplicația din IntelliJ sau:

```bash
mvn spring-boot:run
```

Aplicația pornește pe:
- `http://localhost:8080/`

## 4. Cum testez fiecare feature (rapid, clar)

## Login și UI pe roluri
1. intră pe `http://localhost:8080/`
2. login cu `admin` -> trebuie să vezi `Add Product`, `Delete`, `Register`, `Audit Logs`
3. login cu `staff` -> trebuie să vezi doar `Edit`
4. fără login -> doar view (fără butoane de acțiune)

## Produse
- Add product (ADMIN): completezi formularul și salvezi
- Edit product (ADMIN/STAFF): buton `Edit`
- Delete product (ADMIN): buton `Delete`
- Low stock list: verifici cardul “Low Stock”

## Suppliers
- test din Swagger (mai simplu):
  - admin poate `POST/PUT/DELETE`
  - staff poate doar `GET`

## Validări negative (exemple bune de menționat)
- produs cu preț negativ -> `400 Bad Request`
- produs cu nume gol -> `400 Bad Request`
- supplier cu email invalid -> `400 Bad Request`

## Audit Logs
1. loghează-te admin
2. fă un update/delete pe produs sau supplier
3. verifică în UI secțiunea `Audit Logs`
4. trebuie să apară: user, acțiune, entitate, detalii, timestamp

## 5. Baza de date în pgAdmin (ce trebuie să știi)

DB folosită:
- `health_beauty_inventory`

Tabele importante:
- `users`
- `roles`
- `user_roles`
- `products`
- `suppliers`
- `audit_logs`

Query util pentru utilizatori + roluri:

```sql
SELECT u.id, u.username, u.enabled, r.name AS role
FROM users u
JOIN user_roles ur ON ur.user_id = u.id
JOIN roles r ON r.id = ur.role_id
ORDER BY u.id;
```

Query util pentru audit:

```sql
SELECT id, username, action, entity_type, entity_id, details, created_at
FROM audit_logs
ORDER BY created_at DESC;
```

## 6. Swagger și OpenAPI - ce sunt și cum le folosesc

## Ce este Swagger?
Swagger UI = interfață web unde vezi și testezi endpoint-urile API fără să scrii cod.

URL:
- `http://localhost:8080/api/swagger-ui.html`

## Ce este OpenAPI?
OpenAPI = specificația JSON a API-ului (descriere tehnică a endpoint-urilor).

URL:
- `http://localhost:8080/api/v3/api-docs`

Cum explici simplu:
- Swagger = “pagina frumoasă” pentru test API
- OpenAPI = “fișierul JSON” din spatele Swagger

## Cum testezi în Swagger:
1. deschizi Swagger UI
2. alegi endpoint (ex: `PUT /api/products/{id}`)
3. click `Try it out`
4. bagi datele și execuți
5. vezi status (`200`, `400`, `401`, `403` etc.)

Notă:
- pentru endpoint-uri protejate trebuie auth Basic în Swagger (username/parolă)

## 7. GitHub - ce să arăți la prezentare

Minim important:
- ai repo pe GitHub
- ai commit-uri cu mesaje clare
- ai push la ultimele schimbări

Comenzi utile:

```bash
git status
git add .
git commit -m "Add RBAC UI, validations, audit logs, swagger docs"
git push origin main
```

Ca să vezi remote:

```bash
git remote -v
```

## 8. Întrebări frecvente și răspunsuri scurte

### Cum este securizat proiectul?
Prin Spring Security + Basic Auth + roluri din DB.

### Cum știi că rolurile funcționează?
Am teste de integrare și verificare practică în UI/Swagger (401/403/200/201/204).

### Cum validezi datele?
Cu Bean Validation (`@Valid`, `@NotBlank`, `@Positive`, `@Email`) și teste negative.

### Cum urmărești schimbările critice?
Prin `audit_logs` (user, action, entity, timestamp, details).

### Cum documentezi API-ul?
Cu Swagger UI + OpenAPI JSON.

## 9. Checklist final înainte de prezentare

- aplicația pornește fără erori
- login `admin` și `staff` funcționează
- rolurile se comportă corect în UI
- Swagger UI se deschide
- poți arăta 1-2 request-uri din Swagger
- ai date în `audit_logs`
- ai push pe GitHub la ultima versiune

---

Dacă vrei, în următorul pas îți pot face și o variantă “script de prezentare 5 minute”, adică exact ce să spui, propoziție cu propoziție.
