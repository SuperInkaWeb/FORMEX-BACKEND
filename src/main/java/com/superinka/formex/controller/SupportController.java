package com.superinka.formex.controller;

import com.superinka.formex.model.SupportMessageDTO;
import com.superinka.formex.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SupportController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<String> sendSupportMessage(
            @Valid @RequestBody SupportMessageDTO dto
    ) {
        emailService.sendSupportEmail(
                dto.getName(),
                dto.getEmail(),
                dto.getSubject(),
                dto.getMessage()
        );

        return ResponseEntity.ok("Mensaje enviado correctamente");
    }
}


