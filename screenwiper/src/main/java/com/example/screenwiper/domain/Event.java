package com.example.screenwiper.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 이벤트 이름

    private String date; // 이벤트 날짜 (String으로 변경)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "text_data_id")
    private TextData textData; // 관련 TextData 엔티티

    // 기본 생성자
    public Event() {}

    // 생성자
    public Event(String name, String date) { // 생성자에서 date 타입도 String으로 변경
        this.name = name;
        this.date = date;
    }

    // Getter 및 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() { // Getter도 String 타입으로 변경
        return date;
    }

    public void setDate(String date) { // Setter도 String 타입으로 변경
        this.date = date;
    }

    public TextData getTextData() {
        return textData;
    }

    public void setTextData(TextData textData) {
        this.textData = textData;
    }
}
