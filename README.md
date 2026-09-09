# 🏸 Badminton Doubles Match Management Application

A full-stack web application for managing badminton doubles matches, tracking player statistics, and analyzing pair performance.

## Tech Stack

| Layer     | Technology                          |
|-----------|-------------------------------------|
| Frontend  | React 18 + Vite + React Router 6    |
| Backend   | Spring Boot 3.3 + Java 21          |
| Database  | PostgreSQL 16                       |
| Build     | Docker + Docker Compose             |
| API Docs  | SpringDoc OpenAPI (Swagger UI)      |

## Features

- **Match Management** — Record doubles matches with 4 players (2v2), scores, and notes
- **Player Management** — Full CRUD with search, activate/deactivate status
- **Dashboard** — Overview with total matches, players, recent matches, top/most active players
- **Player Ranking** — Rankings sorted by win percentage with minimum matches filter
- **Pair Statistics** — Performance analysis of all doubles pair combinations
- **Pagination & Sorting** — Server-side pagination with newest/oldest sort for match history
- **Responsive UI** — Mobile-friendly sidebar layout with dark theme navigation

## Architecture

```
badminton_doubles/
├── docker-compose.yml          # Orchestrates all 3 services
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/java/...       # Controllers, Services, Repos, DTOs, Entities
│       ├── main/resources/     # application.properties, Flyway migrations
│       └── test/               # Unit + integration tests
├── frontend/
│   ├── Dockerfile              # Multi-stage build (Node → nginx)
│   ├── nginx.conf              # SPA routing + API proxy
│   ├── package.json
│   └── src/
│       ├── api/api.js          # Axios API service layer
│       ├── utils/format.js     # Date/number formatting utilities
│       ├── components/         # Reusable UI components
│       └── pages/              # Route-level page components
└── .env.example                # Environment variable template
```

### Database Schema (Normalized)

- **players** — id, name, phone, email, active, timestamps
- **matches** — id, played_at, scores, winner_side, notes, timestamps
- **match_players** — match_id, player_id, side (A/B), position (1/2)

Statistics are computed dynamically from match data, not stored.

## Quick Start

### With Docker (Recommended)

```bash
# 1. Clone and configure
cp .env.example .env
# Edit .env if needed

# 2. Build and run
docker compose up --build

# 3. Access the application
# Frontend:  http://localhost:3000
# Backend:   http://localhost:8080
# Swagger:   http://localhost:8080/swagger-ui.html
# Database:  localhost:5432 (badminton/badminton_secret)
```

### Local Development

**Backend:**
```bash
cd backend
# Requires Java 21, Maven, and PostgreSQL running locally
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev    # Runs on http://localhost:5173, proxies /api to :8080
```

## API Endpoints

### Players
| Method | Endpoint                  | Description          |
|--------|---------------------------|----------------------|
| GET    | `/api/players`            | List all players     |
| GET    | `/api/players/{id}`       | Get player by ID     |
| POST   | `/api/players`            | Create new player    |
| PUT    | `/api/players/{id}`       | Update player        |
| PATCH  | `/api/players/{id}/status`| Toggle active status |
| DELETE | `/api/players/{id}`       | Delete player        |

### Matches
| Method | Endpoint           | Description                          |
|--------|--------------------|--------------------------------------|
| GET    | `/api/matches`     | List matches (paginated, filterable) |
| GET    | `/api/matches/{id}`| Get match with player details        |
| POST   | `/api/matches`     | Create new match                     |
| PUT    | `/api/matches/{id}`| Update match                         |
| DELETE | `/api/matches/{id}`| Delete match                         |

### Statistics
| Method | Endpoint                          | Description               |
|--------|-----------------------------------|---------------------------|
| GET    | `/api/statistics/dashboard`       | Dashboard summary data    |
| GET    | `/api/statistics/players`         | All player statistics     |
| GET    | `/api/statistics/players/{id}`    | Single player statistics  |
| GET    | `/api/statistics/players/ranking` | Rankings by win rate      |
| GET    | `/api/statistics/pairs`           | Doubles pair statistics   |

## Testing

**Backend:**
```bash
cd backend
./mvnw test
```

**Frontend:**
```bash
cd frontend
npm test
```

## Environment Variables

| Variable                 | Default                         | Description         |
|--------------------------|---------------------------------|---------------------|
| `POSTGRES_DB`            | badminton                       | Database name       |
| `POSTGRES_USER`          | badminton                       | Database user       |
| `POSTGRES_PASSWORD`      | badminton_secret                | Database password   |
| `SPRING_PROFILES_ACTIVE` | docker                          | Spring profile      |
| `VITE_API_BASE_URL`      | (empty — uses nginx proxy)      | API base URL        |
