package com.draganovik.cryptowallet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CryptoWalletRepository extends JpaRepository<CryptoWallet, UUID> {
    Optional<CryptoWallet> findByEmail(String email);
}
