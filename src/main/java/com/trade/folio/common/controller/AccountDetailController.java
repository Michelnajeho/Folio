package com.trade.folio.common.controller;

import com.trade.folio.domain.account.entity.Account;
import com.trade.folio.domain.account.entity.AccountTransaction;
import com.trade.folio.domain.account.service.AccountService;
import com.trade.folio.domain.account.service.AccountTransactionService;
import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AccountDetailController {

    private final AccountService accountService;
    private final AccountTransactionService transactionService;
    private final MemberMapper memberMapper;

    @GetMapping("/accounts/{id}")
    public String accountDetail(@PathVariable Long id, Model model, Principal principal) {
        Member member = memberMapper.findByLoginId(principal.getName());
        Account account = accountService.getAccountById(id);

        /* 존재하지 않거나 본인 계좌가 아닌 경우 */
        if (account == null || !account.getMemberId().equals(member.getId())) {
            return "redirect:/accounts";
        }

        List<AccountTransaction> transactions = transactionService.getTransactions(id);
        Map<String, BigDecimal> todaySummary = transactionService.getTodaySummary(id);
        Map<String, BigDecimal> totalSummary = transactionService.getTotalSummary(id);

        model.addAttribute("menu", "accounts");
        model.addAttribute("account", account);
        model.addAttribute("transactions", transactions);
        model.addAttribute("todaySummary", todaySummary);
        model.addAttribute("totalSummary", totalSummary);
        return "account-detail";
    }
}
