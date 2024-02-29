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
import com.draganovik.tradeservice.models.FeignBankAccountResponse;
import com.draganovik.tradeservice.models.FeignCryptoWalletResponse;
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

        ResponseEntity<FeignBankAccountResponse> initialBankAccountResponse = feignBankAccount.getBankAccountByCurrentUser(operatorRole.name(), operatorEmail);
        ResponseEntity<FeignCryptoWalletResponse> initialCryptoWalletResponse = feignCryptoWallet.getCryptoWalletByCurrentUser(operatorRole.name(), operatorEmail);

        if (initialBankAccountResponse.getStatusCode() != HttpStatus.OK || initialCryptoWalletResponse.getStatusCode() != HttpStatus.OK) {
            throw new ExtendedExceptions.BadRequestException("Could not get response from downstream services.");
        }

        FeignBankAccountResponse initialBankAccount = initialBankAccountResponse.getBody();
        FeignCryptoWalletResponse initialCryptoWallet = initialCryptoWalletResponse.getBody();

        if (cryptoCurrencies.contains(from) && supportedFiatCurrencies.contains(to)) {
            CryptoCode fromCrypto = CryptoCode.valueOf(from);
            FiatCode toFiat = FiatCode.valueOf(to);

            if (!hasAvailableCryptoBalance(initialCryptoWallet, fromCrypto, quantity)) {
                throw new ExtendedExceptions.BadRequestException("Not enough balance to convert from: " + fromCrypto.name() + ".");
            }

            Optional<TradeExchange> tradeExchangeOptional = tradeExchangeRepository.findByFromAndToIgnoreCase(fromCrypto.name(), toFiat.name());

            if (tradeExchangeOptional.isEmpty()) {
                throw new ExtendedExceptions.BadRequestException("No data for conversion from" + from + " to " + to);
            }

            ResponseEntity<FeignCryptoWalletResponse> cryptoWalletResponse =
                    feignCryptoWallet.cryptoWalletWithdraw(fromCrypto.name(), quantity, operatorEmail, operatorRole.name(), operatorEmail);

            if (cryptoWalletResponse.getStatusCode() != HttpStatus.ACCEPTED) {
                return cryptoWalletResponse;
            }

            BigDecimal depositQuantity = quantity.multiply(tradeExchangeOptional.get().getConversionMultiple());

            ResponseEntity<FeignBankAccountResponse> bankAccountResponse =
                    feignBankAccount.accountExchangeDeposit(
                            toFiat.name(),
                            depositQuantity,
                            operatorEmail,
                            operatorRole.name(),
                            operatorEmail);

            if (bankAccountResponse.getStatusCode() != HttpStatus.ACCEPTED) {
                return bankAccountResponse;
            }
            return feignBankAccount.getBankAccountByCurrentUser(operatorRole.name(), operatorEmail);
        } else if (supportedFiatCurrencies.contains(from) && cryptoCurrencies.contains(to)) {
            FiatCode fromFiat = FiatCode.valueOf(from);
            CryptoCode toCrypto = CryptoCode.valueOf(to);

            if (!hasAvailableFiatBalance(initialBankAccount, fromFiat, quantity)) {
                throw new ExtendedExceptions.BadRequestException("Not enough balance to convert from: " + fromFiat.name() + ".");
            }

            Optional<TradeExchange> tradeExchangeOptional = tradeExchangeRepository.findByFromAndToIgnoreCase(fromFiat.name(), toCrypto.name());

            if (tradeExchangeOptional.isEmpty()) {
                throw new ExtendedExceptions.BadRequestException("No data for conversion from" + from + " to " + to);
            }

            ResponseEntity<FeignBankAccountResponse> bankAccountResponse =
                    feignBankAccount.accountExchangeWithdraw(fromFiat.name(), quantity, operatorEmail, operatorRole.name(), operatorEmail);

            if (bankAccountResponse.getStatusCode() != HttpStatus.ACCEPTED) {
                return bankAccountResponse;
            }

            BigDecimal depositQuantity = quantity.multiply(tradeExchangeOptional.get().getConversionMultiple());

            ResponseEntity<FeignCryptoWalletResponse> cryptoWalletResponse =
                    feignCryptoWallet.cryptoWalletDeposit(
                            toCrypto.name(),
                            depositQuantity,
                            operatorEmail,
                            operatorRole.name(),
                            operatorEmail);

            if (cryptoWalletResponse.getStatusCode() != HttpStatus.ACCEPTED) {
                return cryptoWalletResponse;
            }
            return feignCryptoWallet.getCryptoWalletByCurrentUser(operatorRole.name(), operatorEmail);

        } else {
            throw new ExtendedExceptions.BadRequestException("Trade with provided parameters: from: " + from + ", to: " + to + "is not supported.");
        }
    }

    private Boolean hasAvailableFiatBalance(FeignBankAccountResponse bankAccount, FiatCode code, BigDecimal amount) {
        switch (code) {
            case USD:
                return bankAccount.getQuantityUSD().compareTo(amount) >= 0;
            case EUR:
                return bankAccount.getQuantityEUR().compareTo(amount) >= 0;
            case RSD:
                return bankAccount.getQuantityRSD().compareTo(amount) >= 0;
            case GBP:
                return bankAccount.getQuantityGBP().compareTo(amount) >= 0;
            case CHF:
                return bankAccount.getQuantityCHF().compareTo(amount) >= 0;
        }
        return false;
    }

    private Boolean hasAvailableCryptoBalance(FeignCryptoWalletResponse cryptoWallet, CryptoCode code, BigDecimal amount) {
        switch (code) {
            case BTC:
                return cryptoWallet.getQuantityBTC().compareTo(amount) >= 0;
            case ETH:
                return cryptoWallet.getQuantityETH().compareTo(amount) >= 0;
            case DOGE:
                return cryptoWallet.getQuantityDOGE().compareTo(amount) >= 0;
        }
        return false;
    }
}
