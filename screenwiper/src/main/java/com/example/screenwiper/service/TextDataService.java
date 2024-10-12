package com.example.screenwiper.service;

import com.example.screenwiper.domain.Category;
import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.repository.CategoryRepository;
import com.example.screenwiper.repository.TextDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TextDataService {

    @Autowired
    private TextDataRepository textDataRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<TextData> getTextDataList(String type, Pageable pageable) {
        if (type != null && !type.isEmpty()) {
            return textDataRepository.findByCategoryName(type, pageable);
        } else {
            return textDataRepository.findAll(pageable);
        }
    }

    public TextData getTextDataById(Long id) {
        Optional<TextData> textDataOptional = textDataRepository.findById(id);
        return textDataOptional.orElse(null); // 데이터가 없으면 null 반환
    }

    // 카테고리 이름으로 카테고리 업데이트 메서드
    public TextData updateCategoryByName(Long photoId, String categoryName) {
        Optional<TextData> textDataOptional = textDataRepository.findById(photoId);
        if (textDataOptional.isPresent()) {
            TextData textData = textDataOptional.get();

            // 카테고리 이름으로 카테고리 찾기
            Optional<Category> categoryOptional = categoryRepository.findByCategoryName(categoryName);
            if (categoryOptional.isPresent()) {
                Category category = categoryOptional.get();
                // 카테고리 업데이트
                textData.setCategory(category);
                textData.setCategoryName(category.getCategoryName());

                // 변경 사항 저장
                return textDataRepository.save(textData);
            } else {
                throw new IllegalArgumentException("Category not found: " + categoryName);
            }
        } else {
            throw new IllegalArgumentException("Invalid photo ID: " + photoId);
        }
    }
}
