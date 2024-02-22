package com.draganovik.userservice.models;

import com.draganovik.userservice.entities.Role;
import com.draganovik.userservice.entities.User;

import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String email;
    private Role role;

    public UserResponse(User user) {
        setEmail(user.getEmail());
        setRole(user.getRole());
        setId(user.getId());
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
