package com.superinka.formex.config;

import com.superinka.formex.model.Role;
import com.superinka.formex.model.User;
import com.superinka.formex.model.enums.RoleName;
import com.superinka.formex.repository.RoleRepository;
import com.superinka.formex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Datos del admin por defecto
    // Datos del admin por defecto
    private static final String ADMIN_EMAIL = "admin@fornex.com";
    private static final String ADMIN_PASSWORD = "nuevaContraseña123";

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Validar si ya existe el admin para no duplicarlo
            if (!userRepository.existsByEmail(ADMIN_EMAIL)) {

                System.out.println("--- FORMEX: Creando usuario Admin inicial... ---");

                // 1. Buscar el Rol ADMIN
                Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: El Rol ADMIN no fue encontrado en la BD."));

                // 2. Crear el Usuario
                User admin = User.builder()
                        .fullName("Jeanpier Quispe Santisteba")
                        .email(ADMIN_EMAIL)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .phone("999000111")
                        .enabled(true)
                        .roles(new HashSet<>(Collections.singletonList(adminRole))) // Asignar rol
                        .build();

                // 3. Guardar
                userRepository.save(admin);

                System.out.println("--- FORMEX: Admin creado exitosamente: " + ADMIN_EMAIL + " ---");
            } else {
                System.out.println("--- FORMEX: El usuario Admin ya existe. Omitiendo creación. ---");
            }
        };
    }
}
