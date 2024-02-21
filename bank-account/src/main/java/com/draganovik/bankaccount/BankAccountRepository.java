package com.draganovik.bankaccount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    BankAccount getBankAccountByEmail(String email);
}


