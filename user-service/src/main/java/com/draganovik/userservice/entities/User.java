package com.draganovik.userservice.entities;

import javax.persistence.*;

@Entity(name = "users")
public class User {

    @Id
    private String email;
    @Basic(optional = false)
    private String password;
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Transient
    private String environment;

    public User() {
        super();
    }

    public User(String email, String password, Role role, String environment) {
        super();
        setEmail(email);
        setPassword(password);
        setRole(role);
        setEnvironment(environment);
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}


