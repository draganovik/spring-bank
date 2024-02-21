package com.draganovik.userservice.entities;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "users")
@Table(indexes = @Index(columnList = "email"))
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "ID", updatable = false, nullable = false)
    @ColumnDefault("random_uuid()")
    @Type(type = "uuid-char")
    private UUID id;

    @Basic(optional = false)
    @Column(unique = true, nullable = false)
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

    public User(UUID id, String email, String password, Role role, String environment) {
        this(email, password, role, environment);
        setId(id);
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}


