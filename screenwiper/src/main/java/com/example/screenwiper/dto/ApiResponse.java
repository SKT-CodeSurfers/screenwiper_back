package com.example.screenwiper.dto;

import lombok.Data;
import java.util.List;

@Data
public class ApiResponse {
    private boolean success;
    private String message;
    private List<ResponseDto> data;

    public ApiResponse(boolean success, String message, List<ResponseDto> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
