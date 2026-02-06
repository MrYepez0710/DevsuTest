package com.devsu.application.dto;

import com.devsu.domain.model.Account;
import com.devsu.domain.model.Movement;

/**
 * Mapper class for converting between Movement entity and DTOs
 */
public class MovementMapper {
    
    private MovementMapper() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Convert MovementRequestDTO to Movement entity
     * Note: Account must be set separately by the service
     */
    public static Movement toEntity(MovementRequestDTO dto, Account account) {
        Movement movement = new Movement();
        movement.setAccount(account);
        movement.setMovementDate(dto.getMovementDate());
        movement.setMovementType(dto.getMovementType());
        movement.setAmount(dto.getAmount());
        movement.setState(dto.getState());
        // Balance will be calculated by the service
        return movement;
    }
    
    /**
     * Convert Movement entity to MovementResponseDTO
     */
    public static MovementResponseDTO toResponseDTO(Movement movement) {
        return MovementResponseDTO.builder()
                .id(movement.getId())
                .accountId(movement.getAccount().getId())
                .accountNumber(movement.getAccount().getAccountNumber())
                .movementNumber(movement.getMovementNumber())
                .movementDate(movement.getMovementDate())
                .movementType(movement.getMovementType())
                .amount(movement.getAmount())
                .balance(movement.getBalance())
                .state(movement.getState())
                .build();
    }
    
    /**
     * Update Movement entity from MovementRequestDTO
     * Note: Account and balance should be handled by the service
     */
    public static void updateEntityFromDTO(Movement movement, MovementRequestDTO dto) {
        movement.setMovementDate(dto.getMovementDate());
        movement.setMovementType(dto.getMovementType());
        movement.setAmount(dto.getAmount());
        movement.setState(dto.getState());
    }
}
