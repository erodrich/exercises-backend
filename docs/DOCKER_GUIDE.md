# Docker Deployment Guide

Complete guide for building and running the Exercise Backend application with Docker.

## Overview

The application is containerized using **multi-stage Docker builds** for optimal image size and security. Docker Compose orchestrates the application with PostgreSQL databases for both dev and prod environments.

## Architecture

```
┌─────────────────────┐
│   app-dev:8080      │
│  (exercises-backend)│
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  postgres-dev:5432  │
│   (exercises_dev)   │
└─────────────────────┘

┌─────────────────────┐
│   app-prod:8081     │
│  (exercises-backend)│
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  postgres-prod:5432 │
│   (exercises_prod)  │
└─────────────────────┘
```

## Prerequisites

- Docker 20.10+
- Docker Compose 2.0+
- 2GB RAM minimum
- 5GB disk space

## Quick Start

### Option 1: Using Helper Scripts (Recommended)

#### Development Environment
```bash
# Build and run
./docker-build.sh dev
./docker-run-dev.sh

# Access application
open http://localhost:8080/exercise-logging/swagger-ui/index.html
```

#### Production Environment
```bash
# Set secure password
export DB_PASSWORD=your_secure_password

# Build and run
./docker-build.sh prod
./docker-run-prod.sh

# Access application
open http://localhost:8081/exercise-logging/swagger-ui/index.html
```

### Option 2: Using Docker Compose Directly

#### Development
```bash
# Start services
docker-compose up -d postgres-dev app-dev

# View logs
docker-compose logs -f app-dev

# Stop services
docker-compose down
```

#### Production
```bash
# Set password and start
export DB_PASSWORD=secure_password
docker-compose up -d postgres-prod app-prod

# View logs
docker-compose logs -f app-prod

# Stop services
docker-compose down
```

### Option 3: Manual Docker Commands

#### Build Image
```bash
docker build -t exercises-backend:latest .
```

#### Run with External Database
```bash
docker run -d \
  --name exercises-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://your-host:5432/exercises_prod \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=your_password \
  exercises-backend:latest
```

## Docker Image Details

### Multi-Stage Build

The Dockerfile uses a **two-stage build**:

1. **Builder Stage** (maven:3.9.6-eclipse-temurin-21-alpine)
   - Downloads dependencies
   - Compiles source code
   - Packages application JAR
   - ~500MB intermediate image

2. **Runtime Stage** (eclipse-temurin:21-jre-alpine)
   - JRE only (no build tools)
   - Non-root user (`spring:spring`)
   - Final image: ~300MB
   - Security optimized

### Image Features

✅ **Lightweight**: Alpine Linux base (~300MB final image)
✅ **Secure**: Non-root user execution
✅ **Optimized**: Dependency layer caching
✅ **Health Checks**: Built-in container health monitoring
✅ **Multi-profile**: Supports dev/prod via environment variables

## Docker Compose Services

### Service: `postgres-dev`
- **Image**: postgres:16-alpine
- **Port**: 5432 (host) → 5432 (container)
- **Database**: exercises_dev
- **Credentials**: postgres/postgres
- **Volume**: postgres_dev_data
- **Health Check**: pg_isready

### Service: `postgres-prod`
- **Image**: postgres:16-alpine
- **Port**: 5433 (host) → 5432 (container)
- **Database**: exercises_prod
- **Credentials**: postgres/${DB_PASSWORD}
- **Volume**: postgres_prod_data
- **Health Check**: pg_isready

### Service: `app-dev`
- **Image**: exercises-backend:dev
- **Port**: 8080 (host) → 8080 (container)
- **Profile**: dev
- **Database**: postgres-dev:5432
- **Depends**: postgres-dev (healthy)
- **Restart**: unless-stopped
- **Health Check**: /actuator/health

### Service: `app-prod`
- **Image**: exercises-backend:prod
- **Port**: 8081 (host) → 8080 (container)
- **Profile**: prod
- **Database**: postgres-prod:5432
- **Depends**: postgres-prod (healthy)
- **Restart**: unless-stopped
- **Health Check**: /actuator/health

## Environment Variables

### Application Container

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | dev | Profile (dev/prod) |
| `DB_URL` | - | PostgreSQL JDBC URL |
| `DB_USERNAME` | postgres | Database username |
| `DB_PASSWORD` | postgres | Database password |
| `SERVER_PORT` | 8080 | Application port |

### Database Container

| Variable | Default | Description |
|----------|---------|-------------|
| `POSTGRES_DB` | - | Database name |
| `POSTGRES_USER` | postgres | Admin username |
| `POSTGRES_PASSWORD` | postgres | Admin password |

## Health Checks

### Application Health
```bash
# Dev environment
curl http://localhost:8080/exercise-logging/actuator/health

# Prod environment
curl http://localhost:8081/exercise-logging/actuator/health
```

### Container Health Status
```bash
docker-compose ps
```

Healthy containers show: `healthy` in STATUS column

## Common Commands

### Build & Start
```bash
# Build image
docker-compose build app-dev

# Start specific service
docker-compose up -d app-dev

# Start all services
docker-compose up -d

# Rebuild and start
docker-compose up -d --build app-dev
```

### View Logs
```bash
# Follow logs
docker-compose logs -f app-dev

# Last 100 lines
docker-compose logs --tail=100 app-dev

# All services
docker-compose logs -f
```

### Execute Commands
```bash
# Shell into application container
docker-compose exec app-dev sh

# Check Java version
docker-compose exec app-dev java -version

# Shell into database
docker-compose exec postgres-dev psql -U postgres -d exercises_dev
```

