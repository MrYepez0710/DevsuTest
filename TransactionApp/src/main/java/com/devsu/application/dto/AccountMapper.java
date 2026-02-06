package com.devsu.application.dto;

import com.devsu.domain.model.Account;

/**
 * Mapper class for converting between Account entity and DTOs
 */
public class AccountMapper {
    
    private AccountMapper() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Convert AccountRequestDTO to Account entity
     */
    public static Account toEntity(AccountRequestDTO dto) {
        Account account = new Account();
        account.setAccountNumber(dto.getAccountNumber());
        account.setAccountType(dto.getAccountType());
        account.setBalance(dto.getInitialBalance());
        account.setState(dto.getState());
        account.setAccountKey(dto.getAccountKey());
        account.setClientId(dto.getClientId());
        return account;
    }
    
    /**
     * Convert Account entity to AccountResponseDTO
     */
    public static AccountResponseDTO toResponseDTO(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .state(account.getState())
                .clientId(account.getClientId())
                .build();
    }
    
    /**
     * Update Account entity from AccountRequestDTO
     */
    public static void updateEntityFromDTO(Account account, AccountRequestDTO dto) {
        account.setAccountNumber(dto.getAccountNumber());
        account.setAccountType(dto.getAccountType());
        account.setBalance(dto.getInitialBalance());
        account.setState(dto.getState());
        account.setAccountKey(dto.getAccountKey());
        account.setClientId(dto.getClientId());
    }
}
