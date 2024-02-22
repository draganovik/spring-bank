package com.draganovik.bankaccount;

import com.draganovik.bankaccount.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    Optional<BankAccount> getBankAccountByEmail(String email);
}


