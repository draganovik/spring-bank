package com.draganovik.tradeservice.models;

import java.time.ZonedDateTime;

public class TradeCryptoResponse {

    private final ZonedDateTime timestamp = ZonedDateTime.now();
    private final String environment;
    private String message;
    private FeignNestedFeignCryptoWalletResponse accountBalance;

    public TradeCryptoResponse(FeignNestedFeignCryptoWalletResponse account, String message, String environment) {
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

    public FeignNestedFeignCryptoWalletResponse getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(FeignNestedFeignCryptoWalletResponse account) {
        this.accountBalance = account;
    }

    public String getEnvironment() {
        return environment;
    }
}
