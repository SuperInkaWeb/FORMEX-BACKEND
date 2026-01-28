package com.superinka.formex.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class Auth0Service {

    @Value("${auth0.management.client-id}")
    private String clientId;

    @Value("${auth0.management.client-secret}")
    private String clientSecret;

    @Value("${auth0.domain}")
    private String domain;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Obtiene un token temporal para usar la API de Gestión de Auth0
     */
    private String getManagementAccessToken() {
        String url = "https://" + domain + "/oauth/token";

        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("audience", "https://" + domain + "/api/v2/");
        body.put("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return (String) response.getBody().get("access_token");
        } catch (Exception e) {
            System.err.println("Error obteniendo token de Auth0: " + e.getMessage());
            throw new RuntimeException("No se pudo autenticar con Auth0 Management API");
        }
    }

    /**
     * Crea un usuario en Auth0 (Base de datos Username-Password)
     */
    public void createAuth0User(String email, String password, String name, String roleName) {
        try {
            String token = getManagementAccessToken();
            String url = "https://" + domain + "/api/v2/users";

            Map<String, Object> body = new HashMap<>();
            body.put("email", email);
            body.put("password", password);
            body.put("connection", "Username-Password-Authentication"); // Conexión por defecto
            body.put("name", name);
            body.put("email_verified", true); // Como lo crea un admin, lo marcamos verificado

            // Opcional: Guardar el rol en metadata de Auth0 si lo necesitas
            Map<String, Object> appMetadata = new HashMap<>();
            appMetadata.put("role", roleName);
            body.put("app_metadata", appMetadata);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(url, request, Map.class);
            System.out.println("Usuario creado exitosamente en Auth0: " + email);

        } catch (Exception e) {
            // No queremos que falle toda la creación local si Auth0 falla (o si el usuario ya existe)
            System.err.println("Advertencia: No se pudo crear usuario en Auth0 (¿Ya existe?): " + e.getMessage());
        }
    }
}
