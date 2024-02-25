package com.draganovik.cryptoconversion.feign;

import com.draganovik.cryptoconversion.models.FeignCryptoExchangeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "crypto-exchange")
public interface FeignCryptoExchange {

    @GetMapping("/crypto-exchange/from/{from}/to/{to}")
    ResponseEntity<FeignCryptoExchangeResponse> getExchange(@PathVariable String from, @PathVariable String to);
}
