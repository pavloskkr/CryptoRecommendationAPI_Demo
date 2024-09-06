package com.xm.cryptorec.cryptorec.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    // store the bucket for each ip in a conc map
    private final Map<String, Bucket> ipBucketMap = new ConcurrentHashMap<>();

    @Bean
    public Map<String, Bucket> ipBucketMap() {
        return ipBucketMap;
    }

    @Bean
    public Bucket bucket() {
        Bandwidth limit = Bandwidth.builder().capacity(5).refillGreedy(5, Duration.ofMinutes(1)).build();
        return Bucket.builder().addLimit(limit).build();
    }
}
