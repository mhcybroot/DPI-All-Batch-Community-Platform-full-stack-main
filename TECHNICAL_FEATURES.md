# DPI All Batch Community Platform — Technical Feature Specification

> **Architecture**: Modular Monolith &nbsp;|&nbsp; **Framework**: Spring Boot 4.0.2 &nbsp;|&nbsp; **Java**: 21 (LTS)  
> **Base Package**: `mh.cyb.root.DpiBatchMeetBackend`

---

## 1. Authentication & Authorization Module

**Package**: `modules.auth`, `security`, `config`

### 1.1 User Registration

- **Endpoint**: `POST /api/auth/register`
- **Controller**: `AuthController.registerUser()`
- **Flow**:
  1. Validates email uniqueness via `UserService.existsByEmail()`
  2. Creates `User` entity with `isEnabled = false` and default role `MEMBER`
  3. Creates an `ApprovalRequest` with status `PENDING`
  4. Password hashed using `BCryptPasswordEncoder`
  5. Logs `USER_REGISTRATION` action to `AuditLog` with client IP
  6. Returns `201 Created` with `UserDto`
- **DTO**: `RegisterRequest` → { email, password, fullName }

### 1.2 User Login

- **Endpoint**: `POST /api/auth/login`
- **Controller**: `AuthController.loginUser()`
- **Flow**:
  1. Delegates to Spring Security's `AuthenticationManager.authenticate()` with `UsernamePasswordAuthenticationToken`
  2. `CustomUserDetailsService.loadUserByUsername()` loads user from DB, maps `Role` enum to `GrantedAuthority` with `ROLE_` prefix
  3. On success, `JwtTokenProvider.generateToken()` creates a signed JWT
  4. Logs `USER_LOGIN` action to `AuditLog`
  5. Returns `200 OK` with `AuthResponse` → { accessToken, tokenType: "Bearer" }

### 1.3 JWT Implementation

- **Class**: `JwtTokenProvider`
- **Algorithm**: HMAC-SHA256 via `io.jsonwebtoken` (jjwt 0.13.0)
- **Secret**: Base64-encoded 256-bit key (currently hardcoded — `jwtSecret` field)
- **Expiration**: 86,400,000 ms (24 hours)
- **Token Structure**: Subject = username (email), IssuedAt, Expiration
- **Parsing**: `Jwts.parser().verifyWith(SecretKey).build().parseSignedClaims(token)`

### 1.4 JWT Authentication Filter

- **Class**: `JwtAuthenticationFilter` (extends `OncePerRequestFilter`)
- **Flow per request**:
  1. Extracts `Authorization: Bearer <token>` header
  2. Calls `JwtTokenProvider.validateToken()` — catches all `JwtException` subtypes
  3. Extracts username via `JwtTokenProvider.getUsername()`
  4. Loads `UserDetails` via `CustomUserDetailsService`
  5. Sets `UsernamePasswordAuthenticationToken` into `SecurityContextHolder`

### 1.5 Security Configuration

- **Class**: `SecurityConfig`
- **Session Policy**: `STATELESS` — no HTTP sessions
- **CSRF**: Disabled (JWT-based auth)
- **CORS**: Enabled via `CorsConfig` bean (Spring defaults)
- **Public Endpoints**: `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/swagger-ui.html`
- **Filter Chain**: `JwtAuthenticationFilter` registered before `UsernamePasswordAuthenticationFilter`
- **Method-Level Security**: `@PreAuthorize` with SpEL expressions (`hasRole()`, `hasAnyRole()`)

### 1.6 Role System

- **Enum**: `Role` → `ADMINISTRATOR`, `MODERATOR`, `MEMBER`
- **Storage**: `user_roles` join table (`@ElementCollection` with `@Enumerated(EnumType.STRING)`)
- **Fetch Strategy**: `FetchType.EAGER` on roles collection
- **Authority Mapping**: `ROLE_` prefix added in `CustomUserDetailsService`

---

## 2. User Management Module

**Package**: `modules.user`

### 2.1 User Entity

