package com.trade.folio.common.controller;

import com.trade.folio.domain.exchange.dto.ExchangeRateDto;
import com.trade.folio.domain.exchange.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/exchange")
    public String exchange(Model model) {
        List<ExchangeRateDto> rates = exchangeRateService.getCurrentRates();
        model.addAttribute("menu", "exchange");
        model.addAttribute("rates", rates);
        return "exchange";
    }
}
