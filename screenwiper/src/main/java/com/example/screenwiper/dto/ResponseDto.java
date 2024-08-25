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
    private List<AIAnalysisResponseDto.Event> list;
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
        this.list = aiResponse.getList();
        this.summary = aiResponse.getSummary();
        this.photoName = aiResponse.getPhotoName();
        this.photoUrl = aiResponse.getPhotoUrl();
        // `date` will be set in the controller
    }

    // List<String>을 List<AIAnalysisResponseDto.Event>로 변환할 수 있는 메서드
    public void setList(List<String> list) {
        if (list == null) {
            this.list = Collections.emptyList(); // 혹은 null로 설정
        } else {
            this.list = list.stream()
                    .map(str -> {
                        AIAnalysisResponseDto.Event event = new AIAnalysisResponseDto.Event();
                        // 변환 로직 작성, 예를 들어:
                        // event.setName(str);
                        return event;
                    })
                    .collect(Collectors.toList());
        }
    }

}