- **Table**: `users`
- **Fields**: `id` (BIGSERIAL PK), `email` (UNIQUE, NOT NULL), `password`, `fullName`, `isEnabled`, `isAccountNonLocked`
- **Extends**: `BaseEntity` (provides `createdAt`, `updatedAt` with `@PrePersist` / `@PreUpdate`)
- **Annotations**: `@Entity`, `@Builder`, `@Getter/@Setter`, `@NoArgsConstructor/@AllArgsConstructor`
- **Relationships**: `@ElementCollection` → `user_roles`

### 2.2 User Service

- **Interface**: `UserService`
- **Implementation**: `UserServiceImpl`
- **Key Methods**:
  - `registerUser(RegisterRequest)` → creates User + ApprovalRequest
  - `createUser(CreateUserRequest)` → admin-only user creation with specified roles
  - `findByEmail(String)` → returns `Optional<User>`
  - `existsByEmail(String)` → boolean check
- **Mapper**: `UserMapper` (MapStruct) → `User` ↔ `UserDto` / `UserSummaryDto`

### 2.3 Data Seeder

- **Class**: `DataSeeder` (`@Configuration` + `CommandLineRunner`)
- **Behavior**: On startup, creates `admin@example.com` / `admin123` with `ADMINISTRATOR` role if not exists
- **Guard**: `userRepository.existsByEmail()` check + try-catch for race conditions

---

## 3. Admin Module

**Package**: `modules.admin`

### 3.1 Approval Workflow

- **Controller**: `AdminController` — all endpoints require `@PreAuthorize("hasRole('ADMINISTRATOR')")`
- **Entity**: `ApprovalRequest` → `id`, `userId` (FK → users), `status` (PENDING/APPROVED/REJECTED), `reviewedBy` (FK → users), `rejectionReason`
- **Table**: `approval_requests` with FK constraints
- **Service**: `ApprovalService` / `ApprovalServiceImpl`
- **Endpoints**:
  | Method | Path | Logic |
  |---|---|---|
  | `GET` | `/api/admin/approvals` | Query `ApprovalRequestRepository` for `status = PENDING` |
  | `POST` | `/api/admin/approvals/{id}/approve` | Set status → `APPROVED`, set `reviewedBy`, enable user (`isEnabled = true`) |
  | `POST` | `/api/admin/approvals/{id}/reject` | Set status → `REJECTED`, set `reviewedBy`, store `rejectionReason` |
  | `POST` | `/api/admin/users` | Direct user creation via `UserService.createUser()` — returns `201` |
- **Mapper**: `ApprovalRequestMapper` (MapStruct)

### 3.2 Audit Logging

- **Controller**: `AuditController`
- **Entity**: `AuditLog` → `id`, `timestamp`, `actorId`, `action`, `targetId`, `details`, `ipAddress`
- **Table**: `audit_logs` (indexed on `timestamp`)
- **Service**: `AuditService` / `AuditServiceImpl`
  - `logAction(Long actorId, String action, String targetId, String details, String ipAddress)` — creates and persists log entry
- **Invoked from**: `AuthController` (registration, login events)

---

## 4. Profile Module

**Package**: `modules.profile`

### 4.1 Profile Entity

- **Table**: `profiles` (1:1 with `users` via `user_id` UNIQUE FK)
- **Fields**: `bio`, `dateOfBirth` (DATE), `phoneNumber`, `linkedinUrl`, `githubUrl`, `portfolioUrl`
- **Relationships**:
  - `@ManyToOne` → `Location` (city, country)
  - `@ManyToOne` → `EmploymentStatus` (seeded: Full-time, Student, Unemployed, Freelancer, Entrepreneur)
  - `@ManyToMany` → `Skill` via `profile_skills` join table

### 4.2 Profile Management

- **Controller**: `ProfileController`
- **Endpoints**:
  | Method | Path | Logic |
  |---|---|---|
  | `GET` | `/api/profile/me` | Loads profile by authenticated user; creates empty profile if none exists |
  | `PUT` | `/api/profile/me` | Updates bio, DOB, phone, URLs, location, employment status |
  | `POST` | `/api/profile/me/skills` | Accepts `Set<String>` skill names; creates `Skill` entities if new; links to profile |
