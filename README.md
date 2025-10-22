# ActivWork Training Portal

> A comprehensive Spring Boot web application for managing IT professional training courses, built with Layered Architecture, MVC, and GRASP design patterns for recruitment agencies.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12%2B-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-Academic-yellow.svg)]()

## 📋 Overview

**ActivWork Training Portal** is a locally hosted training management platform designed for recruitment agencies to upskill IT candidates and employees. The system enables instructors to create structured training courses with sections, while candidates can enroll, access materials, track progress, and provide feedback.

**Key Capabilities:**
- 👨‍🏫 **Course Management** - Create courses with sections, upload materials, track analytics
- 🎓 **Candidate Learning** - Enroll in courses, access materials by sections, track progress
- 📊 **Progress Tracking** - Granular material completion with video bookmarking
- 🔔 **Real-time Updates** - AJAX operations with toast notifications
- 🎨 **Modern UI** - Responsive design with dark/light mode support

## 🛠️ Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Backend | Java | 17 |
| Framework | Spring Boot | 3.5.6 |
| ORM | Hibernate (JPA) | 6.x |
| Database | PostgreSQL | 12+ |
| Frontend | Thymeleaf + Tailwind CSS | Latest |
| Build Tool | Maven | 3.8+ |
| Testing | JUnit 5 + Mockito | Latest |

## 🚀 Quick Start

### Prerequisites
- Java 17+, Maven 3.8+, PostgreSQL 12+

### Installation

1. **Setup Database**
   ```bash
   createdb etms
   psql -d etms -f database/01_create_database.sql
   psql -d etms -f database/02_sample_data.sql
   psql -d etms -f database/10_add_course_sections.sql
   ```

2. **Configure Application**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   # Edit application.properties with your database credentials
   ```

3. **Run Application**
   ```bash
   ./mvnw spring-boot:run
   # Access at http://localhost:8080
   ```

### Test Credentials
- **Instructor**: `sarah.johnson@activwork.com` / `instructor123`
- **Candidate**: `alice.thompson@email.com` / `learner123`

## 🧪 Testing

```bash
./mvnw test  # Run all tests
./mvnw test -Dtest=DatabaseConnectivityTest  # Run specific test
```

## 📚 Documentation

- **[Project Requirements](project_requirement.md)** - System specification and features
- **[Project Structure](docs/personal/PROJECT_STRUCTURE.md)** - Codebase organization
- **[Database Guide](database/README.md)** - Schema and setup instructions
- **[Application Setup](docs/personal/APPLICATION_SETUP.md)** - Configuration guide
- **[Course Sections Implementation](SECTION_IMPLEMENTATION_SUMMARY.md)** - Sections feature guide

## 🏗️ Architecture

**Layered Architecture:**
- **Presentation Layer**: Spring MVC Controllers + Thymeleaf templates
- **Service Layer**: Business logic implementing GRASP patterns (Information Expert, Creator, Controller)
- **Persistence Layer**: Spring Data JPA repositories
- **Database Layer**: PostgreSQL with 11 tables, triggers, and constraints

**Design Patterns:** MVC, Repository Pattern, DTO Pattern, GRASP principles

## 🎯 Key Features

### **Course Management**
- ✅ **Course Creation** with rich metadata (categories, difficulty, prerequisites)
- ✅ **Course Sections** - Organize materials into logical groups (like Udemy chapters)
- ✅ **Material Upload** - Support for videos, PDFs, documents, presentations
- ✅ **Section Assignment** - Assign materials to specific sections during upload
- ✅ **Course Analytics** - Track views, enrollments, ratings, and performance

### **Candidate Learning Experience**
- ✅ **Enrollment System** - Easy course enrollment with progress tracking
- ✅ **Accordion UI** - LinkedIn Learning-style section navigation
- ✅ **Progress Tracking** - Per-material and per-section completion tracking
- ✅ **Video Bookmarking** - Resume video playback from last position
- ✅ **Material Access** - Watch videos, open documents, download files

### **Advanced Features**
- ✅ **Real-time Updates** - AJAX operations with toast notifications
- ✅ **Responsive Design** - Works on desktop, tablet, and mobile
- ✅ **Dark/Light Mode** - User preference support
- ✅ **File Management** - Secure file upload with size validation (50MB limit)
- ✅ **Hard Delete** - Clean UI with proper cascade deletion

## 🗄️ Database Schema

**Core Tables (6):**
- `users` - System users (instructors and candidates)
- `courses` - Training courses with lifecycle management
- `materials` - Course materials with section assignment
- `enrollments` - Candidate enrollments with progress tracking
- `feedback` - Course ratings and reviews
- `live_sessions` - Scheduled live training sessions

**Innovative Tables (5):**
- `course_sections` - Course organization into logical groups
- `course_prerequisites` - Course dependency management
- `material_progress` - Granular progress tracking with video bookmarking
- `notifications` - Real-time user engagement
- `course_analytics` - Daily performance metrics

## 🎓 Academic Project

This project demonstrates layered architecture, MVC, GRASP patterns, and database design for MSc Software Engineering coursework.

**GRASP Patterns Implemented:**
- **Information Expert** - Each service is responsible for its domain logic
- **Creator** - Controllers create and manage related objects
- **Controller** - MVC controllers handle user interactions

## 📝 License

Academic project for Module 1 - Software Architecture and Design.

---

**Built with Spring Boot 3.5.6 and PostgreSQL**

*ActivWork Training Portal - Empowering IT professionals through structured learning*