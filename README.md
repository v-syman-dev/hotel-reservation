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

From the root:

```bash
cd backend
./mvnw spring-boot:run
```

After launch, the application is avaible on: `http://localhost:8080`.

To see endpoints open `http://localhost:8080/swagger-ui/index.html`.
