-- ==================================================================================
-- SCRIPT DE INICIALIZACIÓN - FORMEX DB
-- Autor: [Aladan Lazo]
-- Descripción: Estructura completa de la base de datos con datos semilla.
-- Motor: MySQL / MariaDB
-- ==================================================================================

-- 1. CREACIÓN DE LA BASE DE DATOS
-- ----------------------------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS formex_db;
USE formex_db;

-- 2. TABLAS DE SEGURIDAD (Spring Security)
-- ----------------------------------------------------------------------------------

-- Tabla de Roles
CREATE TABLE IF NOT EXISTS roles (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_name (name)
    );

-- Tabla de Usuarios
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(255),
    enabled BIT(1) DEFAULT 1,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
    );

-- Tabla Intermedia Usuarios-Roles
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role_id BIGINT NOT NULL,
                                          PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
    );

-- Tabla de Tokens de Recuperación de Contraseña
CREATE TABLE IF NOT EXISTS password_reset_tokens (
                                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                     token VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    expiry_date DATETIME NOT NULL,
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- 3. TABLAS DE NEGOCIO (Cursos y Categorías)
-- ----------------------------------------------------------------------------------

-- Tabla de Categorías
CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
    );

-- Tabla de Cursos
CREATE TABLE IF NOT EXISTS courses (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       title VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    level VARCHAR(20), -- PRINCIPIANTE, INTERMEDIO, AVANZADO
    image_url VARCHAR(255),
    enabled BIT(1) DEFAULT 1, -- Campo requerido para validación JPA
    instructor_id BIGINT,
    category_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_course_instructor FOREIGN KEY (instructor_id) REFERENCES users(id),
    CONSTRAINT fk_course_category FOREIGN KEY (category_id) REFERENCES categories(id)
    );

-- Tabla de Sesiones del Curso
CREATE TABLE IF NOT EXISTS course_sessions (
                                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               course_id BIGINT NOT NULL,
                                               title VARCHAR(150) NOT NULL,
    start_time DATETIME NOT NULL,
    duration_minutes INT DEFAULT 60,
    meeting_link VARCHAR(255),
    is_completed BIT(1) DEFAULT 0,
    enabled BIT(1) DEFAULT 1,
    CONSTRAINT fk_session_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
    );

-- Tabla de Asistencias
CREATE TABLE IF NOT EXISTS attendances (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           session_id BIGINT NOT NULL,
                                           student_id BIGINT NOT NULL,
                                           attended BOOLEAN DEFAULT TRUE, -- Presente o Ausente
                                           marked_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                           CONSTRAINT fk_attendance_session FOREIGN KEY (session_id) REFERENCES course_sessions(id),
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES users(id)
    );

-- 4. DATOS SEMILLA (SEED DATA)
-- ----------------------------------------------------------------------------------

-- Roles predeterminados
INSERT INTO roles (name) VALUES
                             ('ROLE_ADMIN'),
                             ('ROLE_STUDENT'),
                             ('ROLE_INSTRUCTOR')
    ON DUPLICATE KEY UPDATE name=name;

-- Categorías predeterminadas
INSERT INTO categories (name, description) VALUES
                                               ('Programación', 'Desarrollo de software, web y móvil'),
                                               ('Diseño', 'UX/UI, Diseño Gráfico y Prototipado'),
                                               ('Data', 'Data Science, Big Data y Analytics'),
                                               ('Marketing', 'Marketing Digital, SEO y Growth')
    ON DUPLICATE KEY UPDATE name=name;

-- Fin del script

-- (Opcional) Confirmar que se crearon los roles
SELECT * FROM roles;
SELECT * FROM users;
SELECT * FROM user_roles;
SELECT * FROM courses;
SELECT * FROM password_reset_tokens;
SELECT * FROM categories;
SELECT * FROM course_sessions;
SELECT * FROM attendances;
