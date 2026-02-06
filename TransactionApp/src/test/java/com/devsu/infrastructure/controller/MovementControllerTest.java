package com.devsu.infrastructure.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devsu.application.dto.MovementRequestDTO;
import com.devsu.application.dto.MovementResponseDTO;
import com.devsu.application.service.MovementService;
import com.devsu.domain.exception.InsufficientBalanceException;
import com.devsu.domain.exception.ResourceNotFoundException;
import com.devsu.infrastructure.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for MovementController
 * Tests F2 and F3 functionality
 */
@WebMvcTest(MovementController.class)
@Import(GlobalExceptionHandler.class)
class MovementControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private MovementService movementService;
    
    private MovementRequestDTO movementRequestDTO;
    private MovementResponseDTO movementResponseDTO;
    
    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        movementRequestDTO = new MovementRequestDTO();
        movementRequestDTO.setAccountId(1L);
        movementRequestDTO.setMovementDate(now);
        movementRequestDTO.setMovementType("DEPOSITO");
        movementRequestDTO.setAmount(500.0);
        movementRequestDTO.setState("ACTIVO");
        
        movementResponseDTO = MovementResponseDTO.builder()
                .id(1L)
                .accountId(1L)
                .accountNumber("123456")
                .movementNumber(1L)
                .movementDate(now)
                .movementType("DEPOSITO")
                .amount(500.0)
                .balance(1500.0)
                .state("ACTIVO")
                .build();
    }
    
    @Test
    void testCreateMovement_Deposit_Success() throws Exception {
        when(movementService.createMovement(any(MovementRequestDTO.class)))
            .thenReturn(movementResponseDTO);
        
        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movementRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("123456"))
                .andExpect(jsonPath("$.movementType").value("DEPOSITO"))
                .andExpect(jsonPath("$.amount").value(500.0))
                .andExpect(jsonPath("$.balance").value(1500.0))
                .andExpect(jsonPath("$.state").value("ACTIVO"));
    }
    
    @Test
    void testCreateMovement_Withdrawal_Success() throws Exception {
        movementRequestDTO.setMovementType("RETIRO");
        movementRequestDTO.setAmount(-300.0);
        
        MovementResponseDTO withdrawalResponse = MovementResponseDTO.builder()
                .id(2L)
                .accountId(1L)
                .accountNumber("123456")
                .movementNumber(2L)
                .movementDate(LocalDateTime.now())
                .movementType("RETIRO")
                .amount(-300.0)
                .balance(700.0)
                .state("ACTIVO")
                .build();
        
        when(movementService.createMovement(any(MovementRequestDTO.class)))
            .thenReturn(withdrawalResponse);
        
        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movementRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movementType").value("RETIRO"))
                .andExpect(jsonPath("$.amount").value(-300.0))
                .andExpect(jsonPath("$.balance").value(700.0));
    }
    
    @Test
    void testCreateMovement_InsufficientBalance_F3() throws Exception {
        movementRequestDTO.setMovementType("RETIRO");
        movementRequestDTO.setAmount(-2000.0);
        
        when(movementService.createMovement(any(MovementRequestDTO.class)))
            .thenThrow(new InsufficientBalanceException("Saldo no disponible"));
        
        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movementRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient Balance"))
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));
    }
    
    @Test
    void testCreateMovement_ValidationError() throws Exception {
        MovementRequestDTO invalidDTO = new MovementRequestDTO();
        invalidDTO.setMovementType("");
        invalidDTO.setState("");
        
        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors").isArray());
    }
    
    @Test
    void testCreateMovement_AccountNotFound() throws Exception {
        when(movementService.createMovement(any(MovementRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Account with id 999 not found"));
        
        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movementRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Account with id 999 not found"));
    }
    
    @Test
    void testUpdateMovement_Success() throws Exception {
        MovementRequestDTO updateDTO = new MovementRequestDTO();
        updateDTO.setAccountId(1L);
        updateDTO.setMovementDate(LocalDateTime.now());
        updateDTO.setMovementType("DEPOSITO");
        updateDTO.setAmount(600.0);
        updateDTO.setState("ACTIVO");
        
        MovementResponseDTO updatedResponse = MovementResponseDTO.builder()
                .id(1L)
                .accountId(1L)
                .accountNumber("123456")
                .movementNumber(1L)
                .movementDate(LocalDateTime.now())
                .movementType("DEPOSITO")
                .amount(600.0)
                .balance(1600.0)
                .state("ACTIVO")
                .build();
        
        when(movementService.updateMovement(eq(1L), any(MovementRequestDTO.class)))
            .thenReturn(updatedResponse);
        
        mockMvc.perform(put("/movimientos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(600.0))
                .andExpect(jsonPath("$.balance").value(1600.0));
    }
    
    @Test
    void testUpdateMovement_NotFound() throws Exception {
        when(movementService.updateMovement(eq(999L), any(MovementRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Movement with id 999 not found"));
        
        mockMvc.perform(put("/movimientos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movementRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Movement with id 999 not found"));
    }
    
    @Test
    void testGetMovement_Success() throws Exception {
        when(movementService.getMovement(1L)).thenReturn(movementResponseDTO);
        
        mockMvc.perform(get("/movimientos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("123456"))
                .andExpect(jsonPath("$.amount").value(500.0))
                .andExpect(jsonPath("$.balance").value(1500.0));
    }
    
    @Test
    void testGetMovement_NotFound() throws Exception {
        when(movementService.getMovement(999L))
            .thenThrow(new ResourceNotFoundException("Movement with id 999 not found"));
        
        mockMvc.perform(get("/movimientos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Movement with id 999 not found"));
    }
    
    @Test
    void testGetAllMovements_Success() throws Exception {
        MovementResponseDTO movement2 = MovementResponseDTO.builder()
                .id(2L)
                .accountId(1L)
                .accountNumber("123456")
                .movementNumber(2L)
                .movementDate(LocalDateTime.now())
                .movementType("RETIRO")
                .amount(-200.0)
                .balance(1300.0)
                .state("ACTIVO")
                .build();
        
        List<MovementResponseDTO> movements = Arrays.asList(movementResponseDTO, movement2);
        when(movementService.getAllMovements()).thenReturn(movements);
        
        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].movementType").value("DEPOSITO"))
                .andExpect(jsonPath("$[1].movementType").value("RETIRO"));
    }
    
    @Test
    void testGetAllMovements_Empty() throws Exception {
        when(movementService.getAllMovements()).thenReturn(Arrays.asList());
        
        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
    
    @Test
    void testGetMovementsByAccountId_Success() throws Exception {
        MovementResponseDTO movement2 = MovementResponseDTO.builder()
                .id(2L)
                .accountId(1L)
                .accountNumber("123456")
                .movementNumber(2L)
                .movementDate(LocalDateTime.now())
                .movementType("RETIRO")
                .amount(-200.0)
                .balance(1300.0)
                .state("ACTIVO")
                .build();
        
        List<MovementResponseDTO> movements = Arrays.asList(movementResponseDTO, movement2);
        when(movementService.getMovementsByAccountId(1L)).thenReturn(movements);
        
        mockMvc.perform(get("/movimientos").param("accountId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountId").value(1))
                .andExpect(jsonPath("$[1].accountId").value(1))
                .andExpect(jsonPath("$[0].movementType").value("DEPOSITO"))
                .andExpect(jsonPath("$[1].movementType").value("RETIRO"));
    }
    
    @Test
    void testGetMovementsByAccountId_Empty() throws Exception {
        when(movementService.getMovementsByAccountId(999L)).thenReturn(Arrays.asList());
        
        mockMvc.perform(get("/movimientos").param("accountId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
