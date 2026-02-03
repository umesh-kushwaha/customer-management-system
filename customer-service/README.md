# Customer Service (Spring Boot)

Backend service for the customer-ui application.

## Requirements

- Java 17
- Gradle (or use the provided Gradle wrapper)

## Run the application

From the `customer-service` directory:

```bash
./gradlew bootRun
```

The service will start on `http://localhost:8080` by default.

## Skip tests

If you want to build without running tests:

```bash
./gradlew build -x test
```

## Run with a specific environment

This project uses Spring profiles.

- **Dev** (H2 in-memory DB, CORS for localhost, Swagger enabled):

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

- **Prod** (Swagger disabled):

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## OpenAPI / Swagger

When enabled (e.g., `dev` profile):

- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## H2 Console (dev only)

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:customerdb`
- Username: `sa`
- Password: `password`
