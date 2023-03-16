package com.draganovik.currencyconversion;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController {

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public ResponseEntity<CurrencyConversion> getConversion(@PathVariable String from, @PathVariable String to, @PathVariable double quantity) {
        HashMap<String, String> uriVariables = new HashMap<String, String>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);
        ResponseEntity<CurrencyConversion> respose =
                new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class, uriVariables);

        // return respose.getBody();

        CurrencyConversion cc = respose.getBody();

        if (cc == null) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(new CurrencyConversion(cc.getId(), from, to, cc.getConversionMultiple(), cc.getEnvironment(), quantity, cc.getConversionMultiple().multiply(BigDecimal.valueOf(quantity))));
    }

}

