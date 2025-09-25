package com.ll.demo03.domain.member.member.service;


import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.member.member.repository.MemberRepository;
import com.ll.demo03.global.exception.GlobalException;
import com.ll.demo03.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Transactional
    public RsData<Member> join(String username, String password, String nickname) {
        findByUsername(username).ifPresent(ignored -> {
            throw new GlobalException("400-1", "%s(은)는 이미 존재하는 아이디입니다.".formatted(username));
        });

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .apiKey(UUID.randomUUID().toString())
                .build();

        memberRepository.save(member);

        return RsData.of("회원가입이 완료되었습니다.", member);
    }

    public Member getReferenceById(long id) {
        return memberRepository.getReferenceById(id);
    }

    public long count() {
        return memberRepository.count();
    }

    public boolean matchPassword(String actorPassword, String password) {
        return passwordEncoder.matches(actorPassword, password);
    }

    public Optional <Member> findById(long id) {
        return memberRepository.findById(id);
    }

    public Optional <Member> findByApiKey(String apiKey) {
        return memberRepository.findByApiKey(apiKey);
    }
}
