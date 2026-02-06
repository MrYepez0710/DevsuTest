package com.devsu.application.service;

import com.devsu.application.dto.ReportResponseDTO;

import java.time.LocalDateTime;

/**
 * Service interface for Report operations (F4)
 */
public interface ReportService {
    
    /**
     * Generate account statement report for date range by client
     * F4: Returns all client accounts with movements and summary for specified period
     * 
     * @param clientId the client ID
     * @param startDate start date of report period
     * @param endDate end date of report period
     * @return report with all client accounts, movements and summary
     */
    ReportResponseDTO generateAccountStatement(String clientId, LocalDateTime startDate, LocalDateTime endDate);
}
