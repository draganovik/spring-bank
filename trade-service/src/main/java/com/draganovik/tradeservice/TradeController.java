package com.draganovik.tradeservice;

import com.draganovik.tradeservice.entities.CryptoCode;
import com.draganovik.tradeservice.entities.FiatCode;
import com.draganovik.tradeservice.entities.Role;
import com.draganovik.tradeservice.entities.TradeExchange;
import com.draganovik.tradeservice.exceptions.ExtendedExceptions;
import com.draganovik.tradeservice.feign.FeignBankAccount;
import com.draganovik.tradeservice.feign.FeignCryptoWallet;
import com.draganovik.tradeservice.feign.FeignCurrencyConversion;
import com.draganovik.tradeservice.feign.FeignCurrencyExchange;
import com.draganovik.tradeservice.models.*;
import feign.FeignException;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/trade-service")
public class TradeController {

    private final TradeExchangeRepository tradeExchangeRepository;
    private final FeignCurrencyExchange feignCurrencyExchange;
    private final FeignCurrencyConversion feignCurrencyConversion;
    private final FeignBankAccount feignBankAccount;
    private final FeignCryptoWallet feignCryptoWallet;
    private final Environment environment;
    @Value("#{'${currencies.crypto.suppored}'.split(',')}")
    private List<String> cryptoCurrencies;
    @Value("#{'${currencies.fiat.supported}'.split(',')}")
    private List<String> supportedFiatCurrencies;

    public TradeController(Environment environment, TradeExchangeRepository tradeExchangeRepository,
                           FeignCurrencyExchange feignCurrencyExchange, FeignCurrencyConversion feignCurrencyConversion,
                           FeignBankAccount feignBankAccount, FeignCryptoWallet feignCryptoWallet) {
        this.environment = environment;
        this.tradeExchangeRepository = tradeExchangeRepository;
        this.feignCurrencyExchange = feignCurrencyExchange;
        this.feignCurrencyConversion = feignCurrencyConversion;
        this.feignBankAccount = feignBankAccount;
        this.feignCryptoWallet = feignCryptoWallet;
    }

    @PostMapping()
    @RateLimiter(name = "default")
    @Retry(name = "tade-service", fallbackMethod = "tradeFallbackMethod")
    public ResponseEntity<?> requestTrade(@RequestParam String from, @RequestParam String to,
                                          @RequestParam BigDecimal quantity, HttpServletRequest request) throws Exception {

        String operatorEmail;
        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            operatorEmail = request.getHeader("X-User-Email");
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException("Request is not authorized.");
        }

        if (cryptoCurrencies.contains(from)) {
            CryptoCode fromCrypto = CryptoCode.valueOf(from);
            FiatCode toFiat = FiatCode.valueOf(to);
            FiatCode toSupportedFiat;

            if (!supportedFiatCurrencies.contains(to)) {
                toSupportedFiat = FiatCode.EUR;
            } else {
                toSupportedFiat = toFiat;
            }

            Optional<TradeExchange> tradeExchangeOptional =
                    tradeExchangeRepository.findByFromAndToIgnoreCase(fromCrypto.name(), toSupportedFiat.name());

            if (tradeExchangeOptional.isEmpty()) {
                throw new ExtendedExceptions.BadRequestException("No data for conversion from " + from + " to " + to);
            }

            try {
                feignCryptoWallet.cryptoWalletWithdraw(
                        fromCrypto.name(),
                        quantity,
                        operatorEmail,
                        operatorRole.name(),
                        operatorEmail);
            } catch (FeignException feignException) {
                throw new ExtendedExceptions.BadRequestException(feignException.getMessage());
            }

            BigDecimal depositQuantity = quantity.multiply(tradeExchangeOptional.get().getConversionMultiple());

            try {
                feignBankAccount.accountExchangeDeposit(
                        toSupportedFiat.name(),
                        depositQuantity,
                        operatorEmail,
                        operatorRole.name(),
                        operatorEmail);
            } catch (FeignException feignException) {
                throw new ExtendedExceptions.BadRequestException(feignException.getMessage());
            }

            if (!supportedFiatCurrencies.contains(to)) {
                feignCurrencyConversion.performConversion(
                        toSupportedFiat.name(),
                        toFiat.name(),
                        depositQuantity,
                        operatorRole.name(),
                        operatorEmail);
            }

            ResponseEntity<FeignBankAccountResponse> bankAccount =
                    feignBankAccount.getBankAccountByCurrentUser(operatorRole.name(), operatorEmail);

            TradeCurrencyResponse response = new TradeCurrencyResponse(
                    new FeignNestedFeignBankAccountResponse(Objects.requireNonNull(bankAccount.getBody())),
                    "Successful! Converted " + quantity + " " + fromCrypto.name() + " to " + toFiat.name() + ".",
                    environment.getProperty("local.server.port"));

            return new ResponseEntity<>(response, HttpStatus.OK);

        }

