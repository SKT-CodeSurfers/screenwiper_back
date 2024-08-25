package com.example.screenwiper.dto;

import lombok.Data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AIAnalysisResponseDto {
    private String title;
    private String address;
    private List<String> operatingHours;
    private List<Event> list;
    private String summary;
    private String photoName;
    private String photoUrl;

    // 추가된 필드
    private Long categoryId;
    private String categoryName;

    @Getter
    @Setter
    public static class Event {
        private String name;
        private String date;
    }
}
