package com.draganovik.currencyconversion.feign;

import com.draganovik.currencyconversion.models.BankAccountFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "bank-account")
public interface FeignFeignBankAccount {

    @GetMapping("/bank-account")
    ResponseEntity<BankAccountFeignResponse> getBankAccountByCurrentUser(
            @RequestHeader("X-User-Role") String xUserRole, @RequestHeader("X-User-Email") String xUserEmail);

    @PostMapping("/bank-account/{email}/withdraw")
    ResponseEntity<?> accountExchangeWithdraw(
            @RequestParam String currency, @RequestParam double amount, @PathVariable String email,
            @RequestHeader("X-User-Role") String xUserRole, @RequestHeader("X-User-Email") String xUserEmail);

    @PostMapping("/bank-account/{email}/deposit")
    ResponseEntity<?> accountExchangeDeposit(
            @RequestParam String currency, @RequestParam double amount, @PathVariable String email,
            @RequestHeader("X-User-Role") String xUserRole, @RequestHeader("X-User-Email") String xUserEmail);
}