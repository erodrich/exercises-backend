# Environment Setup Guide

This application supports multiple environments with different database configurations.

## Available Profiles

### 1. **local** (H2 In-Memory) - Default for quick testing
- **Database**: H2 in-memory
- **Use case**: Quick local development without PostgreSQL setup
- **Data**: Lost on restart, populated from `data.sql`
- **H2 Console**: Available at `/h2-console`

### 2. **dev** (PostgreSQL) - Development environment
- **Database**: PostgreSQL (local or Docker)
- **Use case**: Development with persistent data
- **Data**: Persisted, initialized with `data.sql` on first run
- **DDL**: `update` (auto-creates/updates schema)

### 3. **prod** (PostgreSQL) - Production environment
- **Database**: PostgreSQL (production server)
- **Use case**: Production deployment
- **Data**: Persisted, managed via migrations (Flyway/Liquibase recommended)
- **DDL**: `validate` (validates schema, doesn't modify)
- **Security**: Enhanced error handling, minimal logging

## Quick Start

### Option 1: Local Development with H2 (No setup required)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Access H2 Console: `http://localhost:8080/exercise-logging/h2-console`
- JDBC URL: `jdbc:h2:mem:exercises`
- Username: `sa`
- Password: _(empty)_

### Option 2: Development with PostgreSQL

#### Step 1: Start PostgreSQL with Docker

```bash
# Start PostgreSQL container
docker-compose up -d postgres-dev

# Verify it's running
docker-compose ps
```

#### Step 2: Run the application

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Or with default profile (change in `application.properties`):
```bash
./mvnw spring-boot:run
```

### Option 3: Production Deployment

#### Using Docker Compose

```bash
# Set production password
export DB_PASSWORD=your_secure_password

# Start PostgreSQL
docker-compose up -d postgres-prod

# Run application
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod \
  -Dspring-boot.run.arguments="--DB_USERNAME=postgres --DB_PASSWORD=${DB_PASSWORD}"
```

#### Using External PostgreSQL

Set environment variables:

```bash
export DB_URL=jdbc:postgresql://your-host:5432/exercises_prod
export DB_USERNAME=your_username
export DB_PASSWORD=your_password

./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Database Configuration Details

### Local Profile (`application-local.properties`)
```properties
spring.datasource.url=jdbc:h2:mem:exercises
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
```

### Dev Profile (`application-dev.properties`)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/exercises_dev
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
```

### Prod Profile (`application-prod.properties`)
```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/exercises_prod}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.jpa.hibernate.ddl-auto=validate
spring.sql.init.mode=never
```

## Docker Commands

### Development Database

```bash
# Start
docker-compose up -d postgres-dev

# Stop
docker-compose stop postgres-dev

# Remove (deletes data)
docker-compose down postgres-dev -v

# View logs
docker-compose logs -f postgres-dev

# Connect to PostgreSQL CLI
docker exec -it exercises-postgres-dev psql -U postgres -d exercises_dev
```

### Production Database

```bash
# Start with custom password
DB_PASSWORD=securepass docker-compose up -d postgres-prod

# Connect
docker exec -it exercises-postgres-prod psql -U postgres -d exercises_prod
```

## Manual PostgreSQL Setup

If you prefer installing PostgreSQL directly:

### 1. Install PostgreSQL

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

**macOS:**
```bash
brew install postgresql@16
brew services start postgresql@16
```

### 2. Create Databases

```bash
# Connect to PostgreSQL
sudo -u postgres psql

# Create databases
CREATE DATABASE exercises_dev;
CREATE DATABASE exercises_prod;

# (Optional) Create dedicated user
CREATE USER exercises_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE exercises_dev TO exercises_user;
GRANT ALL PRIVILEGES ON DATABASE exercises_prod TO exercises_user;

# Exit
\q
```

### 3. Update Configuration

Edit `application-dev.properties` or `application-prod.properties` with your credentials.

## Environment Variables Reference

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SPRING_PROFILES_ACTIVE` | Active profile (local/dev/prod) | `dev` | No |
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/exercises_prod` | Prod only |
| `DB_USERNAME` | Database username | `postgres` | Prod only |
| `DB_PASSWORD` | Database password | `postgres` | Prod only |
| `SERVER_PORT` | Application port | `8080` | No |

## Testing

Tests always use H2 in-memory database (see `src/test/resources/application.properties`):

```bash
./mvnw test
```

## Production Checklist

- [ ] Use strong database password
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate`
- [ ] Disable `data.sql` initialization (`spring.sql.init.mode=never`)
- [ ] Use environment variables for credentials
- [ ] Enable database migrations (Flyway/Liquibase)
- [ ] Configure connection pool sizing
- [ ] Set up database backups
- [ ] Configure SSL for database connections
- [ ] Review logging levels
- [ ] Enable actuator health checks
- [ ] Set up monitoring (Prometheus, Grafana)

## Troubleshooting

### Connection Refused Error
```
org.postgresql.util.PSQLException: Connection refused
```
**Solution**: Ensure PostgreSQL is running:
```bash
docker-compose ps
# or
sudo systemctl status postgresql
```

### Authentication Failed
```
org.postgresql.util.PSQLException: FATAL: password authentication failed
```
**Solution**: Check credentials in properties file or environment variables.

### Schema Validation Failed (Production)
```
Schema-validation: missing table [users]
```
**Solution**: 
1. In dev, run with `ddl-auto=update` to create schema
2. Export schema and apply to production
3. Or use database migration tools (recommended)

### Port Already in Use
```
Port 5432 is already allocated
```
**Solution**: Stop existing PostgreSQL or change port in `docker-compose.yml`

## Migration to Production

For production, it's recommended to:

1. **Add Flyway or Liquibase** for database migrations
2. **Disable automatic schema generation** (`ddl-auto=validate`)
3. **Use proper schema versioning**
4. **Test migrations in staging environment**

Example Flyway setup:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

Create `src/main/resources/db/migration/V1__Initial_Schema.sql` with your DDL.

## Additional Resources

- [Spring Boot Profiles Documentation](https://docs.spring.io/spring-boot/reference/features/profiles.html)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
