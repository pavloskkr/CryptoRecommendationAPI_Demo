package com.xm.cryptorec.cryptorec.filter;

import com.xm.cryptorec.cryptorec.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class RateLimitFilter implements Filter{

    private final Map<String, Bucket> ipBucketMap;
    private final RateLimitConfig rateLimitConfig;

    @Autowired
    public RateLimitFilter(Map<String, Bucket> ipBucketMap, RateLimitConfig rateLimitConfig) {
        this.ipBucketMap = ipBucketMap;
        this.rateLimitConfig = rateLimitConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
            IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // get requester's ip
        String ip = httpServletRequest.getRemoteAddr();

        // get or create the bucket for the requester's ip
        Bucket bucket = ipBucketMap.computeIfAbsent(ip, k -> rateLimitConfig.bucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

            // Set content type and response body
            httpServletResponse.setContentType("application/json");
            PrintWriter writer = httpServletResponse.getWriter();
            writer.write("{\"timestamp\":\"" + LocalDateTime.now() + "\", " +
                    "\"status\":429, " +
                    "\"error\":\"Too Many Requests\", " +
                    "\"message\":\"Too many requests, please try again later\"}");
            writer.flush();        }
    }
}
