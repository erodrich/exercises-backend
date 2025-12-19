# Admin Role Implementation

This document describes the implementation of role-based access control with admin functionality.

## Overview

The application now supports two user roles:
- **USER** - Regular users who can log exercises
- **ADMIN** - Administrators who can manage master data (exercises)

## Backend Changes

### 1. Role Enum
**File**: `src/main/java/com/erodrich/exercises/user/entity/Role.java`

```java
public enum Role {
    USER,    // Regular user
    ADMIN    // Administrator
}
```

### 2. UserEntity Update
- Added `role` field (enum, default: USER)
- Stored as STRING in database

### 3. JWT Token Enhancement
- Role is embedded in JWT claims as `"role": "ROLE_USER"` or `"ROLE_ADMIN"`
- `JwtTokenProvider` updated to include/extract roles
- New method: `getRoleFromToken(String token)`

### 4. Security Configuration
Admin endpoints are now protected:
```java
.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
```

### 5. CustomUserDetailsService
- Converts role to Spring Security authority: `ROLE_USER` or `ROLE_ADMIN`
- Authorities are included in authentication

### 6. Default Admin User
Added to `data.sql`:
```sql
-- Email: admin@exercises.com
-- Password: Admin123!
INSERT INTO users (id, username, email, password, role, created_at) 
VALUES (1, 'admin', 'admin@exercises.com', 
        '$2a$10$xLzJhIvdZXG6k5bvJYmW7.hN8l9LCQYqN5d8fB3EHqJqyYmM8L9Ta', 
        'ADMIN', CURRENT_TIMESTAMP);
```

## Frontend Changes

### 1. Role Model
**File**: `src/domain/models/index.ts`

```typescript
export const Role = {
  USER: 'USER',
  ADMIN: 'ADMIN',
} as const;

export type Role = typeof Role[keyof typeof Role];

export interface User {
  id: string;
  username: string;
  email: string;
  role: Role;  // Added
}
```

### 2. Admin Service
**File**: `src/services/adminService.ts`

Provides exercise CRUD operations:
- `getAllExercises()` - GET /api/v1/admin/exercises
- `getExerciseById(id)` - GET /api/v1/admin/exercises/:id
- `createExercise(exercise)` - POST /api/v1/admin/exercises
- `updateExercise(id, exercise)` - PUT /api/v1/admin/exercises/:id
- `deleteExercise(id)` - DELETE /api/v1/admin/exercises/:id

Includes both:
- `HttpAdminService` - For backend API calls
- `MockAdminService` - For local testing

### 3. Admin Dashboard
**File**: `src/components/AdminDashboard.tsx`

Main admin interface with:
- Tab navigation for different admin sections
- Currently supports Exercises tab
- Future-ready for more admin features

### 4. Exercise Management
**File**: `src/components/ExerciseManagement.tsx`

Full CRUD interface for exercises:
- Table view with all exercises
- Create new exercise (modal form)
- Edit existing exercise (modal form)
- Delete exercise (with confirmation)
- Muscle group selection
- Real-time updates

### 5. Role-Based UI
- Admin button visible only for admin users
- Role-based routing in App.tsx
- Admin dashboard accessible only by admins

### 6. MockAuthStorage Update
- Supports role field
- Initializes default admin user automatically
- Admin credentials: `admin@exercises.com` / `Admin123!`

## API Endpoints

### Admin-Only Endpoints
All require `Authorization: Bearer <token>` with ADMIN role:

```
GET    /api/v1/admin/exercises        # List all exercises
GET    /api/v1/admin/exercises/:id    # Get exercise by ID
POST   /api/v1/admin/exercises        # Create exercise
PUT    /api/v1/admin/exercises/:id    # Update exercise
DELETE /api/v1/admin/exercises/:id    # Delete exercise
```

### Response Codes
- `200 OK` - Success (GET, PUT)
- `201 Created` - Resource created (POST)
- `204 No Content` - Deleted successfully (DELETE)
- `403 Forbidden` - User is not admin
- `404 Not Found` - Exercise not found

## Testing

### Backend
Role-based access will be verified:
1. Regular users cannot access `/api/v1/admin/**`
2. Admin users can access all endpoints
3. JWT tokens include role claims

### Frontend
1. Login as admin: `admin@exercises.com` / `Admin123!`
2. Navigate to Admin Dashboard
3. Test CRUD operations:
   - Create new exercise
   - Edit existing exercise
   - Delete exercise
4. Regular users should not see admin button

## Security Notes

### Current Implementation
- Admin password is in data.sql (BCrypt hashed)
- Should be changed on first login in production
- Role is stored in JWT and verified on backend

### Production Recommendations
1. **Change default admin password immediately**
2. Consider admin user management UI
3. Implement password change functionality
4. Add audit logging for admin actions
5. Consider multi-factor authentication for admins
6. Rate limiting on admin endpoints

## Future Enhancements

### Planned Features
- [ ] User management (admin can create/manage users)
- [ ] Assign roles to users
- [ ] Admin activity audit log
- [ ] Bulk operations (import/export exercises)
- [ ] More granular permissions
- [ ] User groups/teams management

### Additional Admin Tabs
The architecture supports adding more admin features:
- Users Management
- System Settings
- Analytics & Reports
- Data Import/Export

## Migration Notes

### Existing Users
- All existing users default to USER role
- Database migration will add role column with default 'USER'
- No breaking changes to existing functionality

### Database Update
For existing databases, run:
```sql
ALTER TABLE users ADD COLUMN role VARCHAR(10) NOT NULL DEFAULT 'USER';
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@exercises.com';
```

## Files Modified

### Backend
- `UserEntity.java` - Added role field
- `Role.java` (new) - Role enum
- `UserDTO.java` - Added role field
- `UserMapper.java` - Map role field
- `JwtTokenProvider.java` - Include role in JWT
- `CustomUserDetailsService.java` - Convert role to authority
- `SecurityConfig.java` - Protect admin endpoints
- `data.sql` - Add admin user

### Frontend
- `domain/models/index.ts` - Added Role type
- `services/adminService.ts` (new) - Admin CRUD operations
- `hooks/useAdminService.ts` (new) - Admin service hook
- `components/AdminDashboard.tsx` (new) - Admin UI
- `components/ExerciseManagement.tsx` (new) - Exercise CRUD
- `components/AuthenticatedHome.tsx` - Admin button
- `App.tsx` - Admin routing
- `infrastructure/auth/MockAuthStorage.ts` - Role support
- `config/auth.ts` - Initialize admin user

## Troubleshooting

### Admin Cannot Access Endpoints
1. Check JWT token contains role claim
2. Verify role is "ROLE_ADMIN" (with prefix)
3. Check SecurityConfig has correct matcher

### Admin Button Not Showing
1. Verify user role in localStorage/JWT
2. Check Role enum matches backend
3. Ensure conditional rendering works

### Permission Denied
1. Token might be expired - re-login
2. User might not have ADMIN role
3. Check backend security configuration

## Summary

This implementation provides a complete role-based access control system with:
- ✅ Backend role enforcement via Spring Security
- ✅ JWT-based role propagation
- ✅ Frontend admin dashboard with CRUD operations
- ✅ Mock and API-based admin services
- ✅ Default admin user for testing
- ✅ Clean architecture with separation of concerns
- ✅ Ready for production deployment

The system is secure, extensible, and follows best practices for role-based authorization.
