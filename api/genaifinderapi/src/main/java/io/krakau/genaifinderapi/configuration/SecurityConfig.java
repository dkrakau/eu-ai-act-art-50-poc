package io.krakau.genaifinderapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 *
 * @author Dominik
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Value("${spring.security.allowed.localhost}")
    public String SECURITY_LOCALHOST;
    @Value("${spring.security.allowed.ip}")
    public String SECURITY_IP;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                // Restrict /create endpoints to specific IPs
                .requestMatchers("/create/**")
                .access(this::isAuthorizedHost)
                // All other endpoints are accessible
                .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    private AuthorizationDecision isAuthorizedHost(Supplier<Authentication> authentication,
            RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        String requestUri = request.getRequestURI();

        Logger.getLogger(SecurityConfig.class.getName()).log(Level.INFO, "Access attempt to: " + requestUri + " from IP: " + getClientIpAddress(request));

        String clientIp = getClientIpAddress(request);
        Set<String> allowedIps = Set.of(
                SECURITY_LOCALHOST,
                SECURITY_IP
        );

        boolean isAuthorized = allowedIps.contains(clientIp);

        if (!isAuthorized) {
            Logger.getLogger(SecurityConfig.class.getName()).log(Level.WARNING, "Unauthorized access attempt to " + requestUri + " from IP: " + clientIp);
        }

        return new AuthorizationDecision(isAuthorized);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        // Handle X-Forwarded-For header for proxy/load balancer scenarios
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Take the first IP in the chain (original client)
            return xForwardedFor.split(",")[0].trim();
        }

        // Handle X-Real-IP header
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        // Fallback to remote address
        return request.getRemoteAddr();
    }

}
