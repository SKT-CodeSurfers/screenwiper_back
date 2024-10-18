package com.example.screenwiper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoTokenDto {
    private String accessToken;  // Access Token
    private String tokenType;    // Token Type
    private long expiresIn;      // Token Expiration Time
    private String refreshToken;  // Refresh Token
    private long refreshTokenExpiresIn;  // Refresh Token Expiration Time
}
