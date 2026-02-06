package com.devsu.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.devsu.application.dto.MovementRequestDTO;
import com.devsu.application.dto.MovementResponseDTO;
import com.devsu.domain.exception.InsufficientBalanceException;
import com.devsu.domain.exception.ResourceNotFoundException;
import com.devsu.domain.model.Account;
import com.devsu.domain.model.Movement;
import com.devsu.domain.repository.AccountRepository;
import com.devsu.domain.repository.MovementRepository;

/**
 * Unit tests for MovementServiceImpl
 * Tests F2 (balance update) and F3 (insufficient balance validation)
 */
@ExtendWith(MockitoExtension.class)
class MovementServiceImplTest {
    
    @Mock
    private MovementRepository movementRepository;
    
    @Mock
    private AccountRepository accountRepository;
    
    @InjectMocks
    private MovementServiceImpl movementService;
    
    private Account account;
    private Movement movement;
    private MovementRequestDTO movementRequestDTO;
    
    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAccountNumber("123456");
        account.setAccountType("AHORROS");
        account.setBalance(1000.0);
        account.setState("ACTIVA");
        account.setClientId("client-1");
        
        movement = new Movement();
        movement.setId(1L);
        movement.setAccount(account);
        movement.setMovementNumber(1L);
        movement.setMovementDate(LocalDateTime.now());
        movement.setMovementType("DEPOSITO");
        movement.setAmount(500.0);
        movement.setBalance(1500.0);
        movement.setState("ACTIVO");
        
