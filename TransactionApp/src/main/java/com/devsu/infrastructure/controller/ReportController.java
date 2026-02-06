package com.devsu.infrastructure.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devsu.application.dto.ReportResponseDTO;
import com.devsu.application.service.ReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Report operations
 * Implements F4: Account statement reports by date range
 */
@Slf4j
@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    
    /**
     * Generate account statement report by client
     * GET /reportes?clientId={id}&startDate={date}&endDate={date}
     * F4: Returns all client accounts with movements and summary for specified period
     */
    @GetMapping
    public ResponseEntity<ReportResponseDTO> generateAccountStatement(
            @RequestParam String clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("REST request to generate report for client {} from {} to {}", clientId, startDate, endDate);
        ReportResponseDTO report = reportService.generateAccountStatement(clientId, startDate, endDate);
        return ResponseEntity.ok(report);
    }
}
