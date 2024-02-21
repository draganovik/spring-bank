package com.draganovik.userservice.models;

import com.draganovik.userservice.entities.Role;
import com.draganovik.userservice.entities.User;

public class UserRequest {
    private Role role;

    private String email;

    private String password;

    public UserRequest() {
        super();
    }

    public UserRequest(User user) {
        setRole(user.getRole());
        setPassword(user.getPassword());
    }

    public Role getRole() {
        return role;
    }

    private void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
