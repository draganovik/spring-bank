package com.draganovik.cryptowallet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Entity
public class CryptoWallet {

    @Id
    private long id;

    @Column(unique = true)
    private String email;

    @Column(name = "quantity_BTC")
    private BigDecimal quantityBTC;

    @Column(name = "quantity_ETH")
    private BigDecimal quantityETH;

    @Column(name = "quantity_BNB")
    private BigDecimal quantityBNB;

    @Transient
    private String environment;

    public CryptoWallet() {
        super();
    }

    public CryptoWallet(long id, String email, BigDecimal quantityBTC, BigDecimal quantityETH,
                        BigDecimal quantityBNB, String environment) {
        super();
        this.id = id;
        this.email = email;
        this.quantityBTC = quantityBTC;
        this.quantityETH = quantityETH;
        this.quantityBNB = quantityBNB;
        this.environment = environment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public BigDecimal getQuantityBNB() {
        return quantityBNB;
    }

    public void setQuantityBNB(BigDecimal quantityBNB) {
        this.quantityBNB = quantityBNB;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

}
