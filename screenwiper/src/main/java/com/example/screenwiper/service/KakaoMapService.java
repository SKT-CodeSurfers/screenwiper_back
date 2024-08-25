package com.example.screenwiper.service;

import com.example.screenwiper.dto.KakaoCoordinate;
import com.example.screenwiper.dto.KakaoApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class KakaoMapService {

    private final RestTemplate restTemplate;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;  // application.properties에서 주입받은 API 키

    public KakaoCoordinate getCoordinateFromAddress(String address) {
        String url = UriComponentsBuilder.fromHttpUrl("https://dapi.kakao.com/v2/local/search/address.json")
                .queryParam("query", address)
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<KakaoApiResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, KakaoApiResponse.class);

        if (response.getBody() != null && !response.getBody().getDocuments().isEmpty()) {
            KakaoApiResponse.Document document = response.getBody().getDocuments().get(0);
            return new KakaoCoordinate(Double.parseDouble(document.getX()), Double.parseDouble(document.getY()));
        } else {
            throw new RuntimeException("Failed to get coordinates for address: " + address);
        }
    }


    public String getPlaceNameFromAddress(String address) {
        String url = UriComponentsBuilder.fromHttpUrl("https://dapi.kakao.com/v2/local/search/address.json")
                .queryParam("query", address)
                .build()
                .toUriString();

        // Authorization 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // API 호출
        ResponseEntity<KakaoApiResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, KakaoApiResponse.class);

        if (response.getBody() != null && !response.getBody().getDocuments().isEmpty()) {
            KakaoApiResponse.Document document = response.getBody().getDocuments().get(0);
            return document.getPlaceName(); // 장소 이름을 반환
        } else {
            throw new RuntimeException("Failed to get place name for address: " + address);
        }
    }
}
