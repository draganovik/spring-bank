package com.draganovik.bankaccount.entities;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "bank_accounts")
@Table(indexes = @Index(columnList = "email"))
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

    @Column(name = "quantity_RSD", precision = 14, scale = 2)
    private BigDecimal quantityRSD = BigDecimal.ZERO;

    @Column(name = "quantity_EUR", precision = 14, scale = 2)
    private BigDecimal quantityEUR = BigDecimal.ZERO;

    @Column(name = "quantity_GBP", precision = 14, scale = 2)
    private BigDecimal quantityGBP = BigDecimal.ZERO;

    @Column(name = "quantity_USD", precision = 14, scale = 2)
    private BigDecimal quantityUSD = BigDecimal.ZERO;

    @Column(name = "quantity_CHF", precision = 14, scale = 2)
    private BigDecimal quantityCHF = BigDecimal.ZERO;

    public BankAccount() {
    }

    public BankAccount(String email) {
        this.email = email;
    }

    public BankAccount(UUID id, String email) {
        this.id = id;
        this.email = email;
    }

    public BankAccount(UUID id, String email, BigDecimal quantityRSD, BigDecimal quantityEUR,
                       BigDecimal quantityGBP, BigDecimal quantityUSD, BigDecimal quantityCHF) {
        this(id, email);
        this.quantityRSD = quantityRSD;
        this.quantityEUR = quantityEUR;
        this.quantityGBP = quantityGBP;
        this.quantityUSD = quantityUSD;
        this.quantityCHF = quantityCHF;
    }

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
}
