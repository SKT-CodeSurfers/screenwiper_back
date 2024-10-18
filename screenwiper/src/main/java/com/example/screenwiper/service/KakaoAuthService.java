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
                    newMember.setBirthdate(LocalDate.parse(kakaoProfile.getBirthdate()));
                    newMember.setNickname(kakaoProfile.getNickname());
                    newMember.setEmail(kakaoProfile.getEmail());
                    newMember.setSignupDate(LocalDate.now());
                    newMember.setActive(true);
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
}