- **Service**: `ProfileService` / `ProfileServiceImpl`
- **Mapper**: `ProfileMapper`, `SkillMapper` (MapStruct)

### 4.3 Directory Search

- **Controller**: `DirectoryController`
- **Endpoint**: `GET /api/directory/search?query=`
- **Service**: `DirectoryService` / `DirectoryServiceImpl`
- **Implementation**: Searches profiles by user full name or bio content (case-insensitive partial match)

### 4.4 Additional Entities

- **PrivacySetting** — domain entity for profile visibility controls
- **Repositories**: `ProfileRepository`, `SkillRepository`, `LocationRepository`, `EmploymentStatusRepository`, `PrivacySettingRepository`

---

## 5. Community Module

**Package**: `modules.community`

### 5.1 Discussion Forum

- **Entities**:
  - `ForumCategory` → `id`, `name` (UNIQUE), `description`, `iconUrl`
  - `ForumPost` → `id`, `title`, `content`, `authorId` (FK), `categoryId` (FK), timestamps
  - `ForumComment` → `id`, `content`, `authorId` (FK), `postId` (FK), `createdAt`
- **Controller**: `ForumController` (13 endpoints)
- **Authorization**:
  - Categories: CRUD restricted to `ADMINISTRATOR`
  - Posts: Create = any authenticated user; Update/Delete = author or admin (checked via `user.getRoles().contains(Role.ADMINISTRATOR)`)
  - Comments: Create = any; Delete = author or admin
- **Service**: `ForumService` / `ForumServiceImpl`
- **Filtering**: `GET /api/forum/posts?categoryId=` — optional category filter
- **Indexes**: `idx_post_category`, `idx_comment_post`

### 5.2 Notice Board

- **Entity**: `Notice` → `id`, `title`, `content`, `authorId` (FK), `isPinned` (boolean), `createdAt`, `expiresAt`
- **Controller**: `NoticeController` — CUD operations require `@PreAuthorize("hasRole('ADMINISTRATOR')")`
- **Service**: `NoticeService` / `NoticeServiceImpl`
  - `getAllActiveNotices()` — filters by `expiresAt > now()` or null, sorted by `isPinned DESC, createdAt DESC`
- **Index**: `idx_notice_pinned`

### 5.3 Memory Wall

- **Entity**: `Memory` → `id`, `title`, `description`, `mediaUrl`, `mediaType` (enum: IMAGE, VIDEO), `uploaderId` (FK)
- **Controller**: `MemoryController`
- **Authorization**: Delete = uploader or admin
- **Service**: `MemoryService` / `MemoryServiceImpl`
- **Index**: `idx_memory_uploader`

### 5.4 Birthday Dashboard

- **Controller**: `BirthdayController`
- **Endpoints**:
  - `GET /api/birthdays/today` — profiles where `dateOfBirth` month/day matches today
  - `GET /api/birthdays/upcoming?days=7` — profiles with DOB within next N days
- **Service**: `BirthdayService` / `BirthdayServiceImpl`
- **DTO**: `BirthdayAlertDto` → user name, date of birth
- **Index**: `idx_profile_dob` on `profiles.date_of_birth`

---

## 6. Event Management Module

**Package**: `modules.event`

### 6.1 Event Entity

- **Table**: `events`
- **Fields**: `id`, `title`, `description`, `eventDate` (TIMESTAMP), `venue`, `venueAddress`, `organizerId` (FK), `status`, `maxAttendees`, `registrationDeadline`, `coverImageUrl`, timestamps
- **Status Enum**: `EventStatus` → `DRAFT`, `UPCOMING`, `ONGOING`, `COMPLETED`, `CANCELLED`
- **DB Check Constraint**: `chk_event_status`
- **Indexes**: `idx_event_date`, `idx_event_status`, `idx_event_organizer`

### 6.2 Event CRUD

- **Controller**: `EventController`
- **Authorization**:
  - Create: `@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODERATOR')")`
  - Update: organizer or admin (service-level check)
  - Delete: `@PreAuthorize("hasRole('ADMINISTRATOR')")`
  - Status change: admin/moderator
