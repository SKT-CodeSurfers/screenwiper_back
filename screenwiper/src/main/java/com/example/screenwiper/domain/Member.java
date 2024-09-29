package com.example.screenwiper.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id
    @Column(name = "member_id")
    private Long id;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<TextData> textDataList;

    // 액세스 토큰을 저장할 필드 추가
    private String accessToken;

    private String name;
    private String email;

    private DayOfWeek dayOfWeek;
    private LocalTime time;
    private boolean enabled;


}
