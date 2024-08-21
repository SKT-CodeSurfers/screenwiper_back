package com.example.screenwiper.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ImageAnalyzeRequestDto {
    private List<MultipartFile> files; // 여러 장의 이미지 파일
}
