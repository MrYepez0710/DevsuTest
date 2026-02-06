package com.devsu.application.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Movement creation and update requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementRequestDTO {
    
    @NotNull(message = "Account ID is required")
    private Long accountId;
    
    @NotNull(message = "Movement date is required")
    private LocalDateTime movementDate;
    
    @NotBlank(message = "Movement type is required")
    private String movementType;
    
    @NotNull(message = "Amount is required")
    private Double amount;
    
    @NotBlank(message = "State is required")
    private String state;
}
