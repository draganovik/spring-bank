package com.draganovik.transferservice.models;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class TransferResponse {
    private final ZonedDateTime timestamp = ZonedDateTime.now();
    private final String environment;
    private String message;
    private String withdrawAccount;
    private BigDecimal withdrawQuantity;
    private String depositAccount;
    private BigDecimal depositQuantity;

    public TransferResponse(String message, String withdrawAccount, BigDecimal withdrawQuantity, String depositAccount, BigDecimal depositQuantity, String environment) {
        this.message = message;
        this.withdrawAccount = withdrawAccount;
        this.withdrawQuantity = withdrawQuantity;
        this.depositAccount = depositAccount;
        this.depositQuantity = depositQuantity;
        this.environment = environment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWithdrawAccount() {
        return withdrawAccount;
    }

    public void setWithdrawAccount(String withdrawAccount) {
        this.withdrawAccount = withdrawAccount;
    }

    public BigDecimal getWithdrawQuantity() {
        return withdrawQuantity;
    }

    public void setWithdrawQuantity(BigDecimal withdrawQuantity) {
        this.withdrawQuantity = withdrawQuantity;
    }

    public String getDepositAccount() {
        return depositAccount;
    }

    public void setDepositAccount(String depositAccount) {
        this.depositAccount = depositAccount;
    }

    public BigDecimal getDepositQuantity() {
        return depositQuantity;
    }

    public void setDepositQuantity(BigDecimal depositQuantity) {
        this.depositQuantity = depositQuantity;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getEnvironment() {
        return environment;
    }
}
