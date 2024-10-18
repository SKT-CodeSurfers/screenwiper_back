/*
package com.example.screenwiper.service;

import com.example.screenwiper.domain.Token;
import com.example.screenwiper.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public void saveToken(Long memberId, String accessToken, String refreshToken) {
        Token token = new Token(memberId, accessToken, refreshToken);
        tokenRepository.save(token);
    }

    public void updateToken(Long memberId, String newAccessToken, String newRefreshToken) {
        // memberId로 토큰을 찾고 없으면 새로 생성
        Token token = tokenRepository.findByMemberId(memberId)
                .orElseGet(() -> new Token(memberId, newAccessToken, newRefreshToken));

        // 토큰 업데이트
        token.setAccessToken(newAccessToken);
        if (newRefreshToken != null) {
            token.setRefreshToken(newRefreshToken);  // refreshToken이 null이 아닌 경우에만 업데이트
        }

        tokenRepository.save(token);
    }


    public void deleteToken(Long memberId) {
        tokenRepository.deleteByMemberId(memberId);
    }
}
*/