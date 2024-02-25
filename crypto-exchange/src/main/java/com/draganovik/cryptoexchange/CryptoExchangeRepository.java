package com.draganovik.cryptoexchange;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CryptoExchangeRepository extends JpaRepository<CryptoExchange, Long> {
    Optional<CryptoExchange> findByFromAndToIgnoreCase(String from, String to);
}
