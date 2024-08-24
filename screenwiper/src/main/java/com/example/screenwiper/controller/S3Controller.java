package com.example.screenwiper.controller;

import com.example.screenwiper.S3Uploader;
import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.dto.AIAnalysisResponseDto;
import com.example.screenwiper.dto.ApiResponse;
import com.example.screenwiper.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Uploader s3Uploader;

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse> analyzeImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("memberId") Long memberId) {
        try {
            List<ResponseDto> responseList = new ArrayList<>();

            for (MultipartFile file : files) {
                // 파일을 로컬에 저장
                File localFile = convertMultipartFileToFile(file);

                // 파일을 S3에 업로드하고 URL을 가져옴
                String imageUrl = s3Uploader.upload(localFile.getAbsolutePath());

                // 파일을 AI 모델에 전송하여 분석
                AIAnalysisResponseDto aiResponse = s3Uploader.sendS3FileToAImodel(imageUrl);

                // 분석 결과를 데이터베이스에 저장
                TextData savedTextData = s3Uploader.saveTextData(memberId, aiResponse);

                // ResponseDto 생성 및 설정
                ResponseDto responseDto = new ResponseDto(aiResponse);
                responseDto.setPhotoId(savedTextData.getPhotoId());
                responseDto.setMemberId(memberId);
                responseDto.setDate(LocalDateTime.now().toString());

                responseList.add(responseDto);
            }

            // 성공 응답 반환
            return ResponseEntity.ok(new ApiResponse(true, "Created", responseList));

        } catch (IOException e) {
            log.error("Image analysis failed", e);

            // 오류 응답 반환
            return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // MultipartFile을 File로 변환하는 헬퍼 메서드
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        file.transferTo(convertedFile);
        return convertedFile;
    }
}
