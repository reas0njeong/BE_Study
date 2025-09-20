package com.ll.demo03.domain.member.member.controller;

import com.ll.demo03.domain.member.member.dto.MemberDto;
import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.member.member.service.MemberService;
import com.ll.demo03.global.exception.GlobalException;
import com.ll.demo03.global.rq.Rq;
import com.ll.demo03.global.rsData.RsData;
import com.ll.demo03.standard.dto.Empty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ApiV1MemberController {
    private final MemberService memberService;
    private final Rq rq;

    @AllArgsConstructor
    @Getter
    public static class MemberJoinReqBody {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        @NotBlank
        private String nickname;
    }

    @AllArgsConstructor
    @Getter
    public static class MemberJoinResBody {
        MemberDto item;
    }

    @PostMapping("")
    @Transactional
    public RsData<MemberJoinResBody> join(@RequestBody @Valid MemberJoinReqBody reBody) {
        RsData<Member> joinRs = memberService.join(reBody.username, reBody.password, reBody.nickname);

        return joinRs.newDataOf(
                new MemberJoinResBody(
                        new MemberDto(
                                joinRs.getData()
                        )
                )
        );
    }


    @AllArgsConstructor
    @Getter
    public static class MemberLoginReqBody {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @AllArgsConstructor
    @Getter
    public static class MemberLoginRespBody {
        MemberDto item;
    }

    @PostMapping("/login")
    @Transactional
    public RsData<MemberLoginRespBody> login(@RequestBody @Valid MemberLoginReqBody reqBody) {
        Member member = memberService
                .findByUsername(reqBody.username)
                .orElseThrow(() -> new GlobalException("401-1", "해당 회원이 존재하지 않습니다."));

        if (!member.getPassword().equals(reqBody.password)) {
            throw new GlobalException("401-2", "비밀번호가 일치하지 않습니다.");
        }

        rq.setCookie("actorUsername", member.getUsername());
        rq.setCookie("actorPassword", member.getPassword());

        return RsData.of(
                "200-1",
                "로그인 되었습니다.",
                new MemberLoginRespBody(
                        new MemberDto(member)
                )
        );
    }


    @DeleteMapping("/logout")
    @Transactional
    public RsData<Empty> logout() {
        rq.removeCookie("actorUsername");
        rq.removeCookie("actorPassword");

        return RsData.OK;
    }
}

