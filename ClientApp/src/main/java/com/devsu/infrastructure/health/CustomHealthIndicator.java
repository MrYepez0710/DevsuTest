package com.devsu.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Custom Health Indicator for ClientApp
 * Verifies application status and dependencies
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {
    
    private final DataSource dataSource;
    private final RabbitTemplate rabbitTemplate;
    
    @Override
    public Health health() {
        try {
            // Check database connection
            boolean dbHealthy = checkDatabase();
            
            // Check RabbitMQ connection
            boolean rabbitHealthy = checkRabbitMQ();
            
            // Build health response
            if (dbHealthy && rabbitHealthy) {
                return Health.up()
                    .withDetail("service", "ClientApp")
                    .withDetail("status", "All systems operational")
                    .withDetail("database", "Connected")
                    .withDetail("rabbitmq", "Connected")
                    .withDetail("version", "1.0.0")
                    .build();
            } else {
                return Health.down()
                    .withDetail("service", "ClientApp")
                    .withDetail("database", dbHealthy ? "Connected" : "Disconnected")
                    .withDetail("rabbitmq", rabbitHealthy ? "Connected" : "Disconnected")
                    .build();
            }
            
        } catch (Exception e) {
            log.error("Health check failed", e);
            return Health.down()
                .withDetail("service", "ClientApp")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
    
    private boolean checkDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            log.error("Database health check failed", e);
            return false;
        }
    }
    
    private boolean checkRabbitMQ() {
        try {
            rabbitTemplate.getConnectionFactory().createConnection().isOpen();
            return true;
        } catch (Exception e) {
            log.error("RabbitMQ health check failed", e);
            return false;
        }
    }
}
