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
                                .requestMatchers("/api/login-url", "/api/callback").permitAll()  // 인증 없이 접근 가능
                                .requestMatchers("/api/photos/list").permitAll()  // /api/photos/list 엔드포인트에 대한 접근 허용
                                .anyRequest().authenticated()  // 다른 모든 요청은 인증 필요
                )
                .csrf(csrf -> csrf.disable());  // CSRF 비활성화 (필요에 따라 설정)

        return http.build();
    }
}
