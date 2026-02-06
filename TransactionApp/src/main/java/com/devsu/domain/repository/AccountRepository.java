package com.devsu.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsu.domain.model.Account;

/**
 * Repository interface for Account entity
 * Provides CRUD operations and custom queries
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    /**
     * Find account by account number
     * @param accountNumber the account number
     * @return Optional containing the account if found
     */
    Optional<Account> findByAccountNumber(String accountNumber);
    
    /**
     * Find all accounts by client ID
     * @param clientId the client ID
     * @return List of accounts for the client
     */
    List<Account> findByClientId(String clientId);
}
