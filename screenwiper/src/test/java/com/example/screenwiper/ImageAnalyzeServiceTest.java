package com.example.screenwiper;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.screenwiper.domain.Category;
import com.example.screenwiper.domain.Member;
import com.example.screenwiper.domain.TextData;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        // MockitoAnnotations를 사용하여 Mock 객체 초기화
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAnalyzeImagesAndSave() throws IOException {
        // 1. 테스트에 사용할 파일 생성
        String filePath = "/Users/jordy/Desktop/workspace/test_img/test_img3.jpeg";
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "test_img", file.getName(), "image/jpeg", fileInputStream);

        // ImageAnalyzeRequestDto 객체를 생성하여 테스트에 사용할 파일 추가
        ImageAnalyzeRequestDto requestDto = new ImageAnalyzeRequestDto();
        requestDto.setFiles(List.of(mockMultipartFile));

        // 추가된 파일 정보를 출력
        List<MultipartFile> files = requestDto.getFiles();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile multipartFile : files) {
                System.out.println("File name: " + multipartFile.getName());
                System.out.println("Original file name: " + multipartFile.getOriginalFilename());
                System.out.println("File size: " + multipartFile.getSize() + " bytes");
                System.out.println("File content type: " + multipartFile.getContentType());
            }
        } else {
            System.out.println("No files found in requestDto.");
        }

        // 2. Mock 설정: S3 URL 반환
        String mockS3Url = "https://screen-s3-bucket.s3.ap-northeast-2.amazonaws.com/test_img.jpeg";
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(new URL(mockS3Url));

        // 3. Mock 설정: AI 분석 API 응답 설정
        AIAnalysisResponseDto mockAIResponse = new AIAnalysisResponseDto();
        mockAIResponse.setCategoryId(1L);
        mockAIResponse.setCategoryName("Test Category");
        mockAIResponse.setTitle("Test Title");
        mockAIResponse.setSummary("Test Summary");
        mockAIResponse.setOperatingHours(List.of(new String[]{"9 AM - 5 PM"}));  // 필드 설정 추가
        when(restTemplate.postForObject(anyString(), any(), eq(AIAnalysisResponseDto.class)))
                .thenReturn(mockAIResponse);

        // 4. Mock 설정: Repository에서 반환할 데이터 설정
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(new Member()));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));

        // 5. Mock 설정: TextDataRepository에서 저장된 데이터를 반환하도록 설정
        TextData mockTextData = new TextData();
        mockTextData.setPhotoId(1L);
        when(textDataRepository.save(any(TextData.class))).thenReturn(mockTextData);

        // 6. 실제 테스트 실행
        // 첫 번째 이미지 파일 생성
        FileInputStream fis1 = new FileInputStream("/Users/jordy/Desktop/workspace/test_img/test_img3.jpeg");
        MultipartFile multipartFile1 = new MockMultipartFile(
                "file",                        // 파라미터 이름
                "test_img3.jpeg",              // 파일 이름
                "image/jpeg",                  // 파일 타입
                fis1                           // 파일 데이터
        );

        // 두 번째 이미지 파일 생성
        FileInputStream fis2 = new FileInputStream("/Users/jordy/Desktop/workspace/test_img/test_img2.jpeg");
        MultipartFile multipartFile2 = new MockMultipartFile(
                "file",                        // 파라미터 이름
                "test_img2.jpeg",              // 파일 이름
                "image/jpeg",                  // 파일 타입
                fis2                           // 파일 데이터
        );

        // 리스트에 이미지 파일 추가
        List<MultipartFile> multipartFileList = new ArrayList<>();
        multipartFileList.add(multipartFile1);
        multipartFileList.add(multipartFile2);

        // List<ResponseDto> responses = imageAnalyzeService.analyzeImagesAndSave(multipartFileList, "static");
/*
        // 7. 테스트 결과 검증
        assertNotNull(responses);  // 응답이 null이 아닌지 확인
        assertFalse(responses.isEmpty());  // 응답 리스트가 비어있지 않은지 확인
        assertEquals(mockS3Url, responses.get(0).getPhotoUrl());  // 반환된 URL이 예상된 URL과 일치하는지 확인

        // 리소스 정리
        FileSystemUtils.deleteRecursively(file);

 */
    }
}
