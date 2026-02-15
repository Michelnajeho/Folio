package com.trade.folio.domain.trade.controller;

import com.trade.folio.common.api.ApiResponse;
import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.mapper.MemberMapper;
import com.trade.folio.domain.trade.dto.TradeCreateRequest;
import com.trade.folio.domain.trade.dto.TradeUpdateRequest;
import com.trade.folio.domain.trade.entity.Trade;
import com.trade.folio.domain.trade.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/account/{accountId}/trade")
@RequiredArgsConstructor
public class TradeApiController {

    private final TradeService tradeService;
    private final MemberMapper memberMapper;

    @PostMapping
    public ApiResponse<?> createTrade(
            @PathVariable Long accountId,
            @Valid @RequestBody TradeCreateRequest request,
            Principal principal) {

        Member member = memberMapper.findByLoginId(principal.getName());

        try {
            Trade trade = tradeService.createTrade(accountId, member.getId(), request);
            return ApiResponse.ok(Map.of(
                    "tradeId", trade.getId(),
                    "type", trade.getType(),
                    "ticker", trade.getTicker(),
                    "position", trade.getPosition(),
                    "pnl", trade.getPnl()
            ));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{tradeId}")
    public ApiResponse<?> updateTrade(
            @PathVariable Long accountId,
            @PathVariable Long tradeId,
            @Valid @RequestBody TradeUpdateRequest request,
            Principal principal) {

        Member member = memberMapper.findByLoginId(principal.getName());

        try {
            Trade trade = tradeService.updateTrade(tradeId, member.getId(), request);
            return ApiResponse.ok(Map.of(
                    "tradeId", trade.getId(),
                    "type", trade.getType(),
                    "ticker", trade.getTicker(),
                    "position", trade.getPosition(),
                    "pnl", trade.getPnl()
            ));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{tradeId}")
    public ApiResponse<?> deleteTrade(
            @PathVariable Long accountId,
            @PathVariable Long tradeId,
            Principal principal) {

        Member member = memberMapper.findByLoginId(principal.getName());

        try {
            tradeService.deleteTrade(tradeId, member.getId());
            return ApiResponse.ok();
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
