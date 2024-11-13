package com.example.screenwiper.service;

import com.example.screenwiper.domain.Category;
import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.repository.CategoryRepository;
import com.example.screenwiper.repository.TextDataRepository;
import com.google.cloud.vision.v1.WebDetection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class TextDataService {

    @Autowired
    private TextDataRepository textDataRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImageSearchService imageSearchService; // 구글 Vision API 서비스 객체

    public Page<TextData> getTextDataListByMemberId(Long memberId, String type, Pageable pageable) {
        if (type != null && !type.isEmpty()) {
            return textDataRepository.findByMemberIdAndCategoryName(memberId, type, pageable);
        } else {
            return textDataRepository.findByMemberId(memberId, pageable);
        }
    }

    public TextData getTextDataById(Long id) {
        Optional<TextData> textDataOptional = textDataRepository.findById(id);
        return textDataOptional.orElse(null); // 데이터가 없으면 null 반환
    }

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
                throw new IllegalArgumentException("해당 카테고리는 없는 카테고리 입니다: " + categoryName);
            }
        } else {
            throw new IllegalArgumentException("올바르지 않은 photo ID: " + photoId);
        }
    }

    public boolean deleteTextDataById(Long id) {
        Optional<TextData> textDataOptional = textDataRepository.findById(id);
        if (textDataOptional.isPresent()) {
            textDataRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 이미지 URL을 통해 웹 감지 정보를 TextData에 반영하는 메서드
     *
     * @param photoId 텍스트 데이터의 ID
     * @param imageUrl 웹 감지할 이미지 URL
     * @return 업데이트된 TextData 객체
     */
    public TextData updateTextDataWithWebDetection(Long photoId, String imageUrl) throws IOException {
        // ImageSearchService를 통해 웹 감지 결과 가져오기
        WebDetection webDetection = imageSearchService.getWebDetectionFromUrl(imageUrl);

        // 해당 텍스트 데이터를 조회하여 웹 감지 정보 반영
        Optional<TextData> textDataOptional = textDataRepository.findById(photoId);
        if (textDataOptional.isPresent()) {
            TextData textData = textDataOptional.get();
            textData.setWebDetection(webDetection);  // 웹 감지 정보를 텍스트 데이터에 반영
            return textDataRepository.save(textData);  // 업데이트된 텍스트 데이터 저장
        } else {
            throw new IllegalArgumentException("해당 텍스트 데이터가 존재하지 않습니다.");
        }
    }

    // TextDataService 클래스 내의 getSimilarImagesForTextData 메서드
    public List<String> getSimilarImagesForTextData(Long textDataId) throws IOException {
        TextData textData = textDataRepository.findById(textDataId).orElse(null);
        if (textData != null) {
            String imageUrl = textData.getImagePath(); // TextData에서 이미지를 얻음
            return imageSearchService.getSimilarImageUrls(imageUrl); // 유사한 이미지들을 검색 후 JSON으로 반환
        }
        return null;
    }

    public String getPhotoUrlById(Long photoId) {
        TextData textData = textDataRepository.findById(photoId).orElse(null);
        return textData != null ? textData.getPhotoUrl() : null;
    }


}
