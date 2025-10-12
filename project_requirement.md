**Employment Training Management System (ETMS) --- Developer-Focused
Architecture Blueprint**

## 1. Project Overview

The Employment Training Management System (ETMS) is a locally hosted web
application designed to manage training courses for IT professionals.
Instructors can create, manage, and schedule courses, while learners can
enroll, access materials, attend live sessions, provide feedback, and
track progress.\
The system emphasizes **clean software architecture**, **modularity**,
**maintainability**, and **scalability**, following **Layered
Architecture**, **MVC**, and selected **GRASP** patterns.

**1.1 Goals & scope (short)**

-   **Primary goal:** Build a locally-hosted prototype for an
    *Employment Training Management System (ETMS)* demonstrating layered
    architecture (Presentation → Service → Persistence),
    Model-View-Controller (MVC) pattern, and at least one General
    Responsibility Assignment Software Pattern (GRASP).

-   **Deadline target:** core features implemented and demonstrable by
    **20 Oct 2025** (prototype stage). Focus on basics (CRUD + auth +
    role dashboards). Add innovations later.

**2. Requirements (refined)**

**2.1 Actors**

-   **Instructor** --- creates/manages courses, uploads materials,
    schedules live sessions, views enrolled learners and feedback.

-   **Learner** --- views courses, enrolls, accesses materials, attends
    live sessions, provides feedback, tracks progress.

-   *(Out of scope for prototype)*: Administrator, payments, AI
    recommendations, cloud-hosted services.

## 2. System Requirements

### 2.1 Functional Requirements (FR)

  -----------------------------------------------------------------------------
  **ID**   **Requirement**   **Description**            **Operations**
  -------- ----------------- -------------------------- -----------------------
  FR1      User              Validate login credentials Authenticate
           Authentication                               (Login/Logout,
                                                        session-based)

  FR2      Role              Restrict access based on   Authorize (Role Check)
           Authorization     user role                  

  FR3      Course Management Instructors create,        CRUD
                             update, delete, view       
                             courses                    

  FR4      Material Upload   Instructors upload PDFs,   CRUD
                             videos, notes, images      

  FR5      Course Enrollment Learners view and enroll   Read (View), Create
                             in courses                 (Enroll)

  FR6      Progress Tracking Track learner's course     Read, Update
                             completion                 

  FR7      Feedback &        Learners rate and review   Create, Read
           Ratings           courses                    

  FR8      Live Session      Instructors schedule       CRUD
           Scheduling        sessions (link, datetime)  

  FR9      Dashboard         Personalized per user type Read (dynamic)

  FR10     Search & Filter   Learners can search/filter Read (query)
           Courses           available courses          

  FR11     Quick             Registration for learners  Create
           Registration      & instructors (DB          
                             population)                
  -----------------------------------------------------------------------------

### 2.2 Non-Functional Requirements (NFR)

-   **Performance:** Core pages load within 3 seconds locally

-   **Scalability:** Layered architecture allows future cloud and API
    expansion

-   **Security:** Session-based auth with BCrypt password hashing, CSRF protection, 
    secure file uploads with type validation

-   **Usability:** Responsive and clean UI (desktop + mobile) with modern design

-   **Maintainability:** MVC + Service layer + GRASP patterns + proper exception handling

-   **Reliability:** Graceful handling of concurrent requests and uploads

-   **File Upload:** Maximum 50MB per file, supported formats: PDF, MP4, AVI, MOV, PNG, JPG, DOCX, PPTX

-   **Session Management:** 30-minute inactivity timeout, secure session cookies

## 2.3 Innovative Database Features

### Database Architecture Excellence
Our PostgreSQL database implements production-grade features that exceed basic CRUD requirements:

#### ENUMs for Type Safety
- `user_role`: INSTRUCTOR, LEARNER
- `course_category`: 10 categories (PROGRAMMING, WEB_DEVELOPMENT, DATABASE, DEVOPS, etc.)
- `difficulty_level`: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
- `enrollment_status`: ACTIVE, COMPLETED, CANCELLED, SUSPENDED
- `material_type`: VIDEO, PDF, DOCUMENT, PRESENTATION, AUDIO, IMAGE, CODE_SAMPLE, EXERCISE, QUIZ, CERTIFICATE
- `course_status`: DRAFT, PUBLISHED, ARCHIVED, SUSPENDED
- `session_status`: SCHEDULED, LIVE, COMPLETED, CANCELLED

