package com.devsu.infrastructure.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devsu.application.dto.AccountRequestDTO;
import com.devsu.application.dto.AccountResponseDTO;
import com.devsu.application.service.AccountService;
import com.devsu.domain.exception.BusinessException;
import com.devsu.domain.exception.ResourceNotFoundException;
import com.devsu.infrastructure.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for AccountController
 */
@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private AccountService accountService;
    
    private AccountRequestDTO accountRequestDTO;
    private AccountResponseDTO accountResponseDTO;
    
    @BeforeEach
    void setUp() {
        accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.setAccountNumber("123456");
        accountRequestDTO.setAccountType("AHORROS");
        accountRequestDTO.setInitialBalance(1000.0);
        accountRequestDTO.setState("ACTIVA");
        accountRequestDTO.setAccountKey("key123");
        accountRequestDTO.setClientId("client-1");
        
        accountResponseDTO = AccountResponseDTO.builder()
                .id(1L)
                .accountNumber("123456")
                .accountType("AHORROS")
                .balance(1000.0)
                .state("ACTIVA")
                .clientId("client-1")
                .build();
    }
    
    @Test
    void testCreateAccount_Success() throws Exception {
        when(accountService.createAccount(any(AccountRequestDTO.class)))
            .thenReturn(accountResponseDTO);
        
        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("123456"))
                .andExpect(jsonPath("$.accountType").value("AHORROS"))
                .andExpect(jsonPath("$.balance").value(1000.0))
                .andExpect(jsonPath("$.state").value("ACTIVA"))
                .andExpect(jsonPath("$.clientId").value("client-1"));
    }
    
    @Test
    void testCreateAccount_ValidationError() throws Exception {
        AccountRequestDTO invalidDTO = new AccountRequestDTO();
        invalidDTO.setAccountNumber("");
        invalidDTO.setAccountType("");
        invalidDTO.setState("");
        invalidDTO.setClientId("");
        
        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors").isArray());
    }
    
    @Test
    void testCreateAccount_DuplicateAccountNumber() throws Exception {
        when(accountService.createAccount(any(AccountRequestDTO.class)))
            .thenThrow(new BusinessException("Account with accountNumber 123456 already exists"));
        
        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Account with accountNumber 123456 already exists"));
    }
    
    @Test
    void testUpdateAccount_Success() throws Exception {
        AccountRequestDTO updateDTO = new AccountRequestDTO();
        updateDTO.setAccountNumber("123456");
        updateDTO.setAccountType("CORRIENTE");
        updateDTO.setInitialBalance(2000.0);
        updateDTO.setState("ACTIVA");
        updateDTO.setClientId("client-1");
        
        AccountResponseDTO updatedResponse = AccountResponseDTO.builder()
                .id(1L)
                .accountNumber("123456")
                .accountType("CORRIENTE")
                .balance(2000.0)
                .state("ACTIVA")
                .clientId("client-1")
                .build();
        
        when(accountService.updateAccount(eq(1L), any(AccountRequestDTO.class)))
            .thenReturn(updatedResponse);
        
        mockMvc.perform(put("/cuentas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountType").value("CORRIENTE"))
                .andExpect(jsonPath("$.balance").value(2000.0));
    }
    
    @Test
    void testUpdateAccount_NotFound() throws Exception {
        when(accountService.updateAccount(eq(999L), any(AccountRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Account with id 999 not found"));
        
        mockMvc.perform(put("/cuentas/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Account with id 999 not found"));
    }
    
    @Test
    void testGetAccount_Success() throws Exception {
        when(accountService.getAccount(1L)).thenReturn(accountResponseDTO);
        
        mockMvc.perform(get("/cuentas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("123456"))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }
    
    @Test
    void testGetAccount_NotFound() throws Exception {
        when(accountService.getAccount(999L))
            .thenThrow(new ResourceNotFoundException("Account with id 999 not found"));
        
        mockMvc.perform(get("/cuentas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Account with id 999 not found"));
    }
    
    @Test
    void testGetAllAccounts_Success() throws Exception {
        AccountResponseDTO account2 = AccountResponseDTO.builder()
                .id(2L)
                .accountNumber("654321")
                .accountType("CORRIENTE")
                .balance(500.0)
                .state("ACTIVA")
                .clientId("client-2")
                .build();
        
        List<AccountResponseDTO> accounts = Arrays.asList(accountResponseDTO, account2);
        when(accountService.getAllAccounts()).thenReturn(accounts);
        
        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountNumber").value("123456"))
                .andExpect(jsonPath("$[1].accountNumber").value("654321"));
    }
    
    @Test
    void testGetAllAccounts_Empty() throws Exception {
        when(accountService.getAllAccounts()).thenReturn(Arrays.asList());
        
        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
    
    @Test
    void testGetAccountByAccountNumber_Success() throws Exception {
        when(accountService.getAccountByAccountNumber("123456")).thenReturn(accountResponseDTO);
        
        mockMvc.perform(get("/cuentas/numero/123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("123456"))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }
    
    @Test
    void testGetAccountByAccountNumber_NotFound() throws Exception {
        when(accountService.getAccountByAccountNumber("999999"))
            .thenThrow(new ResourceNotFoundException("Account with accountNumber 999999 not found"));
        
        mockMvc.perform(get("/cuentas/numero/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Account with accountNumber 999999 not found"));
    }
    
    @Test
    void testGetAccountsByClientId_Success() throws Exception {
        AccountResponseDTO account2 = AccountResponseDTO.builder()
                .id(2L)
                .accountNumber("654321")
                .accountType("CORRIENTE")
                .balance(500.0)
                .state("ACTIVA")
                .clientId("client-1")
                .build();
        
        List<AccountResponseDTO> accounts = Arrays.asList(accountResponseDTO, account2);
        when(accountService.getAccountsByClientId("client-1")).thenReturn(accounts);
        
        mockMvc.perform(get("/cuentas").param("clientId", "client-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountNumber").value("123456"))
                .andExpect(jsonPath("$[1].accountNumber").value("654321"))
                .andExpect(jsonPath("$[0].clientId").value("client-1"))
                .andExpect(jsonPath("$[1].clientId").value("client-1"));
    }
    
    @Test
    void testGetAccountsByClientId_Empty() throws Exception {
        when(accountService.getAccountsByClientId("client-999")).thenReturn(Arrays.asList());
        
        mockMvc.perform(get("/cuentas").param("clientId", "client-999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
    
    @Test
    void testCreateAccount_ClientNotFound() throws Exception {
        when(accountService.createAccount(any(AccountRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Client with clientId CLIENTEINEXISTENTE not found"));
        
        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Client with clientId CLIENTEINEXISTENTE not found"));
    }
}
