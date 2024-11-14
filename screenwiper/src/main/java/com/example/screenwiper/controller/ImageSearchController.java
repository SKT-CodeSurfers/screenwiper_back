package com.example.screenwiper.controller;

import com.example.screenwiper.service.TextDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.util.*;

@RestController
@RequestMapping("/api/images")
public class ImageSearchController {

    @Value("${google.vision.api.key}")
    private String googleVisionApiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TextDataService textDataService;

    /**
     * Google Vision API를 사용하여 이미지와 관련된 데이터를 검색
     *
     * @param photoId 이미지의 고유 ID
     * @return 관련 이미지 URL 및 웹 페이지 정보
     */
    @GetMapping("/detect/{photoId}")
    public ResponseEntity<Map<String, Object>> detectImage(@PathVariable Long photoId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 1. TextDataService에서 photoId에 해당하는 photoUrl 가져오기
            String photoUrl = textDataService.getPhotoUrlById(photoId);
            if (photoUrl == null || photoUrl.isEmpty()) {
                response.put("success", "False");
                response.put("message", "Photo URL not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // 2. 이미지를 서버로 다운로드 (CORS 문제 회피)
            URL url = new URL(photoUrl);
            InputStream in = url.openStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            byte[] imageBytes = out.toByteArray();

            // 3. 이미지를 Base64로 인코딩
            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

            // 4. Vision API 호출 (WebDetection 사용)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // JSON 바디 작성
            String body = "{"
                    + "\"requests\": [{"
                    + "\"image\": {\"content\": \"" + encodedImage + "\"},"
                    + "\"features\": [{\"type\": \"WEB_DETECTION\"}]"
                    + "}]"
                    + "}";

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            // 5. Vision API 호출 (API 키를 URL 파라미터로 전달)
            String visionApiUrl = "https://vision.googleapis.com/v1/images:annotate?key=" + googleVisionApiKey;
            ResponseEntity<String> visionResponse = restTemplate.exchange(visionApiUrl, HttpMethod.POST, entity, String.class);

            // 6. Vision API 응답 처리
            String visionApiResponse = visionResponse.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(visionApiResponse);
            JsonNode webDetectionNode = jsonNode.get("responses").get(0).get("webDetection");

            // 7. 웹 페이지 정보 추출 (pagesWithMatchingImages)
            JsonNode pagesWithMatchingImages = webDetectionNode.get("pagesWithMatchingImages");
            List<Map<String, String>> pagesList = new ArrayList<>();
            if (pagesWithMatchingImages != null) {
                for (JsonNode page : pagesWithMatchingImages) {
                    Map<String, String> pageInfo = new HashMap<>();
                    pageInfo.put("url", page.has("url") ? page.get("url").asText() : null);
                    pageInfo.put("title", page.has("pageTitle") ? page.get("pageTitle").asText() : null);
                    pagesList.add(pageInfo);
                }
            }

            // 8. 유사 이미지 정보 추출 (visuallySimilarImages)
            JsonNode visuallySimilarImages = webDetectionNode.get("visuallySimilarImages");
            List<Map<String, String>> similarImagesList = new ArrayList<>();
            if (visuallySimilarImages != null) {
                for (JsonNode image : visuallySimilarImages) {
                    Map<String, String> imageInfo = new HashMap<>();
                    imageInfo.put("url", image.has("url") ? image.get("url").asText() : null);
                    // visuallySimilarImages에서는 title 제공 안 될 가능성이 높음
                    imageInfo.put("title", image.has("title") ? image.get("title").asText() : null);
                    similarImagesList.add(imageInfo);
                }
            }

            // 9. 결과 반환
            if (pagesList.isEmpty() && similarImagesList.isEmpty()) {
                response.put("success", "False");
                response.put("message", "No matching data found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("success", "True");
            response.put("message", "Matching data retrieved successfully.");
            response.put("pagesWithMatchingImages", pagesList);
            response.put("visuallySimilarImages", similarImagesList);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", "False");
            response.put("message", "Image download or Vision API call failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("success", "False");
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
