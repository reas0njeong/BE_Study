package com.ll.demo03.domain.surl.surl.controller;

import com.ll.demo03.domain.auth.auth.service.AuthService;
import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.member.member.service.MemberService;
import com.ll.demo03.domain.surl.surl.dto.SurlDto;
import com.ll.demo03.domain.surl.surl.entity.Surl;
import com.ll.demo03.domain.surl.surl.service.SurlService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/surls")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApiV1SurlController {
    private final SurlService surlService;
    private final AuthService authService;
    private final MemberService memberService;
    private final Rq rq;

    @AllArgsConstructor
    @Getter
    public static class SurlAddReqBody {
        @NotBlank
        private String body;
        @NotBlank
        private String url;
    }

    @AllArgsConstructor
    @Getter
    public static class SurlAddResBody {
        private SurlDto item;
    }

    @PostMapping("")
    @Transactional
    public RsData<SurlAddResBody> add(@RequestBody @Valid SurlAddReqBody reqBody) {
        Member member = rq.getMember();

        RsData<Surl> addRs = surlService.add(member, reqBody.body, reqBody.url);

        return addRs.newDataOf(
                new SurlAddResBody(
                        new SurlDto(addRs.getData())
                )
        );
    }



    @AllArgsConstructor
    @Getter
    public static class SurlGetResBody {
        private SurlDto item;
    }

    @GetMapping("/{id}")
    public RsData<SurlGetResBody> get(
            @PathVariable long id
    ) {
        Surl surl = surlService.findById(id).orElseThrow(GlobalException.E404::new);

        authService.checkCanGetSurl(rq.getMember(), surl);

        return RsData.of(
                new SurlGetResBody(
                        new SurlDto(surl)
                )
        );
    }

    @AllArgsConstructor
    @Getter
    public static class SurlGetItemsResBody {
        private List<SurlDto> items;
    }

    @GetMapping("")
    public RsData<SurlGetItemsResBody> getItems() {
         Member member = rq.getMember();

        List<Surl> surls = surlService.findByAuthorOrderByIdDesc(member);

        return RsData.of(
                new SurlGetItemsResBody(
                        surls.stream()
                                .map(SurlDto::new)
                                .toList()
                )
        );
    }

    @DeleteMapping("/{id}")
    @Transactional
    public RsData<Empty> delete(
            @PathVariable long id
    ) {
        Surl surl = surlService.findById(id).orElseThrow(GlobalException.E404::new);

        Member member = rq.getMember();

       authService.checkCanDeleteSurl(rq.getMember(), surl);

       surlService.delete(surl);

        return RsData.OK;
    }

    @AllArgsConstructor
    @Getter
    public static class SurlModifyReqBody {
        @NotBlank
        private String body;
        @NotBlank
        private String url;
    }

    @AllArgsConstructor
    @Getter
    public static class SurlModifyResBody {
        private SurlDto item;
    }

    @PutMapping("/{id}")
    @Transactional
    public RsData<SurlModifyResBody> modify(
            @PathVariable long id,
            @RequestBody @Valid SurlAddReqBody reqBody) {

        Surl surl = surlService.findById(id).orElseThrow(GlobalException.E404::new);

        authService.checkCanModifySurl(rq.getMember(), surl);

        RsData<Surl> modifyRs = surlService.modify(surl, reqBody.body, reqBody.url);

        return modifyRs.newDataOf(
                new SurlModifyResBody(
                        new SurlDto(modifyRs.getData())
                )
        );
    }
}