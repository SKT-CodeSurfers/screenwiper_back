package com.example.screenwiper.repository;

import com.example.screenwiper.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 카테고리 이름으로 카테고리 찾기
    Optional<Category> findByCategoryName(String categoryName);

}
