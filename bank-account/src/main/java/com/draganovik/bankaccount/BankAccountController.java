package com.draganovik.bankaccount;

import com.draganovik.bankaccount.entities.BankAccount;
import com.draganovik.bankaccount.entities.Role;
import com.draganovik.bankaccount.exceptions.ExtendedExceptions;
import com.draganovik.bankaccount.feign.FeignUserService;
import com.draganovik.bankaccount.models.BankAccountRequest;
import com.draganovik.bankaccount.models.BankAccountResponse;
import com.draganovik.bankaccount.models.UserFeignResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.Optional;

@RestController
@RequestMapping("/bank-account")
public class BankAccountController {

    @Autowired
    private Environment environment;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private FeignUserService feignUserService;


    @GetMapping()
    public ResponseEntity<BankAccountResponse> getBankAccountByCurrentUser(HttpServletRequest request) throws Exception {

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

        Optional<BankAccount> account = bankAccountRepository.getBankAccountByEmail(operatorEmail);

        if (account.isEmpty()) {
            throw new ExtendedExceptions.NotFoundException("Requested bank account does not exist.");
        }


        BankAccountResponse response = new BankAccountResponse(
                account.get().getId(),
                account.get().getEmail(),
                account.get().getQuantityRSD(),
                account.get().getQuantityEUR(),
                account.get().getQuantityGBP(),
                account.get().getQuantityUSD(),
                account.get().getQuantityCHF(),
                environment.getProperty("local.server.port")
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    public ResponseEntity<BankAccountResponse> getBankAccountByEmail(@PathVariable String email, HttpServletRequest request) throws Exception {

        Optional<BankAccount> account = bankAccountRepository.getBankAccountByEmail(email);

        if (account.isEmpty()) {
            throw new ExtendedExceptions.NotFoundException("Requested bank account does not exist.");
        }

        BankAccountResponse response = new BankAccountResponse(
                account.get().getId(),
                account.get().getEmail(),
                account.get().getQuantityRSD(),
                account.get().getQuantityEUR(),
                account.get().getQuantityGBP(),
                account.get().getQuantityUSD(),
                account.get().getQuantityCHF(),
                environment.getProperty("local.server.port")
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{email}")
    public ResponseEntity<BankAccountResponse> createBankAccount(@PathVariable @Email @Valid String email, HttpServletRequest request) throws Exception {

        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException();
        }

        if (operatorRole != Role.ADMIN) {
            throw new ExtendedExceptions.ForbiddenException("Only ADMIN can perform this action.");
        }

        Optional<BankAccount> checkAccount = bankAccountRepository.getBankAccountByEmail(email);

        if (checkAccount.isPresent()) {
            throw new ExtendedExceptions.BadRequestException("Can't create bank account for this profile.");
        }

        ResponseEntity<UserFeignResponse> feignResponse;

        try {
            feignResponse = feignUserService.getUserByEmail(email, operatorRole.name());
        } catch (Exception ex) {
            throw new ExtendedExceptions.BadRequestException(ex.getMessage());
        }

        if (feignResponse.getStatusCode() != HttpStatus.OK) {
            throw new ExtendedExceptions.BadRequestException("Can't create bank account. User profile doesn't exist.");
        }

        UserFeignResponse feignUser = feignResponse.getBody();

        if (feignUser == null || feignResponse.getBody().getRole() != Role.USER) {
            throw new ExtendedExceptions.BadRequestException("Accounts can only be created for profile type USER.");
        }

        BankAccount account = bankAccountRepository.save(new BankAccount(email));

        BankAccountResponse response = new BankAccountResponse(
                account.getId(),
                account.getEmail(),
                account.getQuantityRSD(),
                account.getQuantityEUR(),
                account.getQuantityGBP(),
                account.getQuantityUSD(),
                account.getQuantityCHF(),
                environment.getProperty("local.server.port")
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{email}")
    public ResponseEntity<BankAccountResponse> updateBankAccount(@PathVariable @Email @Valid String email, @RequestBody @Valid BankAccountRequest accountRequest, HttpServletRequest request) throws Exception {

        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException();
        }

        if (operatorRole != Role.ADMIN) {
            throw new ExtendedExceptions.ForbiddenException("Only ADMIN can perform this action.");
        }

        Optional<BankAccount> optionalAccount = bankAccountRepository.getBankAccountByEmail(email);

        if (optionalAccount.isEmpty()) {
            throw new RuntimeException("Bank account you want to update is not found!");
        }

        BankAccount account = optionalAccount.get();


        account.setQuantityRSD(accountRequest.getQuantityRSD());
        account.setQuantityEUR(accountRequest.getQuantityEUR());
        account.setQuantityGBP(accountRequest.getQuantityGBP());
        account.setQuantityUSD(accountRequest.getQuantityUSD());
        account.setQuantityCHF(accountRequest.getQuantityCHF());

        account = bankAccountRepository.save(account);

        BankAccountResponse response = new BankAccountResponse(
                account.getId(),
                account.getEmail(),
                account.getQuantityRSD(),
                account.getQuantityEUR(),
                account.getQuantityGBP(),
                account.getQuantityUSD(),
                account.getQuantityCHF(),
                environment.getProperty("local.server.port")
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<?> deleteBankAccountByEmail(@PathVariable @Email @Valid String email) throws Exception {
        Optional<BankAccount> account = bankAccountRepository.getBankAccountByEmail(email);
        if (account.isEmpty()) {
            throw new ExtendedExceptions.NotFoundException("There is no account associated with this email.");
        }
        bankAccountRepository.delete(account.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
