package com.zorvyn.finance.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (Required for H2 Console to work)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Allow all requests for now so you can develop without 403 errors
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll()
                )

                // 3. Fix the "Frame" issue (H2 uses frames, which Spring blocks by default)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                // 4. Basic login support if needed
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
