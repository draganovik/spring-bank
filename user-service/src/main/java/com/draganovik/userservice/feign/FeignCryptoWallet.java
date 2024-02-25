package com.draganovik.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "crypto-wallet")
public interface FeignCryptoWallet {
    @DeleteMapping("/crypto-wallet/{email}")
    void deleteCryptoWallet(@PathVariable String email);
}