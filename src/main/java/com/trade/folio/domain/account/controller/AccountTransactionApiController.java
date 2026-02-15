package com.trade.folio.domain.account.controller;

import com.trade.folio.common.api.ApiResponse;
import com.trade.folio.domain.account.dto.TransactionCreateRequest;
import com.trade.folio.domain.account.dto.TransactionUpdateRequest;
import com.trade.folio.domain.account.entity.Account;
import com.trade.folio.domain.account.entity.AccountTransaction;
import com.trade.folio.domain.account.mapper.AccountMapper;
import com.trade.folio.domain.account.service.AccountTransactionService;
import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.mapper.MemberMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/account/{accountId}/transaction")
@RequiredArgsConstructor
public class AccountTransactionApiController {

    private final AccountTransactionService transactionService;
    private final AccountMapper accountMapper;
    private final MemberMapper memberMapper;

    @PostMapping
    public ApiResponse<?> createTransaction(
            @PathVariable Long accountId,
            @Valid @RequestBody TransactionCreateRequest request,
            Principal principal) {

        Member member = memberMapper.findByLoginId(principal.getName());

        try {
            AccountTransaction tx = transactionService.createTransaction(accountId, member.getId(), request);

            /* 업데이트된 잔고 조회 */
            Account updated = accountMapper.findById(accountId);

            return ApiResponse.ok(Map.of(
                    "transactionId", tx.getId(),
                    "type", tx.getType(),
                    "amount", tx.getAmount(),
                    "balance", updated.getBalance()
            ));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{transactionId}")
    public ApiResponse<?> updateTransaction(
            @PathVariable Long accountId,
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionUpdateRequest request,
            Principal principal) {

        Member member = memberMapper.findByLoginId(principal.getName());

        try {
            AccountTransaction tx = transactionService.updateTransaction(transactionId, member.getId(), request);

            Account updated = accountMapper.findById(accountId);

            return ApiResponse.ok(Map.of(
                    "transactionId", tx.getId(),
                    "type", tx.getType(),
                    "amount", tx.getAmount(),
                    "balance", updated.getBalance()
            ));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
