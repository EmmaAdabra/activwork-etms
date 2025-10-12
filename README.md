# Employment Training Management System (ETMS)

> A Spring Boot web application for managing IT professional training courses, built with Layered Architecture, MVC, and GRASP design patterns.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12%2B-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-Academic-yellow.svg)]()

## 📋 Overview

ETMS is a locally hosted training management platform for **ActivWork**, enabling instructors to create courses and learners to enroll, track progress, and provide feedback.

**Key Capabilities:**
- 👨‍🏫 Course creation, material uploads, and live session scheduling
- 🎓 Enrollment management with granular progress tracking
- 📊 Analytics dashboards and automated reporting
- 🔔 Notification system for user engagement

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
- **Learner**: `alice.thompson@email.com` / `learner123`

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

## 🏗️ Architecture

**Layered Architecture:**
- **Presentation Layer**: Spring MVC Controllers + Thymeleaf templates
- **Service Layer**: Business logic implementing GRASP patterns (Information Expert, Creator, Controller)
- **Persistence Layer**: Spring Data JPA repositories
- **Database Layer**: PostgreSQL with 10 tables, triggers, and constraints

**Design Patterns:** MVC, Repository Pattern, DTO Pattern, GRASP principles

## 🎓 Academic Project

This project demonstrates layered architecture, MVC, GRASP patterns, and database design for MSc Software Engineering coursework.

## 📝 License

Academic project for Module 1 - Software Architecture and Design.

---

**Built with Spring Boot 3.5.6 and PostgreSQL**

