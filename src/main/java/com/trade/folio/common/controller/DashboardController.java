package com.trade.folio.common.controller;

import com.trade.folio.domain.account.dto.AccountSummary;
import com.trade.folio.domain.account.service.AccountService;
import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AccountService accountService;
    private final MemberMapper memberMapper;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        Member member = memberMapper.findByLoginId(principal.getName());
        AccountSummary summary = accountService.getSummary(member.getId());

        model.addAttribute("menu", "dashboard");
        model.addAttribute("summary", summary);
        return "dashboard";
    }
}
