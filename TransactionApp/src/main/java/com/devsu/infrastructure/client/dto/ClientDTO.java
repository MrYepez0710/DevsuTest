package com.devsu.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Client information from ClientApp REST API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private Long id;
    private String clientId;
    private String name;
    private String gender;
    private Integer age;
    private String idNumber;
    private String address;
    private String phone;
    private String state;
}
