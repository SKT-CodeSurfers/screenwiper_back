package com.example.screenwiper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.http.HttpMethod.*;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/login-url", "/api/callback", "/api/member-info").permitAll()  // 인증 없이 접근 가능
                                .requestMatchers("http://15.164.115.105:8080/analyze_images").permitAll()  // 접근 허용
                                .requestMatchers(POST, "/api/v1/images/analyze").permitAll()
                                .requestMatchers(GET, "/api/photos/list").permitAll()
                                .requestMatchers(GET, "/api/photos/{photoId}").permitAll()   // GET 요청 허용
                                .requestMatchers(PUT, "/api/photos/{photoId}").permitAll()   // PUT 요청 허용
                                .requestMatchers(DELETE, "/api/photos/{photoId}").permitAll() // DELETE 요청 허용
                                .anyRequest().authenticated()  // 다른 모든 요청은 인증 필요
                )
                .csrf(csrf -> csrf.disable());  // CSRF 비활성화 (필요에 따라 설정)

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://15.164.115.105:8080")); // 외부 API 출처 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // 허용할 HTTP 메서드
        configuration.setAllowCredentials(true); //자격증명 허용 코드 (필요에 따라 지워도 괜찮음)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정 적용
        return source;
    }
}
