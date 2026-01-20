package com.superinka.formex.payload.request;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRequest {
    private String fullname;
    private String phone;
    private String email;
    private Set<String> roles;
    private Boolean enabled;
}
