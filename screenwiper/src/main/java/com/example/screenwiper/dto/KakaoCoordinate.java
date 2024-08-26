package com.example.screenwiper.dto;

public class KakaoCoordinate {
    private double x;
    private double y;

    public KakaoCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Getter methods
    public double getX() { // 반환 타입을 double로 변경
        return x;
    }

    public double getY() { // 반환 타입을 double로 변경
        return y;
    }
}
