package com.devsu.application.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Report requests (F4)
 * Request parameters for account statement report
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {
    
    @NotNull(message = "Account ID is required")
    private Long accountId;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
}
