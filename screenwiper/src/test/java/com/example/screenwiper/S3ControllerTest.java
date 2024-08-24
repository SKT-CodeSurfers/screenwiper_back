package com.example.screenwiper;

import com.example.screenwiper.S3Uploader;
import com.example.screenwiper.controller.S3Controller;
import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.dto.AIAnalysisResponseDto;
import com.example.screenwiper.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class S3ControllerTest {

    @Mock
    private S3Uploader s3Uploader;

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
        String filePath = "/Users/jordy/Desktop/t1.png"; // 이미지 파일 경로
        File file = new File(filePath);
        FileInputStream input = new FileInputStream(file);

        MockMultipartFile mockFile = new MockMultipartFile(
                "files", file.getName(), MediaType.IMAGE_PNG_VALUE, input
        );

        String imageUrl = "https://screen-s3-bucket.s3.ap-northeast-2.amazonaws.com/t1.png";

        AIAnalysisResponseDto aiResponse = new AIAnalysisResponseDto();
        aiResponse.setOperatingHours(Collections.emptyList());  // 빈 리스트로 설정

        TextData mockTextData = new TextData();
        mockTextData.setPhotoId(1L);

        // Mock S3Uploader behavior
        given(s3Uploader.upload(any(String.class))).willReturn(imageUrl);
        given(s3Uploader.sendS3FileToAImodel(eq(imageUrl))).willReturn(aiResponse);
        given(s3Uploader.saveTextData(any(Long.class), eq(aiResponse))).willReturn(mockTextData);

        // When
        mockMvc.perform(multipart("/api/v1/images/analyze")
                        .file(mockFile)
                        .param("memberId", "1000"))  // memberId를 1000으로 설정
                .andExpect(status().isOk());

        // Clean up
        input.close();
    }
}
