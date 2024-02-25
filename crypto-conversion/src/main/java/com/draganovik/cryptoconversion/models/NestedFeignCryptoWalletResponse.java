package com.draganovik.cryptoconversion.models;

import java.math.BigDecimal;

public class NestedFeignCryptoWalletResponse {
    private BigDecimal quantityBTC;
    private BigDecimal quantityETH;
    private BigDecimal quantityDOGE;

    public NestedFeignCryptoWalletResponse() {
    }

    public NestedFeignCryptoWalletResponse(FeignCryptoWalletResponse accountFeignResponse) {
        this.setQuantityBTC(accountFeignResponse.getQuantityBTC());
        this.setQuantityETH(accountFeignResponse.getQuantityETH());
        this.setQuantityDOGE(accountFeignResponse.getQuantityDOGE());
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
