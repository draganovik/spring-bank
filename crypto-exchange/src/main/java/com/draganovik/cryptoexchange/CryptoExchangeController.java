package com.draganovik.cryptoexchange;

import com.draganovik.cryptoexchange.exceptions.ExtendedExceptions;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/crypto-exchange")
public class CryptoExchangeController {

    private final CryptoExchangeRepository cryptoExchangeRepository;

    private final Environment environment;

    public CryptoExchangeController(CryptoExchangeRepository repo, Environment environment) {
        this.environment = environment;
        this.cryptoExchangeRepository = repo;
    }

    @GetMapping("/from/{from}/to/{to}")
    public ResponseEntity<CryptoExchangeResponse> getExchange(@PathVariable String from, @PathVariable String to) throws Exception {
        Optional<CryptoExchange> exchange = cryptoExchangeRepository.findByFromAndToIgnoreCase(from, to);

        if (exchange.isEmpty()) {
            throw new ExtendedExceptions.NotFoundException("Can't find exchange rates from " + from + " to " + to);
        }

        CryptoExchangeResponse response = new CryptoExchangeResponse(
                exchange.get().getFrom(),
                exchange.get().getTo(),
                exchange.get().getConversionMultiple(),
                environment.getProperty("local.server.port")
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
