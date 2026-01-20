package com.superinka.formex.repository;

import com.superinka.formex.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCategoryId(Long categoryId);
    List<Course> findByEnabledTrue();
    List<Course> findByInstructorIdAndEnabledTrue(Long instructorId);
}
