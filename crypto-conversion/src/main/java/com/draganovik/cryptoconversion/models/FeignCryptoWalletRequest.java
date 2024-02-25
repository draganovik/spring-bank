package com.draganovik.cryptoconversion.models;

import java.math.BigDecimal;

public class FeignCryptoWalletRequest {
    private BigDecimal quantityBTC;
    private BigDecimal quantityETH;
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
