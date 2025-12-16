package com.superinka.formex.repository;

import com.superinka.formex.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //Metodo clave para el login: buscar por email
    Optional<User> findByEmail(String email);

    //Para validaciones al registrarse
    Boolean existsByEmail(String email);
}
