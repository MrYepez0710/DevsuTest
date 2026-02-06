package com.devsu.infrastructure.client;

import com.devsu.infrastructure.cache.dto.ClientCacheDTO;
import com.devsu.infrastructure.cache.service.ClientCacheService;
import com.devsu.infrastructure.client.dto.ClientDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * REST Client for ClientApp communication
 * Implements REST calls with fallback for cache miss scenarios
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClientServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    private final ClientCacheService clientCacheService;
    
    @Value("${clientapp.url}")
    private String clientAppUrl;
    
    /**
     * Get client information by clientId from ClientApp
     * First checks cache, then makes REST call if cache miss
     */
    public ClientCacheDTO getClientByClientId(String clientId) {
        log.info("Getting client info for: {}", clientId);
        
        // 1. Try cache first
        ClientCacheDTO cachedClient = clientCacheService.getClient(clientId);
        if (cachedClient != null) {
            log.info("Client found in cache: {}", clientId);
            return cachedClient;
        }
        
        // 2. Cache miss - make REST call to ClientApp
        log.warn("Cache MISS for client: {}, calling ClientApp REST API", clientId);
        
        try {
            WebClient webClient = webClientBuilder.baseUrl(clientAppUrl).build();
            
            ClientDTO clientDTO = webClient.get()
                .uri("/clientes/by-clientId/{clientId}", clientId)
                .retrieve()
                .bodyToMono(ClientDTO.class)
                .timeout(Duration.ofSeconds(5))
                .block();
            
            if (clientDTO != null) {
                log.info("Successfully retrieved client {} from ClientApp", clientId);
                
                // Convert to cache DTO and save in cache
                ClientCacheDTO cacheDTO = ClientCacheDTO.builder()
                    .id(clientDTO.getId())
                    .clientId(clientDTO.getClientId())
                    .name(clientDTO.getName())
                    .gender(clientDTO.getGender())
                    .age(clientDTO.getAge())
                    .idNumber(clientDTO.getIdNumber())
                    .address(clientDTO.getAddress())
                    .phone(clientDTO.getPhone())
                    .state(clientDTO.getState())
                    .build();
                
                // Save in cache for future requests
                clientCacheService.saveClient(cacheDTO);
                
                return cacheDTO;
            }
            
        } catch (Exception e) {
            log.error("Unable to retrieve client information for clientId: {}", clientId, e);
            // No usar fallback - lanzar excepción si cliente no existe
            throw new com.devsu.domain.exception.ResourceNotFoundException(
                "Client with clientId " + clientId + " not found"
            );
        }
        
        // Si llegamos aquí, clientDTO es null
        log.error("Client with clientId {} does not exist in the system", clientId);
        throw new com.devsu.domain.exception.ResourceNotFoundException(
            "Client with clientId " + clientId + " not found"
        );
    }
}
