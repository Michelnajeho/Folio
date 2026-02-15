package com.trade.folio.domain.exchange.controller;

import com.trade.folio.common.api.ApiResponse;
import com.trade.folio.domain.exchange.dto.ExchangeRateDto;
import com.trade.folio.domain.exchange.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeApiController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/rates")
    public ApiResponse<List<ExchangeRateDto>> getCurrentRates() {
        List<ExchangeRateDto> rates = exchangeRateService.getCurrentRates();
        return ApiResponse.ok(rates);
    }

    @PostMapping("/refresh")
    public ApiResponse<List<ExchangeRateDto>> refreshRates() {
        List<ExchangeRateDto> rates = exchangeRateService.refreshRates();
        return ApiResponse.ok(rates);
    }
}
