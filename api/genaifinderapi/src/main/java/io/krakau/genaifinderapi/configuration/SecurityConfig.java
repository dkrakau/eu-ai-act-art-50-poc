package io.krakau.genaifinderapi.configuration;

import io.krakau.genaifinderapi.component.EnvironmentVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author Dominik
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private EnvironmentVariables env;
    
    @Autowired
    public SecurityConfig(EnvironmentVariables env) {
        this.env = env;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(request -> {
                    // Allow AdminController only from specific hosts
                    if (request.getRequestURI().startsWith("/create")) {
                        return 
                               env.SECURITY_LOCALHOST.equals(request.getServerName()) || 
                               env.SECURITY_DOMAIN.equals(request.getServerName()) ||
                               env.SECURITY_IP.equals(request.getRemoteAddr());
                    }
                    // Other endpoints accessible from anywhere
                    return true;
                }).permitAll()
            ) 
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
    
}