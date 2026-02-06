package com.devsu.infrastructure.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devsu.application.dto.ClientRequestDTO;
import com.devsu.application.dto.ClientResponseDTO;
import com.devsu.application.service.ClientService;
import com.devsu.domain.exception.BusinessException;
import com.devsu.domain.exception.ResourceNotFoundException;
import com.devsu.infrastructure.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Pruebas unitarias para ClientController
 */
@WebMvcTest(ClientController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ClientController Unit Tests")
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    private ClientRequestDTO clientRequestDTO;
    private ClientResponseDTO clientResponseDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        clientRequestDTO = new ClientRequestDTO();
        clientRequestDTO.setName("José Lema");
        clientRequestDTO.setGender("Masculino");
        clientRequestDTO.setAge(35);
        clientRequestDTO.setIdNumber("1234567890");
        clientRequestDTO.setAddress("Otavalo sn y principal");
        clientRequestDTO.setPhone("098254785");
        clientRequestDTO.setClientId("CLI001");
        clientRequestDTO.setPassword("1234");
        clientRequestDTO.setState("true");

        clientResponseDTO = ClientResponseDTO.builder()
                .id(1L)
                .name("José Lema")
                .gender("Masculino")
                .age(35)
                .idNumber("1234567890")
                .address("Otavalo sn y principal")
                .phone("098254785")
                .clientId("CLI001")
                .state("true")
                .build();
    }

    @Test
    @DisplayName("POST /clientes - Should create client successfully")
    void testCreateClient_Success() throws Exception {
        // Given
        when(clientService.createClient(any(ClientRequestDTO.class))).thenReturn(clientResponseDTO);

        // When & Then
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("José Lema"))
                .andExpect(jsonPath("$.clientId").value("CLI001"))
                .andExpect(jsonPath("$.password").doesNotExist()); // Password no debe estar en response

        verify(clientService, times(1)).createClient(any(ClientRequestDTO.class));
    }

    @Test
    @DisplayName("POST /clientes - Should return 400 when validation fails")
    void testCreateClient_ValidationError() throws Exception {
        // Given - DTO sin campos requeridos
        ClientRequestDTO invalidDTO = new ClientRequestDTO();

        // When & Then
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors").exists());

        verify(clientService, never()).createClient(any(ClientRequestDTO.class));
    }

    @Test
    @DisplayName("POST /clientes - Should return 400 when clientId already exists")
    void testCreateClient_ClientIdExists() throws Exception {
        // Given
        when(clientService.createClient(any(ClientRequestDTO.class)))
                .thenThrow(new BusinessException("Client with clientId CLI001 already exists"));

        // When & Then
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Client with clientId CLI001 already exists"));

        verify(clientService, times(1)).createClient(any(ClientRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /clientes - Should update client successfully")
    void testUpdateClient_Success() throws Exception {
        // Given
        when(clientService.updateClient(any(ClientRequestDTO.class))).thenReturn(clientResponseDTO);

        // When & Then
        mockMvc.perform(put("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("José Lema"));

        verify(clientService, times(1)).updateClient(any(ClientRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /clientes - Should return 404 when client not found")
    void testUpdateClient_NotFound() throws Exception {
        // Given
        when(clientService.updateClient(any(ClientRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Client with clientId CLI001 not found"));

        // When & Then
        mockMvc.perform(put("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Client with clientId CLI001 not found"));

        verify(clientService, times(1)).updateClient(any(ClientRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /clientes/{id} - Should delete client successfully")
    void testDeleteClient_Success() throws Exception {
        // Given
        ClientResponseDTO deletedClient = ClientResponseDTO.builder()
                .id(1L)
                .name("José Lema")
                .clientId("CLI001")
                .state("false") // Soft delete
                .build();
        
        when(clientService.deleteClient(1L)).thenReturn(deletedClient);

        // When & Then
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.state").value("false"));

        verify(clientService, times(1)).deleteClient(1L);
    }

    @Test
    @DisplayName("DELETE /clientes/{id} - Should return 404 when client not found")
    void testDeleteClient_NotFound() throws Exception {
        // Given
        when(clientService.deleteClient(999L))
                .thenThrow(new ResourceNotFoundException("Client with id 999 not found"));

        // When & Then
        mockMvc.perform(delete("/clientes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Client with id 999 not found"));

        verify(clientService, times(1)).deleteClient(999L);
    }

    @Test
    @DisplayName("GET /clientes/{id} - Should get client successfully")
    void testGetClient_Success() throws Exception {
        // Given
        when(clientService.getClient(1L)).thenReturn(clientResponseDTO);

        // When & Then
        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("José Lema"))
                .andExpect(jsonPath("$.clientId").value("CLI001"));

        verify(clientService, times(1)).getClient(1L);
    }

    @Test
    @DisplayName("GET /clientes/{id} - Should return 404 when client not found")
    void testGetClient_NotFound() throws Exception {
        // Given
        when(clientService.getClient(999L))
                .thenThrow(new ResourceNotFoundException("Client with id 999 not found"));

        // When & Then
        mockMvc.perform(get("/clientes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Client with id 999 not found"));

        verify(clientService, times(1)).getClient(999L);
    }

    @Test
    @DisplayName("GET /clientes - Should get all clients successfully")
    void testGetAllClients_Success() throws Exception {
        // Given
        ClientResponseDTO client2 = ClientResponseDTO.builder()
                .id(2L)
                .name("Marianela Montalvo")
                .clientId("CLI002")
                .build();
        
        List<ClientResponseDTO> clients = Arrays.asList(clientResponseDTO, client2);
        when(clientService.getAllClients()).thenReturn(clients);

        // When & Then
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("José Lema"))
                .andExpect(jsonPath("$[1].name").value("Marianela Montalvo"));

        verify(clientService, times(1)).getAllClients();
    }

    @Test
    @DisplayName("GET /clientes - Should return empty list when no clients exist")
    void testGetAllClients_EmptyList() throws Exception {
        // Given
        when(clientService.getAllClients()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(clientService, times(1)).getAllClients();
    }
}
