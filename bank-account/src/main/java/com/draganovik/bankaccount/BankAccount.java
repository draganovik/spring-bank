package com.draganovik.bankaccount;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class BankAccount {

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

    @Column(unique = true)
    private String email;

    @Column(name = "quantity_RSD")
    private BigDecimal quantityRSD;

    @Column(name = "quantity_EUR")
    private BigDecimal quantityEUR;

    @Column(name = "quantity_GBP")
    private BigDecimal quantityGBP;

    @Column(name = "quantity_USD")
    private BigDecimal quantityUSD;

    @Column(name = "quantity_CHF")
    private BigDecimal quantityCHF;

    @Transient
    private String environment;

    public BankAccount() {
        // Default constructor
    }

    public BankAccount(String email, String environment) {
        this.email = email;
        this.environment = environment;
    }

    public BankAccount(UUID id, String email, String environment) {
        this.id = id;
        this.email = email;
        this.environment = environment;
    }

    public BankAccount(UUID id, String email, BigDecimal quantityRSD, BigDecimal quantityEUR,
                       BigDecimal quantityGBP, BigDecimal quantityUSD, BigDecimal quantityCHF,
                       String environment) {
        this(id, email, environment);
        this.quantityRSD = quantityRSD;
        this.quantityEUR = quantityEUR;
        this.quantityGBP = quantityGBP;
        this.quantityUSD = quantityUSD;
        this.quantityCHF = quantityCHF;
    }

    // Getters and setters for each field

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getQuantityRSD() {
        return quantityRSD;
    }

    public void setQuantityRSD(BigDecimal quantityRSD) {
        this.quantityRSD = quantityRSD;
    }

    public BigDecimal getQuantityEUR() {
        return quantityEUR;
    }

    public void setQuantityEUR(BigDecimal quantityEUR) {
        this.quantityEUR = quantityEUR;
    }

    public BigDecimal getQuantityGBP() {
        return quantityGBP;
    }

    public void setQuantityGBP(BigDecimal quantityGBP) {
        this.quantityGBP = quantityGBP;
    }

    public BigDecimal getQuantityUSD() {
        return quantityUSD;
    }

    public void setQuantityUSD(BigDecimal quantityUSD) {
        this.quantityUSD = quantityUSD;
    }

    public BigDecimal getQuantityCHF() {
        return quantityCHF;
    }

    public void setQuantityCHF(BigDecimal quantityCHF) {
        this.quantityCHF = quantityCHF;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
