package com.draganovik.tradeservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class TradeExchange {

    @Id
    private long id;
    @Column(name = "code_from")
    private String from;
    @Column(name = "code_to")
    private String to;
    @Column(name = "conversion_multiple", precision = 19, scale = 6)
    private BigDecimal conversionMultiple;

    public TradeExchange() {
    }

    public TradeExchange(long id, String from, String to, BigDecimal conversionMultiple) {
        setId(id);
        setFrom(from);
        setTo(to);
        setConversionMultiple(conversionMultiple);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getConversionMultiple() {
        return conversionMultiple;
    }

    public void setConversionMultiple(BigDecimal conversionMultiple) {
        this.conversionMultiple = conversionMultiple;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

