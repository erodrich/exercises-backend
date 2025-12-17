# Exercise Logging Backend

A Spring Boot REST API for tracking workout exercises, sets, and progress.

## Features

### User Management
- **User Registration**: Create new user accounts with username, email, and password
- **User Login**: Simple username/password authentication (no JWT)
- **User Profile**: Retrieve user information by username

### Exercise Management (Admin)
- **CRUD Operations**: Create, read, update, and delete exercises
- **Pre-populated Database**: 34 common exercises across 6 muscle groups
- **Muscle Groups**: CHEST, BACK, SHOULDERS, LEGS, BICEPS, TRICEPS
- **Duplicate Prevention**: Ensures unique exercise names per muscle group

### Exercise Logging
- **Log Workouts**: Record exercise sessions with multiple sets
- **User-Scoped Logs**: Each user has their own workout history
- **Set Tracking**: Track weight and reps for each set
- **Failure Tracking**: Mark sets that resulted in failure
- **Timestamp Recording**: Automatic date/time tracking for each log

## Technology Stack

- **Java 21**
- **Spring Boot 4.0.0**
- **Spring Data JPA**
- **PostgreSQL** (production) / **H2** (local development)
- **Lombok** (reduce boilerplate)
- **Maven** (build tool)
- **Docker** (containerization)
- **JUnit 5 & Mockito** (testing)

## Project Structure

```
exercises-project/
├── exercises-backend/          # This directory - Spring Boot application
│   ├── src/                    # Source code
│   ├── docs/                   # Documentation
│   ├── Dockerfile              # Docker image definition
│   ├── buildAndPush.sh         # Build and push to Docker Hub
│   └── README.md               # This file
│
└── exercises-infra/            # Infrastructure as code
    ├── dev/                    # Development environment
    │   └── docker-compose.yml  # Dev deployment
    └── prod/                   # Production environment
        └── docker-compose.yml  # Prod deployment
```

## Quick Start (Local Development)

### Option 1: H2 In-Memory (No Database Setup)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Access H2 Console: `http://localhost:8080/exercise-logging/h2-console`
- JDBC URL: `jdbc:h2:mem:exercises`
- Username: `sa`
- Password: _(empty)_

### Option 2: PostgreSQL with Docker

```bash
# Start PostgreSQL
docker run -d \
  --name postgres-dev \
  -e POSTGRES_DB=exercises_dev \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine

# Run application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### API Access

- **Base URL**: http://localhost:8080/exercise-logging
- **Swagger UI**: http://localhost:8080/exercise-logging/swagger-ui/index.html
- **Health Check**: http://localhost:8080/exercise-logging/actuator/health

## Building Docker Image

### Prerequisites

- Docker installed
- Docker Hub account
- Set `DOCKER_USERNAME` environment variable

### Build and Push

```bash
# Set your Docker Hub username
export DOCKER_USERNAME=your-dockerhub-username

# Build and push to Docker Hub
./buildAndPush.sh 1.0.0

