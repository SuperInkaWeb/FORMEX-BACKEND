-- ======================================================
-- 1. CREAR BASE DE DATOS
-- ======================================================
CREATE DATABASE IF NOT EXISTS formex_db;
USE formex_db;

-- ======================================================
-- 2. ROLES
-- ======================================================
CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL,
                       UNIQUE KEY uk_roles_name (name)
);

INSERT INTO roles (name) VALUES
                             ('ROLE_ADMIN'),
                             ('ROLE_STUDENT'),
                             ('ROLE_INSTRUCTOR')
ON DUPLICATE KEY UPDATE name = name;

-- ======================================================
-- 3. USUARIOS
-- ======================================================
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),
                       avatar_url VARCHAR(255),
                       enabled BIT(1) DEFAULT 1,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       UNIQUE KEY uk_users_email (email)
);

-- ======================================================
-- 4. USUARIOS - ROLES
-- ======================================================
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
                                REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id)
                                REFERENCES roles(id) ON DELETE CASCADE
);

-- ======================================================
-- 5. TOKENS DE RECUPERACIÓN
-- ======================================================
CREATE TABLE password_reset_tokens (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       token VARCHAR(255) NOT NULL,
                                       user_id BIGINT NOT NULL,
                                       expiry_date DATETIME NOT NULL,
                                       CONSTRAINT fk_token_user FOREIGN KEY (user_id)
                                           REFERENCES users(id) ON DELETE CASCADE
);

-- ======================================================
-- 6. CATEGORÍAS
-- ======================================================
CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT
);

INSERT INTO categories (name, description) VALUES
                                               ('Programación', 'Desarrollo de software, web y móvil'),
                                               ('Diseño', 'UX/UI, Diseño Gráfico y Prototipado'),
                                               ('Data', 'Data Science, Big Data y Analytics'),
                                               ('Marketing', 'Marketing Digital, SEO y Growth')
ON DUPLICATE KEY UPDATE name = name;

-- ======================================================
-- 7. CURSOS
-- ======================================================
CREATE TABLE courses (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(200) NOT NULL,
                         description TEXT,
                         price DECIMAL(10,2) NOT NULL,
                         level VARCHAR(20),
                         image_url VARCHAR(255),
                         instructor_id BIGINT,
                         category_id BIGINT,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         CONSTRAINT fk_course_instructor FOREIGN KEY (instructor_id)
                             REFERENCES users(id),
                         CONSTRAINT fk_course_category FOREIGN KEY (category_id)
                             REFERENCES categories(id)
);

-- ======================================================
-- 8. SESIONES DE CURSO
-- ======================================================
CREATE TABLE course_sessions (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 course_id BIGINT NOT NULL,
                                 title VARCHAR(150) NOT NULL,
                                 start_time DATETIME NOT NULL,
                                 duration_minutes INT DEFAULT 60,
                                 meeting_link VARCHAR(255),
                                 is_completed BIT(1) DEFAULT 0,
                                 enabled BIT(1) DEFAULT 1,
                                 CONSTRAINT fk_session_course FOREIGN KEY (course_id)
                                     REFERENCES courses(id) ON DELETE CASCADE
);

-- ======================================================
-- 9. MATRÍCULAS (ENROLLMENTS)
-- ======================================================
CREATE TABLE enrollments (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             course_id BIGINT NOT NULL,
                             enrolled_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                             status VARCHAR(20) DEFAULT 'ACTIVE',
                             CONSTRAINT fk_enroll_user FOREIGN KEY (user_id)
                                 REFERENCES users(id) ON DELETE CASCADE,
                             CONSTRAINT fk_enroll_course FOREIGN KEY (course_id)
                                 REFERENCES courses(id) ON DELETE CASCADE
);

-- ======================================================
-- 10. RELACIÓN USUARIO - CURSO
-- ======================================================
CREATE TABLE user_courses (
                              user_id BIGINT NOT NULL,
                              course_id BIGINT NOT NULL,
                              enrolled_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (user_id, course_id),
                              CONSTRAINT fk_user_courses_user FOREIGN KEY (user_id)
                                  REFERENCES users(id) ON DELETE CASCADE,
                              CONSTRAINT fk_user_courses_course FOREIGN KEY (course_id)
                                  REFERENCES courses(id) ON DELETE CASCADE
);

-- ======================================================
-- 11. MATERIALES
-- ======================================================
CREATE TABLE materials (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           course_id BIGINT NOT NULL,
                           title VARCHAR(150) NOT NULL,
                           description TEXT,
                           file_url VARCHAR(255),
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT fk_material_course FOREIGN KEY (course_id)
                               REFERENCES courses(id) ON DELETE CASCADE
);

-- ======================================================
-- 12. RECURSOS
-- ======================================================
CREATE TABLE resources (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           course_id BIGINT NOT NULL,
                           name VARCHAR(150) NOT NULL,
                           url VARCHAR(255) NOT NULL,
                           type VARCHAR(50),
                           CONSTRAINT fk_resource_course FOREIGN KEY (course_id)
                               REFERENCES courses(id) ON DELETE CASCADE
);

-- ======================================================
-- 13. FORO
-- ======================================================
CREATE TABLE forum_messages (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                course_id BIGINT NOT NULL,
                                user_id BIGINT NOT NULL,
                                message TEXT NOT NULL,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT fk_forum_course FOREIGN KEY (course_id)
                                    REFERENCES courses(id) ON DELETE CASCADE,
                                CONSTRAINT fk_forum_user FOREIGN KEY (user_id)
                                    REFERENCES users(id) ON DELETE CASCADE
);

-- ======================================================
-- 14. ASISTENCIAS (ATTENDANCE_RECORDS)
-- ======================================================
CREATE TABLE attendance_records (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    session_id BIGINT NOT NULL,
                                    student_id BIGINT NOT NULL,
                                    attended BOOLEAN DEFAULT TRUE,
                                    marked_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    UNIQUE KEY uk_session_student (session_id, student_id),
                                    CONSTRAINT fk_att_session FOREIGN KEY (session_id)
                                        REFERENCES course_sessions(id) ON DELETE CASCADE,
                                    CONSTRAINT fk_att_student FOREIGN KEY (student_id)
                                        REFERENCES users(id) ON DELETE CASCADE
);

-- ======================================================
-- 15. VERIFICACIÓN
-- ======================================================
SHOW TABLES;
