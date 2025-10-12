-- =====================================================
-- ETMS Database Creation Script
-- Employment Training Management System
-- =====================================================

-- Create database (run as superuser)
-- CREATE DATABASE etms;
-- \c etms;

-- Create schema for better organization
CREATE SCHEMA IF NOT EXISTS etms;
SET search_path TO etms, public;

-- Enable UUID extension for better ID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- ENUMS
-- =====================================================

-- User roles
CREATE TYPE user_role AS ENUM ('INSTRUCTOR', 'LEARNER');

-- Course categories
CREATE TYPE course_category AS ENUM (
    'PROGRAMMING', 
    'WEB_DEVELOPMENT', 
    'DATABASE', 
    'DEVOPS', 
    'CYBERSECURITY', 
    'DATA_SCIENCE', 
    'MOBILE_DEVELOPMENT', 
    'CLOUD_COMPUTING',
    'SOFTWARE_TESTING',
    'PROJECT_MANAGEMENT'
);

-- Course difficulty levels
CREATE TYPE difficulty_level AS ENUM ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT');

-- Enrollment status
CREATE TYPE enrollment_status AS ENUM ('ACTIVE', 'COMPLETED', 'CANCELLED', 'SUSPENDED');

-- Material types for better organization
CREATE TYPE material_type AS ENUM (
    'VIDEO', 
    'PDF', 
    'DOCUMENT', 
    'PRESENTATION', 
    'AUDIO', 
    'IMAGE', 
    'CODE_SAMPLE', 
    'EXERCISE',
    'QUIZ',
    'CERTIFICATE'
);

-- Course status for better lifecycle management
CREATE TYPE course_status AS ENUM ('DRAFT', 'PUBLISHED', 'ARCHIVED', 'SUSPENDED');

-- Session status for live session management
CREATE TYPE session_status AS ENUM ('SCHEDULED', 'LIVE', 'COMPLETED', 'CANCELLED');

-- =====================================================
-- TABLES
-- =====================================================

-- Users table with enhanced features
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    phone_number VARCHAR(20),
    department VARCHAR(100),
    position_level VARCHAR(50),
    profile_picture_url VARCHAR(500),
    bio TEXT,
    linkedin_url VARCHAR(255),
    github_url VARCHAR(255),
    timezone VARCHAR(50) DEFAULT 'UTC',
    language_preference VARCHAR(10) DEFAULT 'en',
    notification_preferences JSONB DEFAULT '{"email": true, "push": true, "sms": false}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP WITH TIME ZONE,
    last_activity TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMP WITH TIME ZONE,
    
    -- Constraints
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_phone_format CHECK (phone_number IS NULL OR phone_number ~* '^\+?[1-9]\d{1,14}$'),
    CONSTRAINT chk_name_length CHECK (LENGTH(name) >= 2 AND LENGTH(name) <= 100),
    CONSTRAINT chk_linkedin_url CHECK (linkedin_url IS NULL OR linkedin_url ~* '^https://linkedin\.com/in/'),
    CONSTRAINT chk_github_url CHECK (github_url IS NULL OR github_url ~* '^https://github\.com/')
);

-- Courses table with enhanced features
CREATE TABLE courses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500),
    description TEXT,
    instructor_id UUID NOT NULL,
    category course_category NOT NULL,
    difficulty_level difficulty_level NOT NULL,
    duration_hours INTEGER NOT NULL CHECK (duration_hours > 0),
    max_enrollments INTEGER NOT NULL CHECK (max_enrollments > 0),
    price DECIMAL(10,2) DEFAULT 0.00 CHECK (price >= 0),
    status course_status DEFAULT 'DRAFT',
    thumbnail_url VARCHAR(500),
    video_preview_url VARCHAR(500),
    prerequisites TEXT[],
    learning_objectives TEXT[],
    tags TEXT[],
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    enrollment_deadline TIMESTAMP WITH TIME ZONE,
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    view_count INTEGER DEFAULT 0,
    enrollment_count INTEGER DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0.00 CHECK (average_rating >= 0 AND average_rating <= 5),
    total_ratings INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP WITH TIME ZONE,
    
    -- Foreign key
    CONSTRAINT fk_course_instructor FOREIGN KEY (instructor_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT chk_title_length CHECK (LENGTH(title) >= 5 AND LENGTH(title) <= 200),
    CONSTRAINT chk_summary_length CHECK (summary IS NULL OR LENGTH(summary) <= 500),
    CONSTRAINT chk_duration_reasonable CHECK (duration_hours <= 200),
    CONSTRAINT chk_max_enrollments_reasonable CHECK (max_enrollments <= 100),
    CONSTRAINT chk_enrollment_count CHECK (enrollment_count <= max_enrollments),
    CONSTRAINT chk_dates_logical CHECK (end_date IS NULL OR start_date IS NULL OR end_date > start_date),
    CONSTRAINT chk_enrollment_deadline CHECK (enrollment_deadline IS NULL OR start_date IS NULL OR enrollment_deadline <= start_date)
);

