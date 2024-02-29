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
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/trade-service")
public class TradeController {

    @Value("#{'${currencies.crypto.suppored}'.split(',')}")
    private List<String> cryptoCurrencies;

    @Value("#{'${currencies.fiat.suppored}'.split(',')}")
    private List<String> supportedFiatCurrencies;

    @Value("#{'${currencies.fiat.derivable}'.split(',')}")
    private List<String> derivableFiatCurrencies;

    private final TradeExchangeRepository tradeExchangeRepository;

    private final FeignCurrencyExchange feignCurrencyExchange;

    private final FeignCurrencyConversion feignCurrencyConversion;

    private final FeignBankAccount feignBankAccount;

    private final FeignCryptoWallet feignCryptoWallet;

    private final Environment environment;

    public TradeController(TradeExchangeRepository tradeExchangeRepository, FeignCurrencyExchange feignCurrencyExchange, FeignCurrencyConversion feignCurrencyConversion, FeignBankAccount feignBankAccount, FeignCryptoWallet feignCryptoWallet, Environment environment) {
        this.tradeExchangeRepository = tradeExchangeRepository;
        this.feignCurrencyExchange = feignCurrencyExchange;
        this.feignCurrencyConversion = feignCurrencyConversion;
        this.feignBankAccount = feignBankAccount;
        this.feignCryptoWallet = feignCryptoWallet;
        this.environment = environment;
    }

    @PostMapping()
    public ResponseEntity<?> requestTrade(@RequestParam String from, @RequestParam String to, @RequestParam BigDecimal quantity, HttpServletRequest request) throws Exception {

        String operatorEmail;
        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            operatorEmail = request.getHeader("X-User-Email");
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException("Request is not authorized.");
        }

        if (cryptoCurrencies.contains(from) && supportedFiatCurrencies.contains(to)) {
            CryptoCode fromCrypto = CryptoCode.valueOf(from);
            FiatCode toFiat = FiatCode.valueOf(to);

            Optional<TradeExchange> tradeExchangeOptional = tradeExchangeRepository.findByFromAndToIgnoreCase(fromCrypto.name(), toFiat.name());

            if (tradeExchangeOptional.isEmpty()) {
                throw new ExtendedExceptions.BadRequestException("No data for conversion from" + from + " to " + to);
            }

            try {
                feignCryptoWallet.cryptoWalletWithdraw(fromCrypto.name(), quantity, operatorEmail, operatorRole.name(), operatorEmail);
            } catch (FeignException feignException) {
                throw new ExtendedExceptions.BadRequestException(feignException.getMessage());
            }

            BigDecimal depositQuantity = quantity.multiply(tradeExchangeOptional.get().getConversionMultiple());

            try {
                feignBankAccount.accountExchangeDeposit(
                        toFiat.name(),
                        depositQuantity,
                        operatorEmail,
                        operatorRole.name(),
                        operatorEmail);

            } catch (FeignException feignException) {
                throw new ExtendedExceptions.BadRequestException(feignException.getMessage());
            }
            return feignBankAccount.getBankAccountByCurrentUser(operatorRole.name(), operatorEmail);
        } else if (supportedFiatCurrencies.contains(from) && cryptoCurrencies.contains(to)) {
            FiatCode fromFiat = FiatCode.valueOf(from);
            CryptoCode toCrypto = CryptoCode.valueOf(to);

            Optional<TradeExchange> tradeExchangeOptional = tradeExchangeRepository.findByFromAndToIgnoreCase(fromFiat.name(), toCrypto.name());

            if (tradeExchangeOptional.isEmpty()) {
                throw new ExtendedExceptions.BadRequestException("No data for conversion from" + from + " to " + to);
            }

            try {
                feignBankAccount.accountExchangeWithdraw(fromFiat.name(), quantity, operatorEmail, operatorRole.name(), operatorEmail);
            } catch (FeignException feignException) {
                throw new ExtendedExceptions.BadRequestException(feignException.getMessage());
            }

            BigDecimal depositQuantity = quantity.multiply(tradeExchangeOptional.get().getConversionMultiple());

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

            return feignCryptoWallet.getCryptoWalletByCurrentUser(operatorRole.name(), operatorEmail);

        } else {
            throw new ExtendedExceptions.BadRequestException("Trade with provided parameters: from: " + from + ", to: " + to + "is not supported.");
        }
    }
}
