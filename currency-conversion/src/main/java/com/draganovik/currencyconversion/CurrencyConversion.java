package com.draganovik.currencyconversion;

import java.math.BigDecimal;

public class CurrencyConversion {

    private long id;
    private String from;
    private String to;
    private BigDecimal conversionMultiple;
    private String environment;

    private double quantity;
    private BigDecimal conversionTotal;

    public CurrencyConversion() { }

    public CurrencyConversion(long id, String from, String to, BigDecimal conversionMultiple, String envirovement, double quantity, BigDecimal conversionTotal) {
        setId(id);
        setFrom(from);
        setTo(to);
        setConversionMultiple(conversionMultiple);
        setEnvironment(envirovement);
        setQuantity(quantity);
        setConversionTotal(conversionTotal);
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setConversionTotal(BigDecimal conversionTotal) {
        this.conversionTotal = conversionTotal;
    }

    public BigDecimal getConversionTotal() {
        return conversionTotal;
    }

    public double getQuantity() {
        return quantity;
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

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

