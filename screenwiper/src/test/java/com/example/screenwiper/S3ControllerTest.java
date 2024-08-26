package com.example.screenwiper;

import com.example.screenwiper.controller.S3Controller;
import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.dto.AIAnalysisResponseWrapperDto;
import com.example.screenwiper.dto.AIAnalysisResponseDto;
import com.example.screenwiper.dto.ApiResponse;
import com.example.screenwiper.dto.KakaoCoordinate;
import com.example.screenwiper.dto.request.AnalyzeRequestDto;
import com.example.screenwiper.service.KakaoMapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class S3ControllerTest {

    @Mock
    private S3Uploader s3Uploader;

    @Mock
    private KakaoMapService kakaoMapService;

    @InjectMocks
    private S3Controller s3Controller;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(s3Controller).build();
    }

    @Test
    public void testAnalyzeImage() throws Exception {
        // Given
        String filePath = "/Users/jordy/Desktop/t1.png";  // 이미지 파일 경로
        File file = new File(filePath);
        FileInputStream input = new FileInputStream(file);

        MockMultipartFile mockFile = new MockMultipartFile(
                "files", file.getName(), MediaType.IMAGE_PNG_VALUE, input
        );

        String imageUrl = "https://screen-s3-bucket.s3.ap-northeast-2.amazonaws.com/test_img3.jpeg";

        // Mock된 AIAnalysisResponseDto 생성 및 설정
        AIAnalysisResponseDto aiAnalysisResponseDto = new AIAnalysisResponseDto();
        aiAnalysisResponseDto.setCategoryId(1L);
        aiAnalysisResponseDto.setOperatingHours(Collections.emptyList());

        AIAnalysisResponseWrapperDto aiResponseWrapper = new AIAnalysisResponseWrapperDto();
        aiResponseWrapper.setData(Collections.singletonList(aiAnalysisResponseDto));

        TextData mockTextData = new TextData();
        mockTextData.setPhotoId(1L);

        // Mock S3Uploader 동작 설정
        given(s3Uploader.upload(any(String.class))).willReturn(imageUrl);
        given(s3Uploader.sendS3FileToAImodel(Collections.singletonList(imageUrl))).willReturn(aiResponseWrapper);
        given(s3Uploader.saveTextData(any(Long.class), any(AIAnalysisResponseWrapperDto.class)))
                .willReturn(List.of(mockTextData));
        /*
        // When
        MvcResult result = mockMvc.perform(multipart("/api/v1/images/analyze")
                        .file(mockFile)
                        .param("memberId", "1000"))
                .andExpect(status().isOk())
                .andReturn();

         */

        String imageUrls = "https://screen-s3-bucket.s3.ap-northeast-2.amazonaws.com/test_img3.jpeg";

        // 여러 개의 이미지 URL을 배열로 지정
        List<String> imageUrls2 = Arrays.asList(
                "https://screen-s3-bucket.s3.ap-northeast-2.amazonaws.com/test_img3.jpeg"
        );

        // AI 모델에 전송하여 분석 결과 받기
        AIAnalysisResponseWrapperDto aiResponseWrapper2 = s3Uploader.sendS3FileToAImodel(imageUrls2);
        System.out.println("aiResponseWrapper2: " + aiResponseWrapper2);

        // 데이터베이스에 결과 저장
        List<TextData> savedTextDataList = s3Uploader.saveTextData(10000L, aiResponseWrapper2);
        System.out.println("savedTextDataList: " + savedTextDataList);

        // 카카오 맵 API 코드 x, y 가져오기
        KakaoCoordinate coordinate = kakaoMapService.getCoordinateFromAddress(savedTextDataList.getFirst().getAddress());
        System.out.println("coordinate: " + coordinate);

        // Response 출력
        // String responseContent = result.getResponse().getContentAsString();
        // System.out.println("Response: " + responseContent);

        // Clean up
        input.close();
    }
}
