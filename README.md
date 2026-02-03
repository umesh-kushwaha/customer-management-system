# Customer Management System

Full-stack application with a Spring Boot backend (`customer-service`) and a Next.js frontend (`customer-ui`).

## Prerequisites

- Java 17 (backend)
- Node.js 18+ (frontend). If you use nvm and see version warnings, run:

```bash
nvm use 20.11.1
```

- npm (or yarn/pnpm)

## Backend (customer-service)

From `customer-service`:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

- API base: `http://localhost:8080`
- Swagger UI (dev only): `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON (dev only): `http://localhost:8080/v3/api-docs`

## Frontend (customer-ui)

From `customer-ui`:

```bash
npm install
npm run dev
```

- App: `http://localhost:3000`

### API base override (optional)

```bash
NEXT_PUBLIC_API_BASE=http://localhost:8080 npm run dev
```

## Notes

- Swagger/OpenAPI is disabled in `prod` profile.
- See the service-specific READMEs for more detail.
- CSP headers can be added in production for stronger security.
