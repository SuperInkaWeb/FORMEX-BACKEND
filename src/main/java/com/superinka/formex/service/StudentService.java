package com.superinka.formex.service;

import com.superinka.formex.model.Course;
import com.superinka.formex.model.UserCourse;
import com.superinka.formex.model.enums.PaymentStatus;
import com.superinka.formex.repository.UserCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserCourseRepository userCourseRepository;

    public List<Course> getPaidCourses(Long studentId) {

        return userCourseRepository
                .findByUserIdAndPaymentStatus(studentId, PaymentStatus.PAID)
                .stream()
                .map(UserCourse::getCourse)
                .toList();
    }
}

