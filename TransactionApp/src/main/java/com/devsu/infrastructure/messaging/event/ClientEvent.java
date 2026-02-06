package com.devsu.infrastructure.messaging.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event DTO for client operations
 * Received from ClientApp via RabbitMQ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String eventId;
    private String eventType;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private ClientEventData data;
    
    /**
     * Client data within the event
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientEventData implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private Long id;
        private String clientId;
        private String name;
        private String gender;
        private Integer age;
        private String idNumber;
        private String address;
        private String phone;
        private String state;
        private String previousState;
    }
}
