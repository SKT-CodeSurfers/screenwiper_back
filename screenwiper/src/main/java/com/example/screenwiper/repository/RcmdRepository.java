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
            "    SELECT category_id, (" +
            "        SELECT photo_id " +
            "        FROM textdata " +
            "        WHERE category_id = td.category_id " +
            "        ORDER BY RAND() " +
            "        LIMIT 1 " +
            "    ) AS photo_id " +
            "    FROM textdata td " +
            "    WHERE category_id IN (1, 2, 3) " +
            "    GROUP BY category_id " +
            ") AS selected_photos " +
            "ON t.photo_id = selected_photos.photo_id",
            nativeQuery = true)
    List<TextData> findRandomDataByCategory();
}
