package com.superinka.formex.controller;

import com.superinka.formex.payload.response.StudentDto;
import com.superinka.formex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructor/courses")
@RequiredArgsConstructor
public class InstructorCourseController {

    private final UserRepository userRepository;

    @GetMapping("/{id}/students")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public List<StudentDto> getStudents(@PathVariable Long id) {
        return userRepository.findStudentsByCourseId(id);
    }
}