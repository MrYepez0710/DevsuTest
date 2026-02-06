package com.devsu.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequestDTO {
    
    @NotBlank(message = "Name is required")
    private String name; 
    
    @NotBlank(message = "Gender is required")
    private String gender; 
    
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be positive")
    @Max(value = 150, message = "Age must be realistic")
    private Integer age; 
    
    @NotBlank(message = "ID number is required")
    private String idNumber; 
    
    @NotBlank(message = "Address is required")
    private String address; 
    
    @NotBlank(message = "Phone is required")
    private String phone; 
    
    @NotBlank(message = "Client ID is required")
    private String clientId; 
    
    @NotBlank(message = "Password is required")
    private String password; 
    
    @NotBlank(message = "State is required")
    private String state;
}
