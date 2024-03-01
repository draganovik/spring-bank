package com.draganovik.cryptowallet.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CryptoWalletRequest {

    @NotNull(message = "The quantityBTC cannot be null")
    @DecimalMin(value = "0.0", message = "The quantityBTC must be greater than or equal to 0")
    private BigDecimal quantityBTC = BigDecimal.ZERO;

    @NotNull(message = "The quantityETH cannot be null")
    @DecimalMin(value = "0.0", message = "The quantityETH must be greater than or equal to 0")
    private BigDecimal quantityETH = BigDecimal.ZERO;

    @NotNull(message = "The quantityDOGE cannot be null")
    @DecimalMin(value = "0.0", message = "The quantityDOGE must be greater than or equal to 0")
    private BigDecimal quantityDOGE = BigDecimal.ZERO;

    public CryptoWalletRequest() {
        super();
    }

    public CryptoWalletRequest(BigDecimal quantityBTC, BigDecimal quantityETH,
                               BigDecimal quantityDOGE, String environment) {
        super();
        this.quantityBTC = quantityBTC;
        this.quantityETH = quantityETH;
        this.quantityDOGE = quantityDOGE;
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

}
