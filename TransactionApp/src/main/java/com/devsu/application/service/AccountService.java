package com.devsu.application.service;

import java.util.List;

import com.devsu.application.dto.AccountRequestDTO;
import com.devsu.application.dto.AccountResponseDTO;

public interface AccountService {
    
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO);
    public AccountResponseDTO updateAccount(Long id, AccountRequestDTO accountRequestDTO);
    public void deleteAccount(Long id);
    public AccountResponseDTO getAccount(Long id);
    public AccountResponseDTO getAccountByAccountNumber(String accountNumber);
    public List<AccountResponseDTO> getAllAccounts();
    public List<AccountResponseDTO> getAccountsByClientId(String clientId);
}
