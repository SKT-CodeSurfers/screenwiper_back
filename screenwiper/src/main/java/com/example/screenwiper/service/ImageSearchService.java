package com.example.screenwiper.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.vision.v1.*;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageSearchService {

    /**
     * Google Vision API를 사용하여 이미지 URL을 통해 유사한 이미지들을 검색합니다.
     *
     * @param imageUrl 비교할 이미지의 URL
     * @return 유사한 이미지들의 URL 목록
     * @throws IOException
     */
    public List<String> getSimilarImageUrls(String imageUrl) throws IOException {
        WebDetection webDetection = getWebDetectionFromUrl(imageUrl);

        List<String> similarImageUrls = new ArrayList<>();

        // 웹 감지 정보에서 비슷한 이미지들의 URL을 가져오기
        if (webDetection != null) {
            // FullMatchingImages에서 이미지 URL 추출
            webDetection.getFullMatchingImagesList().forEach(webImage ->
                    similarImageUrls.add(webImage.getUrl())
            );

            // PagesWithMatchingImages에서 웹 페이지 URL 추출
            webDetection.getPagesWithMatchingImagesList().forEach(webPage ->
                    similarImageUrls.add(webPage.getUrl())
            );
        }

        return similarImageUrls;
    }

    /**
     * 웹 감지 정보를 가져오는 메서드
     *
     * @param imageUrl 웹 감지할 이미지의 URL
     * @return WebDetection 객체
     * @throws IOException
     */
    public WebDetection getWebDetectionFromUrl(String imageUrl) throws IOException {
        // 인증 정보를 설정하여 ImageAnnotatorClient 생성
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(
                        ServiceAccountCredentials.fromStream(new FileInputStream("/Users/song/Downloads/beaming-team-441315-p0-9e995b87d158.json"))
                ))
                .build();

        try (ImageAnnotatorClient visionClient = ImageAnnotatorClient.create(settings)) {
            // Vision API 요청을 위한 이미지 설정
            Image img = Image.newBuilder().setSource(ImageSource.newBuilder().setImageUri(imageUrl)).build();

            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(Feature.newBuilder().setType(Feature.Type.WEB_DETECTION))
                    .setImage(img)
                    .build();

            BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(Lists.newArrayList(request));
            AnnotateImageResponse imageResponse = response.getResponsesList().get(0);

            // 요청 실패 시 예외 처리
            if (imageResponse.hasError()) {
                System.err.println("Error: " + imageResponse.getError().getMessage());
                return null;
            }

            return imageResponse.getWebDetection();
        }
    }
}
