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
- **H2 Database** (in-memory for development)
- **Lombok** (reduce boilerplate)
- **Maven** (build tool)
- **JUnit 5 & Mockito** (testing)

## Architecture

Clean architecture with separated concerns:

```
├── user/                    # User domain
│   ├── entity/             # User persistence entities
│   ├── repository/         # Data access layer
│   ├── dto/                # Data transfer objects
│   ├── mapper/             # Entity-DTO mapping
│   ├── service/            # Business logic
│   └── UserBoundary.java   # REST controller
├── exercise/                # Exercise domain
│   ├── entity/             # Exercise entities (ExerciseEntity, MuscleGroup)
│   ├── repository/         # Exercise repository
│   ├── dto/                # Exercise DTOs
│   ├── mapper/             # Exercise mapper
│   ├── service/            # Exercise business logic
│   └── ExerciseBoundary.java  # Admin REST controller
└── exerciselogging/         # Exercise logging domain
    ├── entity/             # Log and Set entities
    ├── repository/         # Log repositories
    ├── dto/                # Log DTOs
    ├── mapper/             # Log mapper
    ├── service/            # Logging business logic
    └── ExerciseLoggingBoundary.java  # REST controller
```

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Running the Application

```bash
cd exercises-backend
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080/exercise-logging`

### H2 Console

Access the H2 database console at:
```
http://localhost:8080/exercise-logging/h2-console
```

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:exercisedb`
- Username: `sa`
- Password: _(leave empty)_

### API Documentation (Swagger UI)

Access the interactive API documentation at:
```
http://localhost:8080/exercise-logging/swagger-ui/index.html
```

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

## Example Requests

### Register User
```bash
curl -X POST http://localhost:8080/exercise-logging/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com"
  }'
```

### Login User
```bash
curl -X POST http://localhost:8080/exercise-logging/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

### Create Exercise Log
```bash
curl -X POST http://localhost:8080/exercise-logging/api/v1/users/1/logs \
  -H "Content-Type: application/json" \
  -d '[
    {
      "timestamp": "12/16/2025 15:49:18",
      "exercise": {
        "group": "Chest",
        "name": "Bench Press"
      },
      "sets": [
        {"weight": 100.0, "reps": 10},
        {"weight": 100.0, "reps": 8},
        {"weight": 100.0, "reps": 6}
      ],
      "failure": false
    }
  ]'
```

### List All Exercises
```bash
curl http://localhost:8080/exercise-logging/api/v1/admin/exercises
```

## Testing

Run all tests:
```bash
./mvnw test
```

**Test Coverage:**
- Unit tests for all mappers
- Integration tests for repositories (with H2)
- Unit tests with mocks for services
- 65+ test cases total

## Database Schema

### Users Table
- id (PK)
- username (unique)
- password
- email (unique)
- created_at

### Exercise Entity Table
- id (PK)
- name
- muscle_group (enum: 0-5)

### Exercise Log Entity Table
- id (PK)
- user_id (FK → users)
- exercise_id (FK → exercise_entity)
- date
- has_failed

### Exercise Set Entity Table
- id (PK)
- weight
- reps

### Exercise Log Sets (Join Table)
- exercise_log_id (FK)
- exercise_set_id (FK)

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Context path
server.servlet.context-path=/exercise-logging

# H2 Database
spring.datasource.url=jdbc:h2:mem:exercisedb
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

## Pre-populated Data

The database is automatically populated with 34 exercises on startup via `data.sql`:
- 6 Chest exercises
- 6 Back exercises
- 6 Shoulder exercises
- 6 Leg exercises
- 5 Bicep exercises
- 5 Tricep exercises

## Future Enhancements

- [ ] Add JWT authentication
- [ ] Password encryption (BCrypt)
- [ ] Workout programs/routines
- [ ] Progress tracking and analytics
- [ ] REST API versioning
- [ ] PostgreSQL for production
- [ ] Docker containerization
- [ ] CI/CD pipeline

## License

This project is for educational purposes.
