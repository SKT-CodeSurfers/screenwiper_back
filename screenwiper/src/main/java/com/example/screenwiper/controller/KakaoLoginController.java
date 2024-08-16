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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final MemberService memberService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @GetMapping("/login")
    public String login(Model model) {
        log.info("Rendering login page");

        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri;

        model.addAttribute("kakaoLoginUrl", kakaoLoginUrl);
        return "loginPage"; // 로그인 페이지 템플릿으로 이동
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code, HttpSession session, HttpServletResponse response) throws IOException {
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

        // 홈 페이지로 리디렉션
        response.sendRedirect("/");
    }

    // 홈 페이지를 처리하는 메소드 추가
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember != null) {
            model.addAttribute("memberName", loginMember.getName());
            return "home"; // home.html 템플릿으로 이동
        } else {
            return "redirect:/login"; // 로그인 페이지로 리디렉션
        }
    }
}