# Or use latest tag
./buildAndPush.sh latest
```

The script will:
1. Build the Docker image using multi-stage build
2. Tag with specified version and `latest`
3. Prompt for Docker Hub login
4. Push images to your Docker Hub repository

**Image location:** `docker.io/${DOCKER_USERNAME}/exercises-backend:version`

## Deploying with Docker Compose

For full deployment instructions, see the `exercises-infra` directory:

### Development Deployment

```bash
cd ../exercises-infra/dev
cp .env.example .env
# Edit .env with your Docker Hub username
docker-compose up -d
```

### Production Deployment

```bash
cd ../exercises-infra/prod
cp .env.example .env
# Edit .env with secure credentials
docker-compose up -d
```

📖 **Full deployment guide**: [../exercises-infra/README.md](../exercises-infra/README.md)

## API Endpoints

### User Endpoints
- `POST /api/v1/users/register` - Register new user
- `POST /api/v1/users/login` - Login user
- `GET /api/v1/users/{username}` - Get user by username

### Exercise Admin Endpoints
- `GET /api/v1/admin/exercises` - List all exercises
- `GET /api/v1/admin/exercises/{id}` - Get exercise by ID
- `POST /api/v1/admin/exercises` - Create new exercise
- `PUT /api/v1/admin/exercises/{id}` - Update exercise
- `DELETE /api/v1/admin/exercises/{id}` - Delete exercise

### Exercise Logging Endpoints
- `GET /api/v1/users/{userId}/logs` - Get all logs for user
- `POST /api/v1/users/{userId}/logs` - Create logs for user

## Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

**Test Coverage:**
- ✅ 64 tests passing
- Unit tests for all mappers
- Integration tests for repositories (H2)
- Unit tests with mocks for services

## Configuration Profiles

The application supports multiple Spring profiles:

| Profile | Database | Use Case | DDL Mode |
|---------|----------|----------|----------|
| `local` | H2 in-memory | Quick local testing | create-drop |
| `dev` | PostgreSQL | Development | update |
| `prod` | PostgreSQL | Production | validate |

**Configuration files:**
- `application.properties` - Common settings
- `application-local.properties` - H2 configuration
- `application-dev.properties` - Dev PostgreSQL
- `application-prod.properties` - Prod PostgreSQL

## Database Schema

### Core Tables

**users**
- id (PK), username (unique), password, email (unique), created_at

**exercise_entity**
- id (PK), name, muscle_group (0-5)

**exercise_log_entity**
- id (PK), user_id (FK), exercise_id (FK), date, has_failed

**exercise_set_entity**
- id (PK), weight, reps

**exercise_log_sets** (Join Table)
- exercise_log_id (FK), exercise_set_id (FK)

### Pre-populated Data

34 exercises automatically loaded from `data.sql`:
- 6 Chest exercises
- 6 Back exercises
- 6 Shoulder exercises
- 6 Leg exercises
- 5 Bicep exercises
- 5 Tricep exercises

## Docker Image Details

### Multi-Stage Build

**Builder Stage** (Maven + JDK 21)
- Downloads dependencies (cached)
- Compiles source code
- Packages JAR file

**Runtime Stage** (JRE 21 Alpine)
- Minimal JRE environment
- Non-root user (spring:spring)
- Health check enabled
- Final size: ~300MB

### Image Features

✅ Optimized size with multi-stage build
✅ Security: Non-root user execution
✅ Health checks built-in
✅ Alpine Linux base
✅ Supports environment variable configuration

## Documentation

All documentation is located in the `docs/` directory:

- 📖 [ENVIRONMENT_SETUP.md](docs/ENVIRONMENT_SETUP.md) - Environment configuration guide
- 📖 [CONFIGURATION_SUMMARY.md](docs/CONFIGURATION_SUMMARY.md) - Multi-environment setup
- 📖 [DOCKER_GUIDE.md](docs/DOCKER_GUIDE.md) - Docker deployment guide
- 📖 [DOCKER_SETUP_COMPLETE.md](docs/DOCKER_SETUP_COMPLETE.md) - Docker setup summary

## Architecture

Clean architecture with domain separation:

```
src/main/java/com/erodrich/exercises/
├── user/
│   ├── entity/           # UserEntity
│   ├── repository/       # UserRepository
│   ├── dto/              # UserDTO, LoginRequest, RegisterRequest
│   ├── mapper/           # UserMapper
│   ├── service/          # UserService
│   └── UserBoundary.java # REST Controller
├── exercise/
│   ├── entity/           # ExerciseEntity, MuscleGroup
│   ├── repository/       # ExerciseRepository
│   ├── dto/              # ExerciseDTO
│   ├── mapper/           # ExerciseMapper
│   ├── service/          # ExerciseService
│   └── ExerciseBoundary.java
└── exerciselogging/
    ├── entity/           # ExerciseLogEntity, ExerciseSetEntity
    ├── repository/       # ExerciseLogRepository, ExerciseSetRepository
    ├── dto/              # ExerciseLogDTO, ExerciseSetDTO, ExerciseDTO
    ├── mapper/           # ExerciseLogMapper
    ├── service/          # ExerciseLogService
    └── ExerciseLoggingBoundary.java
```

## Environment Variables

### Application

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | No | `dev` | Active profile |
| `DB_URL` | Prod | - | PostgreSQL JDBC URL |
| `DB_USERNAME` | Prod | `postgres` | Database username |
| `DB_PASSWORD` | Prod | - | Database password |

### Docker Build

| Variable | Required | Description |
|----------|----------|-------------|
| `DOCKER_USERNAME` | Yes | Your Docker Hub username |

## Production Deployment

### Deployment Options

1. **Docker Compose** (Recommended for single server)
   - See `exercises-infra/prod/docker-compose.yml`

2. **Kubernetes**
   - Convert compose files with `kompose`
   - Apply manifests to cluster

3. **Cloud Platforms**
   - AWS ECS/Fargate
   - Google Cloud Run
   - Azure Container Instances

### Production Checklist

- [ ] Build and push image with version tag
- [ ] Set strong database password
- [ ] Use specific image version (not `latest`)
- [ ] Configure environment variables
- [ ] Set up database backups
- [ ] Configure monitoring and alerts
- [ ] Enable SSL/TLS for database
- [ ] Review security settings
- [ ] Set up log aggregation
- [ ] Test rollback procedure

## Development Roadmap

### Implemented ✅
- REST API with CRUD operations
- User management (registration/login)
- Exercise administration
- Workout logging with sets
- PostgreSQL support
- Docker containerization
- Multi-environment configuration
- Comprehensive documentation
- Health check endpoints

### Planned Enhancements
- [ ] JWT authentication & authorization
- [ ] Password encryption (BCrypt)
- [ ] Database migrations (Flyway)
- [ ] Workout programs/routines
- [ ] Progress tracking and analytics
- [ ] Advanced search and filtering
- [ ] API versioning
- [ ] Rate limiting
- [ ] WebSocket for real-time updates

## Contributing

This is an educational project. Contributions and suggestions are welcome!

## License

Educational purposes only.

---

**Need help?**
- Check [docs/](docs/) for detailed guides
- Review deployment docs in [exercises-infra/](../exercises-infra/)
- Open an issue for bugs or questions
