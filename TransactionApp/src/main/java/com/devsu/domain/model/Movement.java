package com.devsu.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "movement")  
public class Movement implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull(message = "Account is required")
    private Account account;
    
    @Column
    private Long movementNumber;
    
    @Column(nullable = false)
    @NotNull(message = "Movement date is required")
    private LocalDateTime movementDate;
    
    @Column(nullable = false)
    @NotBlank(message = "Movement type is required")
    private String movementType;
    
    @Column(nullable = false)
    @NotNull(message = "Amount is required")
    private Double amount;
    
    @Column(nullable = false)
    @NotNull(message = "Balance is required")
    private Double balance;
    
    @Column(nullable = false)
    @NotBlank(message = "State is required")
    private String state;    

    private static final long serialVersionUID = 1L;

    public Movement() { }

    public Movement(Long id, Account account, Long movementNumber, LocalDateTime movementDate, String movementType, double amount, double balance, String state) { 
        this.account = account;
        this.movementNumber = movementNumber;
        this.movementDate = movementDate;
        this.movementType = movementType;
        this.amount = amount;
        this.balance = balance;
        this.state = state;
    }
}   