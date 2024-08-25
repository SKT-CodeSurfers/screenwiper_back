// AIAnalysisResponseWrapperDto.java
package com.example.screenwiper.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AIAnalysisResponseWrapperDto {
    private List<AIAnalysisResponseDto> data;
}
