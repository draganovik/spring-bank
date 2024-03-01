package com.draganovik.currencyconversion;

import com.draganovik.currencyconversion.entities.FiatCode;
import com.draganovik.currencyconversion.entities.Role;
import com.draganovik.currencyconversion.exceptions.ExtendedExceptions;
import com.draganovik.currencyconversion.feign.FeignBankAccount;
import com.draganovik.currencyconversion.feign.FeignCurrencyExchange;
import com.draganovik.currencyconversion.models.CurrencyConversionResponse;
import com.draganovik.currencyconversion.models.FeignBankAccountResponse;
import com.draganovik.currencyconversion.models.FeignCurrencyExchangeResponse;
import com.draganovik.currencyconversion.models.NestedFeignBankAccountResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/currency-conversion")
public class CurrencyConversionController {

    private final Environment environment;
    private final FeignBankAccount feignBankAccount;
    private final FeignCurrencyExchange feignCurrencyExchange;

    public CurrencyConversionController(Environment environment, FeignBankAccount feignBankAccount,
                                        FeignCurrencyExchange feignCurrencyExchange) {
        this.environment = environment;
        this.feignBankAccount = feignBankAccount;
        this.feignCurrencyExchange = feignCurrencyExchange;
    }

    @PostMapping()
    public ResponseEntity<CurrencyConversionResponse> performConversion(@RequestParam FiatCode from, @RequestParam FiatCode to, @RequestParam double quantity, HttpServletRequest request) throws Exception {
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

        ResponseEntity<FeignBankAccountResponse> bankAccountResponse =
                feignBankAccount.getBankAccountByCurrentUser(operatorRole.name(), operatorEmail);

        if (bankAccountResponse.getStatusCode() != HttpStatus.OK) {
            throw new ExtendedExceptions.NotFoundException("Can't find account of a current user.");
        }

        FeignBankAccountResponse bankAccount = bankAccountResponse.getBody();

        if (bankAccount == null) {
            throw new ExtendedExceptions.NotFoundException("Can't find account of a current user.");
        }

        ResponseEntity<FeignCurrencyExchangeResponse> currencyExchangeResponse = feignCurrencyExchange.getExchange(from.name(), to.name());

        if (currencyExchangeResponse.getStatusCode() != HttpStatus.OK) {
            throw new ExtendedExceptions.NotFoundException("Can't get currency exchange.");
        }

        FeignCurrencyExchangeResponse exchange = currencyExchangeResponse.getBody();

        if (exchange == null) {
            throw new ExtendedExceptions.NotFoundException("Can't get currency exchange.");
        }

        BigDecimal convertedQuantity = exchange.getConversionMultiple().multiply(BigDecimal.valueOf(quantity));

        feignBankAccount.accountExchangeWithdraw(from.name(), quantity, bankAccount.getEmail(),
                operatorRole.name(), operatorEmail);

        feignBankAccount.accountExchangeDeposit(to.name(), convertedQuantity.doubleValue(), bankAccount.getEmail(),
                operatorRole.name(), operatorEmail);

        bankAccountResponse =
                feignBankAccount.getBankAccountByCurrentUser(operatorRole.name(), operatorEmail);

        if (bankAccountResponse.getStatusCode() != HttpStatus.OK || bankAccountResponse.getBody() == null) {
            throw new ExtendedExceptions.NotFoundException("Can't find account of a current user.");
        }

        NestedFeignBankAccountResponse CCBAResponse =
                new NestedFeignBankAccountResponse(bankAccountResponse.getBody());

        CurrencyConversionResponse response = new CurrencyConversionResponse(
                CCBAResponse,
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