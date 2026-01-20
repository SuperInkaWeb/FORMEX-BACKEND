package com.superinka.formex.controller;

import com.superinka.formex.model.User;
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
                                .anyMatch(r -> r.getName().name().equals("ROLE_STUDENT"))
                )
                .map(user -> new StudentDto(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhone(),
                        null
                ))
                .toList();
    }
}
