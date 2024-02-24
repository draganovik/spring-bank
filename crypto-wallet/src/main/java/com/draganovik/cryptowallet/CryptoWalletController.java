package com.draganovik.cryptowallet;

import com.draganovik.cryptowallet.exceptions.ExtendedExceptions;
import com.draganovik.cryptowallet.feign.FeignUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/crypto-wallet")
public class CryptoWalletController {

    @Autowired
    private Environment environment;

    @Autowired
    private CryptoWalletRepository cryptoWalletRepository;

    @Autowired
    private FeignUserService feignUserService;


    @GetMapping()
    public ResponseEntity<CryptoWalletResponse> getCryptoWalletByCurrentUser(HttpServletRequest request) throws Exception {

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

        Optional<CryptoWallet> wallet = cryptoWalletRepository.findByEmail(operatorEmail);

        if (wallet.isEmpty()) {
            throw new ExtendedExceptions.NotFoundException("Requested crypto wallet does not exist.");
        }


        CryptoWalletResponse response = new CryptoWalletResponse(
                wallet.get().getId(),
                wallet.get().getEmail(),
                wallet.get().getQuantityBTC(),
                wallet.get().getQuantityETH(),
                wallet.get().getQuantityDOGE(),
                environment.getProperty("local.server.port")
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    public ResponseEntity<CryptoWalletResponse> getCryptoWalletByEmail(@PathVariable String email) throws Exception {

        Optional<CryptoWallet> wallet = cryptoWalletRepository.findByEmail(email);

        if (wallet.isEmpty()) {
            throw new ExtendedExceptions.NotFoundException("Can't find requested crypto wallet.");
        }

        CryptoWalletResponse response = new CryptoWalletResponse(
                wallet.get().getId(),
                wallet.get().getEmail(),
                wallet.get().getQuantityBTC(),
                wallet.get().getQuantityETH(),
                wallet.get().getQuantityDOGE(),
                environment.getProperty("local.server.port")
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{email}")
    public ResponseEntity<CryptoWalletResponse> createBankAccount(@PathVariable String email, HttpServletRequest request) throws Exception {

        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException();
        }

        if (operatorRole != Role.ADMIN) {
            throw new ExtendedExceptions.ForbiddenException("Only ADMIN can perform this action.");
        }

        Optional<CryptoWallet> checkWallet = cryptoWalletRepository.findByEmail(email);

        if (checkWallet.isPresent()) {
            throw new ExtendedExceptions.BadRequestException("Can't create crypto wallet for this profile.");
        }

        ResponseEntity<UserFeignResponse> feignResponse;

        try {
            feignResponse = feignUserService.getUserByEmail(email, operatorRole.name());
        } catch (Exception ex) {
            throw new ExtendedExceptions.BadRequestException(ex.getMessage());
        }

        if (feignResponse.getStatusCode() != HttpStatus.OK) {
            throw new ExtendedExceptions.BadRequestException("Can't create crypto wallet. User profile doesn't exist.");
        }

        UserFeignResponse feignUser = feignResponse.getBody();

        if (feignUser == null || feignResponse.getBody().getRole() != Role.USER) {
            throw new ExtendedExceptions.BadRequestException("Crypto wallet can only be created for profile type USER.");
        }

        CryptoWallet wallet = cryptoWalletRepository.save(new CryptoWallet(email));

        CryptoWalletResponse response = new CryptoWalletResponse(
                wallet.getId(),
                wallet.getEmail(),
                wallet.getQuantityBTC(),
                wallet.getQuantityETH(),
                wallet.getQuantityDOGE(),
                environment.getProperty("local.server.port")
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{email}")
    public ResponseEntity<CryptoWalletResponse> updateCryptoWallet(@PathVariable String email, @RequestBody CryptoWallet walletRequest, HttpServletRequest request) throws Exception {

        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException();
        }

        if (operatorRole != Role.ADMIN) {
            throw new ExtendedExceptions.ForbiddenException("Only ADMIN can perform this action.");
        }

        Optional<CryptoWallet> optionalWallet = cryptoWalletRepository.findByEmail(email);

        if (optionalWallet.isEmpty()) {
            throw new RuntimeException("Crypto wallet you want to update is not found!");
        }

        CryptoWallet wallet = optionalWallet.get();

        wallet.setQuantityBTC(walletRequest.getQuantityBTC());
        wallet.setQuantityETH(walletRequest.getQuantityETH());
        wallet.setQuantityDOGE(walletRequest.getQuantityDOGE());

        wallet = cryptoWalletRepository.save(wallet);

        CryptoWalletResponse response = new CryptoWalletResponse(
                wallet.getId(),
                wallet.getEmail(),
                wallet.getQuantityBTC(),
                wallet.getQuantityETH(),
                wallet.getQuantityDOGE(),
                environment.getProperty("local.server.port")
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
