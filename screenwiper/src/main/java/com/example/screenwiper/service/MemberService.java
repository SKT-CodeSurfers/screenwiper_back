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
        Member findMember = memberRepository.findOne(id);
        return findMember != null;
    }

    @Transactional(readOnly = false)
    public Member login(Long id) {
        Member member = memberRepository.findOne(id);
        return member;
    }

}
