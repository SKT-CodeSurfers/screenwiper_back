package com.example.screenwiper.dto.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AnalyzeRequestDto {
    private String imageUrl;

    // 생성자
    public AnalyzeRequestDto(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
