package com.devsu.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Account creation and update requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO {
    
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    
    @NotBlank(message = "Account type is required")
    private String accountType;
    
    @NotNull(message = "Initial balance is required")
    @Min(value = 0, message = "Balance cannot be negative")
    private Double initialBalance;
    
    @NotBlank(message = "State is required")
    private String state;
    
    private String accountKey;
    
    @NotBlank(message = "Client ID is required")
    private String clientId;
}
