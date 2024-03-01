package com.draganovik.tradeservice;

import com.draganovik.tradeservice.entities.TradeExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradeExchangeRepository extends JpaRepository<TradeExchange, Long> {
    Optional<TradeExchange> findByFromAndToIgnoreCase(String from, String to);
}
