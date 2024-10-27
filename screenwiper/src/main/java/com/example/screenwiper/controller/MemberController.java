package com.example.screenwiper.controller;

import com.example.screenwiper.dto.MemberDto;
import com.example.screenwiper.dto.MemberResponseDto;
import com.example.screenwiper.service.MemberService;
import com.example.screenwiper.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;  // Jakarta 네임스페이스 사용
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    public MemberController(MemberService memberService, JwtUtil jwtUtil) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMemberInfo(HttpServletRequest request) {
        // Authorization 헤더에서 Bearer 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ?
                authorizationHeader.substring(7) : null;

        if (token == null) {
            System.err.println("Authorization token is missing");  // 에러 로그 출력
            return ResponseEntity.status(401).body(Map.of(
                    "success", "False",
                    "message", "Authorization token is missing"
            ));
        }

        Long memberId;
        System.out.println("MemberController - extractMemberId : START");
        try {
            memberId = jwtUtil.extractMemberId(token);  // 토큰에서 member_id 추출
            System.out.println("Member ID from token: " + memberId);  // 로그로 member_id 확인
        } catch (Exception e) {
            System.err.println("Invalid token: " + e.getMessage());  // 에러 로그 출력
            return ResponseEntity.status(401).body(Map.of(
                    "success", "False",
                    "message", "Invalid token"
            ));
        }

        // member 정보를 가져옴
        MemberDto memberDto = memberService.getMemberById(memberId);

        // 응답 데이터 구성
        MemberResponseDto responseDto = new MemberResponseDto(true, "회원 정보 조회 성공", Collections.singletonList(memberDto));
        Map<String, Object> response = Map.of(
                "success", "True",
                "message", "GET MEMBER INFO",
                "data", responseDto
        );

        return ResponseEntity.ok(response);
    }
}
