package com.example.screenwiper.service;

import com.example.screenwiper.dto.KakaoProfileDto;
import com.example.screenwiper.dto.KakaoTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoApiService {

    private final RestTemplate restTemplate;

    // application.properties에서 값 주입
    @Value("${kakao.client_id}")
    private String kakaoClientId;  // 카카오 REST API 키

    @Value("${kakao.redirect_uri}")
    private String kakaoRedirectUri;  // 카카오 리다이렉트 URI

    private final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final String KAKAO_PROFILE_URL = "https://kapi.kakao.com/v2/user/me";

    public KakaoProfileDto getKakaoUserProfile(String authorizationCode) {
        // 1. 카카오로부터 Access Token 요청
        String kakaoAccessToken = getKakaoAccessToken(authorizationCode);

        // 2. 카카오 사용자 정보 가져오기
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 요청 정보 로깅
        System.out.println("Requesting Kakao user profile with Access Token: " + kakaoAccessToken);
        System.out.println("Request headers: " + headers.toString());

        // 카카오 사용자 정보 요청
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                KAKAO_PROFILE_URL, HttpMethod.GET, entity, new ParameterizedTypeReference<Map<String, Object>>() {});

        // 응답 코드 및 본문 로깅
        System.out.println("Response status: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody());

        Map<String, Object> body = response.getBody();
        KakaoProfileDto kakaoProfileDto = new KakaoProfileDto();

        if (body != null) {
            kakaoProfileDto.setId(String.valueOf(body.get("id"))); // 사용자 ID 설정
            Map<String, Object> properties = (Map<String, Object>) body.get("properties");

            if (properties != null) {
                kakaoProfileDto.setNickname((String) properties.get("nickname")); // 사용자 닉네임 설정
            }

            Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");

            if (kakaoAccount != null) {
                kakaoProfileDto.setEmail((String) kakaoAccount.get("email")); // 사용자 이메일 설정
                kakaoProfileDto.setBirthdate((String) kakaoAccount.get("birthdate")); // 사용자 생일 설정 (null 가능)
                kakaoProfileDto.setName((String) properties.get("name")); // 사용자 이름 설정 (사용자 동의 시 제공)
            }
        }

        // KakaoProfileDto 결과 로깅
        System.out.println("KakaoProfileDto: " + kakaoProfileDto.toString());

        return kakaoProfileDto;
    }

    private String getKakaoAccessToken(String authorizationCode) {
        // 인가 코드를 이용해 카카오에서 Access Token 요청
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);  // 카카오 REST API 키
        params.add("redirect_uri", kakaoRedirectUri);  // 카카오 리다이렉트 URI
        params.add("code", authorizationCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 정보 로깅
        System.out.println("Requesting Kakao access token with params: " + params.toString());
        System.out.println("Request headers: " + headers.toString());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenDto> response = restTemplate.exchange(
                KAKAO_TOKEN_URL, HttpMethod.POST, entity, KakaoTokenDto.class);

        // 응답 코드 및 본문 로깅
        System.out.println("Response status: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody());

        // Access Token 로깅
        KakaoTokenDto tokenDto = response.getBody();
        if (tokenDto != null) {
            String accessToken = tokenDto.getAccess_token();  // Access Token 가져오기
            System.out.println("Access Token: " + accessToken);
        } else {
            System.out.println("No token received.");
        }

        return tokenDto != null ? tokenDto.getAccess_token() : null;  // Access Token 반환
    }
}
