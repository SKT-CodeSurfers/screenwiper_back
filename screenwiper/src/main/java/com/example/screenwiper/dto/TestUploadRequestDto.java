package com.example.screenwiper.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class TestUploadRequestDto {
    private List<MultipartFile> files;
}
