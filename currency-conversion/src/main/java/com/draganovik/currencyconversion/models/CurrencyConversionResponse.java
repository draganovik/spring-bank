package com.draganovik.currencyconversion.models;

import java.time.ZonedDateTime;

public class CurrencyConversionResponse {

    String message;

    NestedFeignBankAccountResponse accountBalance;

    private final ZonedDateTime timestamp = ZonedDateTime.now();

    public CurrencyConversionResponse(NestedFeignBankAccountResponse account, String message) {
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

    public NestedFeignBankAccountResponse getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(NestedFeignBankAccountResponse account) {
        this.accountBalance = account;
    }
}
