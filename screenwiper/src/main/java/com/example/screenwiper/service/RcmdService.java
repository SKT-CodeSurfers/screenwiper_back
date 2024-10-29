package com.example.screenwiper.service;

import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.repository.RcmdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RcmdService {

    @Autowired
    private RcmdRepository rcmdRepository;

    @Transactional
    public Map<String, Object> getRandomDataByCategory() {
        // 쿼리에서 모든 카테고리 데이터를 가져옵니다.
        List<TextData> randomData = rcmdRepository.findRandomDataByCategory();

        // 결과를 맵으로 변환
        Map<Long, Map<String, Object>> resultMap = new HashMap<>(); // category_id를 키로 사용하는 맵

        for (TextData data : randomData) {
            // 카테고리 ID를 키로 하여 데이터를 맵에 추가
            Long categoryId = data.getCategoryId();
            if (categoryId != null) {
                // category_id로 중복 체크
                resultMap.putIfAbsent(categoryId, convertToMap(data));
            }
        }

        // 최종 결과를 반환할 맵
        Map<String, Object> finalResult = new HashMap<>();
        resultMap.forEach((categoryId, dataMap) -> {
            finalResult.put(dataMap.get("category_name").toString(), dataMap); // category_name을 키로 하여 최종 결과에 추가
        });

        return finalResult;
    }

    // TextData 객체를 Map으로 변환하는 메서드
    private Map<String, Object> convertToMap(TextData data) {
        Map<String, Object> map = new HashMap<>();
        map.put("photo_id", data.getPhotoId());
        map.put("address", data.getAddress());
        map.put("category_name", data.getCategoryName());
        map.put("date", data.getDate());
        map.put("operating_hours", data.getOperatingHours());
        map.put("photo_name", data.getPhotoName());
        map.put("photo_url", data.getPhotoUrl());
        map.put("summary", data.getSummary());
        map.put("title", data.getTitle());
        // 필요한 다른 필드들도 추가
        return map;
    }
}
