package com.example.screenwiper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MemberDto {
    private Long id;  // 회원 ID
    private String name;  // 이름
    private LocalDate birthdate;  // 생년월일
    private String nickname;  // 닉네임
    private String email;  // 이메일
    private boolean enabled;  // 활동 상태
}
