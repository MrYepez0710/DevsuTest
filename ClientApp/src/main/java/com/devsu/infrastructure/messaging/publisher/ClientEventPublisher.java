package com.devsu.infrastructure.messaging.publisher;

import com.devsu.domain.model.Client;
import com.devsu.infrastructure.messaging.event.ClientEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Publisher for client events to RabbitMQ
 * Publishes events when clients are created, updated, deactivated, or deleted
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClientEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${rabbitmq.exchange.client}")
    private String clientExchange;
    
    @Value("${rabbitmq.routing-key.client-created}")
    private String clientCreatedRoutingKey;
    
    @Value("${rabbitmq.routing-key.client-updated}")
    private String clientUpdatedRoutingKey;
    
    @Value("${rabbitmq.routing-key.client-deactivated}")
    private String clientDeactivatedRoutingKey;
    
    @Value("${rabbitmq.routing-key.client-deleted}")
    private String clientDeletedRoutingKey;
    
    /**
     * Publish client created event
     */
    public void publishClientCreated(Client client) {
        log.info("Publishing ClientCreated event for client: {}", client.getClientId());
        
        ClientEvent event = buildEvent("CLIENT_CREATED", client, null);
        rabbitTemplate.convertAndSend(clientExchange, clientCreatedRoutingKey, event);
        
        log.info("ClientCreated event published successfully for client: {}", client.getClientId());
    }
    
    /**
     * Publish client updated event
     */
    public void publishClientUpdated(Client client, String previousState) {
        log.info("Publishing ClientUpdated event for client: {}", client.getClientId());
        
        ClientEvent event = buildEvent("CLIENT_UPDATED", client, previousState);
        rabbitTemplate.convertAndSend(clientExchange, clientUpdatedRoutingKey, event);
        
        log.info("ClientUpdated event published successfully for client: {}", client.getClientId());
    }
    
    /**
     * Publish client deactivated event
     */
    public void publishClientDeactivated(Client client) {
        log.info("Publishing ClientDeactivated event for client: {}", client.getClientId());
        
        ClientEvent event = buildEvent("CLIENT_DEACTIVATED", client, "ACTIVO");
        rabbitTemplate.convertAndSend(clientExchange, clientDeactivatedRoutingKey, event);
        
        log.info("ClientDeactivated event published successfully for client: {}", client.getClientId());
    }
    
    /**
     * Publish client deleted event
     */
    public void publishClientDeleted(Client client) {
        log.info("Publishing ClientDeleted event for client: {}", client.getClientId());
        
        ClientEvent event = buildEvent("CLIENT_DELETED", client, client.getState());
        rabbitTemplate.convertAndSend(clientExchange, clientDeletedRoutingKey, event);
        
        log.info("ClientDeleted event published successfully for client: {}", client.getClientId());
    }
    
    /**
     * Build event object from client data
     */
    private ClientEvent buildEvent(String eventType, Client client, String previousState) {
        return ClientEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType(eventType)
            .timestamp(LocalDateTime.now())
            .data(ClientEvent.ClientEventData.builder()
                .id(client.getId())
                .clientId(client.getClientId())
                .name(client.getName())
                .gender(client.getGender())
                .age(client.getAge())
                .idNumber(client.getIdNumber())
                .address(client.getAddress())
                .phone(client.getPhone())
                .state(client.getState())
                .previousState(previousState)
                .build())
            .build();
    }
}
