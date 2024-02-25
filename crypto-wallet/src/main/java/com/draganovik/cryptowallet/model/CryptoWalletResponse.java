package com.draganovik.cryptowallet.model;


import java.math.BigDecimal;
import java.util.UUID;

public class CryptoWalletResponse {

    private UUID id;
    private String email;
    private BigDecimal quantityBTC;
    private BigDecimal quantityETH;
    private BigDecimal quantityDOGE;
    private String environment;

    public CryptoWalletResponse(UUID id, String email, BigDecimal quantityBTC, BigDecimal quantityETH, BigDecimal quantityDOGE, String environment) {
        this.id = id;
        this.email = email;
        this.quantityBTC = quantityBTC;
        this.quantityETH = quantityETH;
        this.quantityDOGE = quantityDOGE;
        this.environment = environment;
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

    public BigDecimal getQuantityBTC() {
        return quantityBTC;
    }

    public void setQuantityBTC(BigDecimal quantityBTC) {
        this.quantityBTC = quantityBTC;
    }

    public BigDecimal getQuantityETH() {
        return quantityETH;
    }

    public void setQuantityETH(BigDecimal quantityETH) {
        this.quantityETH = quantityETH;
    }

    public BigDecimal getQuantityDOGE() {
        return quantityDOGE;
    }

    public void setQuantityDOGE(BigDecimal quantityDOGE) {
        this.quantityDOGE = quantityDOGE;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