        movementRequestDTO = new MovementRequestDTO();
        movementRequestDTO.setAccountId(1L);
        movementRequestDTO.setMovementDate(LocalDateTime.now());
        movementRequestDTO.setMovementType("DEPOSITO");
        movementRequestDTO.setAmount(500.0);
        movementRequestDTO.setState("ACTIVO");
    }
    
    @Test
    void testCreateMovement_Deposit_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(movementRepository.findByAccountOrderByMovementDateDesc(account))
            .thenReturn(Arrays.asList());
        when(movementRepository.findByAccount(account)).thenReturn(Arrays.asList());
        when(movementRepository.save(any(Movement.class))).thenReturn(movement);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        
        MovementResponseDTO response = movementService.createMovement(movementRequestDTO);
        
        assertNotNull(response);
        assertEquals(movement.getId(), response.getId());
        assertEquals(movement.getAmount(), response.getAmount());
        assertEquals(1500.0, response.getBalance());
        
        verify(accountRepository).findById(1L);
        verify(movementRepository).save(any(Movement.class));
        verify(accountRepository).save(any(Account.class));
    }
    
    @Test
    void testCreateMovement_Withdrawal_Success() {
        movementRequestDTO.setMovementType("RETIRO");
        movementRequestDTO.setAmount(-300.0);
        
        movement.setAmount(-300.0);
        movement.setBalance(700.0);
        
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(movementRepository.findByAccountOrderByMovementDateDesc(account))
            .thenReturn(Arrays.asList());
        when(movementRepository.findByAccount(account)).thenReturn(Arrays.asList());
        when(movementRepository.save(any(Movement.class))).thenReturn(movement);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        
        MovementResponseDTO response = movementService.createMovement(movementRequestDTO);
        
        assertNotNull(response);
        assertEquals(-300.0, response.getAmount());
        assertEquals(700.0, response.getBalance());
        
        verify(accountRepository).findById(1L);
        verify(movementRepository).save(any(Movement.class));
        verify(accountRepository).save(any(Account.class));
    }
    
    @Test
    void testCreateMovement_InsufficientBalance_F3() {
        movementRequestDTO.setMovementType("RETIRO");
        movementRequestDTO.setAmount(-1500.0);
        
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(movementRepository.findByAccountOrderByMovementDateDesc(account))
            .thenReturn(Arrays.asList());
        
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            movementService.createMovement(movementRequestDTO);
        });
        
        assertEquals("Saldo no disponible", exception.getMessage());
        
        verify(accountRepository).findById(1L);
        verify(movementRepository, never()).save(any(Movement.class));
        verify(accountRepository, never()).save(any(Account.class));
    }
    
    @Test
    void testCreateMovement_AccountNotFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());
        
        movementRequestDTO.setAccountId(999L);
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            movementService.createMovement(movementRequestDTO);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(accountRepository).findById(999L);
        verify(movementRepository, never()).save(any(Movement.class));
    }
    
    @Test
    void testCreateMovement_WithPreviousMovements() {
        Movement previousMovement = new Movement();
        previousMovement.setBalance(1200.0);
        
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(movementRepository.findByAccountOrderByMovementDateDesc(account))
            .thenReturn(Arrays.asList(previousMovement));
        when(movementRepository.findByAccount(account)).thenReturn(Arrays.asList(previousMovement));
        when(movementRepository.save(any(Movement.class))).thenReturn(movement);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        
        MovementResponseDTO response = movementService.createMovement(movementRequestDTO);
        
        assertNotNull(response);
        verify(accountRepository).findById(1L);
        verify(movementRepository).findByAccountOrderByMovementDateDesc(account);
    }
    
    @Test
    void testUpdateMovement_Success() {
        MovementRequestDTO updateDTO = new MovementRequestDTO();
        updateDTO.setAccountId(1L);
        updateDTO.setMovementDate(LocalDateTime.now());
        updateDTO.setMovementType("DEPOSITO");
        updateDTO.setAmount(600.0);
        updateDTO.setState("ACTIVO");
        
        when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(movementRepository.save(any(Movement.class))).thenReturn(movement);
        
        MovementResponseDTO response = movementService.updateMovement(1L, updateDTO);
        
        assertNotNull(response);
        verify(movementRepository).findById(1L);
        verify(movementRepository).save(any(Movement.class));
    }
    
    @Test
    void testUpdateMovement_NotFound() {
        when(movementRepository.findById(999L)).thenReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            movementService.updateMovement(999L, movementRequestDTO);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(movementRepository).findById(999L);
        verify(movementRepository, never()).save(any(Movement.class));
    }
    
    @Test
    void testUpdateMovement_InsufficientBalance() {
        MovementRequestDTO updateDTO = new MovementRequestDTO();
        updateDTO.setAccountId(1L);
        updateDTO.setMovementDate(LocalDateTime.now());
        updateDTO.setMovementType("RETIRO");
        updateDTO.setAmount(-2000.0);
        updateDTO.setState("ACTIVO");
        
        when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));
        
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            movementService.updateMovement(1L, updateDTO);
        });
        
        assertEquals("Saldo no disponible", exception.getMessage());
        verify(movementRepository).findById(1L);
        verify(movementRepository, never()).save(any(Movement.class));
    }
    
    @Test
    void testDeleteMovement_NotSupported() {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            movementService.deleteMovement(1L);
        });
        
        assertTrue(exception.getMessage().contains("not supported"));
    }
    
    @Test
    void testGetMovement_Success() {
        when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));
        
        MovementResponseDTO response = movementService.getMovement(1L);
        
        assertNotNull(response);
        assertEquals(movement.getId(), response.getId());
        assertEquals(movement.getAmount(), response.getAmount());
        verify(movementRepository).findById(1L);
    }
    
    @Test
    void testGetMovement_NotFound() {
        when(movementRepository.findById(999L)).thenReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            movementService.getMovement(999L);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(movementRepository).findById(999L);
    }
    
    @Test
    void testGetAllMovements_Success() {
        Movement movement2 = new Movement();
        movement2.setId(2L);
        movement2.setAccount(account);
        movement2.setMovementNumber(2L);
        movement2.setMovementDate(LocalDateTime.now());
        movement2.setMovementType("RETIRO");
        movement2.setAmount(-200.0);
        movement2.setBalance(1300.0);
        movement2.setState("ACTIVO");
        
        List<Movement> movements = Arrays.asList(movement, movement2);
        when(movementRepository.findAll()).thenReturn(movements);
        
        List<MovementResponseDTO> response = movementService.getAllMovements();
        
        assertNotNull(response);
        assertEquals(2, response.size());
        verify(movementRepository).findAll();
    }
    
    @Test
    void testGetAllMovements_Empty() {
        when(movementRepository.findAll()).thenReturn(Arrays.asList());
        
        List<MovementResponseDTO> response = movementService.getAllMovements();
        
        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(movementRepository).findAll();
    }
    
    @Test
    void testGetMovementsByAccountId_Success() {
        Movement movement2 = new Movement();
        movement2.setId(2L);
        movement2.setAccount(account);
        movement2.setMovementNumber(2L);
        movement2.setMovementDate(LocalDateTime.now());
        movement2.setMovementType("RETIRO");
        movement2.setAmount(-200.0);
        movement2.setBalance(1300.0);
        movement2.setState("ACTIVO");
        
        List<Movement> movements = Arrays.asList(movement, movement2);
        when(movementRepository.findByAccountId(1L)).thenReturn(movements);
        
        List<MovementResponseDTO> response = movementService.getMovementsByAccountId(1L);
        
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(movement.getId(), response.get(0).getId());
        assertEquals(movement2.getId(), response.get(1).getId());
        verify(movementRepository).findByAccountId(1L);
    }
    
    @Test
    void testGetMovementsByAccountId_Empty() {
        when(movementRepository.findByAccountId(999L)).thenReturn(Arrays.asList());
        
        List<MovementResponseDTO> response = movementService.getMovementsByAccountId(999L);
        
        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(movementRepository).findByAccountId(999L);
    }
}
