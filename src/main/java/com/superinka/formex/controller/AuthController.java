package com.superinka.formex.controller;

import com.superinka.formex.model.PasswordResetToken;
import com.superinka.formex.model.Role;
import com.superinka.formex.model.User;
import com.superinka.formex.model.enums.RoleName;
import com.superinka.formex.payload.request.LoginRequest;
import com.superinka.formex.payload.request.SignupRequest;
import com.superinka.formex.payload.response.MessageResponse;
import com.superinka.formex.repository.PasswordResetTokenRepository;
import com.superinka.formex.repository.RoleRepository;
import com.superinka.formex.repository.UserRepository;
import com.superinka.formex.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder encoder;

    //Registro
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        // 1. Validar duplicados
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El email ya está en uso!"));
        }

        // 2. Crear nuevo usuario (con Password Encoder)
        User user = User.builder()
                .fullName(signUpRequest.getFullName())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .phone(signUpRequest.getPhone())
                .enabled(true)
                .build();

        // 3. Asignar Roles
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            // Por defecto: ROL ESTUDIANTE
            Role userRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
            roles.add(userRole);
        } else {
            // Lógica para asignar otros roles si se envían (útil para admins creando usuarios)
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(adminRole);
                        break;
                    case "instructor":
                        Role modRole = roleRepository.findByName(RoleName.ROLE_INSTRUCTOR)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        // Entregable 1.03: Enviar Email de Bienvenida
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
    }

    //Recuperacion de Contraseña
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPasswordResponseEntity(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        tokenRepository.save(myToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
        return ResponseEntity.ok(new MessageResponse("Correo de recuperacion enviado!"));
    }

    //Cambiar la contraseña con el token
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody LoginRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalido"));

        if(resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new MessageResponse("El token ha expirado"));
        }

        User user = resetToken.getUser();
        user.setPassword(encoder.encode(request.getPassword()));
        userRepository.save(user);

        //Borrar token usado
        tokenRepository.delete(resetToken);

        return ResponseEntity.ok(new MessageResponse("Contraseña restablecida exitosamente!"));
    }
}