#### Sophisticated Triggers & Functions
1. **Automatic Enrollment Count Updates**
   - Trigger: `trigger_update_enrollment_count`
   - Updates course enrollment count on INSERT/UPDATE/DELETE of enrollments
   
2. **Automatic Course Rating Calculations**
   - Trigger: `trigger_update_course_rating`
   - Recalculates average rating and total ratings when feedback is submitted/updated
   
3. **Automatic Progress Tracking**
   - Trigger: `trigger_update_enrollment_progress`
   - Calculates progress percentage based on completed materials
   - Auto-updates enrollment status to COMPLETED when progress reaches 100%

#### Performance Optimizations
- **42+ Strategic Indexes**: Including composite indexes for common query patterns
- **Partial Indexes**: e.g., unread notifications for faster queries
- **Foreign Key Indexes**: All foreign keys indexed for join performance
- **Array Field Support**: Prerequisites, learning objectives, and tags stored as PostgreSQL arrays

#### Data Integrity & Validation
- **CHECK Constraints**: Email format, phone format, URL patterns, rating ranges (1-5), file size limits
- **UNIQUE Constraints**: Prevent duplicate enrollments, feedback, prerequisites
- **Cascading Deletes**: Properly configured for data consistency
- **Timestamp Tracking**: `created_at`, `updated_at` with automatic triggers

#### Advanced Features
- **JSONB Support**: Flexible metadata storage (notification preferences, notification metadata)
- **UUID Primary Keys**: Better for distributed systems and security
- **Timezone Awareness**: All timestamps use `TIMESTAMP WITH TIME ZONE`
- **Soft Deletes**: `is_active` flags instead of hard deletes
- **Audit Trail**: Track last_login, last_activity, last_accessed

#### PostgreSQL-Specific Enhancements
- **Array Types**: Store multiple values (prerequisites, objectives, tags) efficiently
- **Custom Schema**: `etms` schema for better organization
- **Extension Usage**: `uuid-ossp` for UUID generation
- **Comments**: Database objects documented with PostgreSQL comments

## 3. Domain Model --- Entities & Relationships (complete)

Use this directly to build your class diagram and DB schema.

### Core Entities (Enhanced)

-   **User** (id, name, email, password_hash, role, phone_number, department, 
    position_level, profile_picture_url, bio, linkedin_url, github_url, timezone, 
    language_preference, notification_preferences, created_at, last_login, 
    last_activity, is_active, is_verified, email_verification_token, 
    password_reset_token, password_reset_expires) ---
    roles: INSTRUCTOR, LEARNER.
    
    *Enhancements:* Social profiles, localization support, email verification, 
    password reset functionality, activity tracking.

-   **Course** (id, title, summary, description, instructor_id, category, 
    difficulty_level, duration_hours, max_enrollments, price, status, 
    thumbnail_url, video_preview_url, prerequisites, learning_objectives, tags, 
    start_date, end_date, enrollment_deadline, is_featured, is_active, view_count, 
    enrollment_count, average_rating, total_ratings, created_at, updated_at, 
    published_at).
    
    *Enhancements:* Course lifecycle management (DRAFT→PUBLISHED→ARCHIVED), 
    multimedia support, automatic rating calculations, analytics tracking, 
    featured courses, array fields for prerequisites/objectives/tags.

-   **Material** (id, course_id, filename, original_filename, mime_type, 
    material_type, path, file_size, duration_seconds, thumbnail_url, download_count, 
    view_count, is_downloadable, is_required, display_order, uploaded_at, 
    description, is_active).
    
    *Enhancements:* Type categorization (VIDEO, PDF, DOCUMENT, PRESENTATION, etc.), 
    video duration tracking, analytics (downloads/views), display ordering, 
    required materials flag.

-   **Enrollment** (id, learner_id, course_id, enrolled_at, status, 
    progress_percent, completed_materials, total_materials, time_spent_minutes, 
    last_accessed, completion_date, certificate_issued, certificate_url, notes).
    
    *Enhancements:* Comprehensive progress tracking, time spent tracking, 
    certificate generation, completion tracking, learner notes.

-   **Feedback** (id, learner_id, course_id, rating int 1..5, comment, 
    created_at, is_visible).
    
    *Enhancements:* Visibility control for moderation, automatic course rating 
    updates via triggers.

