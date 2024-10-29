package com.example.screenwiper.repository;

import com.example.screenwiper.domain.TextData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RcmdRepository extends JpaRepository<TextData, Long> {

    @Query(value = "SELECT t.* " +
            "FROM textdata t " +
            "JOIN ( " +
            "    SELECT category_id, MIN(photo_id) AS photo_id " +
            "    FROM ( " +
            "        SELECT category_id, photo_id " +
            "        FROM textdata " +
            "        WHERE category_id IN (1, 2, 3) " +
            "        ORDER BY RAND() " +
            "    ) AS random_photos " +
            "    GROUP BY category_id " +
            ") AS selected_photos " +
            "ON t.photo_id = selected_photos.photo_id",
            nativeQuery = true)
    List<TextData> findRandomDataByCategory();
}
