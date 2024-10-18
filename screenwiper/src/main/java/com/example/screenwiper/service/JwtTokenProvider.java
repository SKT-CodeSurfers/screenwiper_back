package com.example.screenwiper.service;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenProvider {

    private final String secretKey = "yourSecretKey";  // 실제 비밀 키 사용
    private final long validityInMilliseconds = 3600000;  // 1시간

    public String createAccessToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(String email) {
        // refresh 토큰은 더 긴 유효 기간을 가짐
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + (validityInMilliseconds * 24 * 7));  // 7일간 유효

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
