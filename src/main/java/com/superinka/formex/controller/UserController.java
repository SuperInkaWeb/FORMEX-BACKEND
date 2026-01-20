package com.superinka.formex.controller;

import com.superinka.formex.model.Role;
import com.superinka.formex.model.User;
import com.superinka.formex.model.enums.RoleName;
import com.superinka.formex.repository.RoleRepository;
import com.superinka.formex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @GetMapping("/api/me")
    public User me(@AuthenticationPrincipal Jwt jwt) {

        String auth0Id = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseGet(() -> crearUsuarioDesdeAuth0(jwt, auth0Id, email));


        List<String> roleClaims =
                jwt.getClaimAsStringList("https://formex.com/roles");

        if (roleClaims != null) {
            Set<Role> roles = roleClaims.stream()
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r.toUpperCase())
                    .map(RoleName::valueOf)
                    .map(rn -> roleRepository.findByName(rn)
                            .orElseThrow(() ->
                                    new RuntimeException("Role " + rn + " no existe")))
                    .collect(Collectors.toSet());

            user.setRoles(roles);
            userRepository.save(user);
        }

        return user;
    }

    /* =======================
       MÉTODOS PRIVADOS
       ======================= */

    // ✅ MÉTODO ROBUSTO (NO FALLA SI NO HAY EMAIL)
    private String extractIdentifier(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            return email.toLowerCase();
        }

        String username = jwt.getClaimAsString("preferred_username");
        if (username != null && !username.isBlank()) {
            return username;
        }

        String sub = jwt.getSubject();
        if (sub != null && !sub.isBlank()) {
            return sub;
        }

        throw new IllegalStateException("JWT sin identificador válido");
    }

    private String extractFullName(Jwt jwt) {
        return Optional.ofNullable(jwt.getClaimAsString("name"))
                .or(() -> Optional.ofNullable(jwt.getClaimAsString("nickname")))
                .or(() -> Optional.ofNullable(jwt.getClaimAsString("preferred_username")))
                .orElse("Usuario");
    }

    private User crearUsuarioDesdeAuth0(Jwt jwt, String auth0Id, String email) {
        Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("ROLE_STUDENT no existe"));

        User user = User.builder()
                .auth0Id(auth0Id) // 🔑 identificador único
                .email(email != null ? email.toLowerCase() : auth0Id) // 📧 opcional
                .fullName(extractFullName(jwt))
                .enabled(true)
                .roles(Set.of(studentRole))
                .build();

        return userRepository.save(user);
    }
}
