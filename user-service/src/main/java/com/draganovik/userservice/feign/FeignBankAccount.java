package com.draganovik.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "bank-account")
public interface FeignBankAccount {

    @PostMapping("/bank-account/{email}/update-email/{newEmail}")
    ResponseEntity<?> updateBankAccountEmail(@PathVariable String email,
                                             @PathVariable String newEmail,
                                             @RequestHeader("X-User-Role") String xUserRole);

    @DeleteMapping("/bank-account/{email}")
    void deleteBankAccount(@PathVariable String email,
                           @RequestHeader("X-User-Role") String xUserRole);
}