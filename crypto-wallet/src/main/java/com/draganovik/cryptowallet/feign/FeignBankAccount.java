package com.draganovik.cryptowallet.feign;

import com.draganovik.cryptowallet.FeignBankAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "bank-account")
public interface FeignBankAccount {
    @GetMapping("/bank-account/self")
    ResponseEntity<FeignBankAccountResponse> getBankAccount(@RequestHeader("X-User-Role") String xUserRole, @RequestHeader("X-User-Email") String xUserEmail);
}