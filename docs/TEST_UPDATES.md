# Backend Test Updates for Authentication Refactor

## Overview
This document describes all test updates made to support the new authentication system with JWT tokens, BCrypt password hashing, and Spring Security.

## Test Files Modified

### 1. UserServiceTest.java
**Location:** `src/test/java/com/erodrich/exercises/user/service/UserServiceTest.java`

**Changes:**
- Added `@Mock` for `PasswordEncoder` and `JwtTokenProvider`
- Updated `register_withValidRequest_shouldReturnAuthResponse()`:
  - Now returns `AuthResponse` instead of `UserDTO`
  - Mocks `passwordEncoder.encode()` to return hashed password
  - Mocks `jwtTokenProvider.generateToken()` to return JWT token
  - Verifies password encoding and token generation
  - Updated UserDTO constructor (String id instead of Long, no createdAt)
  
- Updated `register_withExistingEmail_shouldThrowException()`:
  - Uses `findByEmail()` instead of `findAll()`
  
- Updated `login_withValidCredentials_shouldReturnAuthResponse()`:
  - Changed to email-based login (was username-based)
  - Returns `AuthResponse` instead of `UserDTO`
  - Uses `findByEmail()` instead of `findByUsernameAndPassword()`
  - Mocks `passwordEncoder.matches()` for password validation
  - Verifies BCrypt password matching
  
- Split `login_withInvalidCredentials_shouldThrowException()` into two tests:
  - `login_withInvalidEmail_shouldThrowException()`: Tests invalid email
  - `login_withInvalidPassword_shouldThrowException()`: Tests wrong password with BCrypt validation
  
- Updated `getUserByUsername_whenUserExists_shouldReturnUserDTO()`:
  - Updated UserDTO constructor to match new structure

**Test Count:** 8 tests (was 7, added 1 for password validation)

### 2. UserMapperTest.java
**Location:** `src/test/java/com/erodrich/exercises/user/mapper/UserMapperTest.java`

**Changes:**
- Updated `toDTO_withValidEntity_shouldMapCorrectly()`:
  - Changed id assertion from `Long` (1L) to `String` ("1")
  - Removed `createdAt` assertion (field removed from DTO)
  
- Replaced `toDTO_withNullCreatedAt_shouldHandleGracefully()` with:
  - `toDTO_withDifferentIdTypes_shouldConvertToString()`: Tests Long to String conversion

**Test Count:** 5 tests (unchanged)

### 3. UserRepositoryTest.java
**Location:** `src/test/java/com/erodrich/exercises/user/repository/UserRepositoryTest.java`

**Changes:**
- Removed three tests for deprecated `findByUsernameAndPassword()`:
  - `findByUsernameAndPassword_whenCredentialsMatch_shouldReturnUser()`
  - `findByUsernameAndPassword_whenPasswordWrong_shouldReturnEmpty()`
  - `findByUsernameAndPassword_whenUsernameWrong_shouldReturnEmpty()`
  
- Added two tests for new `findByEmail()` method:
  - `findByEmail_whenUserExists_shouldReturnUser()`: Tests successful email lookup
  - `findByEmail_whenUserDoesNotExist_shouldReturnEmpty()`: Tests non-existent email

**Test Count:** 5 tests (was 7, removed 3, added 2)

## New Test Files Created

### 4. JwtTokenProviderTest.java
**Location:** `src/test/java/com/erodrich/exercises/security/JwtTokenProviderTest.java`

**Purpose:** Tests JWT token generation, validation, and parsing

**Test Cases:**
1. `generateToken_withValidEmail_shouldReturnToken()` - Verifies token generation
2. `getEmailFromToken_withValidToken_shouldReturnEmail()` - Tests email extraction
3. `validateToken_withValidToken_shouldReturnTrue()` - Tests valid token validation
4. `validateToken_withInvalidToken_shouldReturnFalse()` - Tests invalid token rejection
5. `validateToken_withExpiredToken_shouldReturnFalse()` - Tests expired token detection
6. `validateToken_withNullToken_shouldReturnFalse()` - Tests null handling
7. `validateToken_withEmptyToken_shouldReturnFalse()` - Tests empty string handling
8. `generateToken_withDifferentEmails_shouldGenerateDifferentTokens()` - Tests uniqueness
9. `tokenContainsCorrectClaims()` - Verifies JWT claims structure

**Test Count:** 9 tests

**Key Features:**
- Uses `ReflectionTestUtils` to set private JwtProperties fields
- Uses 256-bit secret key for HS256 algorithm compliance
- Manually parses JWT to verify claims
- Tests edge cases (null, empty, expired tokens)

### 5. CustomUserDetailsServiceTest.java
**Location:** `src/test/java/com/erodrich/exercises/security/CustomUserDetailsServiceTest.java`

**Purpose:** Tests Spring Security UserDetailsService implementation

