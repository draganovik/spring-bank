package com.draganovik.bankaccount.models;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BankAccountRequest {

    @NotNull(message = "The quantityRSD cannot be null")
    @DecimalMin(value = "0.0", message = "The quantityRSD must be greater than or equal to 0")
    private BigDecimal quantityRSD;
    @NotNull(message = "The quantityEUR cannot be null")
    @DecimalMin(value = "0.0", message = "The quantityEUR must be greater than or equal to 0")
    private BigDecimal quantityEUR;
    @NotNull(message = "The quantityGBP cannot be null")
    @DecimalMin(value = "0.0", message = "The quantityGBP must be greater than or equal to 0")
    private BigDecimal quantityGBP;
    @NotNull(message = "The quantityUSD cannot be null")
    @DecimalMin(value = "0.0", message = "The quantityUSD must be greater than or equal to 0")
    private BigDecimal quantityUSD;
    @NotNull(message = "The quantityCHF cannot be null")
    @DecimalMin(value = "0.0", message = "The quantityCHF must be greater than or equal to 0")
    private BigDecimal quantityCHF;

    public BankAccountRequest() {
    }

    public BankAccountRequest(BigDecimal quantityRSD, BigDecimal quantityEUR,
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
