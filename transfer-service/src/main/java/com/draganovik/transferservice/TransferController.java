package com.draganovik.transferservice;

import com.draganovik.transferservice.entities.CryptoCode;
import com.draganovik.transferservice.entities.CurrencyCode;
import com.draganovik.transferservice.entities.Role;
import com.draganovik.transferservice.exceptions.ExtendedExceptions;
import com.draganovik.transferservice.feign.FeignBankAccount;
import com.draganovik.transferservice.feign.FeignCryptoWallet;
import com.draganovik.transferservice.models.TransferResponse;
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

    public TransferController(FeignCryptoWallet feignCryptoWallet, FeignBankAccount feignBankAccount, Environment environment) {
        this.feignCryptoWallet = feignCryptoWallet;
        this.feignBankAccount = feignBankAccount;
        this.environment = environment;
    }

    @PostMapping()
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

        if (Objects.equals(currency, CryptoCode.BTC.name()) ||
                Objects.equals(currency, CryptoCode.ETH.name()) ||
                Objects.equals(currency, CryptoCode.DOGE.name())
        ) {
            feignCryptoWallet.cryptoWalletWithdraw(currency, quantity, operatorEmail, operatorRole.name(), operatorEmail);
            feignCryptoWallet.cryptoWalletDeposit(currency, quantity.multiply(transferNetAmount), to, operatorRole.name(), operatorEmail);
        } else if (Objects.equals(currency, CurrencyCode.EUR.name()) ||
                Objects.equals(currency, CurrencyCode.USD.name()) ||
                Objects.equals(currency, CurrencyCode.CHF.name()) ||
                Objects.equals(currency, CurrencyCode.RSD.name()) ||
                Objects.equals(currency, CurrencyCode.GBP.name())
        ) {
            feignBankAccount.accountExchangeWithdraw(currency, quantity, operatorEmail, operatorRole.name(), operatorEmail);
            feignBankAccount.accountExchangeDeposit(currency, quantity.multiply(transferNetAmount), to, operatorRole.name(), operatorEmail);
        } else {
            throw new ExtendedExceptions.BadRequestException("Provided currency is not supported.");
        }

        return new ResponseEntity<>(new TransferResponse("Transaction successful.",
                to, quantity, operatorEmail, quantity.multiply(transferNetAmount), environment.getProperty("local.server.port")), HttpStatus.OK);
    }
}
