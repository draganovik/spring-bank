package com.draganovik.cryptowallet;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "crypto_wallets")
@Table(indexes = @Index(columnList = "email"))
public class CryptoWallet {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "ID", updatable = false, nullable = false)
    @ColumnDefault("random_uuid()")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column(name = "quantity_BTC", precision = 19, scale = 6)
    private BigDecimal quantityBTC = BigDecimal.ZERO;

    @Column(name = "quantity_ETH", precision = 19, scale = 6)
    private BigDecimal quantityETH = BigDecimal.ZERO;

    @Column(name = "quantity_DOGE", precision = 19, scale = 6)
    private BigDecimal quantityDOGE = BigDecimal.ZERO;

    public CryptoWallet() {
        super();
    }

    public CryptoWallet(String email) {
        super();
        this.email = email;
    }

    public CryptoWallet(UUID id, String email, BigDecimal quantityBTC, BigDecimal quantityETH,
                        BigDecimal quantityDOGE, String environment) {
        super();
        this.id = id;
        this.email = email;
        this.quantityBTC = quantityBTC;
        this.quantityETH = quantityETH;
        this.quantityDOGE = quantityDOGE;
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

}
