package com.devsu.domain.model;

import jakarta.persistence.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client extends Person {
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Client ID is required")
    private String clientId;
    
    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    private String password;
    
    @Column(nullable = false)
    @NotBlank(message = "State is required")
    private String state;
    
    private static final long serialVersionUID = 1L;

    public Client(String name, String gender, Integer age, String idNumber, String address, String phone, 
                  String clientId, String password, String state) {
        super(null, name, gender, age, idNumber, address, phone);
        this.clientId = clientId;
        this.password = password;
        this.state = state;
    }
}
