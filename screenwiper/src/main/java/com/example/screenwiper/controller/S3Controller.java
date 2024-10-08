package com.example.screenwiper.controller;

import com.example.screenwiper.S3Uploader;
import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.dto.AIAnalysisResponseWrapperDto;
import com.example.screenwiper.dto.ResponseDto;
import com.example.screenwiper.dto.ApiResponse;
import com.example.screenwiper.dto.KakaoCoordinate;
import com.example.screenwiper.service.KakaoMapService;
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
import java.util.stream.Collectors;
import java.util.Collections;



@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Uploader s3Uploader;
    private final KakaoMapService kakaoMapService; // KakaoMapService 주입

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse> analyzeImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("memberId") Long memberId) {
        try {
            // 이미지 URL을 저장할 리스트
            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile file : files) {
                // 파일을 로컬에 저장
                File localFile = convertMultipartFileToFile(file);

                // 파일을 S3에 업로드하고 URL을 가져옴
                String imageUrl = s3Uploader.upload(localFile.getAbsolutePath());
                imageUrls.add(imageUrl);

                // 로컬 파일 삭제 (옵션)
                localFile.delete();
            }

            // AI 모델에 전송하여 분석
            // List<ResponseDto> responseList = s3Uploader.processImagesAndSaveResults(imageUrls, memberId);

            // AI 모델에 전송하여 분석 결과 받기
            AIAnalysisResponseWrapperDto aiResponseWrapper = s3Uploader.sendS3FileToAImodel(imageUrls);

            // 데이터베이스에 결과 저장
            List<TextData> savedTextDataList = s3Uploader.saveTextData(memberId, aiResponseWrapper);

            // 카카오 맵 API 코드 x, y 가져오기
            // KakaoCoordinate coordinate = kakaoMapService.getCoordinateFromAddress(savedTextDataList.getFirst().getAddress());
            // System.out.println("coordinate: " + coordinate);

            // ResponseDto 리스트로 변환
            List<ResponseDto> responseList = savedTextDataList.stream()
                    .map(textData -> {
                        ResponseDto responseDto = new ResponseDto();
                        responseDto.setPhotoId(textData.getPhotoId());
                        responseDto.setMemberId(memberId);
                        responseDto.setCategoryId(textData.getCategory().getId());
                        responseDto.setCategoryName(textData.getCategoryName());
                        responseDto.setDate(LocalDateTime.now().toString());
                        responseDto.setTitle(textData.getTitle());
                        responseDto.setAddress(textData.getAddress());
                        responseDto.setOperatingHours(textData.getOperatingHours());
                        responseDto.setList(textData.getList() != null ? textData.getList() : Collections.emptyList());
                        responseDto.setSummary(textData.getSummary());
                        responseDto.setPhotoName(textData.getPhotoName());
                        responseDto.setPhotoUrl(textData.getPhotoUrl());
                        // responseDto.setXCoordinate();
                        // responseDto.setYCoordinate();
                        // 주소를 이용해 좌표 가져오기
                        try {
                            KakaoCoordinate coordinate = kakaoMapService.getCoordinateFromAddress(textData.getAddress());
                            responseDto.setXCoordinate(Double.toString(coordinate.getX())); // double을 String으로 변환
                            responseDto.setYCoordinate(Double.toString(coordinate.getY())); // double을 String으로 변환
                        } catch (Exception e) {
                            log.error("Failed to get coordinates for address: " + textData.getAddress(), e);
                            responseDto.setXCoordinate(null); // 좌표를 찾지 못한 경우 null 처리
                            responseDto.setYCoordinate(null);
                        }
                        return responseDto;
                    })
                    .collect(Collectors.toList());

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
