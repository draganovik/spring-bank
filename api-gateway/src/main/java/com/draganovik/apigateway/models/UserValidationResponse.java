package com.draganovik.apigateway.models;

import com.draganovik.apigateway.Role;

public class UserValidationResponse {
    private String email;
    private Role role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}