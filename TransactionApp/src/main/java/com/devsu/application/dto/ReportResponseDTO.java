package com.devsu.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Report responses (F4)
 * Account statement report with movements in date range by client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportResponseDTO {
    
    private LocalDateTime reportDate;
    private ClientInfo client;
    private List<AccountInfo> accounts;
    private Summary summary;
    
    /**
     * Client information in report
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientInfo {
        private String clientId;
        private String clientName;
    }
    
    /**
     * Account information in report
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountInfo {
        private Long accountId;
        private String accountNumber;
        private String accountType;
        private String clientId;
        private Double initialBalance;
        private Double finalBalance;
        private List<MovementInfo> movements;
    }
    
    /**
     * Movement information in report
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementInfo {
        private Long movementId;
        private LocalDateTime movementDate;
        private String movementType;
        private Double amount;
        private Double balance;
        private String state;
    }
    
    /**
     * Report summary
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private Integer totalAccounts;
        private Integer totalMovements;
        private Double totalDeposits;
        private Double totalWithdrawals;
        private Double netChange;
    }
}
