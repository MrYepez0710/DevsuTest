package com.devsu.application.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsu.application.dto.ReportResponseDTO;
import com.devsu.application.service.ReportService;
import com.devsu.domain.exception.ResourceNotFoundException;
import com.devsu.domain.model.Account;
import com.devsu.domain.model.Movement;
import com.devsu.domain.repository.AccountRepository;
import com.devsu.domain.repository.MovementRepository;
import com.devsu.infrastructure.cache.dto.ClientCacheDTO;
import com.devsu.infrastructure.client.ClientServiceClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of ReportService
 * Handles F4: Account statement reports by date range
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final ClientServiceClient clientServiceClient;
    
    /**
     * Generate account statement report for date range by client
     * F4: Returns all client accounts with movements and summary for specified period
     */
    @Override
    @Transactional(readOnly = true)
    public ReportResponseDTO generateAccountStatement(String clientId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating account statement for client {} from {} to {}", clientId, startDate, endDate);
        
        // Find all accounts for the client
        List<Account> accounts = accountRepository.findByClientId(clientId);
        if (accounts.isEmpty()) {
            log.error("No accounts found for client {}", clientId);
            throw new ResourceNotFoundException("No accounts found for client " + clientId);
        }
        
        log.info("Found {} accounts for client {}", accounts.size(), clientId);
        
        // Process each account
        List<ReportResponseDTO.AccountInfo> accountInfos = accounts.stream()
            .map(account -> processAccountReport(account, startDate, endDate))
            .collect(Collectors.toList());
        
        // Calculate global summary
        int totalMovements = accountInfos.stream()
            .mapToInt(a -> a.getMovements().size())
            .sum();
        
        double totalDeposits = accountInfos.stream()
            .flatMap(a -> a.getMovements().stream())
            .filter(m -> m.getAmount() > 0)
            .mapToDouble(ReportResponseDTO.MovementInfo::getAmount)
            .sum();
        
        double totalWithdrawals = accountInfos.stream()
            .flatMap(a -> a.getMovements().stream())
            .filter(m -> m.getAmount() < 0)
            .mapToDouble(ReportResponseDTO.MovementInfo::getAmount)
            .sum();
        
        double netChange = accountInfos.stream()
            .mapToDouble(a -> a.getFinalBalance() - a.getInitialBalance())
            .sum();
        
        // Get client information from cache or REST fallback
        ClientCacheDTO clientData = clientServiceClient.getClientByClientId(clientId);
        log.info("Retrieved client info for {}: {}", clientId, clientData.getName());
        
        // Build client info
        ReportResponseDTO.ClientInfo clientInfo = ReportResponseDTO.ClientInfo.builder()
            .clientId(clientId)
            .clientName(clientData.getName())
            .build();
        
        // Build summary
        ReportResponseDTO.Summary summary = ReportResponseDTO.Summary.builder()
            .totalAccounts(accounts.size())
            .totalMovements(totalMovements)
            .totalDeposits(totalDeposits)
            .totalWithdrawals(totalWithdrawals)
            .netChange(netChange)
            .build();
        
        // Build report
        ReportResponseDTO report = ReportResponseDTO.builder()
            .reportDate(LocalDateTime.now())
            .client(clientInfo)
            .accounts(accountInfos)
            .summary(summary)
            .build();
        
        log.info("Report generated successfully for client {} with {} accounts", clientId, accounts.size());
        return report;
    }
    
    /**
     * Process report for a single account
     */
    private ReportResponseDTO.AccountInfo processAccountReport(Account account, LocalDateTime startDate, LocalDateTime endDate) {
        // Get movements in date range
        List<Movement> movements = movementRepository.findByAccountAndMovementDateBetween(account, startDate, endDate);
        
        // Get initial balance (balance before first movement in range)
        Double initialBalance = account.getBalance();
        List<Movement> allMovements = movementRepository.findByAccountOrderByMovementDateDesc(account);
        if (!allMovements.isEmpty() && !movements.isEmpty()) {
            Movement firstInRange = movements.get(0);
            for (Movement m : allMovements) {
                if (m.getMovementDate().isBefore(firstInRange.getMovementDate())) {
                    initialBalance = m.getBalance();
                    break;
                }
            }
        }
        
        // Get final balance (balance after last movement in range)
        Double finalBalance = initialBalance;
        if (!movements.isEmpty()) {
            finalBalance = movements.get(movements.size() - 1).getBalance();
        }
        
        // Build movement infos
        List<ReportResponseDTO.MovementInfo> movementInfos = movements.stream()
            .map(m -> ReportResponseDTO.MovementInfo.builder()
                .movementId(m.getId())
                .movementDate(m.getMovementDate())
                .movementType(m.getMovementType())
                .amount(m.getAmount())
                .balance(m.getBalance())
                .state(m.getState())
                .build())
            .collect(Collectors.toList());
        
        // Build account info
        return ReportResponseDTO.AccountInfo.builder()
            .accountId(account.getId())
            .accountNumber(account.getAccountNumber())
            .accountType(account.getAccountType())
            .clientId(account.getClientId())
            .initialBalance(initialBalance)
            .finalBalance(finalBalance)
            .movements(movementInfos)
            .build();
    }
}
