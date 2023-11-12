package com.draganovik.userservice.models;

import com.draganovik.userservice.entities.Role;
import com.draganovik.userservice.entities.User;

public class UserResponse {

    private Long id;
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

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }
}
