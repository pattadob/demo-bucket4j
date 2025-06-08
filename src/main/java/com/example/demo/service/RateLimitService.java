package com.example.demo.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimitService {

    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

    public Bucket resolveBucket(String key) {
        return cache.get(key, this::newBucket);
    }

    private Bucket newBucket(String key) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(100, Duration.ofMinutes(1))))
                .build();
    }
}
