package com.example.screenwiper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtTokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
