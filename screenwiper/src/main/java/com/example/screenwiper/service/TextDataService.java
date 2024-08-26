package com.example.screenwiper.service;

import com.example.screenwiper.domain.TextData;
import com.example.screenwiper.repository.TextDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TextDataService {

    @Autowired
    private TextDataRepository textDataRepository;

    public Page<TextData> getTextDataList(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (type != null && !type.isEmpty()) {
            return textDataRepository.findByCategoryName(type, pageable);
        } else {
            return textDataRepository.findAll(pageable);
        }
    }
}
