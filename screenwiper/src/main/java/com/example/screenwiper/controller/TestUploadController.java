package com.example.screenwiper.controller;

import com.example.screenwiper.dto.TestUploadRequestDto;
import com.example.screenwiper.service.ImageAnalyzeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestUploadController {

    private final ImageAnalyzeService imageAnalyzeService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadImages(TestUploadRequestDto requestDto) {
        try {
            List<String> imageUrls = imageAnalyzeService.uploadImagesAndGetUrls(requestDto);
            return ResponseEntity.ok(imageUrls);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
