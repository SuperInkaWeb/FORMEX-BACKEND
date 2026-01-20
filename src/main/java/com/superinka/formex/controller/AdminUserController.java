package com.superinka.formex.controller;

import com.superinka.formex.model.Role;
import com.superinka.formex.model.User;
import com.superinka.formex.model.enums.RoleName;
import com.superinka.formex.payload.request.CreateUserRequest;
import com.superinka.formex.payload.request.UpdateUserRequest;
import com.superinka.formex.payload.response.MessageResponse;
import com.superinka.formex.repository.RoleRepository;
import com.superinka.formex.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    //Listar todos los usuarios
    @GetMapping
    public List<User> getAllusers() {
        return userRepository.findAll();
    }

    //Buscar usuario por id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(user);
    }

    //Crear usuario (Docente, Admin, Alumno) manualmente
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
            // Por defecto estudiante si no se especifica
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
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Usuario creado exitosamente con los roles asignados."));
    }

    //Editar usuario
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getFullname() != null) user.setFullName(request.getFullname());
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        // Aquí el admin puede reactivar manualmente a un usuario borrado
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

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Usuario actualizado exitosamente."));
    }

    //Eliminar Usuario (Borrado lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // CORRECCIÓN: No borramos, solo desactivamos.
        // Esto impide el login inmediatamente gracias a Spring Security.
        user.setEnabled(false);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Usuario desactivado (borrado lógico) exitosamente."));
    }
}