- **Service**: `EventService` / `EventServiceImpl`
- **Mapper**: `EventMapper` (MapStruct)
- **DTOs**: `EventDto` (full detail), `EventSummaryDto` (list view), `CreateEventRequest`, `UpdateEventRequest`

### 6.3 Registration System

- **Table**: `registrations`
- **Fields**: `id`, `eventId` (FK with CASCADE DELETE), `userId` (FK), `status`, `rejectionReason`, `reviewedBy`, `reviewedAt`, `notes`, `registeredAt`
- **Status Enum**: `RegistrationStatus` → `PENDING`, `APPROVED`, `REJECTED`, `WAITLISTED`, `CANCELLED`, `ATTENDED`
- **Unique Constraint**: `uq_event_user` (one registration per user per event)
- **Controller**: `RegistrationController` (8 endpoints)
- **Authorization**: Approve/reject requires `ADMINISTRATOR` or `MODERATOR`
- **Service**: `RegistrationService` / `RegistrationServiceImpl`
  - `register()` → checks capacity; assigns `PENDING` or `WAITLISTED`
  - `cancelRegistration()` → sets status to `CANCELLED`
  - `approveRegistration()` → sets status to `APPROVED`, stores reviewer info
  - `markAsAttended()` → sets status to `ATTENDED`

### 6.4 Poll & Voting System

- **Tables**: `polls`, `poll_options`, `votes`
- **Poll Entity**: `id`, `eventId` (FK, nullable — standalone polls supported), `question`, `createdBy` (FK), `isMultipleChoice`, `isAnonymous`, `deadline`, `isClosed`
- **PollOption Entity**: `id`, `pollId` (FK with CASCADE), `optionText`, `voteCount` (denormalized counter)
- **Vote Entity**: `id`, `pollOptionId` (FK with CASCADE), `voterId` (FK), `votedAt`
- **Unique Constraint**: `uq_voter_option` (one vote per user per option)
- **Controller**: `PollController`
  - Create: admin/moderator only
  - Vote: any authenticated user; validated for closed/expired polls
  - Close: creator or admin
- **Service**: `PollService` / `PollServiceImpl`
- **Mapper**: `PollMapper`

---

## 7. Knowledge Module (Q&A)

**Package**: `modules.knowledge`

### 7.1 Entities

- **Question** → `id`, `title`, `body`, `authorId` (FK), `voteScore` (int), `isSolved`, timestamps
- **Answer** → `id`, `questionId` (FK), `authorId`, `body`, `voteScore`, `isAccepted`, timestamps
- **KnowledgeVote** → `id`, `voterId`, `targetId`, `targetType` (enum: `QUESTION` | `ANSWER`), `voteType` (enum: `UPVOTE` | `DOWNVOTE`)

### 7.2 Features

- **Controller**: `KnowledgeController` (9 endpoints)
- **Service**: `KnowledgeService` / `KnowledgeServiceImpl`
- **Question Sorting**: `newest` (createdAt DESC), `votes` (voteScore DESC), `unsolved` (isSolved = false)
- **Pagination**: Spring `Pageable` with configurable page/size
- **Voting Logic**: One vote per user per target; toggles on re-vote; updates `voteScore` on entity
- **Accept Answer**: Only question author can accept; sets `isAccepted = true` and `question.isSolved = true`
- **DTOs**: `QuestionDto`, `AnswerDto`, `CreateQuestionRequest`, `CreateAnswerRequest`, `VoteRequest`

---

## 8. Mentorship Module

**Package**: `modules.mentorship`

### 8.1 Entities

- **MentorProfile** → `id`, `userId` (FK), `expertise` (String), `bio`, `maxMentees`, `status` (enum: `MentorStatus`)
- **MentorshipConnection** → `id`, `mentorId` (FK → MentorProfile), `menteeId` (FK → users), `message`, `status` (enum: `ConnectionStatus`), timestamps

### 8.2 Features

