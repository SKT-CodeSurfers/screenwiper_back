package com.example.screenwiper.service;

import com.example.screenwiper.domain.Member;
import com.example.screenwiper.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = false)
    public Long join(Member member) {
        memberRepository.save(member);
        return member.getId();
    }

    public boolean isMemberExist(Long id) {
        // findById 메소드를 사용하여 Optional<Member>를 반환
        return memberRepository.findById(id).isPresent();
    }

    @Transactional(readOnly = false)
    public Member login(Long id) {
        // findById 메소드를 사용하여 Optional<Member>를 반환
        return memberRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean deleteMember(Long memberId) {
        // findById 메소드를 사용하여 Optional<Member>를 반환
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member != null) {
            memberRepository.delete(member);
            return true;
        } else {
            return false;
        }
    }

    // 기존 회원의 액세스 토큰 업데이트
    @Transactional(readOnly = false)
    public void update(Member member) {
        memberRepository.save(member); // 엔티티를 업데이트
    }
}
