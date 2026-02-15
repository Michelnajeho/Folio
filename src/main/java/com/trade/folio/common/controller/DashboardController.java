package com.trade.folio.common.controller;

import com.trade.folio.domain.account.dto.AccountSummary;
import com.trade.folio.domain.account.service.AccountService;
import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.mapper.MemberMapper;
import com.trade.folio.domain.trade.entity.Trade;
import com.trade.folio.domain.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AccountService accountService;
    private final TradeService tradeService;
    private final MemberMapper memberMapper;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        Member member = memberMapper.findByLoginId(principal.getName());
        AccountSummary summary = accountService.getSummary(member.getId());

        /* 회원의 전체 거래 목록 → 스탯 카드 계산 */
        List<Trade> trades = tradeService.getTradesByMemberId(member.getId());
        Map<String, Object> stats = computeStats(trades);

        /* 날짜별 거래 요약 → Activity 히트맵 + Trade Calendar */
        List<Map<String, Object>> dailySummary = tradeService.getDailySummaryByMemberId(member.getId());

        model.addAttribute("menu", "dashboard");
        model.addAttribute("summary", summary);
        model.addAttribute("stats", stats);
        model.addAttribute("dailySummary", dailySummary);
        return "dashboard";
    }

    /**
     * 대시보드 4개 스탯 카드 데이터 계산
     */
    private Map<String, Object> computeStats(List<Trade> trades) {
        Map<String, Object> stats = new HashMap<>();

        int totalTrades = trades.size();
        stats.put("totalTrades", totalTrades);

        DecimalFormat df = new DecimalFormat("#,##0.00");

        if (totalTrades == 0) {
            stats.put("winRate", "-");
            stats.put("winRatePositive", false);
            stats.put("totalPnl", "+0.00");
            stats.put("totalPnlPositive", false);
            stats.put("avgReturn", "-");
            stats.put("avgReturnPositive", false);
            return stats;
        }

        int wins = 0;
        BigDecimal totalPnl = BigDecimal.ZERO;

        for (Trade trade : trades) {
            BigDecimal pnl = trade.getPnl();
            totalPnl = totalPnl.add(pnl);
            if (pnl.signum() > 0) {
                wins++;
            }
        }

        /* Win Rate */
        BigDecimal winRate = BigDecimal.valueOf(wins)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalTrades), 1, RoundingMode.HALF_UP);
        stats.put("winRate", winRate.toPlainString() + "%");
        stats.put("winRatePositive", winRate.compareTo(BigDecimal.valueOf(50)) >= 0);

        /* Total P&L — 포맷된 문자열로 전달 */
        String totalPnlStr = (totalPnl.signum() >= 0 ? "+" : "") + df.format(totalPnl);
        stats.put("totalPnl", totalPnlStr);
        stats.put("totalPnlPositive", totalPnl.signum() >= 0);

        /* Avg Return (평균 P&L) — 포맷된 문자열로 전달 */
        BigDecimal avgReturn = totalPnl.divide(BigDecimal.valueOf(totalTrades), 2, RoundingMode.HALF_UP);
        String avgReturnStr = (avgReturn.signum() >= 0 ? "+" : "") + df.format(avgReturn);
        stats.put("avgReturn", avgReturnStr);
        stats.put("avgReturnPositive", avgReturn.signum() >= 0);

        return stats;
    }
}
