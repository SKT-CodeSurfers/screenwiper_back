package com.example.screenwiper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoTokenRequestDto {
    private String authorizationCode;  // 인가코드
}
