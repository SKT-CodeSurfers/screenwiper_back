package com.example.screenwiper.dto;

import lombok.Data;
import java.util.List;

@Data
public class AIAnalysisResponseDto {
    private String title;
    private String address;
    private String operatingHours;
    private List<Event> list;
    private String summary;
    private String photoName;
    private String photoUrl;
    private String date;

    // 추가된 필드
    private Long categoryId;
    private String categoryName;

    @Data
    public static class Event {
        private String name;
        private String date;
    }
}
