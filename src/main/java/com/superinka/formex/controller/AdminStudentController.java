package com.superinka.formex.controller;

import com.superinka.formex.model.enums.RoleName;
import com.superinka.formex.payload.response.StudentDto;
import com.superinka.formex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStudentController {

    private final UserRepository userRepository;

    @GetMapping
    public List<StudentDto> getAllStudents() {

        return userRepository.findAll().stream()
                .filter(user ->
                        user.getRoles().stream()
                                // CORRECCIÓN: Comparación segura con RoleName.ROLE_STUDENT
                                .anyMatch(r -> r.getName() == RoleName.ROLE_STUDENT)
                )
                .map(user -> new StudentDto(
                        user.getId(),
                        user.getName(),      // Separado
                        user.getLastname(),  // Separado
                        user.getEmail(),
                        user.getPhone(),
                        null, // paymentStatus es null en listado general
                        0.0   // asistencia es 0.0 en listado general
                ))
                .toList();
    }
}
