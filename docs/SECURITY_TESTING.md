# Security Testing Guide

## Overview
This guide provides comprehensive testing procedures for the authentication and security features of the Exercises Backend application.

## Table of Contents
1. [Unit Tests](#unit-tests)
2. [Integration Tests](#integration-tests)
3. [Manual Testing](#manual-testing)
4. [Security Validation](#security-validation)
5. [Common Test Scenarios](#common-test-scenarios)

---

## Unit Tests

### Running Unit Tests

```bash
# All tests
./mvnw test

# Security tests only
./mvnw test -Dtest="*security*"

# User service tests
./mvnw test -Dtest="UserServiceTest"

# With detailed output
./mvnw test -X
```

### Test Coverage

#### Authentication Flow Tests (UserServiceTest)
- ✅ User registration with password hashing
- ✅ Duplicate username prevention
- ✅ Duplicate email prevention
- ✅ Email-based login
- ✅ Password validation with BCrypt
- ✅ Invalid email handling
- ✅ Invalid password handling
- ✅ JWT token generation on registration
- ✅ JWT token generation on login

#### JWT Token Tests (JwtTokenProviderTest)
- ✅ Token generation with valid email
- ✅ Email extraction from token
- ✅ Token validation
- ✅ Invalid token rejection
- ✅ Expired token handling
- ✅ Null/empty token handling
- ✅ Token uniqueness per user
- ✅ JWT claims verification

#### User Loading Tests (CustomUserDetailsServiceTest)
- ✅ Load user by email
- ✅ User not found exception
- ✅ Multiple user handling

#### Filter Tests (JwtAuthenticationFilterTest)
- ✅ Valid JWT authentication
- ✅ Invalid token rejection
- ✅ Missing authorization header
- ✅ Malformed authorization header
- ✅ Empty bearer token
- ✅ Exception handling

---

## Integration Tests

### Prerequisites
- Java 21 installed
- Backend running on `http://localhost:8080`
- No authentication required for test endpoints

### Starting the Backend

```bash
# Development mode
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Production mode
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Test Endpoints

#### 1. Health Check (Public)
```bash
curl http://localhost:8080/api/health
```
**Expected:** HTTP 200, "healthy"

#### 2. User Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "user": {
    "id": "1",
    "username": "testuser",
    "email": "test@example.com"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Expected:** HTTP 200, AuthResponse with user and JWT token

#### 3. User Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Expected Response:** Same as registration

**Expected:** HTTP 200, AuthResponse with user and JWT token

#### 4. Protected Endpoint (Requires JWT)
```bash
# Save token from login/register response
TOKEN="your.jwt.token.here"

curl http://localhost:8080/api/exercises \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:** HTTP 200, exercises list

#### 5. Invalid Token Test
```bash
curl http://localhost:8080/api/exercises \
  -H "Authorization: Bearer invalid.token"
```

**Expected:** HTTP 401 Unauthorized

#### 6. No Token Test
```bash
curl http://localhost:8080/api/exercises
```

**Expected:** HTTP 401 Unauthorized

---

## Manual Testing

### Test Case 1: Complete Registration Flow

**Steps:**
1. Register new user
2. Verify password is hashed (check database)
3. Verify JWT token received
4. Verify token contains correct email
5. Use token to access protected endpoint

**Validation Points:**
- Password in database should be BCrypt hash (starts with `$2a$` or `$2b$`)
- Token should have 3 parts separated by dots (header.payload.signature)
- Token should be valid for 24 hours
- Protected endpoints should be accessible with valid token

### Test Case 2: Complete Login Flow

**Steps:**
1. Register a user
2. Log out (client-side token removal)
3. Log in with same credentials
4. Verify new JWT token received
5. Use new token to access protected endpoint

**Validation Points:**
- Login with correct email/password succeeds
- Login with wrong email fails (401)
- Login with wrong password fails (401)
- New token is different from registration token
- Both tokens are valid

### Test Case 3: Token Expiration

**Steps:**
1. Generate token with short expiration (modify jwt.expiration)
2. Wait for token to expire
3. Attempt to use expired token

**Expected:** HTTP 401 Unauthorized

### Test Case 4: Duplicate User Prevention

**Steps:**
1. Register user with username "testuser" and email "test@example.com"
2. Attempt to register another user with same username
3. Attempt to register another user with same email

**Expected:** 
- Step 2: HTTP 400, "Username already exists"
- Step 3: HTTP 400, "Email already exists"

### Test Case 5: Input Validation

**Test 5a: Invalid Email Format**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "invalid-email",
    "password": "password123"
  }'
```
**Expected:** HTTP 400, validation error

**Test 5b: Short Password**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "short"
  }'
```
**Expected:** HTTP 400, "Password must be at least 8 characters"

**Test 5c: Invalid Username**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ab",
    "email": "test@example.com",
    "password": "password123"
  }'
```
**Expected:** HTTP 400, "Username must be between 3 and 20 characters"

---

## Security Validation

### Password Security Checklist

- [ ] Passwords stored as BCrypt hashes, not plain text
- [ ] BCrypt work factor is at least 10
- [ ] Plain text passwords never logged
- [ ] Password validation on login uses `passwordEncoder.matches()`
- [ ] No password returned in API responses

**Verification:**
```sql
-- Connect to database and check
SELECT id, username, password FROM users LIMIT 1;
-- Password should look like: $2a$10$abcdef...
```

### JWT Security Checklist

- [ ] JWT secret is at least 256 bits (32 characters)
- [ ] JWT secret stored in environment variables, not code
- [ ] Tokens include expiration time
- [ ] Tokens validated on every protected request
- [ ] Invalid/expired tokens rejected with 401
- [ ] Token signature verified on each request

**Token Inspection:**
```bash
# Decode JWT (header and payload only, signature not verified)
TOKEN="your.jwt.token"
echo $TOKEN | cut -d. -f2 | base64 -d 2>/dev/null | jq
```

**Expected Payload:**
```json
{
  "sub": "test@example.com",
  "iat": 1702867200,
  "exp": 1702953600
}
```

### Spring Security Checklist

- [ ] Public endpoints allow unauthenticated access
- [ ] Protected endpoints require authentication
- [ ] CSRF disabled for stateless JWT authentication
- [ ] CORS configured correctly for frontend
- [ ] Session management set to STATELESS
- [ ] Authentication filter registered in correct order

**Test Public Endpoints:**
```bash
# Should succeed without token
curl http://localhost:8080/api/auth/login
curl http://localhost:8080/api/auth/register
curl http://localhost:8080/api/health
```

**Test Protected Endpoints:**
```bash
# Should fail without token (401)
curl http://localhost:8080/api/exercises
curl http://localhost:8080/api/exercise-logs
```

### HTTPS Enforcement (Production)

- [ ] Application served over HTTPS in production
- [ ] HTTP redirects to HTTPS
- [ ] Secure flag set on cookies (if used)
- [ ] HSTS header enabled

---

## Common Test Scenarios

### Scenario 1: Frontend-Backend Integration

**Setup:**
1. Start backend: `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`
2. Start frontend: `cd exercises-frontend && npm start`
3. Navigate to `http://localhost:3000`

**Test Flow:**
1. Click "Register" and create account
2. Verify redirect to home page
3. Check browser localStorage for token
4. Create exercise log (requires authentication)
5. Log out (token removed)
6. Verify redirect to login page
7. Log in with same credentials
8. Verify access to exercise logs

### Scenario 2: Token Refresh (Future Enhancement)

Currently, tokens expire after 24 hours. Future implementation should include:
- Refresh token endpoint
- Short-lived access tokens (15 min)
- Long-lived refresh tokens (7 days)
- Automatic token refresh on 401 response

### Scenario 3: Multiple Concurrent Users

**Test:**
1. Register User A
2. Register User B
3. Log in as User A, save token A
4. Log in as User B, save token B
5. User A creates exercise log with token A
6. User B creates exercise log with token B
7. Verify User A cannot see User B's logs

**Expected:** Each user has isolated data

### Scenario 4: Brute Force Protection (Future Enhancement)

Currently not implemented. Future security should include:
- Rate limiting on login endpoint
- Account lockout after N failed attempts
- CAPTCHA after repeated failures

---

## Performance Testing

### Load Test Registration Endpoint

```bash
# Using Apache Bench (ab)
ab -n 100 -c 10 -p register.json -T application/json \
  http://localhost:8080/api/auth/register
```

**Expected:** < 500ms average response time

### Load Test Login Endpoint

```bash
ab -n 100 -c 10 -p login.json -T application/json \
  http://localhost:8080/api/auth/login
```

**Expected:** < 300ms average response time (faster than registration)

### Stress Test Protected Endpoints

```bash
# Create token-header.txt with:
# Authorization: Bearer your.token.here

ab -n 1000 -c 50 -H @token-header.txt \
  http://localhost:8080/api/exercises
```

**Expected:** < 200ms average response time

---

## Troubleshooting

### Issue: Tests Fail with "JWT secret too short"

**Solution:** 
Update test properties with 256-bit secret:
```properties
jwt.secret=myVerySecureSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm
```

### Issue: Spring Security blocks all requests

**Solution:**
Check SecurityConfig.java:
- Verify public endpoints in `requestMatchers().permitAll()`
- Check filter order
- Verify CSRF is disabled for stateless API

### Issue: Password validation always fails

**Solution:**
- Ensure PasswordEncoder bean is configured
- Verify BCrypt work factor (default 10)
- Check password stored with correct hash format

### Issue: Token validation fails intermittently

**Solution:**
- Verify server time is synchronized (NTP)
- Check token expiration time
- Ensure secret key is consistent across requests

### Issue: CORS errors in browser

**Solution:**
Verify CorsConfig.java:
```java
.allowedOrigins("http://localhost:3000")
.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
.allowedHeaders("*")
.allowCredentials(true)
.exposedHeaders("Authorization")
```

---

## Security Best Practices

### ✅ Do's
- Always hash passwords with BCrypt
- Use strong JWT secrets (256+ bits)
- Set appropriate token expiration times
- Validate all user inputs
- Use HTTPS in production
- Log security events (failed logins, etc.)
- Keep dependencies updated

### ❌ Don'ts
- Don't store plain text passwords
- Don't log sensitive data (passwords, tokens)
- Don't expose stack traces in production
- Don't use weak secrets or default keys
- Don't skip input validation
- Don't trust client-side validation alone

---

## Next Steps

1. **Implement Rate Limiting:** Prevent brute force attacks
2. **Add Refresh Tokens:** Improve security with short-lived access tokens
3. **Add Account Lockout:** Protect against credential stuffing
4. **Implement 2FA:** Add two-factor authentication option
5. **Add Security Logging:** Log all authentication events
6. **Security Audit:** Regular third-party security review

---

## References

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [JWT Best Practices](https://datatracker.ietf.org/doc/html/rfc8725)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- [BCrypt Algorithm](https://en.wikipedia.org/wiki/Bcrypt)
