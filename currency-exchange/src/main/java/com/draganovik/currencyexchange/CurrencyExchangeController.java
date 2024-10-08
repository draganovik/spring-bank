package com.draganovik.currencyexchange;

import com.draganovik.currencyexchange.exceptions.ExtendedExceptions;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/currency-exchange")
public class CurrencyExchangeController {

    private final CurrencyExchangeRepository currencyExchangeRepository;
    private final Environment environment;

    public CurrencyExchangeController(Environment environment, CurrencyExchangeRepository currencyExchangeRepository) {
        this.environment = environment;
        this.currencyExchangeRepository = currencyExchangeRepository;
    }

    @GetMapping("/from/{from}/to/{to}")
    public ResponseEntity<CurrencyExchangeResponse> getExchange(@PathVariable String from, @PathVariable String to) throws Exception {
        Optional<CurrencyExchange> exchange = currencyExchangeRepository.findByFromAndToIgnoreCase(from, to);

        if (exchange.isEmpty()) {
            throw new ExtendedExceptions.NotFoundException("Can't find exchange rates from " + from + " to " + to);
        }

        CurrencyExchangeResponse response = new CurrencyExchangeResponse(
                exchange.get().getFrom(),
                exchange.get().getTo(),
                exchange.get().getConversionMultiple(),
                environment.getProperty("local.server.port")
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
