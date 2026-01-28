package com.superinka.formex.payload.response;

import com.superinka.formex.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {

    private Long id;
    private String name;      // Separado
    private String lastname;  // Separado
    private String email;
    private String phone;
    private PaymentStatus paymentStatus;
    private double attendancePercentage;

    // Constructor manual SIMPLE (Usado por AdminUserController para listar sin detalles de pago)
    public StudentDto(Long id, String name, String lastname, String email) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
    }

    // Helper: Reconstruye fullName para el frontend
    public String getFullName() {
        return (name != null ? name : "") + " " + (lastname != null ? lastname : "");
    }
}
