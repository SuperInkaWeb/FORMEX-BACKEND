package com.superinka.formex.payload.response;

import com.superinka.formex.model.enums.PaymentStatus;

public class StudentDto {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private PaymentStatus paymentStatus;
    private double attendancePercentage; // 👈 nuevo campo


    public StudentDto(Long id, String fullName, String email, String phone, PaymentStatus paymentStatus) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.paymentStatus = paymentStatus;

    }
    public StudentDto(
            Long id,
            String fullName,
            String email,
            String phone,
            PaymentStatus paymentStatus,
            double attendancePercentage // 👈 incluir en constructor
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.paymentStatus = paymentStatus;
        this.attendancePercentage = attendancePercentage;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public double getAttendancePercentage() { return attendancePercentage; } // 👈 getter

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public void setAttendancePercentage(double attendancePercentage) { // 👈 setter
        this.attendancePercentage = attendancePercentage;
    }
}
