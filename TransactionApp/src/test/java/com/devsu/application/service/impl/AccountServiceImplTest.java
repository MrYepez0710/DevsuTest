package com.devsu.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.devsu.application.dto.AccountRequestDTO;
import com.devsu.application.dto.AccountResponseDTO;
import com.devsu.domain.exception.BusinessException;
import com.devsu.domain.exception.ResourceNotFoundException;
import com.devsu.domain.model.Account;
import com.devsu.domain.repository.AccountRepository;
import com.devsu.infrastructure.client.ClientServiceClient;
import com.devsu.infrastructure.cache.dto.ClientCacheDTO;

/**
 * Unit tests for AccountServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    
    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private ClientServiceClient clientServiceClient;
    
    @InjectMocks
    private AccountServiceImpl accountService;
    
    private Account account;
    private AccountRequestDTO accountRequestDTO;
    
    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAccountNumber("123456");
        account.setAccountType("AHORROS");
        account.setBalance(1000.0);
        account.setState("ACTIVA");
        account.setAccountKey("key123");
        account.setClientId("client-1");
        
        accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.setAccountNumber("123456");
        accountRequestDTO.setAccountType("AHORROS");
        accountRequestDTO.setInitialBalance(1000.0);
        accountRequestDTO.setState("ACTIVA");
        accountRequestDTO.setAccountKey("key123");
        accountRequestDTO.setClientId("client-1");
    }
    
    @Test
    void testCreateAccount_Success() {
        ClientCacheDTO clientCache = ClientCacheDTO.builder()
            .clientId("client-1")
            .name("Test Client")
            .build();
        
        when(clientServiceClient.getClientByClientId("client-1")).thenReturn(clientCache);
        when(accountRepository.findByAccountNumber(accountRequestDTO.getAccountNumber()))
            .thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        
        AccountResponseDTO response = accountService.createAccount(accountRequestDTO);
        
        assertNotNull(response);
        assertEquals(account.getId(), response.getId());
        assertEquals(account.getAccountNumber(), response.getAccountNumber());
        assertEquals(account.getAccountType(), response.getAccountType());
        assertEquals(account.getBalance(), response.getBalance());
        assertEquals(account.getState(), response.getState());
        assertEquals(account.getClientId(), response.getClientId());
        
        verify(clientServiceClient).getClientByClientId("client-1");
        verify(accountRepository).findByAccountNumber(accountRequestDTO.getAccountNumber());
        verify(accountRepository).save(any(Account.class));
    }
    
    @Test
    void testCreateAccount_ClientNotFound() {
        when(clientServiceClient.getClientByClientId("client-1")).thenReturn(null);
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.createAccount(accountRequestDTO);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(clientServiceClient).getClientByClientId("client-1");
        verify(accountRepository, never()).findByAccountNumber(any());
        verify(accountRepository, never()).save(any(Account.class));
    }
    
    @Test
    void testCreateAccount_DuplicateAccountNumber() {
        ClientCacheDTO clientCache = ClientCacheDTO.builder()
            .clientId("client-1")
            .name("Test Client")
            .build();
        
        when(clientServiceClient.getClientByClientId("client-1")).thenReturn(clientCache);
        when(accountRepository.findByAccountNumber(accountRequestDTO.getAccountNumber()))
            .thenReturn(Optional.of(account));
        
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.createAccount(accountRequestDTO);
        });
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(clientServiceClient).getClientByClientId("client-1");
        verify(accountRepository).findByAccountNumber(accountRequestDTO.getAccountNumber());
        verify(accountRepository, never()).save(any(Account.class));
    }
    
    @Test
    void testUpdateAccount_Success() {
        AccountRequestDTO updateDTO = new AccountRequestDTO();
        updateDTO.setAccountNumber("123456");
        updateDTO.setAccountType("CORRIENTE");
        updateDTO.setInitialBalance(2000.0);
        updateDTO.setState("ACTIVA");
        updateDTO.setAccountKey("key456");
        updateDTO.setClientId("client-1");
        
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        
        AccountResponseDTO response = accountService.updateAccount(1L, updateDTO);
        
        assertNotNull(response);
        verify(accountRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
    }
    
    @Test
    void testUpdateAccount_NotFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.updateAccount(999L, accountRequestDTO);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(accountRepository).findById(999L);
        verify(accountRepository, never()).save(any(Account.class));
    }
    
    @Test
    void testUpdateAccount_DuplicateAccountNumber() {
        Account existingAccount = new Account();
        existingAccount.setId(2L);
        existingAccount.setAccountNumber("654321");
        
        AccountRequestDTO updateDTO = new AccountRequestDTO();
        updateDTO.setAccountNumber("654321");
        updateDTO.setAccountType("CORRIENTE");
        updateDTO.setInitialBalance(2000.0);
        updateDTO.setState("ACTIVA");
        updateDTO.setClientId("client-1");
        
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findByAccountNumber("654321"))
            .thenReturn(Optional.of(existingAccount));
        
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.updateAccount(1L, updateDTO);
        });
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(accountRepository).findById(1L);
        verify(accountRepository).findByAccountNumber("654321");
        verify(accountRepository, never()).save(any(Account.class));
    }
    
    @Test
    void testDeleteAccount_NotSupported() {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            accountService.deleteAccount(1L);
        });
        
        assertTrue(exception.getMessage().contains("not supported"));
    }
    
    @Test
    void testGetAccount_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        
        AccountResponseDTO response = accountService.getAccount(1L);
        
        assertNotNull(response);
        assertEquals(account.getId(), response.getId());
        assertEquals(account.getAccountNumber(), response.getAccountNumber());
        verify(accountRepository).findById(1L);
    }
    
    @Test
    void testGetAccount_NotFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getAccount(999L);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(accountRepository).findById(999L);
    }
    
    @Test
    void testGetAllAccounts_Success() {
        Account account2 = new Account();
        account2.setId(2L);
        account2.setAccountNumber("654321");
        account2.setAccountType("CORRIENTE");
        account2.setBalance(500.0);
        account2.setState("ACTIVA");
        account2.setClientId("client-2");
        
        List<Account> accounts = Arrays.asList(account, account2);
        when(accountRepository.findAll()).thenReturn(accounts);
        
        List<AccountResponseDTO> response = accountService.getAllAccounts();
        
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(account.getAccountNumber(), response.get(0).getAccountNumber());
        assertEquals(account2.getAccountNumber(), response.get(1).getAccountNumber());
        verify(accountRepository).findAll();
    }
    
    @Test
    void testGetAllAccounts_Empty() {
        when(accountRepository.findAll()).thenReturn(Arrays.asList());
        
        List<AccountResponseDTO> response = accountService.getAllAccounts();
        
        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(accountRepository).findAll();
    }
    
    @Test
    void testGetAccountByAccountNumber_Success() {
        when(accountRepository.findByAccountNumber("123456")).thenReturn(Optional.of(account));
        
        AccountResponseDTO response = accountService.getAccountByAccountNumber("123456");
        
        assertNotNull(response);
        assertEquals(account.getId(), response.getId());
        assertEquals(account.getAccountNumber(), response.getAccountNumber());
        verify(accountRepository).findByAccountNumber("123456");
    }
    
    @Test
    void testGetAccountByAccountNumber_NotFound() {
        when(accountRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getAccountByAccountNumber("999999");
        });
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(accountRepository).findByAccountNumber("999999");
    }
    
    @Test
    void testGetAccountsByClientId_Success() {
        Account account2 = new Account();
        account2.setId(2L);
        account2.setAccountNumber("654321");
        account2.setAccountType("CORRIENTE");
        account2.setBalance(500.0);
        account2.setState("ACTIVA");
        account2.setClientId("client-1");
        
        List<Account> accounts = Arrays.asList(account, account2);
        when(accountRepository.findByClientId("client-1")).thenReturn(accounts);
        
        List<AccountResponseDTO> response = accountService.getAccountsByClientId("client-1");
        
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(account.getAccountNumber(), response.get(0).getAccountNumber());
        assertEquals(account2.getAccountNumber(), response.get(1).getAccountNumber());
        verify(accountRepository).findByClientId("client-1");
    }
    
    @Test
    void testGetAccountsByClientId_Empty() {
        when(accountRepository.findByClientId("client-999")).thenReturn(Arrays.asList());
        
        List<AccountResponseDTO> response = accountService.getAccountsByClientId("client-999");
        
        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(accountRepository).findByClientId("client-999");
    }
}
