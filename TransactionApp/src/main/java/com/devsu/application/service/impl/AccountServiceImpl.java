package com.devsu.application.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsu.application.dto.AccountMapper;
import com.devsu.application.dto.AccountRequestDTO;
import com.devsu.application.dto.AccountResponseDTO;
import com.devsu.application.service.AccountService;
import com.devsu.domain.exception.BusinessException;
import com.devsu.domain.exception.ResourceNotFoundException;
import com.devsu.domain.model.Account;
import com.devsu.domain.repository.AccountRepository;
import com.devsu.infrastructure.cache.dto.ClientCacheDTO;
import com.devsu.infrastructure.client.ClientServiceClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AccountService
 * Handles business logic for Account operations (CRU - no Delete per F1)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    
    private final AccountRepository accountRepository;
    private final ClientServiceClient clientServiceClient;
    
    /**
     * Create a new account
     * Validates that account number is unique and client exists
     */
    @Override
    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        log.info("Creating new account with accountNumber: {}", accountRequestDTO.getAccountNumber());
        
        // Validate that client exists
        String clientId = accountRequestDTO.getClientId();
        ClientCacheDTO client = clientServiceClient.getClientByClientId(clientId);
        if (client == null) {
            log.error("Client with clientId {} not found", clientId);
            throw new ResourceNotFoundException("Client with clientId " + clientId + " not found");
        }
        log.info("Client {} validated successfully", clientId);
        
        // Validate account number uniqueness
        accountRepository.findByAccountNumber(accountRequestDTO.getAccountNumber())
            .ifPresent(account -> {
                log.error("Account with accountNumber {} already exists", accountRequestDTO.getAccountNumber());
                throw new BusinessException("Account with accountNumber " + accountRequestDTO.getAccountNumber() + " already exists");
            });
        
        // Convert DTO to entity and save
        Account account = AccountMapper.toEntity(accountRequestDTO);
        Account savedAccount = accountRepository.save(account);
        
        log.info("Account created successfully with id: {}", savedAccount.getId());
        return AccountMapper.toResponseDTO(savedAccount);
    }

    /**
     * Update an existing account
     * F1 specifies CRU (Create, Read, Update) - no Delete
     */
    @Override
    @Transactional
    public AccountResponseDTO updateAccount(Long id, AccountRequestDTO accountRequestDTO) {
        log.info("Updating account with id: {}", id);
        
        // Find existing account
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Account with id {} not found", id);
                return new ResourceNotFoundException("Account with id " + id + " not found");
            });
        
        // Validate account number uniqueness if changed
        if (!account.getAccountNumber().equals(accountRequestDTO.getAccountNumber())) {
            accountRepository.findByAccountNumber(accountRequestDTO.getAccountNumber())
                .ifPresent(existingAccount -> {
                    log.error("Account with accountNumber {} already exists", accountRequestDTO.getAccountNumber());
                    throw new BusinessException("Account with accountNumber " + accountRequestDTO.getAccountNumber() + " already exists");
                });
        }
        
        // Update entity from DTO
        AccountMapper.updateEntityFromDTO(account, accountRequestDTO);
        Account updatedAccount = accountRepository.save(account);
        
        log.info("Account updated successfully with id: {}", updatedAccount.getId());
        return AccountMapper.toResponseDTO(updatedAccount);
    }

    /**
     * Delete account - NOT IMPLEMENTED per F1 specification
     * F1 only requires CRU (Create, Read, Update) for Account
     */
    @Override
    public void deleteAccount(Long id) {
        log.warn("Delete operation not implemented for Account per F1 specification");
        throw new UnsupportedOperationException("Delete operation not supported for Account");
    }

    /**
     * Get account by ID
     */
    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO getAccount(Long id) {
        log.info("Fetching account with id: {}", id);
        
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Account with id {} not found", id);
                return new ResourceNotFoundException("Account with id " + id + " not found");
            });
        
        return AccountMapper.toResponseDTO(account);
    }

    /**
     * Get account by account number
     */
    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountByAccountNumber(String accountNumber) {
        log.info("Fetching account with accountNumber: {}", accountNumber);
        
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> {
                log.error("Account with accountNumber {} not found", accountNumber);
                return new ResourceNotFoundException("Account with accountNumber " + accountNumber + " not found");
            });
        
        return AccountMapper.toResponseDTO(account);
    }

    /**
     * Get all accounts
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAllAccounts() {
        log.info("Fetching all accounts");
        
        List<Account> accounts = accountRepository.findAll();
        log.info("Found {} accounts", accounts.size());
        
        return accounts.stream()
            .map(AccountMapper::toResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get accounts by client ID
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAccountsByClientId(String clientId) {
        log.info("Fetching accounts for clientId: {}", clientId);
        
        List<Account> accounts = accountRepository.findByClientId(clientId);
        log.info("Found {} accounts for clientId: {}", accounts.size(), clientId);
        
        return accounts.stream()
            .map(AccountMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
}
