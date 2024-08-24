package com.example.screenwiper;

import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.dto.AIAnalysisResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class S3UploaderTest {

    @Autowired
    private S3Uploader s3Uploader;

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
            String filePath ="/Users/jordy/Desktop/t2.png";
            String uploadName = s3Uploader.upload(filePath);
            // System.out.println(s3Uploader.sendS3FileToAImodel(uploadName));

            AIAnalysisResponseDto aiResponse = s3Uploader.sendS3FileToAImodel(uploadName);
            System.out.println("aiResponse = " + aiResponse);

            TextData savedData = s3Uploader.saveTextData(10000L, aiResponse);

            System.out.println(savedData);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


}
