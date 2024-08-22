package com.example.screenwiper;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.screenwiper.domain.Category;
import com.example.screenwiper.domain.Member;
import com.example.screenwiper.dto.AIAnalysisResponseDto;
import com.example.screenwiper.dto.ImageAnalyzeRequestDto;
import com.example.screenwiper.dto.ResponseDto;
import com.example.screenwiper.repository.CategoryRepository;
import com.example.screenwiper.repository.MemberRepository;
import com.example.screenwiper.repository.TextDataRepository;
import com.example.screenwiper.service.ImageAnalyzeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ImageAnalyzeServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private TextDataRepository textDataRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ImageAnalyzeService imageAnalyzeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAnalyzeImagesAndSave() throws IOException {
        // 로컬 파일 경로
        String filePath = "/Users/jordy/Desktop/workspace/test_img/test_img2.jpeg";

        // 파일을 MockMultipartFile로 변환
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", file.getName(), "image/jpeg", fileInputStream);

        // ImageAnalyzeRequestDto 생성
        ImageAnalyzeRequestDto requestDto = new ImageAnalyzeRequestDto();
        requestDto.setFiles(List.of(mockMultipartFile));

        // Mock AmazonS3 동작 설정
        String mockS3Url = "https://screen-s3-bucket.s3.ap-northeast-2.amazonaws.com/test_img.jpeg";
        URL url = new URL(mockS3Url);
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(url);

        // AmazonS3의 putObject 모킹 (예외가 발생하지 않도록)
        doAnswer(invocation -> null).when(amazonS3).putObject(any(PutObjectRequest.class));

        // Mock AI Model API 응답
        AIAnalysisResponseDto mockAIResponse = new AIAnalysisResponseDto();
        mockAIResponse.setCategoryId(1L);
        mockAIResponse.setCategoryName("Test Category");
        mockAIResponse.setTitle("Test Title");
        mockAIResponse.setSummary("Test Summary");

        when(restTemplate.postForObject(anyString(), any(), eq(AIAnalysisResponseDto.class)))
                .thenReturn(mockAIResponse);

        // Mock MemberRepository
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(new Member()));

        // Mock CategoryRepository
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));

        // Mock TextDataRepository 저장 동작 설정
        when(textDataRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // ImageAnalyzeService 호출
        List<ResponseDto> responses = imageAnalyzeService.analyzeImagesAndSave(requestDto);

        // 테스트 결과 확인
        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(mockS3Url, responses.get(0).getPhotoUrl());
        responses.forEach(response -> {
            System.out.println("Generated S3 URL: " + response.getPhotoUrl());
            System.out.println("AI Model Response: " + response.getTitle());
        });

        // 리소스 정리
        FileSystemUtils.deleteRecursively(file);
    }
}
