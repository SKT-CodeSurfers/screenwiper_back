package com.example.screenwiper.controller;

import com.example.screenwiper.dto.JwtTokenResponseDto;
import com.example.screenwiper.dto.KakaoTokenRequestDto;
import com.example.screenwiper.dto.RefreshTokenRequestDto;
import com.example.screenwiper.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/kakao/login")
    public ResponseEntity<JwtTokenResponseDto> kakaoLogin(@RequestBody KakaoTokenRequestDto tokenRequest) {
        JwtTokenResponseDto jwtTokenResponse = kakaoAuthService.kakaoLogin(tokenRequest.getAuthorizationCode());
        return ResponseEntity.ok(jwtTokenResponse);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<JwtTokenResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto request) { // DTO로 변경
        JwtTokenResponseDto jwtTokenResponse = kakaoAuthService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(jwtTokenResponse);
    }
}
