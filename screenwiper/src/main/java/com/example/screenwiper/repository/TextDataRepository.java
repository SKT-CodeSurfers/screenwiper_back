package com.example.screenwiper.repository;

import com.example.screenwiper.domain.TextData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextDataRepository extends JpaRepository<TextData, Long> {
    Page<TextData> findByCategoryName(String categoryName, Pageable pageable);

    // 추가된 메서드
    Page<TextData> findByMemberId(Long memberId, Pageable pageable);
    Page<TextData> findByMemberIdAndCategoryName(Long memberId, String categoryName, Pageable pageable);
}
