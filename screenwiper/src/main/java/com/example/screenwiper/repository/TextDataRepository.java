package com.example.screenwiper.repository;

import com.example.screenwiper.domain.TextData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextDataRepository extends JpaRepository<TextData, Long> {
}