-   **LiveSession** (id, course_id, title, description, starts_at, ends_at, 
    duration_minutes, status, meeting_link, meeting_id, meeting_password, 
    max_participants, recording_url, recording_available, attendance_count, 
    is_active, created_at, updated_at).
    
    *Enhancements:* Session status lifecycle (SCHEDULED→LIVE→COMPLETED→CANCELLED), 
    meeting credentials, recording support, attendance tracking, participant limits.

### Innovative Additional Entities

-   **CoursePrerequisite** (id, course_id, prerequisite_course_id, is_mandatory, 
    created_at).
    
    *Purpose:* Manage course dependencies and learning paths. Enables structured 
    curriculum design with mandatory and optional prerequisites.

-   **MaterialProgress** (id, enrollment_id, material_id, is_completed, 
    completion_percent, time_spent_minutes, last_position_seconds, completed_at, 
    created_at, updated_at).
    
    *Purpose:* Granular progress tracking per material. Supports video bookmarking 
    (resume playback), detailed analytics, and automatic enrollment progress calculation.

-   **Notification** (id, user_id, title, message, type, is_read, action_url, 
    metadata, created_at, read_at).
    
    *Purpose:* Real-time user engagement. Supports multiple notification types 
    (course updates, session reminders, certificate ready, etc.) with deep linking 
    and metadata storage.

-   **CourseAnalytics** (id, course_id, date, views, enrollments, completions, 
    average_rating, total_ratings, revenue, created_at).
    
    *Purpose:* Historical analytics for instructors. Daily snapshots of course 
    performance including views, enrollments, ratings, and revenue tracking.

-   **Role** (enum: INSTRUCTOR, LEARNER) --- PostgreSQL ENUM type for type safety.

**Relationships (Enhanced ER)**

#### Core Relationships
-   User (1) → (N) Course where User.role = INSTRUCTOR
-   Course (1) → (N) Material
-   Course (1) → (N) LiveSession
-   User[Learner] (1) → (N) Enrollment ← (N) Course (many-to-many)
-   Course (1) → (N) Feedback ← (N) User[Learner]

#### Innovative Relationships
-   Course (1) → (N) CoursePrerequisite (self-referencing for course dependencies)
-   Enrollment (1) → (N) MaterialProgress ← (N) Material (detailed tracking)
-   User (1) → (N) Notification (user engagement)
-   Course (1) → (N) CourseAnalytics (time-series analytics)

**Notes:**

-   Enrollment is the join table that contains progress_percent and
    status (e.g., ACTIVE, COMPLETED).

-   Use cascading deletes carefully: deleting a course may cascade to
    materials and sessions but probably not to historical enrollments
    (archival consideration --- for prototype you may cascade).

**Annotate on diagram:**

-   CourseService --- Information Expert (responsible for business rules
    around courses).

-   CourseController --- Creator (creates Course objects and delegates
    to CourseService).

-   AuthController --- Controller pattern (routes login/logout).

**4. Layered & MVC Mapping**

**4.1 Package structure (Spring Boot project):**

com.activwork.etms

├─ controller // Presentation layer (Spring MVC controllers + Thymeleaf views)
├─ dto // Data Transfer Objects (input/output)
├─ model // Domain entities / JPA entities
├─ repository // Persistence layer (Spring Data JPA repositories)
├─ service // Business/service layer (contains business rules)
├─ config // Security, DB, beans
├─ util // helpers (file storage, validation)
├─ exception // Custom exception handling
├─ security // Security configuration and utilities

**Explanation:**

-   Controllers handle HTTP requests and view resolution

-   Services implement business logic and validations (Information
    Expert GRASP pattern)

-   Repositories manage persistence (ORM via JPA/Hibernate)

-   Entities represent DB tables; DTOs abstract data for views

### 4.2 MVC + Endpoints (Prototype)

**Authentication & Registration**

-   GET /login → Login page

-   POST /login → Process login, create session

-   GET /logout → Invalidate session

-   GET /register → Registration page (learner/instructor)

-   POST /register → Process registration, create DB entry

**Instructor Course Management**

-   GET /instructor/courses → List instructor courses
-   GET /instructor/courses/create → Course creation form
-   POST /instructor/courses/create → Save course
-   GET /instructor/courses/{id}/edit → Edit form
-   POST /instructor/courses/{id}/edit → Save updates
-   POST /instructor/courses/{id}/delete → Delete course

