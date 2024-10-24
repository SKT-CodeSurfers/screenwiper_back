package com.example.screenwiper.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    public Long extractMemberId(String token) {
        System.out.println("JwtUtil - extractMemberId : START");
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))  // UTF-8로 고정
                    .parseClaimsJws(token)
                    .getBody();
            Long memberId = claims.get("member_id", Long.class);
            System.out.println("Extracted memberId from token: " + memberId);  // 로그 출력
            return memberId;
        } catch (Exception e) {
            System.err.println("Error extracting memberId from token: " + e.getMessage());  // 에러 로그 출력
            throw e;
        }
    }
}
