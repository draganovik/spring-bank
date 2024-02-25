package com.draganovik.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bank-account")
public interface FeignBankAccount {

    @PutMapping("/bank-account/{email}/email/{newEmail}")
    ResponseEntity<?> updateBankAccount(@RequestParam String email, @RequestParam String newEmail);

    @DeleteMapping("/bank-account/{email}")
    void deleteBankAccount(@PathVariable String email);
}