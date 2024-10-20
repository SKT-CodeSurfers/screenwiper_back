package com.example.screenwiper.controller;

import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.service.TextDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TextDataController {

    @Autowired
    private TextDataService textDataService;

    @GetMapping("/api/photos/list")
    public ResponseEntity<Map<String, Object>> getTextDataList(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TextData> textDataPage = textDataService.getTextDataList(type, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", "True");
        response.put("message", "GET LIST");

        List<Map<String, Object>> photos = textDataPage.getContent().stream().map(textData -> {
            Map<String, Object> photo = new HashMap<>();
            photo.put("photoId", textData.getPhotoId());
            photo.put("userId", textData.getMember() != null ? textData.getMember().getId() : null);
            photo.put("categoryId", textData.getCategory() != null ? textData.getCategory().getId() : null);
            photo.put("title", textData.getTitle());
            photo.put("address", textData.getAddress());
            photo.put("operatingHours", textData.getOperatingHours());
            photo.put("list", textData.getList().stream().map(event -> {
                Map<String, String> eventMap = new HashMap<>();
                eventMap.put("name", event);
                eventMap.put("date", ""); // 이 부분은 실제 이벤트 날짜로 채워야 합니다
                return eventMap;
            }).collect(Collectors.toList()));
            photo.put("summary", textData.getSummary());
            photo.put("photoName", textData.getPhotoName());
            photo.put("photoUrl", textData.getPhotoUrl());
            photo.put("date", textData.getDate());
            return photo;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("photos", photos);
        data.put("totalPages", textDataPage.getTotalPages());
        data.put("totalElements", textDataPage.getTotalElements());

        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/photos/{photoId}")
    public ResponseEntity<Map<String, Object>> getTextDataById(@PathVariable Long photoId) {
        TextData textData = textDataService.getTextDataById(photoId);

        if (textData == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", "False",
                    "message", "Data not found"
            ));
        }

        Map<String, Object> photo = new HashMap<>();
        photo.put("photoId", textData.getPhotoId());
        photo.put("userId", textData.getMember() != null ? textData.getMember().getId() : null);
        photo.put("categoryId", textData.getCategory() != null ? textData.getCategory().getId() : null);
        photo.put("title", textData.getTitle());
        photo.put("address", textData.getAddress());
        photo.put("operatingHours", textData.getOperatingHours());
        photo.put("list", textData.getList());
        photo.put("summary", textData.getSummary());
        photo.put("photoName", textData.getPhotoName());
        photo.put("photoUrl", textData.getPhotoUrl());
        photo.put("date", textData.getDate());

        Map<String, Object> response = new HashMap<>();
        response.put("success", "True");
        response.put("message", "GET DETAIL");
        response.put("data", photo);

        return ResponseEntity.ok(response);
    }

    // 카테고리 이름으로 카테고리 수정 API (URL: /api/photos/{photoId})
    @PutMapping("/api/photos/{photoId}")
    public ResponseEntity<Map<String, Object>> updateCategoryByName(
            @PathVariable Long photoId,
            @RequestBody Map<String, String> request) {

        String categoryName = request.get("categoryName");

        if (categoryName == null || categoryName.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", "False",
                    "message", "Category name is required"
            ));
        }

        try {
            // 카테고리 이름으로 카테고리 업데이트
            TextData updatedTextData = textDataService.updateCategoryByName(photoId, categoryName);

            Map<String, Object> response = new HashMap<>();
            response.put("success", "True");
            response.put("message", "Category updated successfully");
            response.put("data", Map.of(
                    "photoId", updatedTextData.getPhotoId(),
                    "categoryId", updatedTextData.getCategory().getId(),
                    "categoryName", updatedTextData.getCategory().getCategoryName()
            ));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", "False",
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/api/photos/{photoId}")
    public ResponseEntity<Map<String, Object>> deleteTextDataById(@PathVariable Long photoId) {
        boolean isDeleted = textDataService.deleteTextDataById(photoId);

        if (!isDeleted) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", "False",
                    "message", "Data not found"
            ));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", "True");
        response.put("message", "Data deleted successfully");

        return ResponseEntity.ok(response);
    }
}
