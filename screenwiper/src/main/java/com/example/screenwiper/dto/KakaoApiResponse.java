package com.example.screenwiper.dto;

import lombok.Data;
import java.util.List;

@Data
public class KakaoApiResponse {
    private List<Document> documents;

    @Data
    public static class Document {
        private String placeName; // 장소 이름
        private String addressName; // 주소 이름
        private String x; // 경도
        private String y; // 위도
    }
}