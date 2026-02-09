# Customer UI (Next.js)

Frontend application for the customer-service backend.

## Requirements

- Node.js 18+ (recommended). If you use nvm and hit a version warning, run:

```bash
nvm use 20.11.1
```
- npm (or yarn/pnpm)

## Install dependencies

```bash
npm install
```

## Run the application

```bash
npm run dev
```

The app will be available at `http://localhost:3000`.

## Configure API base URL

By default, the UI calls `http://localhost:8080`. You can override this by setting:

```bash
NEXT_PUBLIC_API_BASE=http://localhost:8080
```

Example (macOS/Linux):

```bash
NEXT_PUBLIC_API_BASE=http://localhost:8080 npm run dev
```

Example (Windows PowerShell):

```powershell
$env:NEXT_PUBLIC_API_BASE="http://localhost:8080"; npm run dev
```

## Production build

```bash
npm run build
npm run start
```
