package com.devsu.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration for ClientApp
 * Configures exchange, queues, and bindings for client events
 */
@Configuration
public class RabbitMQConfig {
    
    @Value("${rabbitmq.exchange.client}")
    private String clientExchange;
    
    @Value("${rabbitmq.queue.client-events}")
    private String clientEventsQueue;
    
    @Value("${rabbitmq.routing-key.client-created}")
    private String clientCreatedRoutingKey;
    
    @Value("${rabbitmq.routing-key.client-updated}")
    private String clientUpdatedRoutingKey;
    
    @Value("${rabbitmq.routing-key.client-deactivated}")
    private String clientDeactivatedRoutingKey;
    
    @Value("${rabbitmq.routing-key.client-deleted}")
    private String clientDeletedRoutingKey;
    
    /**
     * Topic Exchange for client events
     */
    @Bean
    public TopicExchange clientExchange() {
        return new TopicExchange(clientExchange);
    }
    
    /**
     * Queue for client events
     */
    @Bean
    public Queue clientEventsQueue() {
        return new Queue(clientEventsQueue, true);
    }
    
    /**
     * Binding for client created events
     */
    @Bean
    public Binding clientCreatedBinding() {
        return BindingBuilder
            .bind(clientEventsQueue())
            .to(clientExchange())
            .with(clientCreatedRoutingKey);
    }
    
    /**
     * Binding for client updated events
     */
    @Bean
    public Binding clientUpdatedBinding() {
        return BindingBuilder
            .bind(clientEventsQueue())
            .to(clientExchange())
            .with(clientUpdatedRoutingKey);
    }
    
    /**
     * Binding for client deactivated events
     */
    @Bean
    public Binding clientDeactivatedBinding() {
        return BindingBuilder
            .bind(clientEventsQueue())
            .to(clientExchange())
            .with(clientDeactivatedRoutingKey);
    }
    
    /**
     * Binding for client deleted events
     */
    @Bean
    public Binding clientDeletedBinding() {
        return BindingBuilder
            .bind(clientEventsQueue())
            .to(clientExchange())
            .with(clientDeletedRoutingKey);
    }
    
    /**
     * JSON Message Converter for serialization
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * RabbitTemplate with JSON converter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
