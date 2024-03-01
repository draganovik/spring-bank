package com.draganovik.cryptoconversion.models;

import java.time.ZonedDateTime;

public class CryptoConversionResponse {

    private final ZonedDateTime timestamp = ZonedDateTime.now();
    private final String environment;
    private String message;
    private NestedFeignCryptoWalletResponse accountBalance;

    public CryptoConversionResponse(NestedFeignCryptoWalletResponse account, String message, String environment) {
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

    public NestedFeignCryptoWalletResponse getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(NestedFeignCryptoWalletResponse account) {
        this.accountBalance = account;
    }

    public String getEnvironment() {
        return environment;
    }
}
