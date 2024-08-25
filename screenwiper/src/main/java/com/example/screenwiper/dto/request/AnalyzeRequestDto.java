package com.example.screenwiper.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AnalyzeRequestDto {
    private List<String> imageUrls;

    // 생성자
    public AnalyzeRequestDto(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    // Getter 및 Setter
    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public String toString() {
        return "AnalyzeRequestDto{" +
                "imageUrls=" + imageUrls +
                '}';
    }
}
