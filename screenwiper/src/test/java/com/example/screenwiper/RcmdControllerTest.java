////package com.example.screenwiper;
////
////import com.example.screenwiper.controller.RcmdController;
////import com.example.screenwiper.domain.TextData;
////import com.example.screenwiper.service.RcmdService;
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import org.mockito.InjectMocks;
////import org.mockito.Mock;
////import org.mockito.MockitoAnnotations;
////
////import java.util.Arrays;
////import java.util.List;
////
////import static org.junit.jupiter.api.Assertions.*;
////import static org.mockito.Mockito.*;
////
////class RcmdControllerTest {
////
////    @InjectMocks
////    private RcmdController rcmdController;
////
////    @Mock
////    private RcmdService rcmdService;
////
////    @BeforeEach
////    void setUp() {
////        MockitoAnnotations.openMocks(this);
////    }
////
////    @Test
////    void getRandomDataByCategory_ShouldReturnRandomData() {
////        // Mock 데이터 생성
////        TextData textData1 = new TextData();
////        textData1.setPhotoId(1L);
////        textData1.setTitle("Title 1");
////        textData1.setSummary("Summary 1");
////
////        TextData textData2 = new TextData();
////        textData2.setPhotoId(2L);
////        textData2.setTitle("Title 2");
////        textData2.setSummary("Summary 2");
////
////        List<TextData> mockData = Arrays.asList(textData1, textData2);
////
////        // Mock 서비스 메서드 동작 정의
////        when(rcmdService.getRandomDataByCategory()).thenReturn(mockData);
////
////        // Controller 메서드 호출 및 결과 검증
////        List<TextData> result = rcmdController.getRandomDataByCategory();
////
////        // 각 데이터의 필드 출력
////        for (TextData data : result) {
////            System.out.println("Photo ID: " + data.getPhotoId());
////            System.out.println("Category Name: " + (data.getCategory() != null ? data.getCategory().getCategoryName() : "N/A"));
////            System.out.println("Title: " + data.getTitle());
////            System.out.println("Summary: " + data.getSummary());
////            System.out.println("Photo URL: " + data.getPhotoUrl());
////        }
////
////        assertEquals(2, result.size()); // 결과 리스트의 크기 검증
////    }
////
////
////    @Test
////    void testGetRandomDataByCategory_ShouldNotBeEmpty() {
////        // Mock 데이터 생성 및 서비스 메서드 동작 정의
////        TextData textData1 = new TextData();
////        textData1.setPhotoId(1L);
////        textData1.setTitle("Title 1");
////
////        when(rcmdService.getRandomDataByCategory()).thenReturn(List.of(textData1));
////
////        // Controller 메서드 호출
////        List<TextData> result = rcmdController.getRandomDataByCategory();
////
////        // 결과 검증
////        assertNotNull(result);
////        assertFalse(result.isEmpty()); // 결과가 비어 있지 않은지 검증
////    }
////
////
////}
//
//package com.example.screenwiper;
//
//import com.example.screenwiper.domain.TextData;
//import com.example.screenwiper.service.RcmdService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//
//@SpringBootTest // SpringBoot 애플리케이션 전체 문맥을 로드하여 통합 테스트 수행
//@Transactional  // 테스트 후 롤백을 위해 트랜잭션 관리
//public class RcmdControllerTest {
//
//    @Autowired
//    private RcmdService rcmdService;
//
//    @Test
//    void getRandomDataByCategory_ShouldReturnRandomData() {
//        // 실제 데이터베이스에서 데이터 조회
//        List<TextData> result = rcmdService.getRandomDataByCategory();
//
//        // 결과 출력
//        System.out.println("Random Data:");
//        for (TextData data : result) {
//            System.out.println("Photo ID: " + data.getPhotoId());
//            System.out.println("Category Name: " + (data.getCategory() != null ? data.getCategory().getCategoryName() : "N/A"));
//            System.out.println("Title: " + data.getTitle());
//            System.out.println("Summary: " + data.getSummary());
//            System.out.println("Photo URL: " + data.getPhotoUrl());
//        }
//
//        // 검증 - 데이터가 비어있지 않은지 확인
//        assertNotNull(result);
//        assertFalse(result.isEmpty()); // 데이터가 비어있지 않음 확인
//    }
//}
