package com.itbulls.nadine.spring.springbootdemo.config;


import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ➤ تفعيل CORS باستخدام Lambda DSL
            .cors(cors -> cors
                .configurationSource(corsConfigurationSource())
            )

            // ➤ إعداد قواعد التفويض (authorization)
            .authorizeHttpRequests(authorize -> authorize
            		.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/users/**", "/api/events/**").permitAll()
                .anyRequest().authenticated()
            )

            // ➤ تعطيل CSRF لتطبيق REST API
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.addAllowedOrigin("http://localhost:5173");
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        System.out.println("✅ CORS configuration loaded");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }



}
