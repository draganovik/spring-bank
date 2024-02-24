package com.draganovik.bankaccount;

import com.draganovik.bankaccount.entities.BankAccount;
import com.draganovik.bankaccount.entities.Role;
import com.draganovik.bankaccount.exceptions.ExtendedExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/bank-account")
public class TransactionController {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @PostMapping("{email}/withdraw")
    public ResponseEntity<?> requestWithdraw(@PathVariable String email, @RequestParam String currency, @RequestParam double amount, HttpServletRequest request) throws Exception {

        String operatorEmail;
        Role operatorRole;

        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            operatorEmail = request.getHeader("X-User-Email");
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException("Request is not authorized.");
        }

        Optional<BankAccount> checkBankAccount = bankAccountRepository.findByEmail(email);

        if (checkBankAccount.isEmpty()) {
            throw new ExtendedExceptions.BadRequestException("Can't find the requested account.");
        }

        if (operatorRole == Role.USER && operatorEmail.isEmpty() && !operatorEmail.equals(checkBankAccount.get().getEmail())) {
            throw new ExtendedExceptions.UnauthorizedException("No permission to withdraw from this account.");
        }

        BankAccount bankAccount = checkBankAccount.get();

        switch (currency) {
            case "RSD":
                if (bankAccount.getQuantityRSD().compareTo(BigDecimal.valueOf(amount)) < 0) {
                    throw new ExtendedExceptions.BadRequestException("Not enough funds to perform this action.");
                }
                bankAccount.setQuantityRSD(bankAccount.getQuantityRSD().subtract(BigDecimal.valueOf(amount)));
                break;
            case "EUR":
                if (bankAccount.getQuantityEUR().compareTo(BigDecimal.valueOf(amount)) < 0) {
                    throw new ExtendedExceptions.BadRequestException("Not enough funds to perform this action.");
                }
                bankAccount.setQuantityEUR(bankAccount.getQuantityEUR().subtract(BigDecimal.valueOf(amount)));
                break;
            case "USD":
                if (bankAccount.getQuantityUSD().compareTo(BigDecimal.valueOf(amount)) < 0) {
                    throw new ExtendedExceptions.BadRequestException("Not enough funds to perform this action.");
                }
                bankAccount.setQuantityUSD(bankAccount.getQuantityUSD().subtract(BigDecimal.valueOf(amount)));
                break;
            case "GBP":
                if (bankAccount.getQuantityGBP().compareTo(BigDecimal.valueOf(amount)) < 0) {
                    throw new ExtendedExceptions.BadRequestException("Not enough funds to perform this action.");
                }
                bankAccount.setQuantityGBP(bankAccount.getQuantityGBP().subtract(BigDecimal.valueOf(amount)));
                break;
            case "CHF":
                if (bankAccount.getQuantityCHF().compareTo(BigDecimal.valueOf(amount)) < 0) {
                    throw new ExtendedExceptions.BadRequestException("Not enough funds to perform this action.");
                }
                bankAccount.setQuantityCHF(bankAccount.getQuantityCHF().subtract(BigDecimal.valueOf(amount)));
                break;
            default:
                throw new ExtendedExceptions.BadRequestException("Currency is not supported");
        }
        bankAccountRepository.save(bankAccount);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("{email}/deposit")
    public ResponseEntity<?> requestDeposit(@PathVariable String email, @RequestParam String currency, @RequestParam double amount) throws Exception {

        Optional<BankAccount> checkBankAccount = bankAccountRepository.findByEmail(email);

        if (checkBankAccount.isEmpty()) {
            throw new ExtendedExceptions.BadRequestException("Can't find the requested account.");
        }

        BankAccount bankAccount = checkBankAccount.get();

        switch (currency) {
            case "RSD":
                bankAccount.setQuantityRSD(bankAccount.getQuantityRSD().add(BigDecimal.valueOf(amount)));
                break;
            case "EUR":
                bankAccount.setQuantityEUR(bankAccount.getQuantityEUR().add(BigDecimal.valueOf(amount)));
                break;
            case "USD":
                bankAccount.setQuantityUSD(bankAccount.getQuantityUSD().add(BigDecimal.valueOf(amount)));
                break;
            case "GBP":
                bankAccount.setQuantityGBP(bankAccount.getQuantityGBP().add(BigDecimal.valueOf(amount)));
                break;
            case "CHF":
                bankAccount.setQuantityCHF(bankAccount.getQuantityCHF().add(BigDecimal.valueOf(amount)));
                break;
            default:
                throw new ExtendedExceptions.BadRequestException("Currency is not supported");
        }
        bankAccountRepository.save(bankAccount);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
