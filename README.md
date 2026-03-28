# Hotel Reservation System

REST API is a Java + Spring Boot application for managing the hotel booking system.

[SonarCloud](https://sonarcloud.io/summary/overall?id=SosiskaKiller812_hotel-reservation&branch=main)

## Technologies

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA (Hibernate)
- PostgreSQL
- OpenAPI / Swagger UI (springdoc)
- Maven

Next steps to develop:

- SPA on React
- Docker
- Liquibase

## About

The project is a hotel booking service, it features 5 entities:

- Hotel
- Room
- Booking
- Address
- Convenience

You can add new hotels, rooms, reservations, and conveniences. The application supports large bulk operations using multithreading, etc.

## Launching
git clone https://github.com/your-username/hotel-reservation.git
cd hotel-reservation

### Rename .env.example to .env:
On Linux/Mac:
```cp .env.example .env```

On Windows (PowerShell):
```copy .env.example .env```

Now you can change any variable to your own

## Ways to start application:

### 1. The quickest way to run app
Start with docker
```docker-compose up -d --build```


### 2. Basic run

```bash
docker-compose up -d db

cd backend
./mvnw spring-boot:run

cd ../frontend
npm install
npm run dev
```

## Ports

After launch, services are avaible on next addresses:
- Frontend : `http://localhost:5173`
- Backend API: `http://localhost:8080`.

- Also u can see endpoint on `http://localhost:8080/swagger-ui/index.html`.
