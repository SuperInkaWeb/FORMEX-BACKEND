package com.superinka.formex.repository;

import com.superinka.formex.model.User;
import com.superinka.formex.payload.response.StudentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    // 🔑 Nuevo método para buscar por el campo auth0Id
    Optional<User> findByAuth0Id(String auth0Id);

    @Query("""
    SELECT new com.superinka.formex.payload.response.StudentDto(
        u.id,
        u.fullName,
        u.email,
        u.phone,
        uc.paymentStatus,
        0
    )
    FROM UserCourse uc
    JOIN uc.user u
    WHERE uc.course.id = :courseId
""")
    List<StudentDto> findStudentsByCourseId(@Param("courseId") Long courseId);
}
