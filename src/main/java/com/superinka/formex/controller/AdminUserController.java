package com.superinka.formex.controller;

import com.superinka.formex.model.*;
import com.superinka.formex.model.enums.PaymentStatus;
import com.superinka.formex.model.enums.RoleName;
import com.superinka.formex.payload.request.CreateUserRequest;
import com.superinka.formex.payload.request.UpdateUserRequest;
import com.superinka.formex.payload.response.MessageResponse;
import com.superinka.formex.payload.response.StudentDto;
import com.superinka.formex.repository.CourseRepository;
import com.superinka.formex.repository.RoleRepository;
import com.superinka.formex.repository.UserCourseRepository;
import com.superinka.formex.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;

    // Listar todos los usuarios
    @GetMapping
    public List<User> getAllusers() {
        return userRepository.findAll();
    }
    @GetMapping("/instructor/courses/{id}/students")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public List<StudentDto> getStudents(@PathVariable Long id) {
        return userRepository.findStudentsByCourseId(id);
    }

    // Buscar usuario por id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return ResponseEntity.ok(user);
    }

    // Crear usuario (Docente, Admin, Alumno) manualmente
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El email ya está en uso."));
        }

        User user = User.builder()
                .fullName(request.getFullname())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .enabled(true)
                .build();

        Set<String> strRoles = request.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(roleRepository.findByName(RoleName.ROLE_STUDENT).orElseThrow());
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow());
                        break;
                    case "instructor":
                    case "docente":
                        roles.add(roleRepository.findByName(RoleName.ROLE_INSTRUCTOR).orElseThrow());
                        break;
                    default:
                        roles.add(roleRepository.findByName(RoleName.ROLE_STUDENT).orElseThrow());
                }
            });
        }

        user.setRoles(roles);


        userRepository.saveAndFlush(user);

        return ResponseEntity.ok(new MessageResponse("Usuario creado exitosamente con los roles asignados."));
    }

    // Editar usuario
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (request.getFullname() != null) user.setFullName(request.getFullname());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> newRoles = new HashSet<>();
            request.getRoles().forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin": newRoles.add(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow()); break;
                    case "instructor": newRoles.add(roleRepository.findByName(RoleName.ROLE_INSTRUCTOR).orElseThrow()); break;
                    default: newRoles.add(roleRepository.findByName(RoleName.ROLE_STUDENT).orElseThrow());
                }
            });
            user.setRoles(newRoles);
        }

        userRepository.saveAndFlush(user);
        return ResponseEntity.ok(new MessageResponse("Usuario actualizado exitosamente."));
    }


    // Endpoint específico para asignar curso a estudiante (si prefieres ruta separada)
    @Transactional
    @PutMapping("/{userId}/assign-course/{courseId}")
    public ResponseEntity<?> assignCourseToStudent(
            @PathVariable Long userId,
            @PathVariable Long courseId
    ) {
        User student = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        UserCourseId id = new UserCourseId(userId, courseId);

        if (userCourseRepository.existsById(id)) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("El estudiante ya está matriculado en este curso"));
        }

        UserCourse uc = new UserCourse();
        uc.setId(id);
        uc.setUser(student);
        uc.setCourse(course);
        uc.setPaymentStatus(PaymentStatus.PAID); // o PENDING si lo decides

        userCourseRepository.save(uc);

        return ResponseEntity.ok(
                new MessageResponse("Curso asignado correctamente al estudiante")
        );
    }


    // Eliminar Usuario (Borrado lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        user.setEnabled(false);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Usuario desactivado (borrado lógico) exitosamente."));
    }
}