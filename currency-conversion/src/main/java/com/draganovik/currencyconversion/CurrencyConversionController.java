package com.draganovik.currencyconversion;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy proxy;

    //localhost:8100/currency-conversion/from/EUR/to/RSD/quantity/100
    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion getConversion
    (@PathVariable String from, @PathVariable String to, @PathVariable double quantity) {

        HashMap<String, String> uriVariables = new HashMap<String, String>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversion> response =
                new RestTemplate().
                        getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                                CurrencyConversion.class, uriVariables);

        CurrencyConversion cc = response.getBody();

        return new CurrencyConversion(from, to, cc.getConversionMultiple(), cc.getEnvironment(), quantity,
                cc.getConversionMultiple().multiply(BigDecimal.valueOf(quantity)));
    }

    //localhost:8100/currency-conversion?from=EUR&to=RSD&quantity=50
    @GetMapping("/currency-conversion")
    public ResponseEntity<?> getConversionParams(@RequestParam String from, @RequestParam String to, @RequestParam double quantity) {

        HashMap<String, String> uriVariable = new HashMap<String, String>();
        uriVariable.put("from", from);
        uriVariable.put("to", to);

        try {
            ResponseEntity<CurrencyConversion> response = new RestTemplate().
                    getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class, uriVariable);
            CurrencyConversion responseBody = response.getBody();
            return ResponseEntity.status(HttpStatus.OK).body(new CurrencyConversion(from, to, responseBody.getConversionMultiple(), responseBody.getEnvironment(),
                    quantity, responseBody.getConversionMultiple().multiply(BigDecimal.valueOf(quantity))));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    //localhost:8100/currency-conversion-feign?from=EUR&to=RSD&quantity=50
    @GetMapping("/currency-conversion-feign")
    public ResponseEntity<?> getConversionFeign(@RequestParam String from, @RequestParam String to, @RequestParam double quantity) {

        try {
            ResponseEntity<CurrencyConversion> response = proxy.getExchange(from, to);
            CurrencyConversion responseBody = response.getBody();
            return ResponseEntity.ok(new CurrencyConversion(from, to, responseBody.getConversionMultiple(), responseBody.getEnvironment(),
                    quantity, responseBody.getConversionMultiple().multiply(BigDecimal.valueOf(quantity))));
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.getMessage());
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorMessage = "Required parameter is missing: " + ex.getParameterName();
        return new ResponseEntity<>(errorMessage, status);
    }
}