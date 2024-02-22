package com.draganovik.userservice.models;

import com.draganovik.userservice.entities.Role;

public class ValidateResponse {
    private Role role;
    private String email;

    public ValidateResponse() {
        super();
    }

    public ValidateResponse(Role role, String email) {
        this();
        setRole(role);
        setEmail(email);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}