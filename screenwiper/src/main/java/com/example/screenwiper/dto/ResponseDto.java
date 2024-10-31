package com.example.screenwiper.dto;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ResponseDto {
    private Long photoId;
    private Long memberId;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String address;
    private String operatingHours;
    private List<AIAnalysisResponseDto.Event> list; // List<Event>로 유지
    private String summary;
    private String photoName;
    private String photoUrl;
    private String date;
    private String xCoordinate;  // 경도 추가
    private String yCoordinate;  // 위도 추가

    public ResponseDto() {
        // 기본 생성자
    }

    public ResponseDto(AIAnalysisResponseDto aiResponse) {
        this.categoryId = aiResponse.getCategoryId();
        this.categoryName = aiResponse.getCategoryName();
        this.title = aiResponse.getTitle();
        this.address = aiResponse.getAddress();
        this.operatingHours = aiResponse.getOperatingHours().toString();
        this.list = aiResponse.getList(); // list를 AIAnalysisResponseDto에서 가져옴
        this.summary = aiResponse.getSummary();
        this.photoName = aiResponse.getPhotoName();
        this.photoUrl = aiResponse.getPhotoUrl();
        // `date` will be set in the controller
    }

    // List<AIAnalysisResponseDto.Event>을 List<String>으로 변환하는 메서드 추가
    public void setList(List<AIAnalysisResponseDto.Event> list) {
        if (list == null) {
            this.list = Collections.emptyList();
        } else {
            this.list = list; // List<Event>로 직접 설정
        }
    }

    // List<String>을 List<AIAnalysisResponseDto.Event>로 변환하는 메서드 추가
    public void setListFromStrings(List<String> eventNames) {
        if (eventNames == null) {
            this.list = Collections.emptyList();
        } else {
            this.list = eventNames.stream()
                    .map(name -> {
                        AIAnalysisResponseDto.Event event = new AIAnalysisResponseDto.Event();
                        event.setName(name); // Event 클래스에서 name 속성이 있다고 가정
                        return event;
                    })
                    .collect(Collectors.toList());
        }
    }
}
