package com.draganovik.cryptoconversion;

import com.draganovik.cryptoconversion.entities.CryptoCode;
import com.draganovik.cryptoconversion.entities.Role;
import com.draganovik.cryptoconversion.exceptions.ExtendedExceptions;
import com.draganovik.cryptoconversion.feign.FeignCryptoExchange;
import com.draganovik.cryptoconversion.feign.FeignCryptoWallet;
import com.draganovik.cryptoconversion.models.CryptoConversionResponse;
import com.draganovik.cryptoconversion.models.FeignCryptoExchangeResponse;
import com.draganovik.cryptoconversion.models.FeignCryptoWalletResponse;
import com.draganovik.cryptoconversion.models.NestedFeignCryptoWalletResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/crypto-conversion")
public class CryptoConversionController {

    private final Environment environment;

    private final FeignCryptoWallet cryptoWallet;

    private final FeignCryptoExchange feignCryptoExchange;

    public CryptoConversionController(Environment environment, FeignCryptoWallet cryptoWallet, FeignCryptoExchange feignCryptoExchange) {
        this.environment = environment;
        this.cryptoWallet = cryptoWallet;
        this.feignCryptoExchange = feignCryptoExchange;
    }

    @PostMapping()
    @RateLimiter(name = "default")
    public ResponseEntity<CryptoConversionResponse> performConversion(@RequestParam CryptoCode from, @RequestParam CryptoCode to, @RequestParam BigDecimal quantity, HttpServletRequest request) throws Exception {
        String operatorEmail;
        Role operatorRole;

        if (to == from) {
            throw new ExtendedExceptions.BadRequestException("Can't perform conversion to same currency.");
        }

        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            operatorEmail = request.getHeader("X-User-Email");

            if (operatorEmail.isEmpty() || operatorRole != Role.USER) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException("Only logged in USERs can perform this action.");
        }

        ResponseEntity<FeignCryptoWalletResponse> cryptoWalletResponse =
                cryptoWallet.getCryptoWalletByCurrentUser(operatorRole.name(), operatorEmail);

        if (cryptoWalletResponse.getStatusCode() != HttpStatus.OK) {
            throw new ExtendedExceptions.NotFoundException("Can't find account of a current user.");
        }

        FeignCryptoWalletResponse cryptoWallet = cryptoWalletResponse.getBody();

        if (cryptoWallet == null) {
            throw new ExtendedExceptions.NotFoundException("Can't find account of a current user.");
        }

        ResponseEntity<FeignCryptoExchangeResponse> cryptoExchangeResponse = feignCryptoExchange.getExchange(from.name(), to.name());

        if (cryptoExchangeResponse.getStatusCode() != HttpStatus.OK) {
            throw new ExtendedExceptions.NotFoundException("Can't get currency exchange.");
        }

        FeignCryptoExchangeResponse exchange = cryptoExchangeResponse.getBody();

        if (exchange == null) {
            throw new ExtendedExceptions.NotFoundException("Can't get currency exchange.");
        }

        BigDecimal convertedQuantity = exchange.getConversionMultiple().multiply(quantity);

        this.cryptoWallet.cryptoWalletWithdraw(from.name(), quantity, cryptoWallet.getEmail(),
                operatorRole.name(), operatorEmail);

        this.cryptoWallet.cryptoWalletDeposit(to.name(), convertedQuantity, cryptoWallet.getEmail(),
                operatorRole.name(), operatorEmail);

        cryptoWalletResponse =
                this.cryptoWallet.getCryptoWalletByCurrentUser(operatorRole.name(), operatorEmail);

        if (cryptoWalletResponse.getStatusCode() != HttpStatus.OK || cryptoWalletResponse.getBody() == null) {
            throw new ExtendedExceptions.NotFoundException("Can't find account of a current user.");
        }

        NestedFeignCryptoWalletResponse nestedWallet =
                new NestedFeignCryptoWalletResponse(cryptoWalletResponse.getBody());

        CryptoConversionResponse response = new CryptoConversionResponse(
                nestedWallet,
                "Successful! Converted " + quantity + " " + from.name() + " to " + to.name() + ".",
                environment.getProperty("local.server.port"));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorMessage = "Required parameter is missing: " + ex.getParameterName();
        return new ResponseEntity<>(errorMessage, status);
    }
}