package com.devsu.infrastructure.messaging.listener;

import com.devsu.infrastructure.cache.dto.ClientCacheDTO;
import com.devsu.infrastructure.cache.service.ClientCacheService;
import com.devsu.infrastructure.messaging.event.ClientEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener for client events from ClientApp
 * Updates Redis cache when events are received
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClientEventListener {
    
    private final ClientCacheService clientCacheService;
    
    /**
     * Listen to client events from RabbitMQ
     * Updates cache based on event type
     */
    @RabbitListener(queues = "${rabbitmq.queue.client-events}")
    public void handleClientEvent(ClientEvent event) {
        log.info("Received client event: {} for client: {} at {}", 
                event.getEventType(), 
                event.getData().getClientId(),
                event.getTimestamp());
        
        try {
            switch (event.getEventType()) {
                case "CLIENT_CREATED":
                    handleClientCreated(event);
                    break;
                case "CLIENT_UPDATED":
                    handleClientUpdated(event);
                    break;
                case "CLIENT_DEACTIVATED":
                    handleClientDeactivated(event);
                    break;
                case "CLIENT_DELETED":
                    handleClientDeleted(event);
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing client event: {}", event.getEventId(), e);
        }
    }
    
    /**
     * Handle client created event
     * Adds new client to cache
     */
    private void handleClientCreated(ClientEvent event) {
        log.info("Processing CLIENT_CREATED event for: {}", event.getData().getClientId());
        
        ClientCacheDTO cacheDTO = mapEventToCache(event);
        clientCacheService.saveClient(cacheDTO);
        
        log.info("Client added to cache: {}", event.getData().getClientId());
    }
    
    /**
     * Handle client updated event
     * Updates existing client in cache
     */
    private void handleClientUpdated(ClientEvent event) {
        log.info("Processing CLIENT_UPDATED event for: {}", event.getData().getClientId());
        
        ClientCacheDTO cacheDTO = mapEventToCache(event);
        clientCacheService.saveClient(cacheDTO);
        
        log.info("Client updated in cache: {}", event.getData().getClientId());
    }
    
    /**
     * Handle client deactivated event
     * Updates client state in cache
     */
    private void handleClientDeactivated(ClientEvent event) {
        log.info("Processing CLIENT_DEACTIVATED event for: {}", event.getData().getClientId());
        log.warn("Client {} has been deactivated. State: {}", 
                event.getData().getClientId(), 
                event.getData().getState());
        
        ClientCacheDTO cacheDTO = mapEventToCache(event);
        clientCacheService.saveClient(cacheDTO);
        
        log.info("Client deactivated in cache: {}", event.getData().getClientId());
    }
    
    /**
     * Handle client deleted event
     * Removes client from cache
     */
    private void handleClientDeleted(ClientEvent event) {
        log.info("Processing CLIENT_DELETED event for: {}", event.getData().getClientId());
        
        clientCacheService.deleteClient(event.getData().getClientId());
        
        log.info("Client deleted from cache: {}", event.getData().getClientId());
    }
    
    /**
     * Map event data to cache DTO
     */
    private ClientCacheDTO mapEventToCache(ClientEvent event) {
        ClientEvent.ClientEventData data = event.getData();
        
        return ClientCacheDTO.builder()
            .id(data.getId())
            .clientId(data.getClientId())
            .name(data.getName())
            .gender(data.getGender())
            .age(data.getAge())
            .idNumber(data.getIdNumber())
            .address(data.getAddress())
            .phone(data.getPhone())
            .state(data.getState())
            .build();
    }
}
