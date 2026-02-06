package com.devsu.infrastructure.config;

import org.springframework.boot.actuate.autoconfigure.data.redis.RedisReactiveHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to disable Redis Reactive Health Indicator
 * We only need standard Redis health check, not reactive
 */
@Configuration
@EnableAutoConfiguration(exclude = {
    RedisReactiveHealthContributorAutoConfiguration.class
})
public class HealthIndicatorConfig {
    // This class disables the reactive Redis health indicator
    // that was trying to connect to localhost instead of the redis service
}
