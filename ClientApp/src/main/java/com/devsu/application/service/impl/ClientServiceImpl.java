package com.devsu.application.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsu.application.dto.ClientMapper;
import com.devsu.application.dto.ClientRequestDTO;
import com.devsu.application.dto.ClientResponseDTO;
import com.devsu.application.service.ClientService;
import com.devsu.domain.exception.BusinessException;
import com.devsu.domain.exception.ResourceNotFoundException;
import com.devsu.domain.model.Client;
import com.devsu.domain.repository.ClientRepository;
import com.devsu.infrastructure.messaging.publisher.ClientEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepository clientRepository;
    private final ClientEventPublisher clientEventPublisher;
    
    /**
     * Crea un nuevo cliente en el sistema
     * @param clientRequestDTO Datos del cliente a crear
     * @return ClientResponseDTO con los datos del cliente creado
     * @throws BusinessException si el clientId ya existe
     */
    @Override
    @Transactional
    public ClientResponseDTO createClient(ClientRequestDTO clientRequestDTO) {
        log.info("Creating new client with clientId: {}", clientRequestDTO.getClientId());
        
        // Validar que el clientId no exista
        if (clientRepository.findByClientId(clientRequestDTO.getClientId()).isPresent()) {
            log.error("Client with clientId {} already exists", clientRequestDTO.getClientId());
            throw new BusinessException("Client with clientId " + clientRequestDTO.getClientId() + " already exists");
        }
        
        // Validar que el idNumber no exista
        if (clientRepository.findByIdNumber(clientRequestDTO.getIdNumber()).isPresent()) {
            log.error("Client with idNumber {} already exists", clientRequestDTO.getIdNumber());
            throw new BusinessException("Client with idNumber " + clientRequestDTO.getIdNumber() + " already exists");
        }
        
        // Convertir DTO a entidad y guardar
        Client client = ClientMapper.toClient(clientRequestDTO);
        Client savedClient = clientRepository.save(client);
        
        // Publicar evento de cliente creado
        clientEventPublisher.publishClientCreated(savedClient);
        
        log.info("Client created successfully with id: {}", savedClient.getId());
        return ClientMapper.toClientResponseDTO(savedClient);
    }

    /**
     * Actualiza un cliente existente
     * @param clientRequestDTO Datos del cliente a actualizar (debe incluir el id)
     * @return ClientResponseDTO con los datos actualizados
     * @throws ResourceNotFoundException si el cliente no existe
     */
    @Override
    @Transactional
    public ClientResponseDTO updateClient(ClientRequestDTO clientRequestDTO){
        log.info("Updating client with clientId: {}", clientRequestDTO.getClientId());
        
        // Buscar cliente por clientId
        Client existingClient = clientRepository.findByClientId(clientRequestDTO.getClientId())
                .orElseThrow(() -> {
                    log.error("Client with clientId {} not found", clientRequestDTO.getClientId());
                    return new ResourceNotFoundException("Client with clientId " + clientRequestDTO.getClientId() + " not found");
                });
        
        // Guardar estado anterior
        String previousState = existingClient.getState();
        
        // Actualizar los datos del cliente
        ClientMapper.updateClientFromDTO(existingClient, clientRequestDTO);
        Client updatedClient = clientRepository.save(existingClient);
        
        // Publicar evento según el cambio
        if ("INACTIVO".equalsIgnoreCase(updatedClient.getState()) && !"INACTIVO".equalsIgnoreCase(previousState)) {
            clientEventPublisher.publishClientDeactivated(updatedClient);
        } else {
            clientEventPublisher.publishClientUpdated(updatedClient, previousState);
        }
        
        log.info("Client updated successfully with id: {}", updatedClient.getId());
        return ClientMapper.toClientResponseDTO(updatedClient);
    }

    /**
     * Elimina un cliente del sistema (soft delete cambiando estado)
     * @param clientId ID del cliente a eliminar
     * @return ClientResponseDTO con los datos del cliente eliminado
     * @throws ResourceNotFoundException si el cliente no existe
     */
    @Override
    @Transactional
    public ClientResponseDTO deleteClient(Long clientId){
        log.info("Deleting client with id: {}", clientId);
        
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("Client with id {} not found", clientId);
                    return new ResourceNotFoundException("Client with id " + clientId + " not found");
                });
        
        // Soft delete: cambiar estado a inactivo
        client.setState("false");
        Client deletedClient = clientRepository.save(client);
        
        // Publicar evento de cliente eliminado
        clientEventPublisher.publishClientDeleted(deletedClient);
        
        log.info("Client soft deleted successfully with id: {}", clientId);
        return ClientMapper.toClientResponseDTO(deletedClient);
    }

    /**
     * Obtiene un cliente por su ID
     * @param clientId ID del cliente a buscar
     * @return ClientResponseDTO con los datos del cliente
     * @throws ResourceNotFoundException si el cliente no existe
     */
    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO getClient(Long clientId){
        log.info("Fetching client with id: {}", clientId);
        
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("Client with id {} not found", clientId);
                    return new ResourceNotFoundException("Client with id " + clientId + " not found");
                });
        
        return ClientMapper.toClientResponseDTO(client);
    }

    /**
     * Obtiene un cliente por su clientId (para comunicación entre microservicios)
     * @param clientId ClientId del cliente a buscar
     * @return ClientResponseDTO con los datos del cliente
     * @throws ResourceNotFoundException si el cliente no existe
     */
    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO getClientByClientId(String clientId){
        log.info("Fetching client by clientId: {}", clientId);
        
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> {
                    log.error("Client with clientId {} not found", clientId);
                    return new ResourceNotFoundException("Client with clientId " + clientId + " not found");
                });
        
        return ClientMapper.toClientResponseDTO(client);
    }

    /**
     * Obtiene todos los clientes del sistema
     * @return Lista de ClientResponseDTO con todos los clientes
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClientResponseDTO> getAllClients(){
        log.info("Fetching all clients");
        
        List<Client> clients = clientRepository.findAll();
        
        log.info("Found {} clients", clients.size());
        return clients.stream()
                .map(ClientMapper::toClientResponseDTO)
                .collect(Collectors.toList());
    }
    
}
