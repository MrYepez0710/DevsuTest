package com.devsu.application.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsu.application.dto.MovementMapper;
import com.devsu.application.dto.MovementRequestDTO;
import com.devsu.application.dto.MovementResponseDTO;
import com.devsu.application.service.MovementService;
import com.devsu.domain.exception.InsufficientBalanceException;
import com.devsu.domain.exception.ResourceNotFoundException;
import com.devsu.domain.model.Account;
import com.devsu.domain.model.Movement;
import com.devsu.domain.repository.AccountRepository;
import com.devsu.domain.repository.MovementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of MovementService
 * Handles business logic for Movement operations (CRU - no Delete per F1)
 * Implements F2 and F3: Balance calculation and validation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {
    
    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    
    /**
     * Create a new movement
     * F2: Updates account balance and registers transaction
     * F3: Validates sufficient balance for withdrawals
     */
    @Override
    @Transactional
    public MovementResponseDTO createMovement(MovementRequestDTO movementRequestDTO) {
        log.info("Creating new movement for account: {}", movementRequestDTO.getAccountId());
        
        // Find account
        Account account = accountRepository.findById(movementRequestDTO.getAccountId())
            .orElseThrow(() -> {
                log.error("Account with id {} not found", movementRequestDTO.getAccountId());
                return new ResourceNotFoundException("Account with id " + movementRequestDTO.getAccountId() + " not found");
            });
        
        // Get current balance (from account or last movement)
        Double currentBalance = account.getBalance();
        List<Movement> lastMovements = movementRepository.findByAccountOrderByMovementDateDesc(account);
        if (!lastMovements.isEmpty()) {
            currentBalance = lastMovements.get(0).getBalance();
        }
        
        // Calculate new balance
        Double amount = movementRequestDTO.getAmount();
        Double newBalance = currentBalance + amount;
        
        // F3: Validate sufficient balance for withdrawals (negative amounts)
        if (newBalance < 0) {
            log.error("Insufficient balance for account {}. Current: {}, Requested: {}", 
                     account.getAccountNumber(), currentBalance, amount);
            throw new InsufficientBalanceException("Saldo no disponible");
        }
        
        // Create movement
        Movement movement = MovementMapper.toEntity(movementRequestDTO, account);
        movement.setBalance(newBalance);
        
        // Generate movement number (simple sequential)
        Long movementNumber = movementRepository.findByAccount(account).size() + 1L;
        movement.setMovementNumber(movementNumber);
        
        // Save movement
        Movement savedMovement = movementRepository.save(movement);
        
        // F2: Update account balance
        account.setBalance(newBalance);
        accountRepository.save(account);
        
        log.info("Movement created successfully with id: {}. New balance: {}", savedMovement.getId(), newBalance);
        return MovementMapper.toResponseDTO(savedMovement);
    }

    /**
     * Update an existing movement
     * F1 specifies CRU (Create, Read, Update) - no Delete
     * Note: Updating movements may require recalculating subsequent balances
     */
    @Override
    @Transactional
    public MovementResponseDTO updateMovement(Long id, MovementRequestDTO movementRequestDTO) {
        log.info("Updating movement with id: {}", id);
        
        // Find existing movement
        Movement movement = movementRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Movement with id {} not found", id);
                return new ResourceNotFoundException("Movement with id " + id + " not found");
            });
        
        // Find account if changed
        Account account = movement.getAccount();
        if (!account.getId().equals(movementRequestDTO.getAccountId())) {
            account = accountRepository.findById(movementRequestDTO.getAccountId())
                .orElseThrow(() -> {
                    log.error("Account with id {} not found", movementRequestDTO.getAccountId());
                    return new ResourceNotFoundException("Account with id " + movementRequestDTO.getAccountId() + " not found");
                });
            movement.setAccount(account);
        }
        
        // Recalculate balance if amount changed
        if (!movement.getAmount().equals(movementRequestDTO.getAmount())) {
            // Get previous balance (before this movement)
            Double previousBalance = movement.getBalance() - movement.getAmount();
            Double newAmount = movementRequestDTO.getAmount();
            Double newBalance = previousBalance + newAmount;
            
            // F3: Validate sufficient balance
            if (newBalance < 0) {
                log.error("Insufficient balance for movement update. Previous: {}, New amount: {}", 
                         previousBalance, newAmount);
                throw new InsufficientBalanceException("Saldo no disponible");
            }
            
            movement.setBalance(newBalance);
            
            // Update account balance
            account.setBalance(newBalance);
            accountRepository.save(account);
        }
        
        // Update other fields
        MovementMapper.updateEntityFromDTO(movement, movementRequestDTO);
        Movement updatedMovement = movementRepository.save(movement);
        
        log.info("Movement updated successfully with id: {}", updatedMovement.getId());
        return MovementMapper.toResponseDTO(updatedMovement);
    }

    /**
     * Delete movement - NOT IMPLEMENTED per F1 specification
     * F1 only requires CRU (Create, Read, Update) for Movement
     */
    @Override
    public void deleteMovement(Long id) {
        log.warn("Delete operation not implemented for Movement per F1 specification");
        throw new UnsupportedOperationException("Delete operation not supported for Movement");
    }

    /**
     * Get movement by ID
     */
    @Override
    @Transactional(readOnly = true)
    public MovementResponseDTO getMovement(Long id) {
        log.info("Fetching movement with id: {}", id);
        
        Movement movement = movementRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Movement with id {} not found", id);
                return new ResourceNotFoundException("Movement with id " + id + " not found");
            });
        
        return MovementMapper.toResponseDTO(movement);
    }

    /**
     * Get all movements
     */
    @Override
    @Transactional(readOnly = true)
    public List<MovementResponseDTO> getAllMovements() {
        log.info("Fetching all movements");
        
        List<Movement> movements = movementRepository.findAll();
        log.info("Found {} movements", movements.size());
        
        return movements.stream()
            .map(MovementMapper::toResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get movements by account ID
     */
    @Override
    @Transactional(readOnly = true)
    public List<MovementResponseDTO> getMovementsByAccountId(Long accountId) {
        log.info("Fetching movements for accountId: {}", accountId);
        
        List<Movement> movements = movementRepository.findByAccountId(accountId);
        log.info("Found {} movements for accountId: {}", movements.size(), accountId);
        
        return movements.stream()
            .map(MovementMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
}
