package com.devsu.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientResponseDTO {
    
    private Long id;
    private String name; 
    private String gender; 
    private Integer age; 
    private String idNumber; 
    private String address; 
    private String phone; 
    private String clientId;
    private String state;
}
