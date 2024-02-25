package com.draganovik.currencyconversion;

import com.draganovik.currencyconversion.entities.CurrencyCode;
import com.draganovik.currencyconversion.entities.Role;
import com.draganovik.currencyconversion.exceptions.ExtendedExceptions;
import com.draganovik.currencyconversion.feign.FeignBankAccount;
import com.draganovik.currencyconversion.feign.FeignCurrencyExchange;
import com.draganovik.currencyconversion.models.CurrencyConversionResponse;
import com.draganovik.currencyconversion.models.FeignBankAccountResponse;
import com.draganovik.currencyconversion.models.FeignCurrencyExchangeResponse;
import com.draganovik.currencyconversion.models.NestedFeignBankAccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private Environment environment;

    @Autowired
    private FeignBankAccount feignBankAccount;

    @Autowired
    private FeignCurrencyExchange feignCurrencyExchange;

    @PostMapping()
    public ResponseEntity<CurrencyConversionResponse> performConversion(@RequestParam String from, @RequestParam String to, @RequestParam double quantity, HttpServletRequest request) throws Exception {
        String operatorEmail;
        Role operatorRole;
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

        CurrencyCode toCC;
        try {
            toCC = CurrencyCode.valueOf(to);
        } catch (Exception ex) {
            throw new ExtendedExceptions.BadRequestException("Provided 'to' currency: " + to + " is not supported.");
        }

        CurrencyCode fromCC;
        try {
            fromCC = CurrencyCode.valueOf(from);
        } catch (Exception ex) {
            throw new ExtendedExceptions.BadRequestException("Provided 'to' currency: " + from + " is not supported.");
        }

        if (toCC == fromCC) {
            throw new ExtendedExceptions.BadRequestException("Can't perform conversion to same currency.");
        }

        ResponseEntity<FeignCurrencyExchangeResponse> currencyExchangeResponse = feignCurrencyExchange.getExchange(fromCC.name(), toCC.name());

        if (currencyExchangeResponse.getStatusCode() != HttpStatus.OK) {
            throw new ExtendedExceptions.NotFoundException("Can't get currency exchange.");
        }

        FeignCurrencyExchangeResponse exchange = currencyExchangeResponse.getBody();

        if (exchange == null) {
            throw new ExtendedExceptions.NotFoundException("Can't get currency exchange.");
        }

        BigDecimal convertedQuantity = exchange.getConversionMultiple().multiply(BigDecimal.valueOf(quantity));

        feignBankAccount.accountExchangeWithdraw(fromCC.name(), quantity, bankAccount.getEmail(),
                operatorRole.name(), operatorEmail);

        feignBankAccount.accountExchangeDeposit(toCC.name(), convertedQuantity.doubleValue(), bankAccount.getEmail(),
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
                "Successful! Converted " + quantity + " " + fromCC.name() + " to " + toCC.name() + ".",
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