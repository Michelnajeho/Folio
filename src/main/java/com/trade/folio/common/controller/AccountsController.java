package com.trade.folio.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountsController {

    @GetMapping("/accounts")
    public String accounts(Model model) {
        model.addAttribute("menu", "accounts");
        return "accounts";
    }
}
