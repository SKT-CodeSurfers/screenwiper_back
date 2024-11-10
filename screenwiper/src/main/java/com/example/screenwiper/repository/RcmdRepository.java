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
            "    SELECT category_id, photo_id " +
            "    FROM ( " +
            "        SELECT category_id, photo_id, " +
            "            ROW_NUMBER() OVER ( " +
            "                PARTITION BY category_id " +
            "                ORDER BY " +
            "                    CASE WHEN category_id = 2 THEN date ELSE NULL END DESC, " +
            "                    CASE WHEN category_id != 2 THEN RAND() ELSE NULL END " +
            "            ) AS rn " +
            "        FROM textdata " +
            "        WHERE category_id IN (1, 2, 3) " +
            "    ) AS ranked_data " +
            "    WHERE (category_id = 2 AND rn <= 2) " +
            "       OR (category_id != 2 AND rn = 1) " +
            ") AS selected_photos " +
            "ON t.photo_id = selected_photos.photo_id",
            nativeQuery = true)
    List<TextData> findRandomDataByCategory();
}
