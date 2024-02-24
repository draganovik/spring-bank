package com.draganovik.currencyconversion.models;

import java.time.ZonedDateTime;

public class CurrencyConversionResponse {

    String message;

    CurrencyConversionBankAccountResponse accountBalance;

    private final ZonedDateTime timestamp = ZonedDateTime.now();

    public CurrencyConversionResponse(CurrencyConversionBankAccountResponse account, String message) {
        this.message = message;
        this.accountBalance = account;
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

    public CurrencyConversionBankAccountResponse getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(CurrencyConversionBankAccountResponse account) {
        this.accountBalance = account;
    }
}
