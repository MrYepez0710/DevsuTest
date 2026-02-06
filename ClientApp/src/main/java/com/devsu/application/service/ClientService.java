package com.devsu.application.service;

import java.util.List;

import com.devsu.application.dto.ClientRequestDTO;
import com.devsu.application.dto.ClientResponseDTO;

public interface ClientService {
    public ClientResponseDTO createClient(ClientRequestDTO clientRequestDTO);
    public ClientResponseDTO updateClient(ClientRequestDTO clientRequestDTO);
    public ClientResponseDTO deleteClient(Long clientId);
    public ClientResponseDTO getClient(Long clientId);
    public ClientResponseDTO getClientByClientId(String clientId);
    public List<ClientResponseDTO> getAllClients();
}
