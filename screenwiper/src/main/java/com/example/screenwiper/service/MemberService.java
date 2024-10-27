package com.example.screenwiper.service;

import com.example.screenwiper.dto.MemberDto;
import com.example.screenwiper.domain.Member;
import com.example.screenwiper.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberDto getMemberById(Long memberId) {
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            // Member 객체를 MemberDto로 변환
            return new MemberDto(member.getId(), member.getName(), member.getBirthdate(), member.getNickname(), member.getEmail(), member.isEnabled());
        } else {
            throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
        }
    }
}
