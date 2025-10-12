# ETMS Database Setup

This directory contains the PostgreSQL database setup scripts for the Employment Training Management System (ETMS).

## Prerequisites

- PostgreSQL 12+ installed and running
- Database user with appropriate permissions
- Database named `etms` created

## Setup Instructions

### 1. Create Database
```sql
-- Connect to PostgreSQL as superuser
psql -U postgres

-- Create database
CREATE DATABASE etms;

-- Connect to the database
\c etms;
```

### 2. Run Setup Scripts

Execute the scripts in the following order:

```bash
# 1. Create database schema and tables
psql -U postgres -d etms -f 01_create_database.sql

# 2. Insert sample data
psql -U postgres -d etms -f 02_sample_data.sql

# 3. Verify data (optional)
psql -U postgres -d etms -f 03_verify_data.sql
```

**Note**: If you get a "duplicate key" error when running `02_sample_data.sql`, it means the data is already loaded. You can either:
- Skip the error (data is already there)
- Run the cleanup script first (see Reset Data section below)

### 3. Verify Setup

After running the scripts, you should have:

- **6 tables** created with proper relationships
- **Sample data** for testing and demonstration
- **Indexes** for performance optimization
- **Constraints** for data integrity

## Database Schema

### Tables Overview

| Table | Description | Records |
|-------|-------------|---------|
| `users` | System users (instructors and learners) | 9 |
| `courses` | Training courses | 6 |
| `materials` | Course materials (files, documents) | 7 |
| `enrollments` | Learner enrollments with progress | 10 |
| `feedback` | Course feedback and ratings | 4 |
| `live_sessions` | Scheduled live training sessions | 4 |

### Sample Data Includes

- **4 Instructors** with different specializations
- **5 Learners** with various enrollment statuses
- **6 Courses** across different categories and difficulty levels
- **7 Materials** including PDFs and videos
- **10 Enrollments** with progress tracking
- **4 Feedback entries** with ratings and comments
- **4 Live Sessions** scheduled for future dates

## Default Credentials

### Instructors
- **Email**: sarah.johnson@activwork.com
- **Password**: instructor123
- **Role**: INSTRUCTOR

### Learners
- **Email**: alice.thompson@email.com
- **Password**: learner123
- **Role**: LEARNER

## Database Configuration

Update `application.properties` with your PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/etms
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Troubleshooting

### Common Issues

1. **Permission Denied**: Ensure your user has CREATE and INSERT permissions
2. **Schema Not Found**: Make sure you're connected to the `etms` database
3. **Constraint Violations**: Check that sample data doesn't conflict with existing data

### Reset Database

**Option 1: Reset Data Only (Keep Schema)**
```bash
# This removes all data but keeps the schema, indexes, and triggers
psql -U postgres -d etms -f 00_cleanup_data.sql

# Then repopulate with sample data
psql -U postgres -d etms -f 02_sample_data.sql
```

**Option 2: Complete Reset (Drop Everything)**
```sql
-- Drop and recreate database
DROP DATABASE IF EXISTS etms;
CREATE DATABASE etms;
\c etms;

-- Run setup scripts again
\i 01_create_database.sql
\i 02_sample_data.sql
```

## Performance Notes

- All tables have appropriate indexes for common queries
- Foreign key constraints ensure data integrity
- File size limits are enforced at database level
- Progress tracking is optimized for real-time updates
