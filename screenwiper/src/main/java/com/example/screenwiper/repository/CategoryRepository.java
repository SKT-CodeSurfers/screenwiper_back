package com.example.screenwiper.repository;

import com.example.screenwiper.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Category 객체를 ID로 찾는 기본 메서드는 JpaRepository에 포함되어 있으므로 추가적으로 필요한 메서드가 있으면 정의할 수 있습니다.

    // 카테고리 이름으로 카테고리 찾기
    Optional<Category> findByCategoryName(String categoryName);

}
