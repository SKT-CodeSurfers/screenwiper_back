package com.example.screenwiper.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.example.screenwiper.domain.Member;
import com.example.screenwiper.service.KakaoService;
import com.example.screenwiper.service.KakaoUserInfoResponseDto;
import com.example.screenwiper.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> callback(@RequestParam("code") String code, HttpSession session) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        log.info("AccessToken: " + accessToken);

        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);
        log.info("User Info: " + userInfo);

        Long kakaoId = userInfo.getId();
        String nickName = userInfo.getKakaoAccount().getProfile().getNickName();
        String email = userInfo.getKakaoAccount().getEmail();

        if (!memberService.isMemberExist(kakaoId)) {
            // 회원가입 처리
            Member newMember = new Member();
            newMember.setId(kakaoId);
            newMember.setName(nickName);
            newMember.setEmail(email);
            memberService.join(newMember);
            log.info("New member joined: " + newMember.getName());
        }

        // 세션에 로그인 정보 저장
        Member member = memberService.login(kakaoId);
        session.setAttribute("loginMember", member);
        log.info("Member logged in and saved to session: " + member.getName());

        // 로그인 성공 메시지 반환
        return ResponseEntity.ok("Login successful: " + member.getName());
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
}




//package com.example.screenwiper.controller;
//
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import com.example.screenwiper.domain.Member;
//import com.example.screenwiper.service.KakaoService;
//import com.example.screenwiper.service.KakaoUserInfoResponseDto;
//import com.example.screenwiper.service.MemberService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.io.IOException;
//
//@Slf4j
//@RequiredArgsConstructor
//@Controller
//@RequestMapping("")
//public class KakaoLoginController {
//
//    private final KakaoService kakaoService;
//    private final MemberService memberService;
//
//    @Value("${kakao.client_id}")
//    private String clientId;
//
//    @Value("${kakao.redirect_uri}")
//    private String redirectUri;
//
//    @GetMapping("/login")
//    public String login(Model model) {
//        log.info("Rendering login page");
//
//        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize" +
//                "?response_type=code" +
//                "&client_id=" + clientId +
//                "&redirect_uri=" + redirectUri;
//
//        model.addAttribute("kakaoLoginUrl", kakaoLoginUrl);
//        return "loginPage"; // 로그인 페이지 템플릿으로 이동
//    }
//
//    @GetMapping("/callback")
//    public void callback(@RequestParam("code") String code, HttpSession session, HttpServletResponse response) throws IOException {
//        String accessToken = kakaoService.getAccessTokenFromKakao(code);
//        log.info("AccessToken: " + accessToken);
//
//        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);
//        log.info("User Info: " + userInfo);
//
//        Long kakaoId = userInfo.getId();
//        String nickName = userInfo.getKakaoAccount().getProfile().getNickName();
//        String email = userInfo.getKakaoAccount().getEmail();
//
//        if (!memberService.isMemberExist(kakaoId)) {
//            // 회원가입 처리
//            Member newMember = new Member();
//            newMember.setId(kakaoId);
//            newMember.setName(nickName);
//            newMember.setEmail(email);
//            memberService.join(newMember);
//            log.info("New member joined: " + newMember.getName());
//        }
//
//        // 세션에 로그인 정보 저장
//        Member member = memberService.login(kakaoId);
//        session.setAttribute("loginMember", member);
//        log.info("Member logged in and saved to session: " + member.getName());
//
//        // 홈 페이지로 리디렉션
//        response.sendRedirect("/");
//    }
//
//    // 홈 페이지를 처리하는 메소드 추가
//    @GetMapping("/")
//    public String home(HttpSession session, Model model) {
//        Member loginMember = (Member) session.getAttribute("loginMember");
//
//        if (loginMember != null) {
//            model.addAttribute("memberName", loginMember.getName());
//            return "home"; // home.html 템플릿으로 이동
//        } else {
//            return "redirect:/login"; // 로그인 페이지로 리디렉션
//        }
//    }
//}
