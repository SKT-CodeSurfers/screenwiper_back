package com.example.screenwiper.service;

import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.repository.RcmdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        Map<Long, List<Map<String, Object>>> resultMap = new HashMap<>(); // category_id를 키로 사용하는 맵, 각 카테고리마다 여러 데이터를 리스트로 처리

        for (TextData data : randomData) {
            // 카테고리 ID를 키로 하여 데이터를 맵에 추가
            Long categoryId = data.getCategoryId();
            if (categoryId != null) {
                // category_id가 이미 있으면 리스트에 추가, 없으면 새로 리스트를 만들고 추가
                resultMap.computeIfAbsent(categoryId, k -> new ArrayList<>()).add(convertToMap(data));
            }
        }

        // 최종 결과를 반환할 맵
        Map<String, Object> finalResult = new HashMap<>();
        resultMap.forEach((categoryId, dataMapList) -> {
            // category_name을 키로 하여 최종 결과에 추가
            finalResult.put(dataMapList.get(0).get("category_name").toString(), dataMapList);
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

        return map;
    }
}