        if (cryptoCurrencies.contains(to)) {
            FiatCode fromFiat = FiatCode.valueOf(from);
            FiatCode fromSupportedFiat;
            BigDecimal supportedQuantity;
            CryptoCode toCrypto = CryptoCode.valueOf(to);

            if (!supportedFiatCurrencies.contains(from)) {
                fromSupportedFiat = FiatCode.EUR;

                ResponseEntity<FeignCurrencyExchangeResponse> derivableExchange =
                        feignCurrencyExchange.getExchange(fromFiat.name(), fromSupportedFiat.name());

                feignCurrencyConversion.performConversion(
                        fromFiat.name(),
                        fromSupportedFiat.name(),
                        quantity,
                        operatorRole.name(),
                        operatorEmail);

                supportedQuantity = quantity.multiply(
                        Objects.requireNonNull(derivableExchange.getBody()).getConversionMultiple()
                );
            } else {
                fromSupportedFiat = fromFiat;
                supportedQuantity = quantity;
            }

            Optional<TradeExchange> tradeExchangeOptional =
                    tradeExchangeRepository.findByFromAndToIgnoreCase(fromSupportedFiat.name(), toCrypto.name());

            if (tradeExchangeOptional.isEmpty()) {
                throw new ExtendedExceptions.BadRequestException(
                        "No data for conversion from " + fromSupportedFiat.name() + " to " + to
                );
            }

            try {
                feignBankAccount.accountExchangeWithdraw(
                        fromSupportedFiat.name(),
                        supportedQuantity,
                        operatorEmail,
                        operatorRole.name(),
                        operatorEmail);
            } catch (FeignException feignException) {
                throw new ExtendedExceptions.BadRequestException(feignException.getMessage());
            }

            BigDecimal depositQuantity = supportedQuantity.multiply(tradeExchangeOptional.get().getConversionMultiple());

            try {
                feignCryptoWallet.cryptoWalletDeposit(
                        toCrypto.name(),
                        depositQuantity,
                        operatorEmail,
                        operatorRole.name(),
                        operatorEmail);
            } catch (FeignException feignException) {
                throw new ExtendedExceptions.BadRequestException(feignException.getMessage());
            }

            ResponseEntity<FeignCryptoWalletResponse> cryptoWallet =
                    feignCryptoWallet.getCryptoWalletByCurrentUser(operatorRole.name(), operatorEmail);

            TradeCryptoResponse response = new TradeCryptoResponse(
                    new FeignNestedFeignCryptoWalletResponse(Objects.requireNonNull(cryptoWallet.getBody())),
                    "Successful! Converted " + quantity + " " + fromFiat.name() + " to " + toCrypto.name() + ".",
                    environment.getProperty("local.server.port"));

            return new ResponseEntity<>(response, HttpStatus.OK);

        }

        throw new ExtendedExceptions.BadRequestException(
                "Trade with provided parameters: from: " + from + ", to: " + to + "is not supported."
        );
    }

    public ResponseEntity<?> tradeFallbackMethod(@RequestParam String from, @RequestParam String to,
                                                 @RequestParam BigDecimal quantity, HttpServletRequest request, Throwable throwable) throws Exception {
        List<TradeExchange> tradeExchange = tradeExchangeRepository.findAll();

        TradeFallbackResponse response = new TradeFallbackResponse(
                "No change applied. Returning available exchange from database.",
                tradeExchange
        );

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
