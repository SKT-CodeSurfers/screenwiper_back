package com.example.screenwiper.repository;

import com.example.screenwiper.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByMemberId(Long memberId);  // memberId로 토큰을 조회하는 메서드

    void deleteByMemberId(Long memberId);  // memberId로 토큰 삭제
}
