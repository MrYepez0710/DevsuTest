package com.devsu.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Account responses
 * Excludes sensitive information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponseDTO {
    
    private Long id;
    private String accountNumber;
    private String accountType;
    private Double balance;
    private String state;
    private String clientId;
}
