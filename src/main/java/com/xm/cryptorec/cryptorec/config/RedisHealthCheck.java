package com.xm.cryptorec.cryptorec.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisHealthCheck {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @PostConstruct
    public void testRedisConnection() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            if (connection.ping() != null) {
                System.out.println("Redis is available");
            }
        } catch (Exception e) {
            System.out.println("Redis connection error: " + e.getMessage());
        }
    }
}

