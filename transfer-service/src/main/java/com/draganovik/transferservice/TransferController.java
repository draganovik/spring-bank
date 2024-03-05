package com.draganovik.transferservice;

import com.draganovik.transferservice.entities.CryptoCode;
import com.draganovik.transferservice.entities.FiatCode;
import com.draganovik.transferservice.entities.Role;
import com.draganovik.transferservice.exceptions.ExtendedExceptions;
import com.draganovik.transferservice.feign.FeignBankAccount;
import com.draganovik.transferservice.feign.FeignCryptoWallet;
import com.draganovik.transferservice.models.TransferResponse;
import feign.FeignException;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Objects;

@RestController
@RequestMapping("/transfer-service")
public class TransferController {

    private final Environment environment;
    private final FeignCryptoWallet feignCryptoWallet;
    private final FeignBankAccount feignBankAccount;
    private final BigDecimal transferNetAmount = BigDecimal.valueOf(0.99);

    public TransferController(Environment environment, FeignCryptoWallet feignCryptoWallet,
                              FeignBankAccount feignBankAccount) {
        this.environment = environment;
        this.feignCryptoWallet = feignCryptoWallet;
        this.feignBankAccount = feignBankAccount;
    }

    @PostMapping()
    @RateLimiter(name = "default")
    public ResponseEntity<TransferResponse> requestTransfer(
            @RequestParam String currency,
            @RequestParam String to,
            @RequestParam BigDecimal quantity,
            HttpServletRequest request
    ) throws Exception {

        String operatorEmail;
        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            operatorEmail = request.getHeader("X-User-Email");
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException("Request is not authorized.");
        }

        if (Objects.equals(operatorEmail, to)) {
            throw new ExtendedExceptions.BadRequestException("Can't transfer to the same account.");
        }

        if (Objects.equals(currency, CryptoCode.BTC.name()) ||
                Objects.equals(currency, CryptoCode.ETH.name()) ||
                Objects.equals(currency, CryptoCode.DOGE.name())
        ) {
            try {
                feignCryptoWallet.getCryptoWalletByCurrentUser("USER", to);
                feignCryptoWallet.cryptoWalletWithdraw(
                        currency, quantity, operatorEmail, operatorRole.name(), operatorEmail
                );
                feignCryptoWallet.cryptoWalletDeposit(
                        currency, quantity.multiply(transferNetAmount), to, operatorRole.name(), operatorEmail
                );
            } catch (FeignException feignException) {
                throw new ExtendedExceptions.BadRequestException(feignException.getMessage());
            }

        } else if (Objects.equals(currency, FiatCode.EUR.name()) ||
                Objects.equals(currency, FiatCode.USD.name()) ||
                Objects.equals(currency, FiatCode.CHF.name()) ||
                Objects.equals(currency, FiatCode.RSD.name()) ||
                Objects.equals(currency, FiatCode.GBP.name())
        ) {
            try {
                feignBankAccount.getBankAccountByCurrentUser("USER", to);
                feignBankAccount.accountExchangeWithdraw(currency, quantity, operatorEmail, operatorRole.name(), operatorEmail);
                feignBankAccount.accountExchangeDeposit(currency, quantity.multiply(transferNetAmount), to, operatorRole.name(), operatorEmail);
            } catch (FeignException feignException) {
                throw new ExtendedExceptions.BadRequestException(feignException.getMessage());
            }

        } else {
            throw new ExtendedExceptions.BadRequestException("Provided currency is not supported.");
        }

        return new ResponseEntity<>(new TransferResponse("Transaction successful.",
                operatorEmail, quantity, to, quantity.multiply(transferNetAmount),
                environment.getProperty("local.server.port")), HttpStatus.OK);
    }
}
