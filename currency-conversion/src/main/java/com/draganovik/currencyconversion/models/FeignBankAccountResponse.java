package com.draganovik.currencyconversion.models;

import java.math.BigDecimal;
import java.util.UUID;

public class FeignBankAccountResponse {
    private UUID id;
    private String email;
    private BigDecimal quantityRSD;
    private BigDecimal quantityEUR;
    private BigDecimal quantityGBP;
    private BigDecimal quantityUSD;
    private BigDecimal quantityCHF;

    private String environment;

    public FeignBankAccountResponse() {
    }

    public FeignBankAccountResponse(UUID id, String email, BigDecimal quantityRSD, BigDecimal quantityEUR,
                                    BigDecimal quantityGBP, BigDecimal quantityUSD, BigDecimal quantityCHF,
                                    String environment) {
        this.setId(id);
        this.setEmail(email);
        this.setQuantityRSD(quantityRSD);
        this.setQuantityEUR(quantityEUR);
        this.setQuantityGBP(quantityGBP);
        this.setQuantityUSD(quantityUSD);
        this.setQuantityCHF(quantityCHF);
        this.setEnvironment(environment);
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

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
