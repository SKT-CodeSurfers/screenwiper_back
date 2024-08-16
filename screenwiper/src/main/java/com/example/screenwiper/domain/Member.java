package com.example.screenwiper.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter @Setter
public class Member {
    @Id
    @Column(name = "member_id")
    private Long id;
    private String name;
    private String email;

    private DayOfWeek dayOfWeek;
    private LocalTime time;
    private boolean enabled;

}
