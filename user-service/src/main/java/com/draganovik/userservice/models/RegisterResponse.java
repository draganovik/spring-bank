package com.draganovik.userservice.models;

import com.draganovik.userservice.entities.Role;

import java.time.LocalDateTime;

public class RegisterResponse {
    private String jwtToken;
    private Role role;
    private String email;
    private LocalDateTime jwtExpiration;
    private LocalDateTime jwtGeneration;

    public RegisterResponse() {
        super();
    }

    public RegisterResponse(String jwtToken, Role role, String email, LocalDateTime expirationDate, LocalDateTime generatedDate) {
        this();
        setJwtToken(jwtToken);
        setRole(role);
        setEmail(email);
        setJwtExpiration(expirationDate);
        setJwtGeneration(generatedDate);

    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
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

    public LocalDateTime getJwtExpiration() {
        return jwtExpiration;
    }

    public void setJwtExpiration(LocalDateTime jwtExpiration) {
        this.jwtExpiration = jwtExpiration;
    }

    public LocalDateTime getJwtGeneration() {
        return jwtGeneration;
    }

    public void setJwtGeneration(LocalDateTime jwtGeneration) {
        this.jwtGeneration = jwtGeneration;
    }
}