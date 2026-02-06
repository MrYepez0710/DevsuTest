package com.devsu.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsu.domain.model.Account;
import com.devsu.domain.model.Movement;

/**
 * Repository interface for Movement entity
 * Provides CRUD operations and custom queries
 */
@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {
    
    /**
     * Find all movements by account
     * @param account the account
     * @return List of movements for the account
     */
    List<Movement> findByAccount(Account account);
    
    /**
     * Find all movements by account ID
     * @param accountId the account ID
     * @return List of movements for the account
     */
    List<Movement> findByAccountId(Long accountId);
    
    /**
     * Find movements by account and date range
     * @param account the account
     * @param startDate start date
     * @param endDate end date
     * @return List of movements in the date range
     */
    List<Movement> findByAccountAndMovementDateBetween(Account account, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find the latest movement for an account (for getting last balance)
     * @param account the account
     * @return List of movements ordered by date descending
     */
    List<Movement> findByAccountOrderByMovementDateDesc(Account account);
}
