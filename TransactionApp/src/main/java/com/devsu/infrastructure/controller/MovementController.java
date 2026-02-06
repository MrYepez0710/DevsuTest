package com.devsu.infrastructure.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devsu.application.dto.MovementRequestDTO;
import com.devsu.application.dto.MovementResponseDTO;
import com.devsu.application.service.MovementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Movement operations
 * Implements F1: CRU for Movement (no Delete)
 * Implements F2: Movement registration with balance update
 * Implements F3: Insufficient balance validation
 */
@Slf4j
@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@Validated
public class MovementController {
    
    private final MovementService movementService;
    
    /**
     * Create a new movement
     * POST /movimientos
     * F2: Registers movement and updates account balance
     * F3: Returns "Saldo no disponible" if insufficient balance
     */
    @PostMapping
    public ResponseEntity<MovementResponseDTO> createMovement(@Valid @RequestBody MovementRequestDTO movementRequestDTO) {
        log.info("REST request to create Movement for account: {}", movementRequestDTO.getAccountId());
        MovementResponseDTO response = movementService.createMovement(movementRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Update an existing movement
     * PUT /movimientos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MovementResponseDTO> updateMovement(
            @PathVariable Long id,
            @Valid @RequestBody MovementRequestDTO movementRequestDTO) {
        log.info("REST request to update Movement with id: {}", id);
        MovementResponseDTO response = movementService.updateMovement(id, movementRequestDTO);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get movement by ID
     * GET /movimientos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovementResponseDTO> getMovement(@PathVariable Long id) {
        log.info("REST request to get Movement with id: {}", id);
        MovementResponseDTO response = movementService.getMovement(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all movements or filter by accountId
     * GET /movimientos
     * GET /movimientos?accountId=1
     */
    @GetMapping
    public ResponseEntity<List<MovementResponseDTO>> getAllMovements(
            @RequestParam(required = false) Long accountId) {
        if (accountId != null) {
            log.info("REST request to get Movements for accountId: {}", accountId);
            List<MovementResponseDTO> response = movementService.getMovementsByAccountId(accountId);
            return ResponseEntity.ok(response);
        } else {
            log.info("REST request to get all Movements");
            List<MovementResponseDTO> response = movementService.getAllMovements();
            return ResponseEntity.ok(response);
        }
    }
}
