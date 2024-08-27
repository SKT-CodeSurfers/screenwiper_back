
package com.example.screenwiper.controller;

import jakarta.servlet.http.HttpSession;
import com.example.screenwiper.domain.Member;
import com.example.screenwiper.service.KakaoService;
import com.example.screenwiper.service.KakaoUserInfoResponseDto;
import com.example.screenwiper.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api") // 모든 API 경로 앞에 /api 추가
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final MemberService memberService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    // 로그인 URL을 제공하는 API
    @GetMapping("/login-url")
    public ResponseEntity<String> getLoginUrl() {
        log.info("Providing Kakao login URL");

        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri;

        return ResponseEntity.ok(kakaoLoginUrl); // URL을 JSON 형태로 반환
    }

    // 카카오 콜백을 처리하는 API
    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> callback(@RequestParam("code") String code, HttpSession session) {
        try {
            log.info("Authorization code: " + code);

            String accessToken = kakaoService.getAccessTokenFromKakao(code);
            log.info("AccessToken: " + accessToken);

            KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);
            log.info("User Info: " + userInfo);

            Long kakaoId = userInfo.getId();
            String nickName = userInfo.getKakaoAccount().getProfile().getNickName();
            String email = userInfo.getKakaoAccount().getEmail();

            if (!memberService.isMemberExist(kakaoId)) {
                Member newMember = new Member();
                newMember.setId(kakaoId);
                newMember.setName(nickName);
                newMember.setEmail(email);
                memberService.join(newMember);
                log.info("New member joined: " + newMember.getName());
            }

            Member member = memberService.login(kakaoId);
            session.setAttribute("loginMember", member);
            log.info("Member logged in and saved to session: " + member.getName());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("name", member.getName());
            response.put("accessToken", accessToken); // 액세스 토큰을 응답에 추가


            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to handle Kakao callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Failed to login with Kakao"));
        }
    }


    // 로그인된 회원 정보를 반환하는 API
    @GetMapping("/member-info")
    public ResponseEntity<String> getMemberInfo(HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember != null) {
            return ResponseEntity.ok("Logged in as: " + loginMember.getName());
        } else {
            return ResponseEntity.status(401).body("Not logged in");
        }
    }

    // 회원 탈퇴 로직
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<String> deleteMember(@PathVariable Long memberId) {
        log.info("Deleting memberID :"+ memberId);
        boolean isDeleted = memberService.deleteMember(memberId);

        if (isDeleted) {
            return ResponseEntity.ok("Member deleted 완료");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
        }
    }

    @DeleteMapping("/members/me")
    public ResponseEntity<String> deleteCurrentMember(HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        Long memberId = loginMember.getId();
        log.info("Deleting memberID :" + memberId);
        boolean isDeleted = memberService.deleteMember(memberId);

        if (isDeleted) {
            session.invalidate(); // 세션 무효화 (로그아웃)
            return ResponseEntity.ok("Member deleted 완료");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
        }
    }
}
