package com.example.screenwiper.repository;

import com.example.screenwiper.domain.TextData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface TextDataRepository extends JpaRepository<TextData, Long> {
    Page<TextData> findByCategoryName(String categoryName, Pageable pageable);
}
