# Backoffice API

REST API MVP built with Spring Boot and Hexagonal Architecture as part of the Ethos PBS technical challenge.

## Tech Stack

- **Java 17** + **Spring Boot 3.5**
- **Spring Security** — JWT-based authentication, role-based access control (ADMIN / USER)
- **Spring Data JPA** + **PostgreSQL** via Supabase
- **Flyway** — versioned schema migrations
- **Custom Rate Limiter** — sliding window, `ConcurrentHashMap`, no external libraries
- **springdoc-openapi** — Swagger UI with JWT support

## Architecture

Hexagonal (Ports & Adapters):

```
domain/         → models, input/output port interfaces, domain services (no Spring)
application/    → use cases, DTO orchestration
infrastructure/ → REST controllers, JPA adapters, security filters, config
shared/         → DTOs, exceptions, OpenAPI annotations
```

## Prerequisites

- Java 17

## Setup

> **For evaluators:** A Supabase instance is already provisioned. Copy `.env.example` to `.env` and fill in only `DB_PASSWORD` with the value provided.

**1. Clone and configure environment**

```bash
cp .env.example .env
# Set DB_PASSWORD with the provided value
```

**2. Database**

The app connects to a PostgreSQL instance (Supabase recommended). Flyway runs migrations automatically on startup. No manual SQL needed.

**3. Run**

```bash
./gradlew bootRun
```

The app seeds an admin user on startup:
- Email: `admin@backoffice.com`
- Password: `admin`

## API

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

| Method | Endpoint | Auth |
|--------|----------|------|
| POST | `/api/auth/login` | Public |
| POST | `/api/users` | Public |
| GET / PUT / DELETE | `/api/users/**` | ADMIN |
| GET / POST / PUT / DELETE | `/api/projects/**` | Authenticated |

**Auth flow:**
1. `POST /api/auth/login` → returns `{ "token": "..." }`
2. Add header `Authorization: Bearer <token>` to subsequent requests

## Testing the Flow

The recommended way to explore the API is via Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

**As a regular user:**

1. `POST /api/users` — register a new user with email, name and password
2. `POST /api/auth/login` — login with those credentials, copy the returned `token`
3. Click **Authorize** in Swagger UI and paste the token
4. `POST /api/projects` — create a project (use your user ID as `ownerId`)
5. `GET /api/projects` — list all projects
6. `GET /api/projects/{id}` — get the created project by ID
7. `PUT /api/projects/{id}` — update name, description or status
8. `DELETE /api/projects/{id}` — delete the project

**As admin:**

9. `POST /api/auth/login` — login with `admin@backoffice.com` / `admin`, copy the token
10. Click **Authorize** and paste the admin token
11. `GET /api/users` — list all users (ADMIN only)
12. `GET /api/users/{id}` — get a specific user

**Security scenarios:**

13. Try `GET /api/users` with a regular user token → `403 Forbidden`
14. Try any protected endpoint without a token → `401 Unauthorized`

## Rate Limiter

Tracks requests per authenticated user (by email) or by IP for anonymous requests. Configurable via `.env`:

```
RATE_LIMITER_MAX_REQUESTS=60
RATE_LIMITER_WINDOW_SECONDS=60
```

Exceeding the limit returns `429 Too Many Requests`.

## Tests

```bash
./gradlew test
```

- **Unit tests** — domain services (`UserService`, `ProjectService`) and security (`JwtService`, `RateLimiterFilter`) using Mockito
- **Slice tests** — `@WebMvcTest` for all controllers, covering auth (401), access control (403), validation (400) and happy paths
