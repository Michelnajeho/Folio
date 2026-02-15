package com.trade.folio.common.controller;

import com.trade.folio.domain.account.dto.AccountSummary;
import com.trade.folio.domain.account.entity.Account;
import com.trade.folio.domain.account.service.AccountService;
import com.trade.folio.domain.account.service.AccountTransactionService;
import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AccountsController {

    private final AccountService accountService;
    private final AccountTransactionService transactionService;
    private final MemberMapper memberMapper;

    @GetMapping("/accounts")
    public String accounts(Model model, Principal principal) {
        Member member = memberMapper.findByLoginId(principal.getName());

        List<Account> accounts = accountService.getAccountsByMemberId(member.getId());
        AccountSummary summary = accountService.getSummary(member.getId());

        /* 계좌별 오늘 입출금 합계 */
        Map<Long, Map<String, BigDecimal>> todayMap = new HashMap<>();
        for (Account acc : accounts) {
            todayMap.put(acc.getId(), transactionService.getTodaySummary(acc.getId()));
        }

        model.addAttribute("menu", "accounts");
        model.addAttribute("accounts", accounts);
        model.addAttribute("summary", summary);
        model.addAttribute("todayMap", todayMap);
        return "accounts";
    }
}
