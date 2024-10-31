package com.example.screenwiper.domain;

import jakarta.persistence.*;
import lombok.Data;

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
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String categoryName;
    private String title;
    private String address;
    private String operatingHours;

    @ElementCollection
    @CollectionTable(name = "event", joinColumns = @JoinColumn(name = "textdata_id"))
    private List<Event> list;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String photoName;
    private String photoUrl;
    private String date;

    public Long getCategoryId() {
        return category != null ? category.getId() : null; // Category 객체가 null이 아닌 경우 ID 반환
    }

}
