package com.draganovik.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "crypto-wallet")
public interface FeignCryptoWallet {

    @PostMapping("/crypto-wallet/{email}/update-email/{newEmail}")
    ResponseEntity<?> updateCryptoWalletEmail(@PathVariable String email,
                                              @PathVariable String newEmail,
                                              @RequestHeader("X-User-Role") String xUserRole);

    @DeleteMapping("/crypto-wallet/{email}")
    void deleteCryptoWallet(@PathVariable String email,
                            @RequestHeader("X-User-Role") String xUserRole);

}