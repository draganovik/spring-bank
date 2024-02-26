package com.draganovik.transferservice.feign;

import com.draganovik.transferservice.models.FeignBankAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "bank-account")
public interface FeignBankAccount {

    @GetMapping("/bank-account/self")
    ResponseEntity<FeignBankAccountResponse> getBankAccountByCurrentUser(
            @RequestHeader("X-User-Role") String xUserRole, @RequestHeader("X-User-Email") String xUserEmail);

    @PostMapping("/bank-account/{email}/withdraw")
    ResponseEntity<FeignBankAccountResponse> accountExchangeWithdraw(
            @RequestParam String currency, @RequestParam BigDecimal amount, @PathVariable String email,
            @RequestHeader("X-User-Role") String xUserRole, @RequestHeader("X-User-Email") String xUserEmail);

    @PostMapping("/bank-account/{email}/deposit")
    ResponseEntity<FeignBankAccountResponse> accountExchangeDeposit(
            @RequestParam String currency, @RequestParam BigDecimal amount, @PathVariable String email,
            @RequestHeader("X-User-Role") String xUserRole, @RequestHeader("X-User-Email") String xUserEmail);
}