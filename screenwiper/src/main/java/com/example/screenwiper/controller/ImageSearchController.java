//package com.example.screenwiper.controller;
//
//import com.example.screenwiper.service.TextDataService;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.*;
//import java.net.*;
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/images")
//public class ImageSearchController {
//
//    @Value("${google.vision.api.key}")
//    private String googleVisionApiKey;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    private TextDataService textDataService; // TextDataService 추가
//
//    @GetMapping("/detect/{photoId}")
//    public ResponseEntity<Map<String, Object>> detectImage(@PathVariable Long photoId) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            // 1. TextData에서 photoUrl 가져오기
//            String photoUrl = textDataService.getPhotoUrlById(photoId);
//            if (photoUrl == null || photoUrl.isEmpty()) {
//                response.put("success", "False");
//                response.put("message", "Photo URL not found.");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            }
//
//            // 2. 이미지를 서버로 다운로드 (CORS 문제 회피)
//            URL url = new URL(photoUrl); // photoUrl을 사용
//            InputStream in = url.openStream();
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            byte[] buffer = new byte[4096];
//            int length;
//            while ((length = in.read(buffer)) != -1) {
//                out.write(buffer, 0, length);
//            }
//            byte[] imageBytes = out.toByteArray();
//
//            // 3. 이미지를 Base64로 인코딩
//            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
//
//            // 4. Vision API 호출 (TEXT_DETECTION 사용하여 텍스트 추출)
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            // JSON 바디 작성 (TEXT_DETECTION 사용)
//            String body = "{"
//                    + "\"requests\": [{"
//                    + "\"image\": {\"content\": \"" + encodedImage + "\"},"
//                    + "\"features\": [{\"type\": \"TEXT_DETECTION\"}]"
//                    + "}]"
//                    + "}";
//
//            HttpEntity<String> entity = new HttpEntity<>(body, headers);
//
//            // 5. Vision API 호출 (API 키를 URL 파라미터로 전달)
//            String visionApiUrl = "https://vision.googleapis.com/v1/images:annotate?key=" + googleVisionApiKey;
//
//            // 6. API 요청 보내기
//            ResponseEntity<String> visionResponse = restTemplate.exchange(visionApiUrl, HttpMethod.POST, entity, String.class);
//
//            // Vision API 응답에서 텍스트 추출
//            String visionApiResponse = visionResponse.getBody();
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(visionApiResponse);
//            JsonNode textAnnotationsNode = jsonNode.get("responses").get(0).get("textAnnotations");
//
//            // 추출된 텍스트 (첫 번째 텍스트 항목 사용)
//            String extractedText = "";
//            if (textAnnotationsNode != null && textAnnotationsNode.size() > 0) {
//                extractedText = textAnnotationsNode.get(0).get("description").asText();
//            }
//
//            // 텍스트 추출 실패한 경우
//            if (extractedText.isEmpty()) {
//                response.put("success", "False");
//                response.put("message", "Text extraction failed.");
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//            }
//
//            // 7. 추출된 텍스트로 이미지 검색 (Google Custom Search API 사용)
//            String customSearchApiKey = "YOUR_CUSTOM_SEARCH_API_KEY";
//            String cx = "YOUR_CUSTOM_SEARCH_ENGINE_ID"; // Custom Search Engine ID
//
//            // Google Custom Search API URL
//            String apiUrl = "https://www.googleapis.com/customsearch/v1?q=" + extractedText + "&cx=" + cx + "&key=" + customSearchApiKey + "&searchType=image";
//
//            ResponseEntity<String> searchResponse = restTemplate.exchange(apiUrl, HttpMethod.GET, null, String.class);
//
//            // JSON 응답에서 이미지 URLs 추출
//            String searchResult = searchResponse.getBody();
//            JsonNode searchJsonNode = objectMapper.readTree(searchResult);
//            JsonNode items = searchJsonNode.get("items");
//
//            List<String> imageUrls = new ArrayList<>();
//            for (JsonNode item : items) {
//                imageUrls.add(item.get("link").asText());  // 이미지 URL
//            }
//
//            // 결과 반환
//            response.put("success", "True");
//            response.put("message", "Similar images retrieved successfully");
//            response.put("similarImages", imageUrls);
//            return ResponseEntity.ok(response);
//
//        } catch (IOException e) {
//            response.put("success", "False");
//            response.put("message", "Image download or Vision API call failed: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//}

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
    private TextDataService textDataService; // TextDataService 추가

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

            // 2. 이미지를 서버로 다운로드
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

            // 4. Vision API 호출
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = "{"
                    + "\"requests\": [{"
                    + "\"image\": {\"content\": \"" + encodedImage + "\"},"
                    + "\"features\": [{\"type\": \"WEB_DETECTION\"}]"
                    + "}]"
                    + "}";

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            // 5. Vision API 호출 (API 키를 URL 파라미터로 전달)
            String visionApiUrl = "https://vision.googleapis.com/v1/images:annotate?key=" + googleVisionApiKey;

            // 6. API 요청 보내기
            ResponseEntity<String> visionResponse = restTemplate.exchange(visionApiUrl, HttpMethod.POST, entity, String.class);

            // Vision API 응답에서 유사 이미지 URL 추출
            String visionApiResponse = visionResponse.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(visionApiResponse);
            JsonNode webDetectionNode = jsonNode.get("responses").get(0).get("webDetection");

            List<Map<String, String>> similarImages = new ArrayList<>();
            List<Map<String, String>> recommendImages = new ArrayList<>();

            // 5. visuallySimilarImages 추가
            JsonNode visuallySimilarImages = webDetectionNode.get("visuallySimilarImages");
            if (visuallySimilarImages != null) {
                for (JsonNode image : visuallySimilarImages) {
                    Map<String, String> img = new HashMap<>();
                    img.put("imageUrl", image.get("url").asText());
                    similarImages.add(img);
                }
            }

            // 6. pagesWithMatchingImages 추가
            JsonNode pagesWithMatchingImages = webDetectionNode.get("pagesWithMatchingImages");
            if (pagesWithMatchingImages != null) {
                for (JsonNode page : pagesWithMatchingImages) {
                    String pageTitle = page.has("pageTitle") ? page.get("pageTitle").asText() : "Matching Page";
                    for (JsonNode image : page.get("partialMatchingImages")) {
                        Map<String, String> img = new HashMap<>();
                        img.put("imageUrl", image.get("url").asText());
                        img.put("title", pageTitle);
                        recommendImages.add(img);
                    }
                }
            }

            if (similarImages.isEmpty()) {
                response.put("success", "False");
                response.put("message", "No similar images found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("success", "True");
            response.put("message", "Similar images retrieved successfully");
            response.put("images", similarImages);
            response.put("recommendImages", recommendImages);
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
