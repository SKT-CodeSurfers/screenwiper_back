package com.example.screenwiper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoTokenDto {
    private String access_token;  // Access Token
    private String token_type;    // Token Type
    private long expires_in;      // Token Expiration Time
    private String refresh_token;  // Refresh Token
    private long refresh_token_expires_in;  // Refresh Token Expiration Time
}
