package com.ll.demo03.global.rq;

import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.member.member.service.MemberService;
import com.ll.demo03.global.exception.GlobalException;
import com.ll.demo03.standard.dto.util.Ut;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final MemberService memberService;
    private Member member;

    public Member getMember() {
        if ( member != null ) return member;

        getCookieValue("actorUsername", null);

        String actorUsername = getCookieValue("actorUsername", null);
        String actorPassword = getCookieValue("actorPassword", null);

        if (actorUsername == null || actorPassword == null) {
            String authorization = req.getHeader("Authorization");
            if (authorization != null) {
                authorization = authorization.substring("bearer ".length());
                String[] authorizationBits = authorization.split(" ", 2);
                actorUsername = authorizationBits[0];
                actorPassword = authorizationBits.length == 2 ? authorizationBits[1] : null;
            }
        }

        if ( Ut.str.isBlank(actorUsername)) throw new GlobalException("401-1", "인증정보(아이디)를 입력해주세요");
        if ( Ut.str.isBlank(actorPassword)) throw new GlobalException("401-1", "인증정보(비밀번호)를 입력해주세요");

        Member loginedMember = memberService.findByUsername(actorUsername).orElseThrow(() -> new GlobalException("403-2", "해당 회원이 존재하지 않습니다."));
        if (!loginedMember.getPassword().equals(actorPassword)) throw new GlobalException("403-3", "비밀번호가 일치하지 않습니다.");

        member = loginedMember;

        return loginedMember;
    }

    public String getCurrentUrlPath() {
        return req.getRequestURI();
    }

    public void setStatusCode(int statusCode) {
        resp.setStatus(statusCode);
    }

    private String getCookieValue(String cookieName, String defaultValue) {
        return Arrays.stream(req.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(defaultValue);

    }

    public void removeCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    public void setCookie(String actorUsername, String username) {
        Cookie cookie = new Cookie(actorUsername, username);
        cookie.setMaxAge(60 * 60 * 24 * 365);
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

}