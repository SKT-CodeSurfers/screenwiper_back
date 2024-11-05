package com.example.screenwiper.repository;

import com.example.screenwiper.domain.TextData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TextDataRepository extends JpaRepository<TextData, Long> {
    Page<TextData> findByCategoryName(String categoryName, Pageable pageable);

    Page<TextData> findByMemberId(Long memberId, Pageable pageable);

    Page<TextData> findByMemberIdAndCategoryName(Long memberId, String categoryName, Pageable pageable);

    @Query("SELECT t FROM TextData t LEFT JOIN t.list e " +
            "WHERE t.member.id = :memberId AND (t.title LIKE %:keyword% " +
            "OR t.summary LIKE %:keyword% OR e.name LIKE %:keyword%)")
    Page<TextData> findByMemberIdAndKeyword(Long memberId, String keyword, Pageable pageable);
}
