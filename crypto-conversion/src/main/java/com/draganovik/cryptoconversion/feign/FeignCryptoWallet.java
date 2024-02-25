package com.draganovik.cryptoconversion.feign;

import com.draganovik.cryptoconversion.models.FeignCryptoWalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "crypto-wallet")
public interface FeignCryptoWallet {

    @GetMapping("/crypto-wallet/self")
    ResponseEntity<FeignCryptoWalletResponse> getCryptoWalletByCurrentUser(
            @RequestHeader("X-User-Role") String xUserRole, @RequestHeader("X-User-Email") String xUserEmail);

    @PostMapping("/crypto-wallet/{email}/withdraw")
    ResponseEntity<?> cryptoWalletWithdraw(
            @RequestParam String currency, @RequestParam BigDecimal amount, @PathVariable String email,
            @RequestHeader("X-User-Role") String xUserRole, @RequestHeader("X-User-Email") String xUserEmail);

    @PostMapping("/crypto-wallet/{email}/deposit")
    ResponseEntity<?> cryptoWalletDeposit(
            @RequestParam String currency, @RequestParam BigDecimal amount, @PathVariable String email,
            @RequestHeader("X-User-Role") String xUserRole, @RequestHeader("X-User-Email") String xUserEmail);
}