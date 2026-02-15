package com.trade.folio.domain.member.controller;

import com.trade.folio.common.api.ApiResponse;
import com.trade.folio.domain.member.dto.JoinRequest;
import com.trade.folio.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/check-login-id")
    public ApiResponse<Map<String, Boolean>> checkLoginId(@RequestParam String loginId) {
        boolean available = memberService.isLoginIdAvailable(loginId);
        return ApiResponse.ok(Map.of("available", available));
    }

    @GetMapping("/check-email")
    public ApiResponse<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean available = memberService.isEmailAvailable(email);
        return ApiResponse.ok(Map.of("available", available));
    }

    @PostMapping("/join")
    public ApiResponse<Void> join(@Valid @RequestBody JoinRequest request) {
        memberService.join(request);
        return ApiResponse.ok();
    }
}
