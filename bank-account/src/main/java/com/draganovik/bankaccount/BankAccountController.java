package com.draganovik.bankaccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
public class BankAccountController {

    @Autowired
    private Environment environment;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @GetMapping("/bank-account/email/{email}")
    public BankAccount getBankAccountByEmail(@PathVariable String email) {

        try {
            String port = environment.getProperty("local.server.port");
            BankAccount temp = bankAccountRepository.getBankAccountByEmail(email);

            return new BankAccount(temp.getId(), email, temp.getQuantityRSD(), temp.getQuantityEUR(),
                    temp.getQuantityGBP(), temp.getQuantityUSD(), temp.getQuantityCHF(), port);
        } catch (Throwable e) {
            System.out.println("Bank account with forwarded email doesn't exist!");
            return null;
        }
    }

    @PutMapping("/bank-account")
    public BankAccount updateBankAccount(@RequestBody BankAccount bankAccount) {

        if (!bankAccountRepository.existsById(bankAccount.getId())) {
            throw new RuntimeException("Bank account you want to update is not found!");
        }

        bankAccountRepository.save(bankAccount);
        String port = environment.getProperty("local.server.port");
        bankAccount.setEnvironment(port);
        return bankAccount;
    }

}
