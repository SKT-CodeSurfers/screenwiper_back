package com.example.screenwiper.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;  // 회원 ID

    @Column(nullable = true)  // 카카오에서 정보를 못 가져오는 경우 null 허용
    private String name;  // 이름

    @Column(nullable = true)
    private LocalDate birthdate;  // 생년월일

    @Column(nullable = true)
    private String nickname;  // 닉네임

    @Column(nullable = false, unique = true)
    private String email;  // 이메일

    // 가입 날짜에 기본값으로 현재 날짜를 설정
    @Column(name = "signup_date", nullable = false)
    private LocalDate signupDate = LocalDate.now();  // 가입 날짜 (기본값: 현재 날짜)

    @Column(nullable = false)
    private boolean enabled = true;  // 활동 상태 (기본값 true)

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<TextData> textDataList;  // TextData와의 관계 설정

    // 생성자
    public Member(String name, LocalDate birthdate, String nickname, String email, LocalDate signupDate) {
        this.name = name;
        this.birthdate = birthdate;
        this.nickname = nickname;
        this.email = email;
        this.signupDate = signupDate != null ? signupDate : LocalDate.now();  // 만약 signupDate가 null이면 기본값으로 현재 날짜 설정
        this.enabled = true;
    }
}
