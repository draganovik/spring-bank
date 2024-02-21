package com.draganovik.bankaccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BankAccountController {

    @Autowired
    private Environment environment;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @GetMapping("/bank-account/{email}")
    public ResponseEntity<BankAccount> getBankAccountByEmail(@PathVariable String email) {

        try {
            String port = environment.getProperty("local.server.port");
            BankAccount account = bankAccountRepository.getBankAccountByEmail(email);

            account.setEnvironment(port);

            return new ResponseEntity<>(account, HttpStatus.OK);

        } catch (Throwable e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/bank-account")
    public ResponseEntity<BankAccount> updateBankAccount(@RequestBody BankAccount bankAccount) {

        if (!bankAccountRepository.existsById(bankAccount.getId())) {
            throw new RuntimeException("Bank account you want to update is not found!");
        }

        bankAccountRepository.save(bankAccount);
        String port = environment.getProperty("local.server.port");
        bankAccount.setEnvironment(port);
        return new ResponseEntity<>(bankAccount, HttpStatus.OK);
    }

}
