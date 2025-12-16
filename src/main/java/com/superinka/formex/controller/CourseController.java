package com.superinka.formex.controller;

import com.superinka.formex.model.Category;
import com.superinka.formex.model.Course;
import com.superinka.formex.model.User;
import com.superinka.formex.payload.request.CourseRequest;
import com.superinka.formex.payload.response.MessageResponse;
import com.superinka.formex.repository.CategoryRepository;
import com.superinka.formex.repository.CourseRepository;
import com.superinka.formex.repository.UserRepository;
import com.superinka.formex.service.impl.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    //Listado de categorias(publico)
    @GetMapping("/public/categories")
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    //Listado de cursos(publico)
    @GetMapping("/public/courses")
    public List<Course> getAllCourses() {
        return courseRepository.findByEnabledTrue();
    }

    //Detalle de curso
    @GetMapping("/public/courses/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + id));

        if (!course.getEnabled()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Este curso ya no está disponible."));
        }

        return ResponseEntity.ok(course);
    }

    //Dashboard INSTRUCTOR (Ver mis cursos)
    @GetMapping("/instructor/courses")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole ('ADMIN')")
    public List<Course> getInstructorCourses() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return courseRepository.findByInstructorIdAndEnabledTrue(userDetails.getId());
    }

    //Asignar Docente a Curso (Solo admin)
    @PutMapping ("courses/{id}/assign-instructor/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignInstructor(@PathVariable Long id, @PathVariable Long instructorId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor no encontrado"));

        //Validar que sea realmente un instructor
        boolean isInstructor = instructor.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ROLE_INSTRUCTOR"));

        if(!isInstructor) {
            return ResponseEntity.badRequest().body(new MessageResponse("El usuario no tiene rol de Instructor"));
        }
        course.setInstructor(instructor);
        courseRepository.save(course);

        return ResponseEntity.ok(new MessageResponse("Instructor asignado correctamente: " + instructor.getFullName()));
    }


    //Crear Curso (Solo admin o instructor)
    @PostMapping("/courses")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<?> createCourse(@RequestBody CourseRequest request) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User instructor = userRepository.findById(userDetails.getId()).orElseThrow();

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .level(request.getLevel())
                .imageUrl(request.getImageUrl())
                .category(category)
                .instructor(instructor)
                .build();
        courseRepository.save(course);

        return ResponseEntity.ok(new MessageResponse("Curso creado exitosamente"));
    }

    // Editar Curso
    @PutMapping("/courses/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());
        course.setLevel(request.getLevel());
        course.setImageUrl(request.getImageUrl());
        course.setCategory(category);

        courseRepository.save(course);
        return ResponseEntity.ok(new MessageResponse("Curso actualizado exitosamente"));
    }

    // Eliminar Curso (NUEVO)
    @DeleteMapping("/courses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        course.setEnabled(false);
        courseRepository.save(course);

        return ResponseEntity.ok(new MessageResponse("Curso eliminado (desactivado) exitosamente"));
    }
}
