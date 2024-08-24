package com.example.screenwiper.dto;

import lombok.Data;
import java.util.List;

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
}
