package com.draganovik.cryptowallet;

import com.draganovik.cryptowallet.entities.CryptoWallet;
import com.draganovik.cryptowallet.entities.Role;
import com.draganovik.cryptowallet.exceptions.ExtendedExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/crypto-wallet")
public class TransactionController {

    private final CryptoWalletRepository cryptoWalletRepository;

    public TransactionController(CryptoWalletRepository cryptoWalletRepository) {
        this.cryptoWalletRepository = cryptoWalletRepository;
    }

    @PostMapping("{email}/withdraw")
    public ResponseEntity<?> requestWithdraw(@PathVariable String email, @RequestParam String currency, @RequestParam BigDecimal amount, HttpServletRequest request) throws Exception {

        String operatorEmail;
        Role operatorRole;
        try {
            operatorRole = Role.valueOf(request.getHeader("X-User-Role"));
            operatorEmail = request.getHeader("X-User-Email");
        } catch (Exception e) {
            throw new ExtendedExceptions.UnauthorizedException("Request is not authorized.");
        }

        Optional<CryptoWallet> checkCryptoWallet = cryptoWalletRepository.findByEmail(email);

        if (checkCryptoWallet.isEmpty()) {
            throw new ExtendedExceptions.BadRequestException("Can't find the requested wallet.");
        }

        if (operatorRole == Role.USER && operatorEmail.isEmpty() && !operatorEmail.equals(checkCryptoWallet.get().getEmail())) {
            throw new ExtendedExceptions.UnauthorizedException("No permission to withdraw from this wallet.");
        }

        CryptoWallet cryptoWallet = checkCryptoWallet.get();

        switch (currency) {
            case "BTC":
                if (cryptoWallet.getQuantityBTC().compareTo(amount) < 0) {
                    throw new ExtendedExceptions.BadRequestException("Not enough funds to perform this action.");
                }
                cryptoWallet.setQuantityBTC(cryptoWallet.getQuantityBTC().subtract(amount));
                break;
            case "ETH":
                if (cryptoWallet.getQuantityETH().compareTo(amount) < 0) {
                    throw new ExtendedExceptions.BadRequestException("Not enough funds to perform this action.");
                }
                cryptoWallet.setQuantityETH(cryptoWallet.getQuantityETH().subtract(amount));
                break;
            case "DOGE":
                if (cryptoWallet.getQuantityDOGE().compareTo(amount) < 0) {
                    throw new ExtendedExceptions.BadRequestException("Not enough funds to perform this action.");
                }
                cryptoWallet.setQuantityDOGE(cryptoWallet.getQuantityDOGE().subtract(amount));
                break;
            default:
                throw new ExtendedExceptions.BadRequestException("Currency is not supported");
        }
        cryptoWalletRepository.save(cryptoWallet);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("{email}/deposit")
    public ResponseEntity<?> requestDeposit(@PathVariable String email, @RequestParam String currency, @RequestParam BigDecimal amount) throws Exception {

        Optional<CryptoWallet> checkCryptoWallet = cryptoWalletRepository.findByEmail(email);

        if (checkCryptoWallet.isEmpty()) {
            throw new ExtendedExceptions.BadRequestException("Can't find the requested wallet.");
        }

        CryptoWallet bankAccount = checkCryptoWallet.get();

        switch (currency) {
            case "BTC":
                bankAccount.setQuantityBTC(bankAccount.getQuantityBTC().add(amount));
                break;
            case "ETH":
                bankAccount.setQuantityETH(bankAccount.getQuantityETH().add(amount));
                break;
            case "DOGE":
                bankAccount.setQuantityDOGE(bankAccount.getQuantityDOGE().add(amount));
                break;
            default:
                throw new ExtendedExceptions.BadRequestException("Crypto Currency is not supported");
        }
        cryptoWalletRepository.save(bankAccount);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