-- Materials table with enhanced features
CREATE TABLE materials (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_id UUID NOT NULL,
    filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    material_type material_type NOT NULL,
    path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL CHECK (file_size > 0),
    duration_seconds INTEGER, -- For video/audio files
    thumbnail_url VARCHAR(500), -- For video files
    download_count INTEGER DEFAULT 0,
    view_count INTEGER DEFAULT 0,
    is_downloadable BOOLEAN DEFAULT TRUE,
    is_required BOOLEAN DEFAULT FALSE,
    display_order INTEGER DEFAULT 0,
    uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Foreign key
    CONSTRAINT fk_material_course FOREIGN KEY (course_id) 
        REFERENCES courses(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT chk_file_size_limit CHECK (file_size <= 52428800), -- 50MB limit
    CONSTRAINT chk_filename_length CHECK (LENGTH(filename) >= 1 AND LENGTH(filename) <= 255),
    CONSTRAINT chk_duration_positive CHECK (duration_seconds IS NULL OR duration_seconds > 0),
    CONSTRAINT chk_display_order_positive CHECK (display_order >= 0)
);

-- Enrollments table with enhanced progress tracking
CREATE TABLE enrollments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    learner_id UUID NOT NULL,
    course_id UUID NOT NULL,
    enrolled_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status enrollment_status DEFAULT 'ACTIVE',
    progress_percent DECIMAL(5,2) DEFAULT 0.00 CHECK (progress_percent >= 0 AND progress_percent <= 100),
    completed_materials INTEGER DEFAULT 0 CHECK (completed_materials >= 0),
    total_materials INTEGER DEFAULT 0 CHECK (total_materials >= 0),
    time_spent_minutes INTEGER DEFAULT 0 CHECK (time_spent_minutes >= 0),
    last_accessed TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completion_date TIMESTAMP WITH TIME ZONE,
    certificate_issued BOOLEAN DEFAULT FALSE,
    certificate_url VARCHAR(500),
    notes TEXT,
    
    -- Foreign keys
    CONSTRAINT fk_enrollment_learner FOREIGN KEY (learner_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) 
        REFERENCES courses(id) ON DELETE CASCADE,
    
    -- Unique constraint to prevent duplicate enrollments
    CONSTRAINT uk_enrollment_learner_course UNIQUE (learner_id, course_id),
    
    -- Note: Role validation will be handled at the application level
    -- CONSTRAINT chk_learner_role CHECK (
    --     EXISTS (SELECT 1 FROM users WHERE id = learner_id AND role = 'LEARNER')
    -- )
    CONSTRAINT chk_completed_materials CHECK (completed_materials <= total_materials),
    CONSTRAINT chk_completion_date CHECK (
        completion_date IS NULL OR 
        (status = 'COMPLETED' AND completion_date IS NOT NULL)
    )
);

