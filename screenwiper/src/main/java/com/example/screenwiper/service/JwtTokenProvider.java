package com.example.screenwiper.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long validityInMilliseconds = 86400000;  // 24시간

    // Access Token 생성 시 member_id 추가
    public String createAccessToken(String email, Long memberId) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("member_id", memberId);  // member_id 추가
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    // Refresh Token 생성 시 member_id 추가
    public String createRefreshToken(String email, Long memberId) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("member_id", memberId);  // member_id 추가
        Date now = new Date();
        Date validity = new Date(now.getTime() + (validityInMilliseconds * 7));  // 7일간 유효

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    // 토큰에서 member_id 추출
    public Long getMemberIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();
        return claims.get("member_id", Long.class);
    }
}
