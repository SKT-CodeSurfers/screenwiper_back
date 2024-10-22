package com.example.screenwiper.dto;

import javax.validation.constraints.NotBlank;

public class RefreshTokenRequestDto {
    @NotBlank
    private String refreshToken;

    // 기본 생성자
    public RefreshTokenRequestDto() {}

    public RefreshTokenRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Getter와 Setter
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
