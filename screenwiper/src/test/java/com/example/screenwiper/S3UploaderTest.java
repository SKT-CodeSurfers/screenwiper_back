package com.example.screenwiper;

import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.dto.AIAnalysisResponseDto;
import com.example.screenwiper.dto.AIAnalysisResponseWrapperDto;
import com.example.screenwiper.dto.KakaoCoordinate;
import com.example.screenwiper.dto.request.AnalyzeRequestDto;
import com.example.screenwiper.service.KakaoMapService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
public class S3UploaderTest {

    @Autowired
    private S3Uploader s3Uploader;
    @Autowired
    private KakaoMapService kakaoMapService;


    @Test
    public void testUpload() {
        try{
            String filePath ="/Users/jordy/Desktop/yy.png";
            String uploadName = s3Uploader.upload(filePath);
            System.out.println("uploadName = " + uploadName);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testRemove() {
        try {
            s3Uploader.removeS3File("test.png");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testAISendImgSaveData() {
        try {
            // 단일 파일 테스트
            // String filePath ="/Users/jordy/Desktop/test_img3.jpeg";
            // String uploadName = s3Uploader.upload(filePath);
            // System.out.println(s3Uploader.sendS3FileToAImodel(uploadName));
            // AIAnalysisResponseDto aiResponse = s3Uploader.sendS3FileToAImodel(uploadName);

            // 여러 개의 이미지 URL을 배열로 지정
            List<String> imageUrls = Arrays.asList(
                    "https://screen-s3-bucket.s3.ap-northeast-2.amazonaws.com/place2.png",
                    "https://screen-s3-bucket.s3.ap-northeast-2.amazonaws.com/place3.png"
            );
            // AnalyzeRequestDto requestDto = new AnalyzeRequestDto(imageUrls);

            // 여러 개의 이미지 URL을 사용하여 요청
            AIAnalysisResponseWrapperDto aiResponseWrapperResult = s3Uploader.sendS3FileToAImodel(imageUrls);
            System.out.println("aiResponseWrapperResult = " + aiResponseWrapperResult);

            // 카카오 맵 API 코드 x, y 가져오기
            // 첫 번째 address 값 추출
            String firstAddress = aiResponseWrapperResult.getData().stream()
                    .map(AIAnalysisResponseDto::getAddress)
                    .findFirst()
                    .orElse(null); // 없을 경우 null 반환

            if (firstAddress != null) {
                KakaoCoordinate coordinate = kakaoMapService.getCoordinateFromAddress(firstAddress);
                System.out.println("First Address: " + firstAddress + ", Coordinate: " + coordinate);
            } else {
                System.out.println("No address found in the response.");
            }

            //TextData savedData = (TextData) s3Uploader.saveTextData(10000L, aiResponse);
            //System.out.println("savedData = " + savedData);
            List<TextData> savedDataList = s3Uploader.saveTextData(10000L, aiResponseWrapperResult);
            savedDataList.forEach(savedData -> System.out.println("savedData = " + savedData));



        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


}