**Material Management**

-   GET /instructor/courses/{id}/materials → List course materials
-   POST /instructor/courses/{id}/materials/upload → Upload material
-   DELETE /instructor/courses/{id}/materials/{materialId} → Delete material

**Live Session Management**

-   GET /instructor/courses/{id}/sessions → List course sessions
-   POST /instructor/courses/{id}/sessions → Create session
-   PUT /instructor/courses/{id}/sessions/{sessionId} → Update session
-   DELETE /instructor/courses/{id}/sessions/{sessionId} → Delete session

**Learner Course Interaction**

-   GET /courses → Browse/search courses
-   POST /courses/{id}/enroll → Enroll in course
-   GET /courses/{id} → View course details & materials
-   POST /courses/{id}/feedback → Submit course feedback

**Progress Tracking**

-   GET /learner/courses/{id}/progress → View course progress
-   PUT /learner/courses/{id}/progress → Update progress (mark material complete)
-   GET /learner/dashboard → Learner dashboard with progress overview

**Future API Mapping (Optional / Future React Migration)**

-   GET /api/courses

-   GET /api/courses/{id}

-   POST /api/instructor/courses

-   POST /api/courses/{id}/enroll

-   Authentication: JWT (future)

## 5. GRASP Patterns --- what to show and where

Pick **Information Expert** and **Creator** for clear, demonstrable
usage.

-   **Information Expert**: CourseService is responsible for
    course-related logic (create, update, validate business rules) ---
    annotate class diagram and code screenshot showing this logic.

-   **Creator**: CourseController or InstructorService creates Course
    instances and delegates persistence to CourseRepository. Annotate
    where object creation responsibility is assigned.

-   (Optional) **Controller**: built-in GRASP controller pattern ---
    show how controllers route requests and coordinate services.

In the class diagram annotate each service class with the applied GRASP
pattern.

## 6. Tools & Technologies

  ------------------------------------------------------------------------------
  **Layer**        **Technology**   **Notes**
  ---------------- ---------------- --------------------------------------------
  Frontend         Thymeleaf +      Fast prototype, integrates with Spring MVC.
                   Tailwind         It should be visually appealing and elegant,
                                    responsive on all screens and with a great
                                    user experience

  Backend          Java (Spring     Standard, scalable for layered architecture
                   Boot)            

  ORM              JPA + Hibernate  Modern, widely used, maps entities to
                                    Postgres

  Database         PostgreSQL       Relational, supports all core
                   (local)          functionalities

  Authentication   Session-based    Simple, prototype-friendly; JWT planned for
                                    future
  ------------------------------------------------------------------------------

## 7. File storage

-   Local directory under project root uploads/ with subfolders per
    course (e.g., /uploads/course-\<id\>/materials/).

-   File validation: Type checking, size limits (50MB), secure naming

-   Supported formats: PDF, MP4, AVI, MOV, PNG, JPG, DOCX, PPTX

-   Later scale to Cloudinary hook as an env-configurable option ---
    document it and keep cloud usage optional.

**8. Progress Tracking Implementation**

-   **Method**: Material completion-based tracking
-   **Calculation**: (Completed materials / Total materials) × 100
-   **Storage**: Progress percentage and completed materials count in Enrollment entity
-   **Updates**: Real-time progress updates when materials are marked complete

**9. Search & Filter Implementation**

-   **Initial**: Field-based filtering (title, category, instructor, difficulty level)
-   **Future**: Full-text search with database indexes
-   **UI**: Advanced search form with multiple filter options

**10. Future Upgrade / Modernization**

**10.1 Authentication & Security**

-   **Upgrade:** Session → JWT or OAuth2

-   **Reason:** Stateless, API-ready, SPA-friendly

**10.2 Frontend**

-   **Upgrade:** Thymeleaf → React / Next.js SPA

-   **Reason:** Rich UX, easier API integration, modern standard

**10.3 API & Architecture**

-   **Upgrade:** REST controllers under /api/\*

-   **Reason:** Future multi-client support, mobile integration

**10.4 Cloud & DevOps**

-   DB → Cloud RDS (Postgres), file storage → S3/Cloudinary

-   Dockerize app, setup CI/CD pipelines

-   Prepare layered design for microservices decomposition

**10.5 Rationale**

All temporary prototype compromises are **documented**, and the
layered + MVC + DTO + Service design ensures **smooth migration** to
modern tools without rewriting core business logic.

