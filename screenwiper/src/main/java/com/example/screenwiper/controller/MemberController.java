package com.example.screenwiper.controller;

import com.example.screenwiper.dto.MemberDto;
import com.example.screenwiper.dto.MemberResponseDto;
import com.example.screenwiper.service.MemberService;
import com.example.screenwiper.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
            System.err.println("Authorization token is missing");
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Authorization token is missing"
            ));
        }

        Long memberId;
        System.out.println("MemberController - extractMemberId : START");
        try {
            memberId = jwtUtil.extractMemberId(token);
            System.out.println("Member ID from token: " + memberId);
        } catch (Exception e) {
            System.err.println("Invalid token: " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Invalid token"
            ));
        }

        // member 정보를 가져옴
        MemberDto memberDto = memberService.getMemberById(memberId);

        // 요청된 JSON 구조로 응답 구성
        Map<String, Object> response = Map.of(
                "success", true,
                "message", "GET MEMBER INFO",
                "data", Collections.singletonList(memberDto)
        );

        return ResponseEntity.ok(response);
    }
}