- **Controller**: `MentorshipController` (7 endpoints)
- **Service**: `MentorshipService` / `MentorshipServiceImpl`
- **Register as Mentor**: Creates or updates `MentorProfile` for authenticated user
- **Search Mentors**: Filter by `expertise` (partial match), paginated via `PageRequest`
- **Connection Flow**: Mentee sends request → Mentor accepts/rejects → Status updated (`ConnectionStatus`)
- **Views**: Outgoing requests (as mentee), incoming requests (as mentor)
- **DTOs**: `MentorProfileDto`, `RegisterMentorRequest`, `ConnectionRequestDto`, `CreateConnectionRequest`

---

## 9. Professional Module

**Package**: `modules.professional`

### 9.1 Job Board

- **Entity**: `JobPost` → `id`, `title`, `description`, `company`, `location`, `salary`, `jobType` (enum: `JobType`), `status` (enum: `JobStatus`), `posterId` (FK), timestamps
- **Controller**: `JobController` (6 endpoints)
- **Pagination**: `@PageableDefault(sort = "createdAt", direction = DESC)`
- **Filtering**: By `JobStatus` (default: ACTIVE) and optional `JobType`
- **Authorization**: Update/delete/status-change = poster only (service-level check)
- **Validation**: `@Valid` on request DTOs
- **Service**: `JobService` / `JobServiceImpl`
- **Mapper**: `JobMapper` (MapStruct)

### 9.2 Business Directory

- **Entity**: `BusinessProfile` → `id`, `businessName`, `description`, `category`, `website`, `contactEmail`, `contactPhone`, `address`, `ownerId` (FK), timestamps
- **Controller**: `BusinessController` (5 endpoints)
- **Search**: By `businessName` or keyword (partial match), paginated via `@PageableDefault(sort = "businessName")`
- **Authorization**: Update/delete = owner only
- **Validation**: `@Valid` on request DTOs
- **Service**: `BusinessService` / `BusinessServiceImpl`

### 9.3 Blood Donor Registry

- **Entity**: `BloodDonorProfile` → `id`, `userId` (FK), `bloodGroup` (enum: `BloodGroup`), `location`, `isAvailable`, `lastDonationDate`, timestamps
- **Controller**: `BloodDonorController` (5 endpoints)
- **Search**: Filter by `BloodGroup` enum + optional `location` string
- **Availability Toggle**: `PATCH /api/blood-donors/status?isAvailable=true|false`
- **Donation Tracking**: `PATCH /api/blood-donors/last-donation` with `UpdateLastDonationRequest`
- **Service**: `BloodDonorService` / `BloodDonorServiceImpl`
- **Mapper**: `BloodDonorMapper` (MapStruct)

---

## 10. Cross-Cutting Concerns

### 10.1 Base Entity

- **Class**: `BaseEntity` (`common.domain`)
- **Fields**: `createdAt` (TIMESTAMP, `@PrePersist`), `updatedAt` (TIMESTAMP, `@PreUpdate`)
- **Usage**: Extended by `User`, `ApprovalRequest`, and other entities that need audit timestamps

### 10.2 Exception Handling

- **Class**: `GlobalExceptionHandler` (`@RestControllerAdvice`)
- **Custom Exceptions**:
  - `BadRequestException` → `400 Bad Request`
  - `ResourceNotFoundException` → `404 Not Found`
- **Response Format**: `ErrorResponse` DTO → { message, timestamp, status }

### 10.3 API Documentation

- **Config**: `OpenApiConfig` class with SpringDoc configuration
- **UI**: Swagger UI at `/swagger-ui.html`
- **Spec**: OpenAPI 3.0 JSON at `/v3/api-docs`
- **Annotations Used**: `@Tag`, `@Operation`, `@ApiResponse`, `@ApiResponses`

### 10.4 CORS Configuration

- **Class**: `CorsConfig` — Spring's default CORS configuration with `Customizer.withDefaults()`

### 10.5 Object Mapping

- **Library**: MapStruct 1.6.3 (compile-time code generation)
- **Mappers** (9 total): `UserMapper`, `ApprovalRequestMapper`, `AuditLogMapper`, `ProfileMapper`, `SkillMapper`, `EventMapper`, `RegistrationMapper`, `PollMapper`, `JobMapper`, `BusinessMapper`, `BloodDonorMapper`
- **Processing**: `annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")`

---

## 11. Database Architecture

