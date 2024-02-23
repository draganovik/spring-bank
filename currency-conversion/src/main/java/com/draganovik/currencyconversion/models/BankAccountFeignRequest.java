package com.draganovik.currencyconversion.models;

import java.math.BigDecimal;
import java.util.UUID;

public class BankAccountFeignRequest {
    private BigDecimal quantityRSD;
    private BigDecimal quantityEUR;
    private BigDecimal quantityGBP;
    private BigDecimal quantityUSD;
    private BigDecimal quantityCHF;

    public BankAccountFeignRequest() {
    }

    public BankAccountFeignRequest(BigDecimal quantityRSD, BigDecimal quantityEUR,
                                   BigDecimal quantityGBP, BigDecimal quantityUSD, BigDecimal quantityCHF) {
        this.setQuantityRSD(quantityRSD);
        this.setQuantityEUR(quantityEUR);
        this.setQuantityGBP(quantityGBP);
        this.setQuantityUSD(quantityUSD);
        this.setQuantityCHF(quantityCHF);
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
