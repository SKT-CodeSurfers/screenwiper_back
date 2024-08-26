package com.example.screenwiper.controller;

import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.service.TextDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TextDataController {

    @Autowired
    private TextDataService textDataService;

    @GetMapping("/api/photos/list")
    public ResponseEntity<Map<String, Object>> getTextDataList(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<TextData> textDataPage = textDataService.getTextDataList(type, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "List_TextData_GET");
        response.put("data", textDataPage.getContent());

        return ResponseEntity.ok(response);
    }
}