-- Feedback table
CREATE TABLE feedback (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    learner_id UUID NOT NULL,
    course_id UUID NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_visible BOOLEAN DEFAULT TRUE,
    
    -- Foreign keys
    CONSTRAINT fk_feedback_learner FOREIGN KEY (learner_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_feedback_course FOREIGN KEY (course_id) 
        REFERENCES courses(id) ON DELETE CASCADE,
    
    -- Unique constraint to prevent duplicate feedback
    CONSTRAINT uk_feedback_learner_course UNIQUE (learner_id, course_id)
    
    -- Note: Role validation will be handled at the application level
    -- CONSTRAINT chk_feedback_learner_role CHECK (
    --     EXISTS (SELECT 1 FROM users WHERE id = learner_id AND role = 'LEARNER')
    -- )
);

-- Live Sessions table with enhanced features
CREATE TABLE live_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    starts_at TIMESTAMP WITH TIME ZONE NOT NULL,
    ends_at TIMESTAMP WITH TIME ZONE NOT NULL,
    duration_minutes INTEGER NOT NULL CHECK (duration_minutes > 0),
    status session_status DEFAULT 'SCHEDULED',
    meeting_link VARCHAR(500),
    meeting_id VARCHAR(100),
    meeting_password VARCHAR(50),
    max_participants INTEGER DEFAULT 100 CHECK (max_participants > 0),
    recording_url VARCHAR(500),
    recording_available BOOLEAN DEFAULT FALSE,
    attendance_count INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key
    CONSTRAINT fk_session_course FOREIGN KEY (course_id) 
        REFERENCES courses(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT chk_session_title_length CHECK (LENGTH(title) >= 1 AND LENGTH(title) <= 200),
    CONSTRAINT chk_session_duration_reasonable CHECK (duration_minutes <= 480), -- 8 hours max
    CONSTRAINT chk_session_times CHECK (ends_at > starts_at),
    CONSTRAINT chk_attendance_count CHECK (attendance_count <= max_participants)
);

-- =====================================================
-- INNOVATIVE ADDITIONAL TABLES
-- =====================================================

-- Course Prerequisites table for better course management
CREATE TABLE course_prerequisites (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_id UUID NOT NULL,
    prerequisite_course_id UUID NOT NULL,
    is_mandatory BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_prereq_course FOREIGN KEY (course_id) 
        REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_prereq_prerequisite FOREIGN KEY (prerequisite_course_id) 
        REFERENCES courses(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT chk_no_self_prerequisite CHECK (course_id != prerequisite_course_id),
    CONSTRAINT uk_course_prerequisite UNIQUE (course_id, prerequisite_course_id)
);

-- Material Progress tracking for detailed analytics
CREATE TABLE material_progress (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    enrollment_id UUID NOT NULL,
    material_id UUID NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completion_percent DECIMAL(5,2) DEFAULT 0.00 CHECK (completion_percent >= 0 AND completion_percent <= 100),
    time_spent_minutes INTEGER DEFAULT 0 CHECK (time_spent_minutes >= 0),
    last_position_seconds INTEGER DEFAULT 0 CHECK (last_position_seconds >= 0), -- For videos
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_progress_enrollment FOREIGN KEY (enrollment_id) 
        REFERENCES enrollments(id) ON DELETE CASCADE,
    CONSTRAINT fk_progress_material FOREIGN KEY (material_id) 
        REFERENCES materials(id) ON DELETE CASCADE,
    
    -- Unique constraint
    CONSTRAINT uk_enrollment_material UNIQUE (enrollment_id, material_id)
);

-- Notifications table for user engagement
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL, -- 'COURSE_UPDATE', 'SESSION_REMINDER', 'CERTIFICATE_READY', etc.
    is_read BOOLEAN DEFAULT FALSE,
    action_url VARCHAR(500),
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP WITH TIME ZONE,
    
    -- Foreign key
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT chk_notification_title_length CHECK (LENGTH(title) >= 1 AND LENGTH(title) <= 200)
);

-- Course Analytics table for instructor insights
CREATE TABLE course_analytics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_id UUID NOT NULL,
    date DATE NOT NULL,
    views INTEGER DEFAULT 0,
    enrollments INTEGER DEFAULT 0,
    completions INTEGER DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    total_ratings INTEGER DEFAULT 0,
    revenue DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key
    CONSTRAINT fk_analytics_course FOREIGN KEY (course_id) 
        REFERENCES courses(id) ON DELETE CASCADE,
    
    -- Unique constraint
    CONSTRAINT uk_course_date UNIQUE (course_id, date),
    
    -- Constraints
    CONSTRAINT chk_positive_values CHECK (
        views >= 0 AND enrollments >= 0 AND completions >= 0 AND 
        total_ratings >= 0 AND revenue >= 0
    )
);

