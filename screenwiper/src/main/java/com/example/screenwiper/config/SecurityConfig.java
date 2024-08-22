package com.example.screenwiper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .anyRequest().permitAll()  // 모든 요청을 인증 없이 접근 가능
                )
                .csrf(csrf -> csrf.disable());  // CSRF 비활성화 (필요에 따라 설정)

        return http.build();
    }
}

