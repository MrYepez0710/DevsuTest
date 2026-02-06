package com.devsu.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.devsu.application.dto.ReportResponseDTO;
import com.devsu.domain.exception.ResourceNotFoundException;
import com.devsu.domain.model.Account;
import com.devsu.domain.model.Movement;
import com.devsu.domain.repository.AccountRepository;
import com.devsu.domain.repository.MovementRepository;
import com.devsu.infrastructure.cache.dto.ClientCacheDTO;
import com.devsu.infrastructure.client.ClientServiceClient;

/**
 * Unit tests for ReportServiceImpl
 * Tests F4: Account statement reports by date range
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReportServiceImpl Unit Tests")
class ReportServiceImplTest {
    
    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private MovementRepository movementRepository;
    
    @Mock
    private ClientServiceClient clientServiceClient;
    
    @InjectMocks
    private ReportServiceImpl reportService;
    
    private Account account1;
    private Account account2;
    private Movement movement1;
    private Movement movement2;
    private Movement movement3;
    private ClientCacheDTO clientCache;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.of(2026, 2, 1, 0, 0);
        endDate = LocalDateTime.of(2026, 2, 28, 23, 59);
        
        // Setup accounts
        account1 = new Account();
        account1.setId(1L);
        account1.setAccountNumber("123456");
        account1.setAccountType("AHORROS");
        account1.setBalance(1500.0);
        account1.setState("ACTIVA");
        account1.setClientId("JLEMA001");
        
        account2 = new Account();
        account2.setId(2L);
        account2.setAccountNumber("654321");
        account2.setAccountType("CORRIENTE");
        account2.setBalance(700.0);
        account2.setState("ACTIVA");
        account2.setClientId("JLEMA001");
        
        // Setup movements
        movement1 = new Movement();
        movement1.setId(1L);
        movement1.setAccount(account1);
        movement1.setMovementNumber(1L);
        movement1.setMovementDate(LocalDateTime.of(2026, 2, 4, 10, 0));
        movement1.setMovementType("Retiro de 575");
        movement1.setAmount(-575.0);
        movement1.setBalance(1425.0);
        movement1.setState("ACTIVO");
        
        movement2 = new Movement();
        movement2.setId(2L);
        movement2.setAccount(account1);
        movement2.setMovementNumber(2L);
        movement2.setMovementDate(LocalDateTime.of(2026, 2, 5, 11, 0));
        movement2.setMovementType("Deposito de 100");
        movement2.setAmount(100.0);
        movement2.setBalance(1525.0);
        movement2.setState("ACTIVO");
        
        movement3 = new Movement();
        movement3.setId(3L);
        movement3.setAccount(account2);
        movement3.setMovementNumber(1L);
        movement3.setMovementDate(LocalDateTime.of(2026, 2, 6, 12, 0));
        movement3.setMovementType("Deposito de 600");
        movement3.setAmount(600.0);
        movement3.setBalance(700.0);
        movement3.setState("ACTIVO");
        
        // Setup client cache
        clientCache = ClientCacheDTO.builder()
            .id(1L)
            .clientId("JLEMA001")
            .name("José Lema")
            .gender("M")
            .age(35)
            .idNumber("1234567890")
            .address("Otavalo sn y principal")
            .phone("098254785")
            .state("true")
            .build();
    }
    
    @Test
    @DisplayName("Should generate account statement successfully")
    void testGenerateAccountStatement_Success() {
        // Given
        List<Account> accounts = Arrays.asList(account1, account2);
        List<Movement> movements1 = Arrays.asList(movement1, movement2);
        List<Movement> movements2 = Arrays.asList(movement3);
        
        when(accountRepository.findByClientId("JLEMA001")).thenReturn(accounts);
        when(movementRepository.findByAccountAndMovementDateBetween(eq(account1), any(), any()))
            .thenReturn(movements1);
        when(movementRepository.findByAccountAndMovementDateBetween(eq(account2), any(), any()))
            .thenReturn(movements2);
        when(movementRepository.findByAccountOrderByMovementDateDesc(account1))
            .thenReturn(Arrays.asList(movement2, movement1));
        when(movementRepository.findByAccountOrderByMovementDateDesc(account2))
            .thenReturn(Arrays.asList(movement3));
        when(clientServiceClient.getClientByClientId("JLEMA001")).thenReturn(clientCache);
        
        // When
        ReportResponseDTO report = reportService.generateAccountStatement("JLEMA001", startDate, endDate);
        
        // Then
        assertNotNull(report);
        assertNotNull(report.getReportDate());
        assertEquals("JLEMA001", report.getClient().getClientId());
        assertEquals("José Lema", report.getClient().getClientName());
        assertEquals(2, report.getAccounts().size());
        assertEquals(2, report.getSummary().getTotalAccounts());
        assertEquals(3, report.getSummary().getTotalMovements());
        assertTrue(report.getSummary().getTotalDeposits() > 0);
        assertTrue(report.getSummary().getTotalWithdrawals() < 0);
        
        verify(accountRepository).findByClientId("JLEMA001");
        verify(clientServiceClient).getClientByClientId("JLEMA001");
    }
    
    @Test
    @DisplayName("Should throw exception when no accounts found for client")
    void testGenerateAccountStatement_NoAccountsFound() {
        // Given
        when(accountRepository.findByClientId("NOCLIENT")).thenReturn(Arrays.asList());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            reportService.generateAccountStatement("NOCLIENT", startDate, endDate);
        });
        
        assertTrue(exception.getMessage().contains("No accounts found"));
        verify(accountRepository).findByClientId("NOCLIENT");
        verify(clientServiceClient, never()).getClientByClientId(any());
    }
    
    @Test
    @DisplayName("Should generate report with empty movements")
    void testGenerateAccountStatement_NoMovementsInRange() {
        // Given
        List<Account> accounts = Arrays.asList(account1);
        
        when(accountRepository.findByClientId("JLEMA001")).thenReturn(accounts);
        when(movementRepository.findByAccountAndMovementDateBetween(eq(account1), any(), any()))
            .thenReturn(Arrays.asList());
        when(movementRepository.findByAccountOrderByMovementDateDesc(account1))
            .thenReturn(Arrays.asList());
        when(clientServiceClient.getClientByClientId("JLEMA001")).thenReturn(clientCache);
        
        // When
        ReportResponseDTO report = reportService.generateAccountStatement("JLEMA001", startDate, endDate);
        
        // Then
        assertNotNull(report);
        assertEquals(1, report.getAccounts().size());
        assertEquals(0, report.getAccounts().get(0).getMovements().size());
        assertEquals(0, report.getSummary().getTotalMovements());
        assertEquals(0.0, report.getSummary().getTotalDeposits());
        assertEquals(0.0, report.getSummary().getTotalWithdrawals());
        
        verify(accountRepository).findByClientId("JLEMA001");
    }
    
    @Test
    @DisplayName("Should calculate summary correctly with deposits and withdrawals")
    void testGenerateAccountStatement_CalculateSummary() {
        // Given
        List<Account> accounts = Arrays.asList(account1);
        List<Movement> movements = Arrays.asList(movement1, movement2);
        
        when(accountRepository.findByClientId("JLEMA001")).thenReturn(accounts);
        when(movementRepository.findByAccountAndMovementDateBetween(eq(account1), any(), any()))
            .thenReturn(movements);
        when(movementRepository.findByAccountOrderByMovementDateDesc(account1))
            .thenReturn(Arrays.asList(movement2, movement1));
        when(clientServiceClient.getClientByClientId("JLEMA001")).thenReturn(clientCache);
        
        // When
        ReportResponseDTO report = reportService.generateAccountStatement("JLEMA001", startDate, endDate);
        
        // Then
        assertEquals(1, report.getSummary().getTotalAccounts());
        assertEquals(2, report.getSummary().getTotalMovements());
        assertEquals(100.0, report.getSummary().getTotalDeposits());
        assertEquals(-575.0, report.getSummary().getTotalWithdrawals());
        
        double expectedNetChange = report.getAccounts().get(0).getFinalBalance() 
                                 - report.getAccounts().get(0).getInitialBalance();
        assertEquals(expectedNetChange, report.getSummary().getNetChange());
    }
    
    @Test
    @DisplayName("Should generate report for multiple accounts")
    void testGenerateAccountStatement_MultipleAccounts() {
        // Given
        List<Account> accounts = Arrays.asList(account1, account2);
        List<Movement> movements1 = Arrays.asList(movement1);
        List<Movement> movements2 = Arrays.asList(movement3);
        
        when(accountRepository.findByClientId("JLEMA001")).thenReturn(accounts);
        when(movementRepository.findByAccountAndMovementDateBetween(eq(account1), any(), any()))
            .thenReturn(movements1);
        when(movementRepository.findByAccountAndMovementDateBetween(eq(account2), any(), any()))
            .thenReturn(movements2);
        when(movementRepository.findByAccountOrderByMovementDateDesc(account1))
            .thenReturn(Arrays.asList(movement1));
        when(movementRepository.findByAccountOrderByMovementDateDesc(account2))
            .thenReturn(Arrays.asList(movement3));
        when(clientServiceClient.getClientByClientId("JLEMA001")).thenReturn(clientCache);
        
        // When
        ReportResponseDTO report = reportService.generateAccountStatement("JLEMA001", startDate, endDate);
        
        // Then
        assertEquals(2, report.getAccounts().size());
        assertEquals("123456", report.getAccounts().get(0).getAccountNumber());
        assertEquals("654321", report.getAccounts().get(1).getAccountNumber());
        assertEquals(1, report.getAccounts().get(0).getMovements().size());
        assertEquals(1, report.getAccounts().get(1).getMovements().size());
    }
    
    @Test
    @DisplayName("Should include correct movement details in report")
    void testGenerateAccountStatement_MovementDetails() {
        // Given
        List<Account> accounts = Arrays.asList(account1);
        List<Movement> movements = Arrays.asList(movement1);
        
        when(accountRepository.findByClientId("JLEMA001")).thenReturn(accounts);
        when(movementRepository.findByAccountAndMovementDateBetween(eq(account1), any(), any()))
            .thenReturn(movements);
        when(movementRepository.findByAccountOrderByMovementDateDesc(account1))
            .thenReturn(Arrays.asList(movement1));
        when(clientServiceClient.getClientByClientId("JLEMA001")).thenReturn(clientCache);
        
        // When
        ReportResponseDTO report = reportService.generateAccountStatement("JLEMA001", startDate, endDate);
        
        // Then
        ReportResponseDTO.MovementInfo movementInfo = report.getAccounts().get(0).getMovements().get(0);
        assertEquals(1L, movementInfo.getMovementId());
        assertEquals("Retiro de 575", movementInfo.getMovementType());
        assertEquals(-575.0, movementInfo.getAmount());
        assertEquals(1425.0, movementInfo.getBalance());
        assertEquals("ACTIVO", movementInfo.getState());
        assertNotNull(movementInfo.getMovementDate());
    }
}
