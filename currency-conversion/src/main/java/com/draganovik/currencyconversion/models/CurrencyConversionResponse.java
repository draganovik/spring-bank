package com.draganovik.currencyconversion.models;

import java.time.ZonedDateTime;

public class CurrencyConversionResponse {

    String message;

    NestedFeignBankAccountResponse accountBalance;

    private final ZonedDateTime timestamp = ZonedDateTime.now();

    private final String environment;

    public CurrencyConversionResponse(NestedFeignBankAccountResponse account, String message, String environment) {
        this.message = message;
        this.accountBalance = account;
        this.environment = environment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public NestedFeignBankAccountResponse getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(NestedFeignBankAccountResponse account) {
        this.accountBalance = account;
    }

    public String getEnvironment() {
        return environment;
    }
}
