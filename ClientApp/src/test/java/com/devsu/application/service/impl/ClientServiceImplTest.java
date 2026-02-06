package com.devsu.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.devsu.application.dto.ClientRequestDTO;
import com.devsu.application.dto.ClientResponseDTO;
import com.devsu.domain.exception.BusinessException;
import com.devsu.domain.exception.ResourceNotFoundException;
import com.devsu.domain.model.Client;
import com.devsu.domain.repository.ClientRepository;
import com.devsu.infrastructure.messaging.publisher.ClientEventPublisher;

/**
 * Pruebas unitarias para ClientServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClientServiceImpl Unit Tests")
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientEventPublisher clientEventPublisher;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private ClientRequestDTO clientRequestDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        client = new Client();
        client.setId(1L);
        client.setName("José Lema");
        client.setGender("Masculino");
        client.setAge(35);
        client.setIdNumber("1234567890");
        client.setAddress("Otavalo sn y principal");
        client.setPhone("098254785");
        client.setClientId("CLI001");
        client.setPassword("1234");
        client.setState("true");

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
    }

    @Test
    @DisplayName("Should create client successfully")
    void testCreateClient_Success() {
        // Given
        when(clientRepository.findByClientId(clientRequestDTO.getClientId())).thenReturn(Optional.empty());
        when(clientRepository.findByIdNumber(clientRequestDTO.getIdNumber())).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // When
        ClientResponseDTO result = clientService.createClient(clientRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(client.getId(), result.getId());
        assertEquals(client.getName(), result.getName());
        assertEquals(client.getClientId(), result.getClientId());
        // Password no se incluye en ClientResponseDTO por seguridad
        
        verify(clientRepository, times(1)).findByClientId(clientRequestDTO.getClientId());
        verify(clientRepository, times(1)).findByIdNumber(clientRequestDTO.getIdNumber());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when clientId already exists")
    void testCreateClient_ClientIdExists() {
        // Given
        when(clientRepository.findByClientId(clientRequestDTO.getClientId())).thenReturn(Optional.of(client));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            clientService.createClient(clientRequestDTO);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(clientRepository, times(1)).findByClientId(clientRequestDTO.getClientId());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when idNumber already exists")
    void testCreateClient_IdNumberExists() {
        // Given
        when(clientRepository.findByClientId(clientRequestDTO.getClientId())).thenReturn(Optional.empty());
        when(clientRepository.findByIdNumber(clientRequestDTO.getIdNumber())).thenReturn(Optional.of(client));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            clientService.createClient(clientRequestDTO);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(clientRepository, times(1)).findByIdNumber(clientRequestDTO.getIdNumber());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should update client successfully")
    void testUpdateClient_Success() {
        // Given
        when(clientRepository.findByClientId(clientRequestDTO.getClientId())).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // When
        ClientResponseDTO result = clientService.updateClient(clientRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(client.getId(), result.getId());
        verify(clientRepository, times(1)).findByClientId(clientRequestDTO.getClientId());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent client")
    void testUpdateClient_NotFound() {
        // Given
        when(clientRepository.findByClientId(clientRequestDTO.getClientId())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            clientService.updateClient(clientRequestDTO);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(clientRepository, times(1)).findByClientId(clientRequestDTO.getClientId());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should delete client successfully (soft delete)")
    void testDeleteClient_Success() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // When
        ClientResponseDTO result = clientService.deleteClient(1L);

        // Then
        assertNotNull(result);
        assertEquals("false", client.getState()); // Verificar soft delete
        verify(clientRepository, times(1)).findById(1L);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent client")
    void testDeleteClient_NotFound() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            clientService.deleteClient(999L);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(clientRepository, times(1)).findById(999L);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should get client by id successfully")
    void testGetClient_Success() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        // When
        ClientResponseDTO result = clientService.getClient(1L);

        // Then
        assertNotNull(result);
        assertEquals(client.getId(), result.getId());
        assertEquals(client.getName(), result.getName());
        // Password no se incluye en ClientResponseDTO por seguridad
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when getting non-existent client")
    void testGetClient_NotFound() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            clientService.getClient(999L);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(clientRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get all clients successfully")
    void testGetAllClients_Success() {
        // Given
        Client client2 = new Client();
        client2.setId(2L);
        client2.setName("Marianela Montalvo");
        client2.setClientId("CLI002");
        
        List<Client> clients = Arrays.asList(client, client2);
        when(clientRepository.findAll()).thenReturn(clients);

        // When
        List<ClientResponseDTO> result = clientService.getAllClients();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("José Lema", result.get(0).getName());
        assertEquals("Marianela Montalvo", result.get(1).getName());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no clients exist")
    void testGetAllClients_EmptyList() {
        // Given
        when(clientRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<ClientResponseDTO> result = clientService.getAllClients();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(clientRepository, times(1)).findAll();
    }
}
