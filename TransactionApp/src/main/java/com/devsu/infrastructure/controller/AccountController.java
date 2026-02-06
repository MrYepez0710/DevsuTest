package com.devsu.infrastructure.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devsu.application.dto.AccountRequestDTO;
import com.devsu.application.dto.AccountResponseDTO;
import com.devsu.application.service.AccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Account operations
 * Implements F1: CRU for Account (no Delete)
 */
@Slf4j
@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@Validated
public class AccountController {
    
    private final AccountService accountService;
    
    /**
     * Create a new account
     * POST /cuentas
     */
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        log.info("REST request to create Account: {}", accountRequestDTO.getAccountNumber());
        AccountResponseDTO response = accountService.createAccount(accountRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Update an existing account
     * PUT /cuentas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        log.info("REST request to update Account with id: {}", id);
        AccountResponseDTO response = accountService.updateAccount(id, accountRequestDTO);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get account by ID
     * GET /cuentas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable Long id) {
        log.info("REST request to get Account with id: {}", id);
        AccountResponseDTO response = accountService.getAccount(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get account by account number
     * GET /cuentas/numero/{accountNumber}
     */
    @GetMapping("/numero/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getAccountByAccountNumber(@PathVariable String accountNumber) {
        log.info("REST request to get Account with accountNumber: {}", accountNumber);
        AccountResponseDTO response = accountService.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all accounts or filter by clientId
     * GET /cuentas
     * GET /cuentas?clientId=JLEMA001
     */
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts(
            @RequestParam(required = false) String clientId) {
        if (clientId != null && !clientId.isEmpty()) {
            log.info("REST request to get Accounts for clientId: {}", clientId);
            List<AccountResponseDTO> response = accountService.getAccountsByClientId(clientId);
            return ResponseEntity.ok(response);
        } else {
            log.info("REST request to get all Accounts");
            List<AccountResponseDTO> response = accountService.getAllAccounts();
            return ResponseEntity.ok(response);
        }
    }
}
