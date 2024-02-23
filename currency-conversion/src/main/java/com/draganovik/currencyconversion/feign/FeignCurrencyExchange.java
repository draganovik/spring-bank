package com.draganovik.currencyconversion.feign;

import com.draganovik.currencyconversion.models.CurrencyExchangeFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "currency-exchange")
public interface FeignCurrencyExchange {

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    ResponseEntity<CurrencyExchangeFeignResponse> getExchange(@PathVariable String from, @PathVariable String to);
}