**11. Innovative Features Summary**

### What Makes This System Modern & Production-Ready

#### Beyond Basic CRUD
1. **10 Tables Instead of 6**: Added CoursePrerequisite, MaterialProgress, Notification, CourseAnalytics
2. **Automatic Calculations**: Triggers handle ratings, counts, and progress automatically
3. **Rich Data Types**: ENUMs, Arrays, JSONB for flexible data storage
4. **Complete Audit Trail**: Track user activity, material views, session attendance

#### Advanced User Experience
5. **Video Resume Feature**: Save playback position (last_position_seconds)
6. **Notification System**: Real-time alerts for course updates, sessions, certificates
7. **Course Prerequisites**: Structured learning paths with dependencies
8. **Analytics Dashboard**: Daily performance metrics for instructors

#### Enterprise-Grade Features
9. **Multi-language Support**: Timezone and language preferences per user
10. **Social Integration**: LinkedIn/GitHub profile links
11. **Email Verification**: Secure account activation workflow
12. **Password Reset**: Token-based password recovery
13. **Certificate Generation**: Automated certificate issuance on completion

#### Production-Ready Database
14. **42+ Indexes**: Optimized query performance
15. **CHECK Constraints**: Data validation at database level
16. **Soft Deletes**: is_active flags for data preservation
17. **Cascading Rules**: Proper referential integrity
18. **Type Safety**: PostgreSQL ENUMs prevent invalid data

#### Scalability Features
19. **UUID Keys**: Distributed-system ready
20. **Array Fields**: Efficient storage of related data
21. **JSONB Metadata**: Flexible configuration storage
22. **Time-Series Data**: CourseAnalytics for historical tracking

**11. Conclusion**

This document is a **developer blueprint** that:

-   **Exceeds assessment criteria** (Layered Architecture, MVC, GRASP, CRUD, FR1--FR11)
    with innovative features that demonstrate professional software engineering

-   **Demonstrates production-grade database design** with triggers, functions, 
    constraints, and optimizations typically found in enterprise systems

-   **Implements modern features** like video bookmarking, real-time notifications, 
    analytics dashboards, and learning path management

-   Allows fast prototype delivery with Thymeleaf + session auth while maintaining
    code quality and architectural integrity

-   Includes comprehensive file upload, progress tracking, search functionality,
    and user engagement features

-   **Provides 22+ innovative enhancements** beyond basic requirements that showcase
    advanced PostgreSQL features and software design patterns

-   Clearly defines future expansion paths for modern tools (JWT, React, microservices)
    and architecture without requiring core logic rewrites

**12. Implementation Priority for Assessment**

**Phase 1 (Core Features - Week 1):**
1. Database setup with all 10 tables ✅ **COMPLETED**
2. User authentication and role management (INSTRUCTOR/LEARNER)
3. Course CRUD operations with status lifecycle (DRAFT→PUBLISHED→ARCHIVED)
4. Basic enrollment system
5. Material upload with type categorization

**Phase 2 (Enhanced Features - Week 2):**
1. Granular progress tracking system (MaterialProgress)
2. Feedback and automatic rating calculation (triggers)
3. Live session management with status tracking
4. Notification system for user engagement
5. Dashboard views with analytics

**Phase 3 (Innovative Features - Week 2-3):**
1. Course prerequisites and learning paths
2. Video bookmarking (resume playback)
3. Certificate generation on completion
4. Instructor analytics dashboard
5. Search and advanced filter functionality

**Phase 4 (Polish & Demo - Final Week):**
1. UI/UX improvements with Tailwind CSS
2. Comprehensive error handling and validation
3. Performance optimizations
4. Demo video preparation (5 minutes)
5. Documentation and code annotations for GRASP patterns

### Database Setup Status
✅ **Schema Created**: All 10 tables with relationships
✅ **Indexes**: 42+ indexes for performance
✅ **Triggers**: 3 sophisticated triggers for automation
✅ **Functions**: 3 PL/pgSQL functions for business logic
✅ **Sample Data**: 9 users, 6 courses, 7 materials, 10 enrollments, 4 feedback, 4 sessions
✅ **Constraints**: CHECK, UNIQUE, and FOREIGN KEY constraints
✅ **ENUMs**: 7 custom types for data integrity
✅ **Verified**: Database populated and tested successfully
