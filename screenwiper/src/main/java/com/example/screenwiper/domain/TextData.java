package com.example.screenwiper.domain;

import com.google.cloud.vision.v1.WebDetection;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "textdata")
public class TextData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id") // 데이터베이스 컬럼명 지정
    private Long photoId; // 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 텍스트 데이터와 연결된 Member 엔티티

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // 텍스트 데이터와 연결된 Category 엔티티

    private String categoryName; // 카테고리 이름 (옵션으로 사용)
    private String title; // 텍스트 제목
    private String address; // 주소
    private String operatingHours; // 운영 시간

    @ElementCollection
    @CollectionTable(name = "event", joinColumns = @JoinColumn(name = "textdata_id"))
    private List<Event> list; // 텍스트 데이터에 관련된 이벤트들

    @Column(columnDefinition = "TEXT")
    private String summary; // 텍스트 요약

    private String photoName; // 사진 이름
    private String photoUrl; // 사진 URL
    private String date; // 사진 관련 날짜

    @Setter
    @Transient
    private WebDetection webDetection; // Google Vision API에서 받은 웹 감지 결과

    @ElementCollection
    @CollectionTable(name = "similar_image_urls", joinColumns = @JoinColumn(name = "textdata_id"))
    @Column(name = "url")
    private List<String> similarImageUrls; // 유사한 이미지 URL 리스트 추가

    // Category ID를 반환하는 메서드 (Category가 null일 경우 null 반환)
    public Long getCategoryId() {
        return category != null ? category.getId() : null; // Category 객체가 null이 아닌 경우 ID 반환
    }

    // 이미지를 반환하는 URL을 반환하는 메서드 (photoUrl이 실제 이미지 URL을 저장한다고 가정)
    public String getImageUrl() {
        return photoUrl; // photoUrl을 이미지 URL로 반환
    }

    // 이미지 경로를 반환하는 메서드
    public String getImagePath() {
        return photoUrl; // photoUrl을 이미지 경로로 반환
    }

    // WebDetection을 통해 유사한 이미지 URL을 반환하는 메서드
    public List<String> getSimilarImageUrls() {
        if (webDetection != null) {
            List<String> extractedUrls = new ArrayList<>();
            // FullMatchingImages에서 이미지 URL 추출
            webDetection.getFullMatchingImagesList().forEach(webImage -> extractedUrls.add(webImage.getUrl()));
            // PagesWithMatchingImages에서 웹 페이지 URL 추출
            webDetection.getPagesWithMatchingImagesList().forEach(webPage -> extractedUrls.add(webPage.getUrl()));
            return extractedUrls;
        }
        return similarImageUrls;
    }
}
