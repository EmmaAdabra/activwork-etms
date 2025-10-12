-- =====================================================
-- ETMS Sample Data Script
-- Employment Training Management System
-- =====================================================

-- Set schema
SET search_path TO etms, public;

-- =====================================================
-- SAMPLE USERS
-- =====================================================

-- Instructors (password: instructor123) with enhanced profiles
INSERT INTO users (id, name, email, password_hash, role, phone_number, department, position_level, profile_picture_url, bio, linkedin_url, github_url, timezone, is_active, is_verified) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Dr. Sarah Johnson', 'sarah.johnson@activwork.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'INSTRUCTOR', '+442079460958', 'Software Engineering', 'Senior', 'https://example.com/profiles/sarah.jpg', 'Senior Software Engineer with 10+ years experience in Java and Spring Boot. Passionate about teaching and mentoring developers.', 'https://linkedin.com/in/sarahjohnson', 'https://github.com/sarahjohnson', 'Europe/London', TRUE, TRUE),
('550e8400-e29b-41d4-a716-446655440002', 'Prof. Michael Chen', 'michael.chen@activwork.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'INSTRUCTOR', '+442079460959', 'Data Science', 'Lead', 'https://example.com/profiles/michael.jpg', 'Data Science Lead with expertise in Python, Machine Learning, and Big Data technologies. PhD in Computer Science.', 'https://linkedin.com/in/michaelchen', 'https://github.com/michaelchen', 'Europe/London', TRUE, TRUE),
('550e8400-e29b-41d4-a716-446655440003', 'Emma Williams', 'emma.williams@activwork.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'INSTRUCTOR', '+442079460960', 'Cybersecurity', 'Principal', 'https://example.com/profiles/emma.jpg', 'Principal Cybersecurity Expert with 15+ years in security architecture and penetration testing. CISSP certified.', 'https://linkedin.com/in/emmawilliams', 'https://github.com/emmawilliams', 'Europe/London', TRUE, TRUE),
('550e8400-e29b-41d4-a716-446655440004', 'James Rodriguez', 'james.rodriguez@activwork.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'INSTRUCTOR', '+442079460961', 'DevOps', 'Senior', 'https://example.com/profiles/james.jpg', 'Senior DevOps Engineer specializing in Docker, Kubernetes, and cloud technologies. AWS and Azure certified.', 'https://linkedin.com/in/jamesrodriguez', 'https://github.com/jamesrodriguez', 'Europe/London', TRUE, TRUE);

-- Learners (password: learner123)
INSERT INTO users (id, name, email, password_hash, role, phone_number, department, position_level, is_active) VALUES
('550e8400-e29b-41d4-a716-446655440010', 'Alice Thompson', 'alice.thompson@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'LEARNER', '+442079460970', 'Marketing', 'Junior', TRUE),
('550e8400-e29b-41d4-a716-446655440011', 'Bob Smith', 'bob.smith@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'LEARNER', '+442079460971', 'Finance', 'Mid-level', TRUE),
('550e8400-e29b-41d4-a716-446655440012', 'Carol Davis', 'carol.davis@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'LEARNER', '+442079460972', 'Operations', 'Senior', TRUE),
('550e8400-e29b-41d4-a716-446655440013', 'David Wilson', 'david.wilson@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'LEARNER', '+442079460973', 'HR', 'Manager', TRUE),
('550e8400-e29b-41d4-a716-446655440014', 'Eva Brown', 'eva.brown@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'LEARNER', '+442079460974', 'Sales', 'Director', TRUE);

-- =====================================================
-- SAMPLE COURSES
-- =====================================================

INSERT INTO courses (id, title, summary, description, instructor_id, category, difficulty_level, duration_hours, max_enrollments, price, status, thumbnail_url, video_preview_url, prerequisites, learning_objectives, tags, start_date, end_date, is_featured, is_active) VALUES
('650e8400-e29b-41d4-a716-446655440001', 'Java Spring Boot Fundamentals', 'Master Spring Boot development with hands-on projects', 'This comprehensive course covers Spring Boot fundamentals, including dependency injection, auto-configuration, and building RESTful APIs. Students will learn to create production-ready applications with Spring Boot, Spring Data JPA, and Spring Security.', '550e8400-e29b-41d4-a716-446655440001', 'PROGRAMMING', 'INTERMEDIATE', 40, 25, 299.99, 'PUBLISHED', 'https://example.com/thumbnails/spring-boot.jpg', 'https://example.com/previews/spring-boot-preview.mp4', ARRAY['Basic Java knowledge', 'Understanding of OOP concepts'], ARRAY['Build RESTful APIs with Spring Boot', 'Implement database operations with JPA', 'Secure applications with Spring Security', 'Deploy applications to production'], ARRAY['java', 'spring-boot', 'rest-api', 'jpa', 'security'], CURRENT_TIMESTAMP + INTERVAL '7 days', CURRENT_TIMESTAMP + INTERVAL '30 days', TRUE, TRUE),

('650e8400-e29b-41d4-a716-446655440002', 'Advanced Data Science with Python', 'Data analysis, machine learning, and visualization', 'Learn advanced data science techniques using Python, pandas, scikit-learn, and matplotlib. This course covers statistical analysis, machine learning algorithms, and data visualization for real-world applications.', '550e8400-e29b-41d4-a716-446655440002', 'DATA_SCIENCE', 'ADVANCED', 60, 20, 399.99, 'PUBLISHED', 'https://example.com/thumbnails/data-science.jpg', 'https://example.com/previews/data-science-preview.mp4', ARRAY['Python basics', 'Statistics knowledge'], ARRAY['Master pandas for data manipulation', 'Build ML models with scikit-learn', 'Create visualizations with matplotlib', 'Apply statistical analysis techniques'], ARRAY['python', 'data-science', 'machine-learning', 'pandas', 'matplotlib'], CURRENT_TIMESTAMP + INTERVAL '14 days', CURRENT_TIMESTAMP + INTERVAL '45 days', TRUE, TRUE),

('650e8400-e29b-41d4-a716-446655440003', 'Cybersecurity Essentials', 'Protect systems and data from cyber threats', 'Comprehensive cybersecurity course covering threat analysis, security protocols, encryption, and incident response. Students will learn to identify vulnerabilities and implement security measures.', '550e8400-e29b-41d4-a716-446655440003', 'CYBERSECURITY', 'INTERMEDIATE', 35, 30, 349.99, 'PUBLISHED', 'https://example.com/thumbnails/cybersecurity.jpg', 'https://example.com/previews/cybersecurity-preview.mp4', ARRAY['Basic networking knowledge', 'Understanding of operating systems'], ARRAY['Identify security vulnerabilities', 'Implement security protocols', 'Respond to security incidents', 'Understand encryption methods'], ARRAY['cybersecurity', 'security', 'penetration-testing', 'encryption'], CURRENT_TIMESTAMP + INTERVAL '21 days', CURRENT_TIMESTAMP + INTERVAL '35 days', FALSE, TRUE),

('650e8400-e29b-41d4-a716-446655440004', 'Docker and Kubernetes Mastery', 'Containerization and orchestration', 'Learn Docker containerization and Kubernetes orchestration for modern application deployment. Covers container lifecycle, networking, scaling, and monitoring in production environments.', '550e8400-e29b-41d4-a716-446655440004', 'DEVOPS', 'ADVANCED', 45, 15, 449.99, 'PUBLISHED', 'https://example.com/thumbnails/docker-k8s.jpg', 'https://example.com/previews/docker-k8s-preview.mp4', ARRAY['Linux command line', 'Basic networking'], ARRAY['Master Docker containerization', 'Deploy applications with Kubernetes', 'Implement CI/CD pipelines', 'Monitor production environments'], ARRAY['docker', 'kubernetes', 'devops', 'containers', 'orchestration'], CURRENT_TIMESTAMP + INTERVAL '28 days', CURRENT_TIMESTAMP + INTERVAL '50 days', TRUE, TRUE),

('650e8400-e29b-41d4-a716-446655440005', 'React.js Complete Guide', 'Modern frontend development with React', 'Complete React.js course covering hooks, state management, routing, and testing. Build modern, responsive web applications with React ecosystem tools and best practices.', '550e8400-e29b-41d4-a716-446655440001', 'WEB_DEVELOPMENT', 'INTERMEDIATE', 50, 30, 279.99, 'PUBLISHED', 'https://example.com/thumbnails/react.jpg', 'https://example.com/previews/react-preview.mp4', ARRAY['JavaScript ES6+', 'HTML/CSS basics'], ARRAY['Build React applications', 'Master React hooks', 'Implement state management', 'Create responsive UIs'], ARRAY['react', 'javascript', 'frontend', 'hooks', 'state-management'], CURRENT_TIMESTAMP + INTERVAL '10 days', CURRENT_TIMESTAMP + INTERVAL '40 days', FALSE, TRUE),

('650e8400-e29b-41d4-a716-446655440006', 'Database Design and Optimization', 'SQL, NoSQL, and performance tuning', 'Master database design principles, SQL optimization, and NoSQL databases. Learn indexing strategies, query optimization, and database performance monitoring techniques.', '550e8400-e29b-41d4-a716-446655440002', 'DATABASE', 'INTERMEDIATE', 30, 25, 199.99, 'PUBLISHED', 'https://example.com/thumbnails/database.jpg', 'https://example.com/previews/database-preview.mp4', ARRAY['Basic SQL knowledge', 'Understanding of data structures'], ARRAY['Design efficient databases', 'Optimize SQL queries', 'Work with NoSQL databases', 'Implement database monitoring'], ARRAY['sql', 'database', 'optimization', 'nosql', 'performance'], CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP + INTERVAL '25 days', FALSE, TRUE);

-- =====================================================
-- SAMPLE MATERIALS
-- =====================================================

INSERT INTO materials (id, course_id, filename, original_filename, mime_type, material_type, path, file_size, duration_seconds, thumbnail_url, is_downloadable, is_required, display_order, description, is_active) VALUES
-- Java Spring Boot course materials
('750e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440001', 'spring-boot-introduction.pdf', 'Spring Boot Introduction Guide.pdf', 'application/pdf', 'PDF', '/uploads/course-650e8400-e29b-41d4-a716-446655440001/materials/spring-boot-introduction.pdf', 2048576, NULL, NULL, TRUE, TRUE, 1, 'Introduction to Spring Boot framework', TRUE),
('750e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440001', 'rest-api-tutorial.mp4', 'REST API Tutorial.mp4', 'video/mp4', 'VIDEO', '/uploads/course-650e8400-e29b-41d4-a716-446655440001/materials/rest-api-tutorial.mp4', 52428800, 1800, 'https://example.com/thumbnails/rest-api-tutorial.jpg', TRUE, TRUE, 2, 'Building RESTful APIs with Spring Boot', TRUE),
('750e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440001', 'spring-security-guide.pdf', 'Spring Security Guide.pdf', 'application/pdf', 'PDF', '/uploads/course-650e8400-e29b-41d4-a716-446655440001/materials/spring-security-guide.pdf', 1536000, NULL, NULL, TRUE, TRUE, 3, 'Spring Security implementation guide', TRUE),

-- Data Science course materials
('750e8400-e29b-41d4-a716-446655440004', '650e8400-e29b-41d4-a716-446655440002', 'python-data-analysis.pdf', 'Python Data Analysis Guide.pdf', 'application/pdf', 'PDF', '/uploads/course-650e8400-e29b-41d4-a716-446655440002/materials/python-data-analysis.pdf', 3072000, NULL, NULL, TRUE, TRUE, 1, 'Python for data analysis fundamentals', TRUE),
('750e8400-e29b-41d4-a716-446655440005', '650e8400-e29b-41d4-a716-446655440002', 'machine-learning-basics.mp4', 'Machine Learning Basics.mp4', 'video/mp4', 'VIDEO', '/uploads/course-650e8400-e29b-41d4-a716-446655440002/materials/machine-learning-basics.mp4', 41943040, 2400, 'https://example.com/thumbnails/ml-basics.jpg', TRUE, TRUE, 2, 'Introduction to machine learning concepts', TRUE),

-- Cybersecurity course materials
('750e8400-e29b-41d4-a716-446655440006', '650e8400-e29b-41d4-a716-446655440003', 'cybersecurity-fundamentals.pdf', 'Cybersecurity Fundamentals.pdf', 'application/pdf', 'PDF', '/uploads/course-650e8400-e29b-41d4-a716-446655440003/materials/cybersecurity-fundamentals.pdf', 2560000, NULL, NULL, TRUE, TRUE, 1, 'Cybersecurity principles and practices', TRUE),
('750e8400-e29b-41d4-a716-446655440007', '650e8400-e29b-41d4-a716-446655440003', 'penetration-testing-guide.pdf', 'Penetration Testing Guide.pdf', 'application/pdf', 'PDF', '/uploads/course-650e8400-e29b-41d4-a716-446655440003/materials/penetration-testing-guide.pdf', 1800000, NULL, NULL, TRUE, TRUE, 2, 'Penetration testing methodologies', TRUE);

-- =====================================================
-- SAMPLE ENROLLMENTS
-- =====================================================

INSERT INTO enrollments (id, learner_id, course_id, status, progress_percent, completed_materials, total_materials, last_accessed) VALUES
-- Alice Thompson enrollments
('850e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440010', '650e8400-e29b-41d4-a716-446655440001', 'ACTIVE', 25.50, 1, 3, CURRENT_TIMESTAMP - INTERVAL '2 days'),
('850e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440010', '650e8400-e29b-41d4-a716-446655440005', 'ACTIVE', 60.00, 2, 3, CURRENT_TIMESTAMP - INTERVAL '1 day'),

-- Bob Smith enrollments
('850e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440011', '650e8400-e29b-41d4-a716-446655440002', 'ACTIVE', 40.00, 1, 2, CURRENT_TIMESTAMP - INTERVAL '3 days'),
('850e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440011', '650e8400-e29b-41d4-a716-446655440006', 'ACTIVE', 50.00, 0, 0, CURRENT_TIMESTAMP - INTERVAL '1 week'),

-- Carol Davis enrollments
('850e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440012', '650e8400-e29b-41d4-a716-446655440003', 'ACTIVE', 15.00, 0, 2, CURRENT_TIMESTAMP - INTERVAL '5 days'),
('850e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440012', '650e8400-e29b-41d4-a716-446655440004', 'ACTIVE', 30.00, 1, 3, CURRENT_TIMESTAMP - INTERVAL '2 days'),

-- David Wilson enrollments
('850e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440013', '650e8400-e29b-41d4-a716-446655440001', 'COMPLETED', 100.00, 3, 3, CURRENT_TIMESTAMP - INTERVAL '2 weeks'),
('850e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440013', '650e8400-e29b-41d4-a716-446655440005', 'ACTIVE', 75.00, 2, 3, CURRENT_TIMESTAMP - INTERVAL '1 day'),

-- Eva Brown enrollments
('850e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440014', '650e8400-e29b-41d4-a716-446655440002', 'ACTIVE', 20.00, 0, 2, CURRENT_TIMESTAMP - INTERVAL '4 days'),
('850e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440014', '650e8400-e29b-41d4-a716-446655440006', 'ACTIVE', 50.00, 1, 3, CURRENT_TIMESTAMP - INTERVAL '1 day');

-- =====================================================
-- SAMPLE FEEDBACK
-- =====================================================

INSERT INTO feedback (id, learner_id, course_id, rating, comment, is_visible) VALUES
('950e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440011', '650e8400-e29b-41d4-a716-446655440006', 5, 'Excellent course! The instructor explained complex database concepts very clearly. Highly recommended for anyone looking to improve their database skills.', TRUE),
('950e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440013', '650e8400-e29b-41d4-a716-446655440001', 4, 'Great Spring Boot course with practical examples. The hands-on projects really helped me understand the concepts better.', TRUE),
('950e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440010', '650e8400-e29b-41d4-a716-446655440005', 5, 'Amazing React course! The instructor covered everything from basics to advanced topics. The code examples were very helpful.', TRUE),
('950e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440012', '650e8400-e29b-41d4-a716-446655440003', 4, 'Good cybersecurity course with real-world examples. Would have liked more hands-on labs, but overall very informative.', TRUE);

-- =====================================================
-- SAMPLE LIVE SESSIONS
-- =====================================================

INSERT INTO live_sessions (id, course_id, title, description, starts_at, ends_at, duration_minutes, meeting_link, is_active) VALUES
('a50e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440001', 'Spring Boot Q&A Session', 'Live Q&A session for Spring Boot course students', CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP + INTERVAL '3 days' + INTERVAL '60 minutes', 60, 'https://meet.google.com/abc-defg-hij', TRUE),
('a50e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440002', 'Data Science Project Review', 'Review of final projects and advanced topics', CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP + INTERVAL '5 days' + INTERVAL '90 minutes', 90, 'https://meet.google.com/xyz-uvw-rst', TRUE),
('a50e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440003', 'Cybersecurity Workshop', 'Hands-on cybersecurity workshop with live demonstrations', CURRENT_TIMESTAMP + INTERVAL '7 days', CURRENT_TIMESTAMP + INTERVAL '7 days' + INTERVAL '120 minutes', 120, 'https://meet.google.com/mno-pqr-stu', TRUE),
('a50e8400-e29b-41d4-a716-446655440004', '650e8400-e29b-41d4-a716-446655440004', 'Docker Best Practices', 'Advanced Docker and Kubernetes best practices session', CURRENT_TIMESTAMP + INTERVAL '10 days', CURRENT_TIMESTAMP + INTERVAL '10 days' + INTERVAL '75 minutes', 75, 'https://meet.google.com/vwx-yza-bcd', TRUE);

-- =====================================================
-- UPDATE STATISTICS
-- =====================================================

-- Update last_login for some users
UPDATE users SET last_login = CURRENT_TIMESTAMP - INTERVAL '1 day' WHERE id IN (
    '550e8400-e29b-41d4-a716-446655440001',
    '550e8400-e29b-41d4-a716-446655440010',
    '550e8400-e29b-41d4-a716-446655440011'
);

-- Update course updated_at timestamps
UPDATE courses SET updated_at = CURRENT_TIMESTAMP - INTERVAL '1 week' WHERE id = '650e8400-e29b-41d4-a716-446655440001';
UPDATE courses SET updated_at = CURRENT_TIMESTAMP - INTERVAL '3 days' WHERE id = '650e8400-e29b-41d4-a716-446655440002';

-- =====================================================
-- SAMPLE DATA FOR INNOVATIVE TABLES
-- =====================================================

-- Course Prerequisites
INSERT INTO course_prerequisites (id, course_id, prerequisite_course_id, is_mandatory) VALUES
('b50e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440006', TRUE), -- Data Science requires Database course
('b50e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440004', '650e8400-e29b-41d4-a716-446655440001', FALSE); -- Docker course recommends Spring Boot

-- Material Progress (detailed tracking)
-- Only tracking progress for enrollments that have materials in the database
INSERT INTO material_progress (id, enrollment_id, material_id, is_completed, completion_percent, time_spent_minutes, last_position_seconds, completed_at) VALUES
-- Alice Thompson's progress in Spring Boot course (enrollment: 850e8400-e29b-41d4-a716-446655440001)
('c50e8400-e29b-41d4-a716-446655440001', '850e8400-e29b-41d4-a716-446655440001', '750e8400-e29b-41d4-a716-446655440001', TRUE, 100.00, 45, NULL, CURRENT_TIMESTAMP - INTERVAL '2 days'),
('c50e8400-e29b-41d4-a716-446655440002', '850e8400-e29b-41d4-a716-446655440001', '750e8400-e29b-41d4-a716-446655440002', FALSE, 60.00, 20, 1080, NULL);

-- Notifications
INSERT INTO notifications (id, user_id, title, message, type, is_read, action_url, metadata) VALUES
('d50e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440010', 'Welcome to Spring Boot Course!', 'You have successfully enrolled in the Java Spring Boot Fundamentals course. Start your learning journey now!', 'COURSE_ENROLLMENT', FALSE, '/courses/650e8400-e29b-41d4-a716-446655440001', '{"course_id": "650e8400-e29b-41d4-a716-446655440001", "course_title": "Java Spring Boot Fundamentals"}'),
('d50e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440011', 'Course Completed!', 'Congratulations! You have completed the Database Design and Optimization course. Your certificate is ready for download.', 'CERTIFICATE_READY', TRUE, '/certificates/850e8400-e29b-41d4-a716-446655440004', '{"course_id": "650e8400-e29b-41d4-a716-446655440006", "enrollment_id": "850e8400-e29b-41d4-a716-446655440004"}'),
('d50e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', 'New Student Enrolled', 'Alice Thompson has enrolled in your Java Spring Boot Fundamentals course.', 'INSTRUCTOR_NOTIFICATION', FALSE, '/instructor/courses/650e8400-e29b-41d4-a716-446655440001/enrollments', '{"course_id": "650e8400-e29b-41d4-a716-446655440001", "learner_name": "Alice Thompson"}'),
('d50e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440010', 'Live Session Reminder', 'Your live Q&A session for Spring Boot course starts in 1 hour. Join the session using the provided link.', 'SESSION_REMINDER', FALSE, '/sessions/a50e8400-e29b-41d4-a716-446655440001', '{"session_id": "a50e8400-e29b-41d4-a716-446655440001", "session_title": "Spring Boot Q&A Session"}');

-- Course Analytics (sample data for the last 30 days)
INSERT INTO course_analytics (id, course_id, date, views, enrollments, completions, average_rating, total_ratings, revenue) VALUES
('e50e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440001', CURRENT_DATE - INTERVAL '30 days', 15, 2, 0, 0.00, 0, 599.98),
('e50e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440001', CURRENT_DATE - INTERVAL '25 days', 23, 1, 0, 0.00, 0, 299.99),
('e50e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440001', CURRENT_DATE - INTERVAL '20 days', 18, 0, 0, 0.00, 0, 0.00),
('e50e8400-e29b-41d4-a716-446655440004', '650e8400-e29b-41d4-a716-446655440001', CURRENT_DATE - INTERVAL '15 days', 31, 3, 1, 4.50, 2, 899.97),
('e50e8400-e29b-41d4-a716-446655440005', '650e8400-e29b-41d4-a716-446655440001', CURRENT_DATE - INTERVAL '10 days', 27, 1, 0, 4.50, 2, 299.99),
('e50e8400-e29b-41d4-a716-446655440006', '650e8400-e29b-41d4-a716-446655440001', CURRENT_DATE - INTERVAL '5 days', 35, 2, 1, 4.25, 3, 599.98),
('e50e8400-e29b-41d4-a716-446655440007', '650e8400-e29b-41d4-a716-446655440001', CURRENT_DATE, 42, 1, 0, 4.25, 3, 299.99),
('e50e8400-e29b-41d4-a716-446655440008', '650e8400-e29b-41d4-a716-446655440006', CURRENT_DATE - INTERVAL '20 days', 12, 1, 1, 5.00, 1, 199.99),
('e50e8400-e29b-41d4-a716-446655440009', '650e8400-e29b-41d4-a716-446655440006', CURRENT_DATE - INTERVAL '15 days', 8, 0, 0, 5.00, 1, 0.00),
('e50e8400-e29b-41d4-a716-446655440010', '650e8400-e29b-41d4-a716-446655440006', CURRENT_DATE - INTERVAL '10 days', 15, 1, 0, 5.00, 1, 199.99);

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Display summary statistics
SELECT 
    'Users' as table_name, 
    COUNT(*) as total_records,
    COUNT(CASE WHEN role = 'INSTRUCTOR' THEN 1 END) as instructors,
    COUNT(CASE WHEN role = 'LEARNER' THEN 1 END) as learners
FROM users
UNION ALL
SELECT 
    'Courses' as table_name, 
    COUNT(*) as total_records,
    COUNT(CASE WHEN is_active = true THEN 1 END) as active_courses,
    0 as learners
FROM courses
UNION ALL
SELECT 
    'Enrollments' as table_name, 
    COUNT(*) as total_records,
    COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) as active_enrollments,
    COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed_enrollments
FROM enrollments
UNION ALL
SELECT 
    'Materials' as table_name, 
    COUNT(*) as total_records,
    COUNT(CASE WHEN is_active = true THEN 1 END) as active_materials,
    0 as learners
FROM materials
UNION ALL
SELECT 
    'Feedback' as table_name, 
    COUNT(*) as total_records,
    COUNT(CASE WHEN is_visible = true THEN 1 END) as visible_feedback,
    0 as learners
FROM feedback
UNION ALL
SELECT 
    'Live Sessions' as table_name, 
    COUNT(*) as total_records,
    COUNT(CASE WHEN is_active = true THEN 1 END) as active_sessions,
    0 as learners
FROM live_sessions;
