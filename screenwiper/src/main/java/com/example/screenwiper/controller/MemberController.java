package com.example.screenwiper.controller;

import com.example.screenwiper.dto.ApiResponse;
import com.example.screenwiper.dto.MemberDto;
import com.example.screenwiper.service.MemberService;
import com.example.screenwiper.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ApiResponse> getMemberInfo(@RequestHeader("Authorization") String token) {
        // Bearer에서 실제 token 추출
        String accessToken = token.replace("Bearer ", "");

        // token에서 member_id 추출
        Long memberId = jwtUtil.extractMemberId(accessToken);

        // member 정보를 가져옴
        MemberDto memberDto = memberService.getMemberById(memberId);

        // ApiResponse 반환
        ApiResponse response = new ApiResponse(true, "회원 정보 조회 성공", List.of(memberDto));
        return ResponseEntity.ok(response);
    }
}
