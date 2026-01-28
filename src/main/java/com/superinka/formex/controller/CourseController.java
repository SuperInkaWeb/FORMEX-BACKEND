package com.superinka.formex.controller;

import com.superinka.formex.model.Category;
import com.superinka.formex.model.Course;
import com.superinka.formex.model.User;
import com.superinka.formex.model.enums.PaymentStatus;
import com.superinka.formex.payload.request.CourseRequest;
import com.superinka.formex.payload.response.MessageResponse;
import com.superinka.formex.payload.response.StudentDto;
import com.superinka.formex.repository.CategoryRepository;
import com.superinka.formex.repository.CourseRepository;
import com.superinka.formex.repository.UserRepository;
import com.superinka.formex.service.SessionStudentService;
import com.superinka.formex.service.impl.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import com.superinka.formex.model.UserCourse;
import com.superinka.formex.model.UserCourseId;
import com.superinka.formex.payload.request.UpdatePaymentStatusRequest;
import com.superinka.formex.repository.UserCourseRepository;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;
    private final SessionStudentService sessionStudentService; // 👈 Inyectamos el servicio

    // --- RUTAS PÚBLICAS ---

    // Listar cursos habilitados (Público)
    // URL: /api/public/courses
    @GetMapping("/public/courses")
    public List<Course> getAllCourses() {
        return courseRepository.findByEnabledTrue();
    }

    // Ver detalle de curso (Público)
    // URL: /api/public/courses/{id}
    @GetMapping("/public/courses/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Course not found."));
        return ResponseEntity.ok(course);
    }

    // Listar categorías (Público)
    // URL: /api/public/categories
    @GetMapping("/public/categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // --- RUTAS PROTEGIDAS (ADMIN/INSTRUCTOR) ---

    // Crear Curso
    // URL: /api/courses
    @PostMapping("/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseRequest courseRequest,
                                          @AuthenticationPrincipal Jwt jwt) {
        String auth0Id = jwt.getSubject();

        User instructor = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new RuntimeException("Error: Instructor no encontrado en BD local."));

        Category category = categoryRepository.findById(courseRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Error: Category not found."));

        Course course = new Course();
        course.setTitle(courseRequest.getTitle());
        course.setDescription(courseRequest.getDescription());
        course.setPrice(courseRequest.getPrice());
        course.setLevel(courseRequest.getLevel());
        course.setImageUrl(courseRequest.getImageUrl());

        course.setCategory(category);
        course.setInstructor(instructor);
        course.setEnabled(true);

        courseRepository.save(course);

        return ResponseEntity.ok(new MessageResponse("Curso creado exitosamente!"));
    }

    // Editar Curso
    @PutMapping("/courses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody CourseRequest courseRequest) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Course not found."));

        course.setTitle(courseRequest.getTitle());
        course.setDescription(courseRequest.getDescription());
        course.setPrice(courseRequest.getPrice());
        course.setLevel(courseRequest.getLevel());
        course.setImageUrl(courseRequest.getImageUrl());

        if (courseRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(courseRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Error: Category not found."));
            course.setCategory(category);
        }

        courseRepository.save(course);
        return ResponseEntity.ok(new MessageResponse("Curso actualizado exitosamente!"));
    }

    // Eliminar Curso (Soft Delete)
    @DeleteMapping("/courses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Course not found."));

        course.setEnabled(false);
        courseRepository.save(course);

        return ResponseEntity.ok(new MessageResponse("Curso eliminado exitosamente"));
    }

    // 🔥 NUEVO ENDPOINT: Listar Alumnos Inscritos en un Curso
    // URL: /api/courses/{courseId}/students
    @GetMapping("/courses/{courseId}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<StudentDto>> getCourseStudents(@PathVariable Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Error: Course not found.");
        }
        // Usamos el servicio que ya tiene la lógica de mapeo
        List<StudentDto> students = sessionStudentService.getStudentsForCourse(courseId);

        // Si no hay alumnos, devuelve lista vacía [] con estado 200 OK (no error 404)
        return ResponseEntity.ok(students);
    }

    // Actualizar estados de pago
    @PutMapping("/courses/{courseId}/payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePayments(
            @PathVariable Long courseId,
            @RequestBody List<UpdatePaymentStatusRequest> payments
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        for (UpdatePaymentStatusRequest p : payments) {
            User user = userRepository.findById(p.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            UserCourseId id = new UserCourseId();
            id.setUserId(user.getId());
            id.setCourseId(course.getId());

            UserCourse uc = userCourseRepository.findById(id)
                    .orElseGet(() -> {
                        UserCourse newUc = new UserCourse();
                        newUc.setId(id);
                        newUc.setUser(user);
                        newUc.setCourse(course);
                        newUc.setPaymentStatus(PaymentStatus.PENDING);
                        return newUc;
                    });

            uc.setPaymentStatus(p.getPaymentStatus());
            userCourseRepository.save(uc);
        }

        return ResponseEntity.ok(new MessageResponse("Estados de pago actualizados correctamente"));
    }
}