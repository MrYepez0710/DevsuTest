package com.devsu.infrastructure.cache.service;

import com.devsu.infrastructure.cache.dto.ClientCacheDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for managing client cache in Redis
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientCacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${cache.client.ttl}")
    private long cacheTtl;
    
    @Value("${cache.client.prefix}")
    private String cachePrefix;
    
    /**
     * Get client from cache
     * Returns null if not found (cache miss)
     */
    public ClientCacheDTO getClient(String clientId) {
        String key = cachePrefix + clientId;
        
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            
            if (cached != null) {
                log.info("Cache HIT for client: {}", clientId);
                return (ClientCacheDTO) cached;
            } else {
                log.warn("Cache MISS for client: {}", clientId);
                return null;
            }
        } catch (Exception e) {
            log.error("Error getting client from cache: {}", clientId, e);
            return null;
        }
    }
    
    /**
     * Save or update client in cache
     */
    public void saveClient(ClientCacheDTO client) {
        String key = cachePrefix + client.getClientId();
        
        try {
            redisTemplate.opsForValue().set(key, client, cacheTtl, TimeUnit.SECONDS);
            log.info("Cache UPDATED for client: {} (TTL: {} seconds)", client.getClientId(), cacheTtl);
        } catch (Exception e) {
            log.error("Error saving client to cache: {}", client.getClientId(), e);
        }
    }
    
    /**
     * Delete client from cache
     */
    public void deleteClient(String clientId) {
        String key = cachePrefix + clientId;
        
        try {
            redisTemplate.delete(key);
            log.info("Cache DELETED for client: {}", clientId);
        } catch (Exception e) {
            log.error("Error deleting client from cache: {}", clientId, e);
        }
    }
    
    /**
     * Get client with fallback
     * Returns fallback DTO if cache miss
     */
    public ClientCacheDTO getClientWithFallback(String clientId) {
        ClientCacheDTO client = getClient(clientId);
        
        if (client != null) {
            return client;
        }
        
        // Fallback: return temporary DTO
        log.info("Using fallback for client: {}", clientId);
        return ClientCacheDTO.builder()
            .clientId(clientId)
            .name("Cliente " + clientId)
            .state("UNKNOWN")
            .build();
    }
}
