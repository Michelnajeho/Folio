package com.trade.folio.common.controller;

import com.trade.folio.domain.account.entity.Account;
import com.trade.folio.domain.account.service.AccountService;
import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TradingHistoryController {

    private final AccountService accountService;
    private final MemberMapper memberMapper;

    @GetMapping("/trading-history")
    public String tradingHistory(
            @RequestParam(required = false) Long accountId,
            Model model,
            Principal principal) {

        Member member = memberMapper.findByLoginId(principal.getName());
        List<Account> accounts = accountService.getAccountsByMemberId(member.getId());

        Account selectedAccount = null;
        if (accountId != null) {
            selectedAccount = accountService.getAccountById(accountId);
            if (selectedAccount == null || !selectedAccount.getMemberId().equals(member.getId())) {
                selectedAccount = null;
            }
        }

        /* accountId 파라미터가 없으면 첫 번째 계좌 자동 선택 */
        if (selectedAccount == null && !accounts.isEmpty()) {
            selectedAccount = accounts.get(0);
        }

        model.addAttribute("menu", "trading-history");
        model.addAttribute("accounts", accounts);
        model.addAttribute("selectedAccount", selectedAccount);
        return "trading-history";
    }
}
