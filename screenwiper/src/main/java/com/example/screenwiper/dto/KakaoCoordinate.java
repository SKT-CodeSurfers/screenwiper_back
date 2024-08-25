package com.example.screenwiper.dto;

import lombok.Data;

@Data
public class KakaoCoordinate {
    private final String x;
    private final String y;

    public KakaoCoordinate(String x, String y) {
        this.x = x;
        this.y = y;
    }
}
