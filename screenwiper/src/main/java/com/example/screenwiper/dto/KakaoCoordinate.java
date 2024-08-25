package com.example.screenwiper.dto;

public class KakaoCoordinate {
    private double x;
    private double y;

    public KakaoCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Getter methods
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
