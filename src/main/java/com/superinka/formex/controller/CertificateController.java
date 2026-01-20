package com.superinka.formex.controller;

import com.superinka.formex.model.CertificateDataDTO;
import com.superinka.formex.service.CertificateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateCertificate(@RequestBody CertificateDataDTO data) {
        if (data.getAttendancePercentage() < 85) {
            return ResponseEntity.badRequest()
                    .body(("El alumno no cumple el 85% de asistencia").getBytes());
        }

        byte[] pdfBytes = certificateService.generateCertificate(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=certificado_" + data.getFullName() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
