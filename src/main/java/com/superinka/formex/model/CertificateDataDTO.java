package com.superinka.formex.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CertificateDataDTO {

    private Long studentId;
    private String fullName;
    private String courseName;
    private double attendancePercentage;
}
