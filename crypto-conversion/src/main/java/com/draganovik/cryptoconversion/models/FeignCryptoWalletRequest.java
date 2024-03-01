package com.draganovik.cryptoconversion.models;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class FeignCryptoWalletRequest {

    @NotNull(message = "The quantityBTC cannot be null")
    @DecimalMin(value = "0.0", message = "The quantityRSD must be greater than or equal to 0")
    private BigDecimal quantityBTC;
    @NotNull(message = "The quantityETH cannot be null")
    @DecimalMin(value = "0.0", message = "The quantityRSD must be greater than or equal to 0")
    private BigDecimal quantityETH;
    @NotNull(message = "The quantityDOGE cannot be null")
    @DecimalMin(value = "0.0", message = "The quantityRSD must be greater than or equal to 0")
    private BigDecimal quantityDOGE;

    public FeignCryptoWalletRequest() {
    }

    public FeignCryptoWalletRequest(BigDecimal quantityBTC, BigDecimal quantityETH,
                                    BigDecimal quantityDOGE) {
        this.setQuantityBTC(quantityBTC);
        this.setQuantityETH(quantityETH);
        this.setQuantityDOGE(quantityDOGE);
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
