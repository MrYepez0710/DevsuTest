package com.devsu.infrastructure.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for caching client information in Redis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientCacheDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
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
