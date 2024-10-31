package com.example.screenwiper.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class Event {
    private String name;
    private String date;

    // 기본 생성자
    public Event() {}

    // 생성자
    public Event(String name, String date) {
        this.name = name;
        this.date = date;
    }

    // getters 및 setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
