package com.example.screenwiper;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.screenwiper.domain.Category;
import com.example.screenwiper.domain.Member;
import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.dto.AIAnalysisResponseDto;
import com.example.screenwiper.dto.request.AnalyzeRequestDto;
import com.example.screenwiper.repository.CategoryRepository;
import com.example.screenwiper.repository.MemberRepository;
import com.example.screenwiper.repository.TextDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;


import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class S3Uploader {

    private final AmazonS3 amazonS3Client;
    private final RestTemplate restTemplate;
    private final TextDataRepository textDataRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucket; // S3 버킷 이름

    @Value("${ai.model.api.url}")
    private String aiModelApiUrl; // AI 모델 주소

    // S3로 파일 업로드하기
    public String upload(String filePath) throws RuntimeException {
        File targetFile = new File(filePath);
        String uploadImageUrl = putS3(targetFile, targetFile.getName()); // S3로 업로드
        System.out.println("targetFile = " + targetFile);
        System.out.println("targetFile.getName() = " + targetFile.getName());
        System.out.println("uploadImageUrl = " + uploadImageUrl);
        removeOriginalFile(targetFile);
        return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) throws RuntimeException {
        System.out.println("putS3 uploadFile= " + uploadFile);
        System.out.println("putS3 fileName= " + fileName);
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName,
                uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // S3 업로드 후 원본 파일 삭제
    private void removeOriginalFile(File targetFile) {
        if (targetFile.exists() && targetFile.delete()) {
            log.info("File delete success");
        } else {
            log.info("fail to remove");
        }
    }

    public void removeS3File(String fileName) {
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, fileName);
        amazonS3Client.deleteObject(deleteObjectRequest);
    }

    public AIAnalysisResponseDto sendS3FileToAImodel(String imageUrl) {
        AnalyzeRequestDto request = new AnalyzeRequestDto(imageUrl);
        return restTemplate.postForObject(aiModelApiUrl + "/analyze_image", request, AIAnalysisResponseDto.class);
    }

    @Transactional
    public TextData saveTextData(Long memberId, AIAnalysisResponseDto aiResponse) {
        TextData textData = new TextData();

        // Member 및 Category를 조회하고 설정합니다.
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        Member member = memberOptional.orElseThrow(() -> new RuntimeException("Member not found"));
        textData.setMember(member);

        Optional<Category> categoryOptional = categoryRepository.findById(aiResponse.getCategoryId());
        Category category = categoryOptional.orElseThrow(() -> new RuntimeException("Category not found"));
        textData.setCategory(category);

        // 외래키는 그대로 두고 문자열 필드만 처리합니다.
        textData.setCategoryName(aiResponse.getCategoryName() != null ? aiResponse.getCategoryName() : "");
        textData.setTitle(aiResponse.getTitle() != null ? aiResponse.getTitle() : "");
        textData.setAddress(aiResponse.getAddress() != null ? aiResponse.getAddress() : "");
        textData.setOperatingHours(aiResponse.getOperatingHours() != null ? String.join(", ", aiResponse.getOperatingHours()) : "");
        textData.setList(aiResponse.getList() != null ? aiResponse.getList().stream()
                .map(event -> event.getName() != null ? event.getName() + ": " + (event.getDate() != null ? event.getDate() : "") : "")
                .collect(Collectors.toList()) : Collections.emptyList());
        textData.setSummary(aiResponse.getSummary() != null ? aiResponse.getSummary() : "");
        textData.setPhotoName(aiResponse.getPhotoName() != null ? aiResponse.getPhotoName() : "");
        textData.setPhotoUrl(aiResponse.getPhotoUrl() != null ? aiResponse.getPhotoUrl() : "");
        textData.setDate(String.valueOf(LocalDateTime.now()));

        return textDataRepository.save(textData);
    }


}
