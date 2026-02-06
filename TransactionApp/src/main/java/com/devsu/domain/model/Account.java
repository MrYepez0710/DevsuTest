package com.devsu.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "account")
public class Account implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    
    @Column(nullable = false)
    @NotBlank(message = "Account type is required")
    private String accountType;
    
    @Column(nullable = false)
    @NotNull(message = "Balance is required")
    @Min(value = 0, message = "Balance cannot be negative")
    private Double balance;
    
    @Column(nullable = false)
    @NotBlank(message = "State is required")
    private String state;
    
    @Column
    private String accountKey;
    
    @Column(nullable = false)
    @NotBlank(message = "Client ID is required")
    private String clientId;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movement> movements = new ArrayList<>();
    
    private static final long serialVersionUID = 1L;

    public Account() { }

    public Account(Long id, String accountNumber, String accountType, double balance, String state, String accountKey, String clientId) { 
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.state = state;
        this.accountKey = accountKey;
        this.clientId = clientId;
    }
}