**Test Cases:**
1. `loadUserByUsername_whenUserExists_shouldReturnUserDetails()` - Tests successful user loading
2. `loadUserByUsername_whenUserDoesNotExist_shouldThrowException()` - Tests missing user handling
3. `loadUserByUsername_withDifferentUsers_shouldReturnCorrectDetails()` - Tests multiple users

**Test Count:** 3 tests

**Key Features:**
- Mocks `UserRepository.findByEmail()`
- Verifies UserDetails properties (authorities, account status, etc.)
- Tests `UsernameNotFoundException` for missing users

### 6. JwtAuthenticationFilterTest.java
**Location:** `src/test/java/com/erodrich/exercises/security/JwtAuthenticationFilterTest.java`

**Purpose:** Tests JWT authentication filter in request processing

**Test Cases:**
1. `doFilterInternal_withValidToken_shouldSetAuthentication()` - Tests valid JWT authentication
2. `doFilterInternal_withInvalidToken_shouldNotSetAuthentication()` - Tests invalid token rejection
3. `doFilterInternal_withNoAuthorizationHeader_shouldNotSetAuthentication()` - Tests missing header
4. `doFilterInternal_withInvalidAuthorizationHeader_shouldNotSetAuthentication()` - Tests malformed header
5. `doFilterInternal_withEmptyBearerToken_shouldNotSetAuthentication()` - Tests empty token
6. `doFilterInternal_whenExceptionOccurs_shouldContinueFilterChain()` - Tests error handling

**Test Count:** 6 tests

**Key Features:**
- Mocks HttpServletRequest, HttpServletResponse, FilterChain
- Tests SecurityContextHolder authentication setting
- Verifies filter chain continuation in all scenarios
- Tests various authorization header formats
- Tests exception handling and graceful degradation

## Test Summary

### Total Test Count
- **Modified Files:** 3 files (UserServiceTest, UserMapperTest, UserRepositoryTest)
- **New Files:** 3 files (JwtTokenProviderTest, CustomUserDetailsServiceTest, JwtAuthenticationFilterTest)
- **Total Tests:** 31 tests
  - UserServiceTest: 8 tests
  - UserMapperTest: 5 tests
  - UserRepositoryTest: 5 tests
  - JwtTokenProviderTest: 9 tests
  - CustomUserDetailsServiceTest: 3 tests
  - JwtAuthenticationFilterTest: 6 tests

### Test Coverage Areas
✅ User registration with BCrypt password hashing  
✅ Email-based login with password verification  
✅ JWT token generation and validation  
✅ Token expiration handling  
✅ Spring Security UserDetailsService integration  
✅ JWT authentication filter request processing  
✅ Error handling and edge cases  
✅ Repository email lookup  
✅ DTO mapping with new structure  

## Running the Tests

### Prerequisites
- Java 21
- Maven 3.9+

### Commands
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=UserServiceTest

# Run with coverage
./mvnw test jacoco:report

# Run security tests only
./mvnw test -Dtest="*security*"
```

### Expected Results
All 31 tests should pass when:
- Backend compiles successfully with Java 21
- All dependencies are resolved (Spring Security, JWT, etc.)
- Test application context starts correctly

## Integration with Existing Tests

### Unchanged Tests
The following test files remain unchanged and should continue to pass:
- `ExerciseMapperTest.java`
- `ExerciseRepositoryTest.java`
- `ExerciseServiceTest.java`
- `ExerciseLogMapperTest.java`
- `ExerciseLogRepositoryTest.java`
- `ExerciseSetRepositoryTest.java`
- `ExerciseLogServiceTest.java`
- `ExercisesBackendApplicationTests.java`

### Test Application Properties
Update `src/test/resources/application.properties` if needed:
```properties
# JWT properties for testing
jwt.secret=myVerySecureSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm
jwt.expiration=86400000

# H2 in-memory database
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

## Next Steps

1. **Compile with Java 21:**
   ```bash
   ./mvnw clean compile
   ```

2. **Run Tests:**
   ```bash
   ./mvnw test
   ```

3. **Fix Any Failures:** Review test output and adjust mocks/assertions as needed

4. **Integration Testing:** Test with running application:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

5. **Frontend Integration:** Test authentication endpoints with frontend application

## Notes

- All tests use Mockito for dependency mocking
- Spring Boot Test autoconfiguration handles context loading
- Security tests verify Spring Security integration
- JWT tests use actual JWT library for token manipulation
- Tests follow AAA pattern (Arrange, Act, Assert)
- Clear test names describe expected behavior

## Troubleshooting

### Common Issues

**Issue:** JWT secret key too short  
**Solution:** Ensure 256-bit (32+ character) secret in test properties

**Issue:** Spring Security context not clearing between tests  
**Solution:** Added `SecurityContextHolder.clearContext()` in `@BeforeEach`

**Issue:** PasswordEncoder not found  
**Solution:** Mock PasswordEncoder in tests, configured in SecurityConfig for runtime

**Issue:** Test compilation errors with Java 11  
**Solution:** Requires Java 21+ for Spring Boot 4.0.0
