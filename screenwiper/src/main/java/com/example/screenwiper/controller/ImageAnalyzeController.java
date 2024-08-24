package com.example.screenwiper.controller;

import com.example.screenwiper.dto.ApiResponse;
import com.example.screenwiper.dto.ResponseDto;
import com.example.screenwiper.service.ImageAnalyzeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageAnalyzeController {

    private final ImageAnalyzeService imageAnalyzeService;

    @PostMapping("/analyzeimage")
    public ResponseEntity<ApiResponse> analyzeImages(
            @RequestParam("files") List<MultipartFile> files) {
        try {
            // 서비스에서 이미지 분석 및 데이터 저장 수행
            String dirName = "screenwiper";
            List<ResponseDto> responseList = imageAnalyzeService.analyzeImagesAndSave(files, dirName);

            // 성공 응답 생성
            return ResponseEntity.ok(new ApiResponse(true, "Created", responseList));
        } catch (IOException e) {
            log.error("Image analysis failed", e);

            // 오류 응답 생성
            return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
