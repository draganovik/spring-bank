package com.draganovik.cryptowallet;

import java.util.UUID;

public class FeignUserResponse {
    private UUID id;
    private String email;
    private Role role;

    public FeignUserResponse() {
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    private void setRole(Role role) {
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    private void setId(UUID id) {
        this.id = id;
    }
}