### 11.1 Engine & Configuration

- **RDBMS**: PostgreSQL 15 (Alpine in Docker)
- **Dialect**: `org.hibernate.dialect.PostgreSQLDialect`
- **DDL Strategy**: `hibernate.ddl-auto=update` (supplements Flyway)
- **Connection**: JDBC via `spring.datasource.url`

### 11.2 Flyway Migrations

| Version | File | Tables | Indexes |
|---|---|---|---|
| V1 | `V1__init_security_schema.sql` | `users`, `user_roles`, `approval_requests`, `audit_logs` | `idx_users_email`, `idx_approval_status`, `idx_audit_timestamp` |
| V2 | `V2__add_profile_and_community_tables.sql` | `profiles`, `locations`, `employment_statuses`, `skills`, `profile_skills`, `notices`, `forum_categories`, `forum_posts`, `forum_comments`, `memories` | `idx_notice_pinned`, `idx_post_category`, `idx_comment_post`, `idx_memory_uploader`, `idx_profile_dob` |
| V3 | `V3__add_event_management_tables.sql` | `events`, `registrations`, `polls`, `poll_options`, `votes` | `idx_event_date`, `idx_event_status`, `idx_event_organizer`, `idx_registration_event`, `idx_registration_user`, `idx_poll_event`, `idx_poll_deadline`, `idx_vote_option` |

### 11.3 Hibernate-Managed Tables (via `ddl-auto=update`)

- Knowledge module: `questions`, `answers`, `knowledge_votes`
- Mentorship module: `mentor_profiles`, `mentorship_connections`
- Professional module: `job_posts`, `business_profiles`, `blood_donor_profiles`

---

## 12. Deployment Architecture

### 12.1 Docker Multi-Stage Build

```
Stage 1 (Build):  eclipse-temurin:21-jdk-alpine
  → Copy Gradle wrapper + build files
  → ./gradlew dependencies (cache layer)
  → Copy src → ./gradlew bootJar -x test

Stage 2 (Runtime): eclipse-temurin:21-jre-alpine
  → COPY app.jar from build stage
  → ENTRYPOINT java -jar /app.jar
```

### 12.2 Docker Compose Services

| Service | Image | Port | Volumes |
|---|---|---|---|
| `app` | Custom (Dockerfile) | 8080:8080 | — |
| `db` | `postgres:15-alpine` | 5432:5432 | `postgres_data` (named volume) |
| `db-backup` | `prodrigestivill/postgres-backup-local` | — | `./backups` (bind mount) |

### 12.3 Backup Configuration

- **Schedule**: `@daily` (cron)
- **Retention**: 7 days, 4 weeks, 6 months
- **Health Check** (db): `pg_isready -U postgres` — interval 10s, timeout 5s, 5 retries

---

## 13. Testing Architecture

### 13.1 Test Stack

- JUnit 5 (`junit-platform-launcher`)
- Spring Boot Test (`@SpringBootTest`, `@WebMvcTest`)
- Spring Security Test (`@WithMockUser`)
- Execution: `./gradlew test`

### 13.2 Test Coverage (40 files)

| Module | Controller Tests | Service Tests | Other |
|---|---|---|---|
| Admin | `AdminControllerTest`, `AuditControllerTest` | `ApprovalServiceImplTest`, `AuditServiceImplTest` | — |
| Auth | `AuthControllerTest` | `CustomUserDetailsServiceTest` | — |
| Community | Forum, Notice, Memory, Birthday | Forum, Notice, Memory, Birthday | — |
| Event | Event, Poll, Registration | Event, Poll, Registration | — |
| Knowledge | `KnowledgeControllerTest` | `KnowledgeServiceImplTest` | — |
| Mentorship | `MentorshipControllerTest` | `MentorshipServiceImplTest` | — |
| Professional | Job, Business, BloodDonor | Job, Business, BloodDonor | — |
| Profile | Profile, Directory | Profile, Directory | — |
| User | — | `UserServiceImplTest`, `UserServiceTest` | `UserRepositoryTest` |
| Security | — | — | `JwtTokenProviderTest` |
| Config | — | — | `DataSeederTest` |
