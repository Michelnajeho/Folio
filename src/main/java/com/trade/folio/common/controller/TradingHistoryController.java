package com.trade.folio.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TradingHistoryController {

    @GetMapping("/trading-history")
    public String tradingHistory(Model model) {
        model.addAttribute("menu", "trading-history");
        return "trading-history";
    }
}
