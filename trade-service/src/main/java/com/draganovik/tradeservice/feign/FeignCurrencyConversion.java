package com.draganovik.tradeservice.feign;

import com.draganovik.tradeservice.models.FeignCurrencyConversionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "currency-conversion")
public interface FeignCurrencyConversion {

    @PostMapping("/currency-conversion")
    ResponseEntity<FeignCurrencyConversionResponse> performConversion(
            @RequestParam String from, @RequestParam String to, @RequestParam BigDecimal quantity,
            @RequestHeader("X-User-Role") String xUserRole, @RequestHeader("X-User-Email") String xUserEmail);
}