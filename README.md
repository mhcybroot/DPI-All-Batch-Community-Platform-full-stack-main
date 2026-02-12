# DPI All Batch Community Platform — Backend

A **Spring Boot** REST API powering a community/alumni platform with user management, forums, events, mentorship, Q&A, job board, and more.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.2 · Spring WebMVC · Spring Security |
| Language | Java 21 |
| Database | PostgreSQL 15 |
| ORM / Migrations | Spring Data JPA · Flyway 12 |
| Auth | JWT (jjwt 0.13.0) · BCrypt |
| Mapping | MapStruct 1.6.3 · Lombok |
| API Docs | SpringDoc OpenAPI 3.0.1 (Swagger UI) |
| Build | Gradle (Kotlin DSL) |
| Containerization | Docker · Docker Compose |

---

## Getting Started

### Prerequisites

- **Java 21** (Eclipse Temurin recommended)
- **PostgreSQL 15+** running locally — or use Docker Compose
- **Gradle 8+** (wrapper included)

### Option 1 — Run with Docker Compose

```bash
docker compose up --build
```

This starts:
- **App** on `http://localhost:8080`
- **PostgreSQL** on `localhost:5432`
- **Automated daily backups**

### Option 2 — Run Locally

1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE batch_comm_plat;
   ```

2. Update `src/main/resources/application.properties` if your DB credentials differ:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/batch_comm_plat
   spring.datasource.username=postgres
   spring.datasource.password=root
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

4. The API starts at `http://localhost:8080`

### Default Admin Account

On first startup, a default admin is seeded automatically:

| Field | Value |
|---|---|
| Email | `admin@example.com` |
| Password | `admin123` |
| Role | `ADMINISTRATOR` |

---

## API Documentation

Interactive Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

---

## Modules & Endpoints

### 🔐 Authentication (`/api/auth`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/register` | Register a new user (pending approval) |
| `POST` | `/login` | Login & receive JWT token |

### 🛡️ Admin (`/api/admin`) — *Requires ADMINISTRATOR*

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/approvals` | List pending approvals |
| `POST` | `/approvals/{id}/approve` | Approve user |
| `POST` | `/approvals/{id}/reject` | Reject user |
| `POST` | `/users` | Create user with roles |

### 👤 Profile (`/api/profile`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/me` | Get own profile |
| `PUT` | `/me` | Update profile |
| `POST` | `/me/skills` | Add skills |

### 🔍 Directory (`/api/directory`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/search?query=` | Search members |

### 💬 Forum (`/api/forum`)

| Method | Endpoint | Description |
|---|---|---|
| `GET/POST` | `/categories` | List / create categories |
| `GET/POST` | `/posts` | List / create posts |
| `GET/PUT/DELETE` | `/posts/{id}` | Post CRUD |
| `GET/POST` | `/posts/{id}/comments` | Comments |

### 📢 Notices (`/api/notices`) — *Admin only for CUD*

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/` | List active notices |
| `POST/PUT/DELETE` | `/`, `/{id}` | Create / update / delete |

### 🖼️ Memory Wall (`/api/memories`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/` | All memories |
| `POST` | `/` | Upload memory |
| `DELETE` | `/{id}` | Delete (owner/admin) |

### 🎂 Birthdays (`/api/birthdays`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/today` | Today's birthdays |
| `GET` | `/upcoming?days=7` | Upcoming birthdays |

### 📅 Events (`/api/events`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/` | Upcoming events |
| `POST` | `/` | Create event (admin/mod) |
| `PUT` | `/{id}` | Update event |
| `DELETE` | `/{id}` | Delete event (admin) |
| `PATCH` | `/{id}/status` | Change status |
| `POST` | `/events/{id}/register` | Register for event |
| `PATCH` | `/registrations/{id}/approve` | Approve registration |

### 🗳️ Polls (`/api/polls`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Create poll (admin/mod) |
| `POST` | `/{id}/vote/{optionId}` | Cast vote |
| `PATCH` | `/{id}/close` | Close poll |
| `GET` | `/{id}/results` | View results |
| `GET` | `/active` | Active polls |

### 📚 Knowledge Q&A (`/api/knowledge`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/questions` | Ask question |
| `GET` | `/questions` | List (sort: newest/votes/unsolved) |
| `POST` | `/questions/{id}/answers` | Post answer |
| `POST` | `/questions/{id}/vote` | Vote on question |
| `POST` | `/answers/{id}/vote` | Vote on answer |
| `PATCH` | `/answers/{id}/accept` | Accept answer |

### 🤝 Mentorship (`/api/mentorship`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/mentors/register` | Register as mentor |
| `GET` | `/mentors` | Search mentors |
| `POST` | `/connect` | Send request |
| `GET` | `/requests/outgoing` | Sent requests |
| `GET` | `/requests/incoming` | Incoming requests |
| `PATCH` | `/requests/{id}` | Accept/reject |

### 💼 Jobs (`/api/jobs`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Create job post |
| `GET` | `/` | List jobs (paginated, filterable) |
| `PUT` | `/{id}` | Update job |
| `DELETE` | `/{id}` | Delete job |
| `PATCH` | `/{id}/status` | Change status |

### 🏢 Businesses (`/api/businesses`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Register business |
| `GET` | `/` | List (search + pagination) |
| `PUT/DELETE` | `/{id}` | Update / delete |

### 🩸 Blood Donors (`/api/blood-donors`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/search` | Search by blood group & location |
| `POST` | `/profile` | Register as donor |
| `GET` | `/me` | View donor profile |
| `PATCH` | `/status` | Toggle availability |
| `PATCH` | `/last-donation` | Update last donation date |

---

## Project Structure

```
src/main/java/mh/cyb/root/DpiBatchMeetBackend/
├── common/           # Base entity, error DTOs, exception handlers
├── config/           # Security, CORS, OpenAPI, data seeder
├── security/         # JWT token provider & auth filter
└── modules/
    ├── auth/         # Login & registration
    ├── admin/        # User approvals & audit logs
    ├── user/         # User entity, roles, service
    ├── profile/      # Profiles, skills, directory search
    ├── community/    # Forum, notices, memories, birthdays
    ├── event/        # Events, registrations, polls & voting
    ├── knowledge/    # Q&A with voting (StackOverflow-style)
    ├── mentorship/   # Mentor profiles & connections
    └── professional/ # Job board, business directory, blood donors
```

---

## Database

- **Engine**: PostgreSQL 15
- **Schema management**: Flyway migrations + Hibernate auto-update
- **Migrations** in `src/main/resources/db/migration/`:
  - `V1` — Users, roles, approvals, audit logs
  - `V2` — Profiles, forum, notices, memories
  - `V3` — Events, registrations, polls, votes

---

## Security

- **JWT** Bearer token authentication (24h expiry)
- **BCrypt** password hashing
- **3-tier role system**: `ADMINISTRATOR` → `MODERATOR` → `MEMBER`
- New registrations require admin approval before login is enabled
- All auth events are logged to the audit trail

---

## Testing

Run the full test suite:

```bash
./gradlew test
```

The project includes **40 test files** covering controllers, services, repositories, and security components across all modules.

---

## Docker

### Build & Run

```bash
docker compose up --build
```

### Services

| Service | Description | Port |
|---|---|---|
| `app` | Spring Boot API | 8080 |
| `db` | PostgreSQL 15 | 5432 |
| `db-backup` | Automated daily backups | — |

Backup retention: 7 days / 4 weeks / 6 months.

---

## License

This project is for educational and community purposes.
