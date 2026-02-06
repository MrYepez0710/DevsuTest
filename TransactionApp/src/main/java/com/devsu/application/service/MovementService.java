package com.devsu.application.service;

import java.util.List;

import com.devsu.application.dto.MovementRequestDTO;
import com.devsu.application.dto.MovementResponseDTO;

public interface MovementService {
    
    public MovementResponseDTO createMovement(MovementRequestDTO movementRequestDTO);
    public MovementResponseDTO updateMovement(Long id, MovementRequestDTO movementRequestDTO);
    public void deleteMovement(Long id);
    public MovementResponseDTO getMovement(Long id);
    public List<MovementResponseDTO> getAllMovements();
    public List<MovementResponseDTO> getMovementsByAccountId(Long accountId);
}
