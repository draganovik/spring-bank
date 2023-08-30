package com.draganovik.userservice.models;

import com.draganovik.userservice.entities.Role;
import com.draganovik.userservice.entities.User;

public class UserCreatedResponse {
    private String email;
    private Role role;
    private String environment;

    public UserCreatedResponse(User user) {
        setEmail(user.getEmail());
        setRole(user.getRole());
        setEnvironment(user.getEnvironment());
    }

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

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
