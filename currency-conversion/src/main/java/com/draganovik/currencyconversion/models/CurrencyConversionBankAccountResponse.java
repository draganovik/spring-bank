package com.draganovik.currencyconversion.models;

import java.math.BigDecimal;

public class CurrencyConversionBankAccountResponse {
    private BigDecimal quantityRSD;
    private BigDecimal quantityEUR;
    private BigDecimal quantityGBP;
    private BigDecimal quantityUSD;
    private BigDecimal quantityCHF;

    public CurrencyConversionBankAccountResponse() {
    }

    public CurrencyConversionBankAccountResponse(BankAccountFeignResponse accountFeignResponse) {
        this.setQuantityRSD(accountFeignResponse.getQuantityRSD());
        this.setQuantityEUR(accountFeignResponse.getQuantityEUR());
        this.setQuantityGBP(accountFeignResponse.getQuantityGBP());
        this.setQuantityUSD(accountFeignResponse.getQuantityUSD());
        this.setQuantityCHF(accountFeignResponse.getQuantityCHF());
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