-- =====================================================
-- INDEXES for Performance
-- =====================================================

-- User indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(is_active);

-- Course indexes
CREATE INDEX idx_courses_instructor ON courses(instructor_id);
CREATE INDEX idx_courses_category ON courses(category);
CREATE INDEX idx_courses_difficulty ON courses(difficulty_level);
CREATE INDEX idx_courses_active ON courses(is_active);
CREATE INDEX idx_courses_created_at ON courses(created_at);

-- Material indexes
CREATE INDEX idx_materials_course ON materials(course_id);
CREATE INDEX idx_materials_active ON materials(is_active);

-- Enrollment indexes
CREATE INDEX idx_enrollments_learner ON enrollments(learner_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);

-- Feedback indexes
CREATE INDEX idx_feedback_learner ON feedback(learner_id);
CREATE INDEX idx_feedback_course ON feedback(course_id);
CREATE INDEX idx_feedback_rating ON feedback(rating);

-- Live Session indexes
CREATE INDEX idx_sessions_course ON live_sessions(course_id);
CREATE INDEX idx_sessions_starts_at ON live_sessions(starts_at);
CREATE INDEX idx_sessions_active ON live_sessions(is_active);
CREATE INDEX idx_sessions_status ON live_sessions(status);

-- Additional table indexes
CREATE INDEX idx_prerequisites_course ON course_prerequisites(course_id);
CREATE INDEX idx_prerequisites_prerequisite ON course_prerequisites(prerequisite_course_id);
CREATE INDEX idx_material_progress_enrollment ON material_progress(enrollment_id);
CREATE INDEX idx_material_progress_material ON material_progress(material_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_unread ON notifications(user_id, is_read) WHERE is_read = FALSE;
CREATE INDEX idx_analytics_course ON course_analytics(course_id);
CREATE INDEX idx_analytics_date ON course_analytics(date);

-- Composite indexes for common queries
CREATE INDEX idx_courses_status_category ON courses(status, category);
CREATE INDEX idx_courses_instructor_status ON courses(instructor_id, status);
CREATE INDEX idx_enrollments_learner_status ON enrollments(learner_id, status);
CREATE INDEX idx_materials_course_type ON materials(course_id, material_type);
CREATE INDEX idx_feedback_course_rating ON feedback(course_id, rating);

-- =====================================================
-- TRIGGERS for Updated At
-- =====================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger for courses table
CREATE TRIGGER update_courses_updated_at 
    BEFORE UPDATE ON courses 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Trigger for live_sessions table
CREATE TRIGGER update_sessions_updated_at 
    BEFORE UPDATE ON live_sessions 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Trigger for material_progress table
CREATE TRIGGER update_material_progress_updated_at 
    BEFORE UPDATE ON material_progress 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- SOPHISTICATED FUNCTIONS
-- =====================================================

-- Function to update course enrollment count
CREATE OR REPLACE FUNCTION update_course_enrollment_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE courses 
        SET enrollment_count = enrollment_count + 1 
        WHERE id = NEW.course_id;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE courses 
        SET enrollment_count = enrollment_count - 1 
        WHERE id = OLD.course_id;
        RETURN OLD;
    ELSIF TG_OP = 'UPDATE' THEN
        IF OLD.course_id != NEW.course_id THEN
            UPDATE courses 
            SET enrollment_count = enrollment_count - 1 
            WHERE id = OLD.course_id;
            UPDATE courses 
            SET enrollment_count = enrollment_count + 1 
            WHERE id = NEW.course_id;
        END IF;
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ language 'plpgsql';

-- Function to update course average rating
CREATE OR REPLACE FUNCTION update_course_rating()
RETURNS TRIGGER AS $$
DECLARE
    course_avg_rating DECIMAL(3,2);
    course_total_ratings INTEGER;
BEGIN
    -- Calculate new average rating for the course
    SELECT 
        COALESCE(AVG(rating), 0.00),
        COUNT(*)
    INTO course_avg_rating, course_total_ratings
    FROM feedback 
    WHERE course_id = COALESCE(NEW.course_id, OLD.course_id)
    AND is_visible = TRUE;
    
    -- Update course table
    UPDATE courses 
    SET 
        average_rating = course_avg_rating,
        total_ratings = course_total_ratings
    WHERE id = COALESCE(NEW.course_id, OLD.course_id);
    
    RETURN COALESCE(NEW, OLD);
END;
$$ language 'plpgsql';

-- Function to update enrollment progress
CREATE OR REPLACE FUNCTION update_enrollment_progress()
RETURNS TRIGGER AS $$
DECLARE
    v_total_materials INTEGER;
    v_completed_materials INTEGER;
    v_progress_percent DECIMAL(5,2);
BEGIN
    -- Get total materials for the course
    SELECT COUNT(*) INTO v_total_materials
    FROM materials 
    WHERE course_id = (SELECT course_id FROM enrollments WHERE id = NEW.enrollment_id)
    AND is_active = TRUE;
    
    -- Get completed materials for this enrollment
    SELECT COUNT(*) INTO v_completed_materials
    FROM material_progress 
    WHERE enrollment_id = NEW.enrollment_id 
    AND is_completed = TRUE;
    
    -- Calculate progress percentage
    IF v_total_materials > 0 THEN
        v_progress_percent := (v_completed_materials::DECIMAL / v_total_materials::DECIMAL) * 100;
    ELSE
        v_progress_percent := 0;
    END IF;
    
    -- Update enrollment
    UPDATE enrollments 
    SET 
        progress_percent = v_progress_percent,
        completed_materials = v_completed_materials,
        total_materials = v_total_materials,
        status = CASE 
            WHEN v_progress_percent = 100 THEN 'COMPLETED'::enrollment_status
            ELSE status
        END,
        completion_date = CASE 
            WHEN v_progress_percent = 100 AND status != 'COMPLETED' THEN CURRENT_TIMESTAMP
            ELSE completion_date
        END
    WHERE id = NEW.enrollment_id;
    
    RETURN NEW;
END;
$$ language 'plpgsql';

-- =====================================================
-- TRIGGERS for Business Logic
-- =====================================================

-- Trigger to update course enrollment count
CREATE TRIGGER trigger_update_enrollment_count
    AFTER INSERT OR UPDATE OR DELETE ON enrollments
    FOR EACH ROW EXECUTE FUNCTION update_course_enrollment_count();

-- Trigger to update course rating
CREATE TRIGGER trigger_update_course_rating
    AFTER INSERT OR UPDATE OR DELETE ON feedback
    FOR EACH ROW EXECUTE FUNCTION update_course_rating();

-- Trigger to update enrollment progress
CREATE TRIGGER trigger_update_enrollment_progress
    AFTER INSERT OR UPDATE ON material_progress
    FOR EACH ROW EXECUTE FUNCTION update_enrollment_progress();

-- =====================================================
-- COMMENTS for Documentation
-- =====================================================

COMMENT ON SCHEMA etms IS 'Employment Training Management System database schema';
COMMENT ON TABLE users IS 'System users (instructors and learners)';
COMMENT ON TABLE courses IS 'Training courses created by instructors';
COMMENT ON TABLE materials IS 'Course materials (files, documents, videos)';
COMMENT ON TABLE enrollments IS 'Learner enrollments in courses with progress tracking';
COMMENT ON TABLE feedback IS 'Learner feedback and ratings for courses';
COMMENT ON TABLE live_sessions IS 'Live training sessions scheduled by instructors';

-- Column comments
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';
COMMENT ON COLUMN enrollments.progress_percent IS 'Course completion percentage (0-100)';
COMMENT ON COLUMN enrollments.completed_materials IS 'Number of materials marked as completed';
COMMENT ON COLUMN materials.file_size IS 'File size in bytes (max 50MB)';
COMMENT ON COLUMN live_sessions.duration_minutes IS 'Session duration in minutes (max 8 hours)';
