package com.devsu.application.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Movement responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovementResponseDTO {
    
    private Long id;
    private Long accountId;
    private String accountNumber;
    private Long movementNumber;
    private LocalDateTime movementDate;
    private String movementType;
    private Double amount;
    private Double balance;
    private String state;
}
