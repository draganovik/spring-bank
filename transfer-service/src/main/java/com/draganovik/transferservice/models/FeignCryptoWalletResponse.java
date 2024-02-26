package com.draganovik.transferservice.models;

import java.math.BigDecimal;
import java.util.UUID;

public class FeignCryptoWalletResponse {
    private UUID id;
    private String email;
    private BigDecimal quantityBTC;
    private BigDecimal quantityETH;
    private BigDecimal quantityDOGE;

    private String environment;

    public FeignCryptoWalletResponse() {
    }

    public FeignCryptoWalletResponse(UUID id, String email, BigDecimal quantityBTC, BigDecimal quantityETH,
                                     BigDecimal quantityDOGE,
                                     String environment) {
        this.setId(id);
        this.setEmail(email);
        this.setQuantityBTC(quantityBTC);
        this.setQuantityETH(quantityETH);
        this.setQuantityDOGE(quantityDOGE);
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
