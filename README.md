# Hotel Reservation System

Hotel Reservation System is a full-stack project for managing hotels, rooms, bookings, addresses, and conveniences.

SonarCloud: [Project Overview](https://sonarcloud.io/summary/overall?id=SosiskaKiller812_hotel-reservation&branch=main)

## Stack

- Java 21
- Spring Boot 4
- Spring Web
- Spring Data JPA (Hibernate)
- PostgreSQL
- Redis
- Liquibase
- OpenAPI / Swagger UI (`springdoc-openapi`)
- Maven
- Docker / Docker Compose

## Domain Model

Main entities:

- Hotel
- Room
- Booking
- Address
- Convenience

The API supports single and bulk operations for core entities.

## Prerequisites

For local run without Docker for everything:

- Java 21
- Node.js 20+
- npm 10+
- Docker and Docker Compose (for PostgreSQL & Redis)

For Docker-only run:

- Docker and Docker Compose

## Environment Configuration

1. Create `.env` from `.env.example`.

Linux/macOS:

```bash
cp .env.example .env
```

Windows (PowerShell):

```powershell
Copy-Item .env.example .env
```

2. Update values if needed:

- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_NAME`
- `DB_PORT_HOST`
- `REDIS_HOST`
- `REDIS_PORT`
- `BACKEND_PORT`
- `FRONTEND_PORT`

## Run Options

### 1. Quick Start (Docker)

Runs backend + PostgreSQL in containers.

```bash
docker compose up -d --build
```

### 2. Local Development (recommended for coding)

Start database:

```bash
docker compose up -d db
```

Run backend:

```bash
cd backend
./mvnw spring-boot:run
```

On Windows (PowerShell), you can also use:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Run frontend:

```bash
cd frontend
npm install
npm run dev
```

## URLs

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Health endpoint: `http://localhost:8080/actuator/health`

## Database Migrations

Liquibase is enabled by default. Migrations run automatically on backend startup.

Changelog entry point:

- `backend/src/main/resources/db/changelog/db.changelog-master.yaml`

## Quality Checks

Backend:

```bash
cd backend
./mvnw test
```

Frontend:

```bash
cd frontend
npm run lint
npm run build
```

## API Documentation

OpenAPI docs are available through Swagger UI after backend startup.

## Notes

- Backend logs are written to `logs/hotel-app.log`.
- Hibernate schema mode is `validate` (no auto schema generation in runtime).
