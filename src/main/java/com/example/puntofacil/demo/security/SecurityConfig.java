package com.example.puntofacil.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("🛡️ DEBUG: Configurando SecurityFilterChain - PERMITIR TODO");
        
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // ✅ PERMITIR ABSOLUTAMENTE TODO
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.disable())  // Deshabilitar login de Spring Security
            .logout(logout -> logout.disable()) // Deshabilitar logout de Spring Security
            .httpBasic(basic -> basic.disable()); // Deshabilitar autenticación básica

        System.out.println("✅ DEBUG: Security configurado - TODO PERMITIDO");
        return http.build();
    }
}