package com.example.demo.filter;

import com.example.demo.service.RateLimitService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class RateLimitFilter implements Filter {

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientKey = getClientKey(httpRequest);
        Bucket bucket = rateLimitService.resolveBucket(clientKey);

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().write("Rate limit exceeded");
        }
    }

    private String getClientKey(HttpServletRequest request) {
        // Try to get API key from header first
        String apiKey = request.getHeader("X-API-Key");
        if (StringUtils.hasText(apiKey)) {
            return "api_key:" + apiKey;
        }

        // Try to get user ID from header
        String userId = request.getHeader("X-User-ID");
        if (StringUtils.hasText(userId)) {
            return "user:" + userId;
        }

        // Fall back to IP address
        String clientIp = getClientIpAddress(request);
        return "ip:" + clientIp;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
