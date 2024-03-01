package com.draganovik.tradeservice.models;

import java.time.ZonedDateTime;

public class FeignCurrencyConversionResponse {

    private final ZonedDateTime timestamp = ZonedDateTime.now();
    private final String environment;
    String message;
    FeignNestedFeignBankAccountResponse accountBalance;

    public FeignCurrencyConversionResponse(FeignNestedFeignBankAccountResponse account, String message, String environment) {
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

    public FeignNestedFeignBankAccountResponse getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(FeignNestedFeignBankAccountResponse account) {
        this.accountBalance = account;
    }

    public String getEnvironment() {
        return environment;
    }
}
