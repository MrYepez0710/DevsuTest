package com.devsu.application.dto;

import com.devsu.domain.model.Client;

public class ClientMapper {
    
    /**
     * Convierte una entidad Client a ClientResponseDTO
     * Nota: El password NO se incluye por seguridad
     */
    public static ClientResponseDTO toClientResponseDTO(Client client) {
        return ClientResponseDTO.builder()
                .id(client.getId())
                .name(client.getName())
                .gender(client.getGender())
                .age(client.getAge())
                .idNumber(client.getIdNumber())
                .address(client.getAddress())
                .phone(client.getPhone())
                .clientId(client.getClientId())
                .state(client.getState())
                .build();
    }

    /**
     * Convierte un ClientRequestDTO a entidad Client
     * El id se asigna automáticamente por la base de datos
     */
    public static Client toClient(ClientRequestDTO clientRequestDTO) {
        Client client = new Client();
        client.setName(clientRequestDTO.getName());
        client.setGender(clientRequestDTO.getGender());
        client.setAge(clientRequestDTO.getAge());
        client.setIdNumber(clientRequestDTO.getIdNumber());
        client.setAddress(clientRequestDTO.getAddress());
        client.setPhone(clientRequestDTO.getPhone());
        client.setClientId(clientRequestDTO.getClientId());
        client.setPassword(clientRequestDTO.getPassword());
        client.setState(clientRequestDTO.getState());
        return client;
    }
    
    /**
     * Actualiza una entidad Client existente con datos de ClientRequestDTO
     * Útil para operaciones de UPDATE
     */
    public static void updateClientFromDTO(Client client, ClientRequestDTO dto) {
        client.setName(dto.getName());
        client.setGender(dto.getGender());
        client.setAge(dto.getAge());
        client.setIdNumber(dto.getIdNumber());
        client.setAddress(dto.getAddress());
        client.setPhone(dto.getPhone());
        client.setClientId(dto.getClientId());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            client.setPassword(dto.getPassword());
        }
        client.setState(dto.getState());
    }
}
