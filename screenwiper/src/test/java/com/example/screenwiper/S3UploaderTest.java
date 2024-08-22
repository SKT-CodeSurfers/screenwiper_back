package com.example.screenwiper;

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
            String filePath ="/Users/jordy/Desktop/tt.png";
            String uploadName = s3Uploader.upload(filePath);
            System.out.println("uploadName = " + uploadName);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

//    @Test
//    public void testRemove() {
//        try {
//            s3Uploader.removeS3File("test.png");
//        }catch(Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
}
