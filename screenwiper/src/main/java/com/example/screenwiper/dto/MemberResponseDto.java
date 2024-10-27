package com.example.screenwiper.dto;

import lombok.Data;
import java.util.List;

@Data
public class MemberResponseDto {
    private boolean success;       // 요청 성공 여부
    private String message;        // 메시지
    private List<MemberDto> data;  // 사용자 정보 리스트

    // 생성자
    public MemberResponseDto(boolean success, String message, List<MemberDto> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