### Stop & Remove
```bash
# Stop services (preserve data)
docker-compose stop

# Stop and remove containers (preserve data)
docker-compose down

# Remove containers and volumes (DELETE DATA!)
docker-compose down -v

# Remove specific service
docker-compose stop app-dev
docker-compose rm -f app-dev
```

### Manage Data
```bash
# Backup database
docker-compose exec postgres-dev pg_dump -U postgres exercises_dev > backup.sql

# Restore database
docker-compose exec -T postgres-dev psql -U postgres exercises_dev < backup.sql

# List volumes
docker volume ls

# Inspect volume
docker volume inspect exercises-backend_postgres_dev_data
```

## Troubleshooting

### Container Won't Start

**Check logs:**
```bash
docker-compose logs app-dev
```

**Common issues:**
- Database not ready: Wait for health check
- Port already in use: Change port in docker-compose.yml
- Out of memory: Increase Docker memory limit

### Database Connection Error

**Error:**
```
org.postgresql.util.PSQLException: Connection refused
```

**Solutions:**
1. Check database is healthy:
   ```bash
   docker-compose ps postgres-dev
   ```

2. Verify network connectivity:
   ```bash
   docker-compose exec app-dev ping postgres-dev
   ```

3. Check database logs:
   ```bash
   docker-compose logs postgres-dev
   ```

### Application Not Responding

**Check health:**
```bash
curl http://localhost:8080/exercise-logging/actuator/health
```

**Check if running:**
```bash
docker-compose ps app-dev
```

**Restart service:**
```bash
docker-compose restart app-dev
```

### Out of Disk Space

**Check disk usage:**
```bash
docker system df
```

**Clean up:**
```bash
# Remove unused images
docker image prune -a

# Remove unused volumes
docker volume prune

# Remove everything unused
docker system prune -a --volumes
```

### Port Already in Use

**Error:**
```
Error starting userland proxy: listen tcp4 0.0.0.0:8080: bind: address already in use
```

**Solution - Find and kill process:**
```bash
# Linux/Mac
lsof -ti:8080 | xargs kill -9

# Or change port in docker-compose.yml
ports:
  - "8082:8080"  # Use different host port
```

## Performance Tuning

### Memory Limits
```yaml
app-dev:
  # ... other config
  deploy:
    resources:
      limits:
        memory: 1G
      reservations:
        memory: 512M
```

### JVM Options
```yaml
app-dev:
  environment:
    JAVA_OPTS: "-Xmx512m -Xms256m -XX:+UseG1GC"
```

### Connection Pool
```yaml
app-dev:
  environment:
    SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE: 20
    SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE: 5
```

## Production Deployment

### Security Checklist

- [x] Non-root container user
- [x] Environment variable for passwords
- [ ] Use secrets management (Docker Swarm/Kubernetes)
- [ ] Enable SSL/TLS for database
- [ ] Set up reverse proxy (Nginx/Traefik)
- [ ] Implement rate limiting
- [ ] Configure firewall rules
- [ ] Enable audit logging
- [ ] Regular security updates

### Recommended Setup

1. **Use Docker Secrets** (Swarm mode)
```bash
echo "secure_password" | docker secret create db_password -
```

2. **Enable SSL** for PostgreSQL
```yaml
postgres-prod:
  command: >
    -c ssl=on
    -c ssl_cert_file=/etc/ssl/certs/server.crt
    -c ssl_key_file=/etc/ssl/private/server.key
```

3. **Add Reverse Proxy**
```yaml
nginx:
  image: nginx:alpine
  ports:
    - "80:80"
    - "443:443"
  volumes:
    - ./nginx.conf:/etc/nginx/nginx.conf
```

4. **Use Docker Compose Override**
```bash
# Production overrides
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## Monitoring

### Container Stats
```bash
# Real-time stats
docker stats

# Specific container
docker stats exercises-backend-dev
```

### Logs with Timestamps
```bash
docker-compose logs -f -t app-dev
```

### Export Metrics
```bash
# Access Prometheus metrics (if enabled)
curl http://localhost:8080/exercise-logging/actuator/prometheus
```

## CI/CD Integration

### GitHub Actions Example
```yaml
- name: Build Docker Image
  run: docker build -t exercises-backend:${{ github.sha }} .

- name: Push to Registry
  run: |
    docker tag exercises-backend:${{ github.sha }} registry.example.com/exercises-backend:latest
    docker push registry.example.com/exercises-backend:latest
```

### GitLab CI Example
```yaml
build:
  stage: build
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA .
    - docker push $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA
```

## Kubernetes Deployment

### Convert to K8s
```bash
# Generate Kubernetes manifests
kompose convert -f docker-compose.yml
```

### Deploy to K8s
```bash
kubectl apply -f postgres-deployment.yaml
kubectl apply -f app-deployment.yaml
```

## Additional Resources

- **Dockerfile**: Multi-stage build configuration
- **docker-compose.yml**: Service orchestration
- **docker-build.sh**: Build helper script
- **docker-run-dev.sh**: Dev environment launcher
- **docker-run-prod.sh**: Prod environment launcher
- **.dockerignore**: Build context exclusions

## Support

For issues or questions:
1. Check container logs: `docker-compose logs`
2. Verify health checks: `docker-compose ps`
3. Review this guide's troubleshooting section
4. Check application logs in container

## Summary

The Exercise Backend is fully containerized with:
- ✅ Multi-stage Docker builds
- ✅ Docker Compose orchestration
- ✅ Separate dev/prod environments
- ✅ Health check monitoring
- ✅ Persistent data volumes
- ✅ Security best practices
- ✅ Helper scripts for easy deployment
