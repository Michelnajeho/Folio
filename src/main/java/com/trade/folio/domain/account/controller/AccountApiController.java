package com.trade.folio.domain.account.controller;

import com.trade.folio.common.api.ApiResponse;
import com.trade.folio.domain.account.dto.AccountCreateRequest;
import com.trade.folio.domain.account.dto.AccountUpdateRequest;
import com.trade.folio.domain.account.entity.Account;
import com.trade.folio.domain.account.service.AccountService;
import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.mapper.MemberMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountApiController {

    private final AccountService accountService;
    private final MemberMapper memberMapper;

    @GetMapping("/check-name")
    public ApiResponse<Map<String, Boolean>> checkAccountName(
            @RequestParam String accountName,
            Principal principal) {

        Member member = memberMapper.findByLoginId(principal.getName());
        boolean duplicate = accountService.isAccountNameDuplicate(member.getId(), accountName);

        return ApiResponse.ok(Map.of("available", !duplicate));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getAccount(@PathVariable Long id, Principal principal) {
        Member member = memberMapper.findByLoginId(principal.getName());
        Account account = accountService.getAccountById(id);

        if (account == null || !account.getMemberId().equals(member.getId())) {
            return ApiResponse.error("계좌를 찾을 수 없습니다.");
        }

        return ApiResponse.ok(Map.of(
                "id", account.getId(),
                "accountName", account.getAccountName(),
                "broker", account.getBroker() != null ? account.getBroker() : "",
                "currency", account.getCurrency(),
                "balance", account.getBalance()
        ));
    }

    @PostMapping
    public ApiResponse<?> createAccount(
            @Valid @RequestBody AccountCreateRequest request,
            Principal principal) {

        Member member = memberMapper.findByLoginId(principal.getName());

        try {
            Account account = accountService.createAccount(member.getId(), request);
            return ApiResponse.ok(Map.of(
                    "id", account.getId(),
                    "accountName", account.getAccountName()
            ));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountUpdateRequest request,
            Principal principal) {

        Member member = memberMapper.findByLoginId(principal.getName());

        try {
            Account account = accountService.updateAccount(id, member.getId(), request);
            return ApiResponse.ok(Map.of(
                    "id", account.getId(),
                    "accountName", account.getAccountName()
            ));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteAccount(@PathVariable Long id, Principal principal) {
        Member member = memberMapper.findByLoginId(principal.getName());

        try {
            accountService.deleteAccount(id, member.getId());
            return ApiResponse.ok();
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
