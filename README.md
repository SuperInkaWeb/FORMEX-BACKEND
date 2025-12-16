# 📘 FORMEX - Backend API

API RESTful para la plataforma educativa FORMEX, un LMS enfocado en cursos en vivo con sincronización, mentoría y comunidad.

---

## Stack Tecnológico

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3.5.8
- **Base de Datos:** MySQL 8.0
- **Seguridad:** Spring Security + JWT (JSON Web Tokens)
- **ORM:** Hibernate / Spring Data JPA
- **Herramientas:** Maven, Lombok, JavaMailSender
- **Almacenamiento de Archivos:** Local (Carpeta `/uploads` en raíz)

---

## Configuración del Entorno

### Requisitos Previos

- JDK 21 instalado
- MySQL Server corriendo en el puerto 3306
- Maven instalado

### Paso 1: Base de Datos

**Importante:** Debes ejecutar el script SQL que se encuentra en la raíz del proyecto para crear la base de datos y las tablas necesarias.

```bash
mysql -u tu_usuario -p < formex_db.sql
```

O también puedes ejecutar el script directamente desde MySQL:

```sql
source /ruta/al/proyecto/formex_db. sql;
```

Este script creará: 
- La base de datos `formex_db`
- Todas las tablas necesarias (users, roles, courses, categories, etc.)
- Los roles predeterminados (ROLE_ADMIN, ROLE_STUDENT, ROLE_INSTRUCTOR)
- Las categorías iniciales

### Paso 2: Configuración

Actualizar `src/main/resources/application.properties` con tus credenciales:  

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/formex_db
spring.datasource.username=tu_usuario
spring.datasource. password=tu_password

# Configuración de Email (Gmail)
spring.mail.username=tu_email@gmail.com
spring. mail.password=tu_app_password
```

### Paso 3: Descomentar DataInitializr

**Importante:** La clase `DataInitializr` está comentada por defecto.  Debes descomentarla para que se cree automáticamente el usuario administrador al iniciar la aplicación.

1. Localiza el archivo `DataInitializr. java` en el proyecto
2. Descomenta la anotación `@Component` y todo el código de la clase
3. Guarda los cambios

Esta clase creará automáticamente un usuario ADMIN al iniciar la aplicación: 

- **Email:** admin@formex.com
- **Password:** admin123

### Paso 4: Ejecutar

```bash
mvn spring-boot:run
```

Al primer inicio, después de haber descomentado `DataInitializr`, el sistema creará automáticamente el usuario administrador. 

---

## Arquitectura y Estructura

### Estructura del Proyecto (`src/main/java/com/formex/backend`)

- **/config:** Configuración de Seguridad (CORS, JWT Filter) y Web (Recursos estáticos)
- **/controller:** Endpoints de la API
  - `AuthController`: Login, Registro, Recuperación de Password
  - `AdminUserController`: Gestión de usuarios (Solo Admin)
  - `CourseController`: CRUD de cursos (Público y Privado)
  - `MediaController`: Subida de imágenes
- **/model:** Entidades JPA (`User`, `Course`, `Role`, `Ticket`)
- **/repository:** Interfaces de acceso a datos
- **/service:** Lógica de negocio (`EmailService`)

---

## Endpoints Principales

### Autenticación
- `POST /api/auth/register` - Registro de usuarios
- `POST /api/auth/login` - Login y generación de JWT
- `POST /api/auth/forgot-password` - Recuperación de contraseña

### Usuarios (Admin)
- `GET /api/admin/users` - Listar todos los usuarios
- `POST /api/admin/users` - Crear nuevo usuario
- `PUT /api/admin/users/{id}` - Actualizar usuario
- `DELETE /api/admin/users/{id}` - Desactivar usuario (Soft Delete)

### Cursos
- `GET /api/courses` - Listar cursos públicos
- `GET /api/courses/{id}` - Detalle de curso
- `POST /api/admin/courses` - Crear curso (Admin/Instructor)
- `PUT /api/admin/courses/{id}` - Actualizar curso
- `DELETE /api/admin/courses/{id}` - Eliminar curso

### Media
- `POST /api/media/upload` - Subir imagen

---

## Funcionalidades Implementadas ✅

- ✅ Autenticación completa con JWT
- ✅ Sistema de roles:  ADMIN, INSTRUCTOR, STUDENT
- ✅ Recuperación de contraseña vía Email
- ✅ Gestión de usuarios (CRUD con Soft Delete)
- ✅ Gestión de cursos (CRUD completo)
- ✅ Subida de imágenes
- ✅ Sistema de tickets de soporte

---

## Próximos Pasos 🚧

### Prioridad Alta
- **Matrícula:** Crear tabla `enrollments` y endpoint `POST /api/enroll`
- **Módulos y Lecciones:** Estructura de contenido del curso
- **Aula Virtual:** Endpoint para listar cursos inscritos por usuario

### Prioridad Media
- **Pagos:** Integración con Stripe/PayPal
- **Gestión de Tickets:** Endpoints para administrar tickets desde panel admin

---

## Notas Técnicas Importantes

- **Imágenes:** Actualmente se guardan en `./uploads`. Para producción, migrar a AWS S3 o Cloudinary
- **CORS:** Configurado para `localhost:5173`. Actualizar en `SecurityConfig. java` para otros dominios
- **Seguridad:** El endpoint `/error` está abierto para depuración.  Cerrar en producción

---

## Repositorio Frontend

🔗 [FORMEX Frontend Repository](https://github.com/SuperInkaWeb/FORMEX-frontend)

---

## 📧 Contacto

**Email:** faridlazo1921@gmail. com

---
