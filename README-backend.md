\# 📘 FORMEX - Backend API



API RESTful para la plataforma educativa FORMEX, un LMS enfocado en cursos en vivo con sincronización, mentoría y comunidad.



---



\## Stack Tecnológico



\- \*\*Lenguaje:\*\* Java 17

\- \*\*Framework:\*\* Spring Boot 3.2.x

\- \*\*Base de Datos:\*\* MySQL 8.0

\- \*\*Seguridad:\*\* Spring Security + JWT (JSON Web Tokens)

\- \*\*ORM:\*\* Hibernate / Spring Data JPA

\- \*\*Herramientas:\*\* Maven, Lombok, JavaMailSender

\- \*\*Almacenamiento de Archivos:\*\* Local (Carpeta `/uploads` en raíz)



---



\## Configuración del Entorno



\### Requisitos Previos



\- JDK 17 instalado

\- MySQL Server corriendo en el puerto 3306

\- Maven instalado



\### Paso 1: Base de Datos



Crear una base de datos vacía llamada `formex\_db`:



```sql

CREATE DATABASE formex\_db;

```



El backend está configurado en modo `update`, por lo que creará las tablas automáticamente al iniciar. 



\### Paso 2: Configuración



Actualizar `src/main/resources/application.properties` con tus credenciales: 



```properties

spring.datasource.url=jdbc:mysql://localhost:3306/formex\_db

spring.datasource.username=tu\_usuario

spring.datasource.password=tu\_password



\# Configuración de Email (Gmail)

spring.mail.username=tu\_email@gmail.com

spring.mail.password=tu\_app\_password

```



\### Paso 3: Ejecutar



```bash

mvn spring-boot:run

```



\*\*Nota:\*\* Al primer inicio, el sistema crea automáticamente un usuario ADMIN:



\- \*\*Email:\*\* admin@formex.com

\- \*\*Password:\*\* admin123



---



\## Arquitectura y Estructura



\### Estructura del Proyecto (`src/main/java/com/formex/backend`)



\- \*\*/config:\*\* Configuración de Seguridad (CORS, JWT Filter) y Web (Recursos estáticos)

\- \*\*/controller:\*\* Endpoints de la API

&nbsp; - `AuthController`: Login, Registro, Recuperación de Password

&nbsp; - `AdminUserController`: Gestión de usuarios (Solo Admin)

&nbsp; - `CourseController`: CRUD de cursos (Público y Privado)

&nbsp; - `MediaController`: Subida de imágenes

\- \*\*/model:\*\* Entidades JPA (`User`, `Course`, `Role`, `Ticket`)

\- \*\*/repository:\*\* Interfaces de acceso a datos

\- \*\*/service:\*\* Lógica de negocio (`EmailService`)



---



\## Endpoints Principales



\### Autenticación

\- `POST /api/auth/register` - Registro de usuarios

\- `POST /api/auth/login` - Login y generación de JWT

\- `POST /api/auth/forgot-password` - Recuperación de contraseña



\### Usuarios (Admin)

\- `GET /api/admin/users` - Listar todos los usuarios

\- `POST /api/admin/users` - Crear nuevo usuario

\- `PUT /api/admin/users/{id}` - Actualizar usuario

\- `DELETE /api/admin/users/{id}` - Desactivar usuario (Soft Delete)



\### Cursos

\- `GET /api/courses` - Listar cursos públicos

\- `GET /api/courses/{id}` - Detalle de curso

\- `POST /api/admin/courses` - Crear curso (Admin/Instructor)

\- `PUT /api/admin/courses/{id}` - Actualizar curso

\- `DELETE /api/admin/courses/{id}` - Eliminar curso



\### Media

\- `POST /api/media/upload` - Subir imagen



---



\## Funcionalidades Implementadas ✅



\- ✅ Autenticación completa con JWT

\- ✅ Sistema de roles: ADMIN, INSTRUCTOR, STUDENT

\- ✅ Recuperación de contraseña vía Email

\- ✅ Gestión de usuarios (CRUD con Soft Delete)

\- ✅ Gestión de cursos (CRUD completo)

\- ✅ Subida de imágenes

\- ✅ Sistema de tickets de soporte



---



\## Próximos Pasos 🚧



\### Prioridad Alta

\- \*\*Matrícula:\*\* Crear tabla `enrollments` y endpoint `POST /api/enroll`

\- \*\*Módulos y Lecciones:\*\* Estructura de contenido del curso

\- \*\*Aula Virtual:\*\* Endpoint para listar cursos inscritos por usuario



\### Prioridad Media

\- \*\*Pagos:\*\* Integración con Stripe/PayPal

\- \*\*Gestión de Tickets:\*\* Endpoints para administrar tickets desde panel admin



---



\## Notas Técnicas Importantes



\- \*\*Imágenes:\*\* Actualmente se guardan en `./uploads`. Para producción, migrar a AWS S3 o Cloudinary

\- \*\*CORS:\*\* Configurado para `localhost:5173`. Actualizar en `SecurityConfig.java` para otros dominios

\- \*\*Seguridad:\*\* El endpoint `/error` está abierto para depuración. Cerrar en producción



---



\## Repositorio Frontend



🔗 \[FORMEX Frontend Repository](https://github.com/SuperInkaWeb/FORMEX-frontend)



---



\## 📧 Contacto



\*\*Email:\*\* faridlazo1921@gmail.com



---

