package com.example.screenwiper.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.example.screenwiper.dto.AIAnalysisResponseDto;
import com.example.screenwiper.dto.ImageAnalyzeRequestDto;
import com.example.screenwiper.dto.ResponseDto;
import com.example.screenwiper.domain.Category;
import com.example.screenwiper.domain.Member;
import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.dto.TestUploadRequestDto;
import com.example.screenwiper.repository.CategoryRepository;
import com.example.screenwiper.repository.MemberRepository;
import com.example.screenwiper.repository.TextDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j

@Service
@RequiredArgsConstructor
public class ImageAnalyzeService {

    private final AmazonS3 amazonS3;
    private final TextDataRepository textDataRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Value("${ai.model.api.url}")
    private String aiModelApiUrl;

    public List<ResponseDto> analyzeImagesAndSave(ImageAnalyzeRequestDto requestDto) throws IOException {
        // 현재 로그인한 사용자 정보를 가져옵니다.
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Long memberId = Long.parseLong(authentication.getName()); // 사용자 ID를 토큰에서 가져온다고 가정
        Long memberId = 1L; // 테스트를 위한 임시 값, 실제 사용 시에는 적절한 값으로 설정해야 합니다.


        List<MultipartFile> files = requestDto.getFiles();
        return files.stream().map(file -> {

            try {
                // String imageUrl = uploadImageToS3(file);
                String imageUrl = uploadImageToS3(file);

                AIAnalysisResponseDto aiResponse = analyzeImage(imageUrl);
                log.info("Response ");
                log.info(aiResponse.toString());

                TextData savedData = saveTextData(memberId, aiResponse, file.getOriginalFilename(), imageUrl);

                ResponseDto responseDto = new ResponseDto(aiResponse);
                responseDto.setPhotoId(savedData.getPhotoId());
                responseDto.setMemberId(memberId);
                responseDto.setDate(savedData.getDate());


                return responseDto;

            } catch (IOException e) {
                log.error("Service");
                e.printStackTrace();
                throw new RuntimeException("Failed to upload or analyze image", e);
            }
        }).collect(Collectors.toList());
    }
/*
    private String uploadImageToS3(MultipartFile image) throws IOException {
        File file = convertMultipartFileToFile(image);
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        if (!file.delete()) {
            System.err.println("Failed to delete temporary file: " + file.getAbsolutePath());
        }
        return amazonS3.getUrl(bucketName, fileName).toString();
    }
*/
    private String uploadImageToS3(MultipartFile image) throws IOException {
        // MultipartFile을 File로 변환
        File file = convertMultipartFileToFile(image);
        // S3에 파일 업로드
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        /*
        if (!file.delete()) {
            System.err.println("Failed to delete temporary file: " + file.getAbsolutePath());
        }
        */
        String url = amazonS3.getUrl(bucketName, fileName).toString();
        return url;
    }


    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }

    private AIAnalysisResponseDto analyzeImage(String imageUrl) {
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디 설정 (form-data 형식)
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("imageUrl", imageUrl);

        // 요청 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // POST 요청 전송
            ResponseEntity<AIAnalysisResponseDto> responseEntity = restTemplate.exchange(
                    aiModelApiUrl + "/analyze_image",
                    HttpMethod.POST,
                    requestEntity,
                    AIAnalysisResponseDto.class
            );


            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                log.info("[RESPONSE]");
                log.info(responseEntity.getBody().toString());
                return responseEntity.getBody();
            } else {
                throw new RuntimeException("Failed to analyze image, status code: " + responseEntity.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while analyzing image: " + e.getMessage(), e);
        }
    }

    private TextData saveTextData(Long memberId, AIAnalysisResponseDto aiResponse, String photoName, String photoUrl) {
        TextData textData = new TextData();

        Optional<Member> memberOptional = memberRepository.findById(memberId);
        Member member = memberOptional.orElseThrow(() -> new RuntimeException("Member not found"));
        textData.setMember(member);

        Optional<Category> categoryOptional = categoryRepository.findById(aiResponse.getCategoryId());
        Category category = categoryOptional.orElseThrow(() -> new RuntimeException("Category not found"));
        textData.setCategory(category);

        textData.setCategoryName(aiResponse.getCategoryName());
        textData.setTitle(aiResponse.getTitle());
        textData.setAddress(aiResponse.getAddress());
        textData.setOperatingHours(aiResponse.getOperatingHours());
        textData.setList(aiResponse.getList().stream()
                .map(event -> event.getName() + ": " + event.getDate())
                .collect(Collectors.toList()));
        textData.setSummary(aiResponse.getSummary());
        textData.setPhotoName(photoName);
        textData.setPhotoUrl(photoUrl);
        textData.setDate(aiResponse.getDate());

        return textDataRepository.save(textData);
    }

    public List<String> uploadImagesAndGetUrls(TestUploadRequestDto requestDto) throws IOException {
        List<MultipartFile> files = requestDto.getFiles();
        return files.stream().map(file -> {
            try {
                return uploadImageToS3(file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }).collect(Collectors.toList());
    }
}
