package com.draganovik.currencyconversion.models;

import java.math.BigDecimal;

public class CurrencyExchangeFeignResponse {

    private String from;
    private String to;
    private BigDecimal conversionMultiple;
    private String environment;

    public CurrencyExchangeFeignResponse() {
    }

    public CurrencyExchangeFeignResponse(String from, String to, BigDecimal conversionMultiple, String environment) {
        setFrom(from);
        setTo(to);
        setConversionMultiple(conversionMultiple);
        setEnvironment(environment);
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
}

