package com.example.screenwiper.domain;

import com.example.screenwiper.domain.Member;
import com.example.screenwiper.domain.Category;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "textdata")
public class TextData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId; // 기본 키

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String categoryName;
    private String title;
    private String address;
    private String operatingHours;

    @ElementCollection
    @CollectionTable(name = "event_list", joinColumns = @JoinColumn(name = "text_data_id"))
    @Column(name = "event")
    private List<String> list;

    private String summary;
    private String photoName;
    private String photoUrl;
    private String date;

}
