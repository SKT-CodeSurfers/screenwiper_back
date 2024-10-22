package com.example.screenwiper.service;

import com.example.screenwiper.domain.Member;
import com.example.screenwiper.domain.Token;
import com.example.screenwiper.dto.JwtTokenResponseDto;
import com.example.screenwiper.dto.KakaoProfileDto;
import com.example.screenwiper.repository.MemberRepository;
import com.example.screenwiper.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoApiService kakaoApiService;

    public JwtTokenResponseDto kakaoLogin(String authorizationCode) {
        // 1. 카카오 API에서 Access Token 및 유저 정보 가져오기
        KakaoProfileDto kakaoProfile = kakaoApiService.getKakaoUserProfile(authorizationCode);

        // 2. DB에서 이메일로 사용자 조회 (존재하지 않으면 회원가입)
        Member member = memberRepository.findByEmail(kakaoProfile.getEmail())
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setName(kakaoProfile.getName());
                    newMember.setBirthdate(kakaoProfile.getBirthdate() != null
                            ? LocalDate.parse(kakaoProfile.getBirthdate())
                            : null);
                    newMember.setNickname(kakaoProfile.getNickname());
                    newMember.setEmail(kakaoProfile.getEmail());
                    newMember.setSignupDate(LocalDate.now());
                    newMember.setEnabled(true);
                    return memberRepository.save(newMember);
                });

        // 3. JWT Access Token 및 Refresh Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        // 4. Token DB에 저장 (새로 생성하거나 기존 값 갱신)
        Token token = tokenRepository.findByMemberId(member.getId())
                .orElse(new Token());
        token.setMemberId(member.getId());
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        tokenRepository.save(token);

        // 5. JWT 토큰을 반환
        JwtTokenResponseDto jwtTokenResponse = new JwtTokenResponseDto();
        jwtTokenResponse.setAccessToken(accessToken);
        jwtTokenResponse.setRefreshToken(refreshToken);
        return jwtTokenResponse;
    }

    // Refresh Token을 사용해 새로운 Access Token 재발급
    public JwtTokenResponseDto refreshAccessToken(String refreshToken) {
        // 1. Refresh Token이 유효한지 검증
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);

            // 2. 유저가 존재하는지 확인
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 3. 새로운 Access Token 생성
            String newAccessToken = jwtTokenProvider.createAccessToken(email);

            // 4. 기존 토큰 업데이트
            Token token = tokenRepository.findByMemberId(member.getId())
                    .orElseThrow(() -> new RuntimeException("Token not found"));
            token.setAccessToken(newAccessToken);
            tokenRepository.save(token);

            // 5. 새로운 Access Token 반환
            JwtTokenResponseDto jwtTokenResponse = new JwtTokenResponseDto();
            jwtTokenResponse.setAccessToken(newAccessToken);
            jwtTokenResponse.setRefreshToken(refreshToken);
            return jwtTokenResponse;
        } else {
            throw new RuntimeException("Invalid Refresh Token");
        }
    }
}
