package com.trade.folio.common.controller;

import com.trade.folio.domain.account.entity.Account;
import com.trade.folio.domain.account.service.AccountService;
import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.mapper.MemberMapper;
import com.trade.folio.domain.trade.entity.Trade;
import com.trade.folio.domain.trade.mapper.DailyTradeSummaryMapper;
import com.trade.folio.domain.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class TradingHistoryController {

    private final AccountService accountService;
    private final MemberMapper memberMapper;
    private final TradeService tradeService;
    private final DailyTradeSummaryMapper dailyTradeSummaryMapper;

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

        /* 선택된 계좌의 거래 기록 조회 (테이블 표시용) */
        List<Trade> trades = Collections.emptyList();
        if (selectedAccount != null) {
            trades = tradeService.getTradesByAccountId(selectedAccount.getId());
        }

        /* 통계 계산: 요약 테이블에서 조회 */
        Map<String, Object> stats;
        if (selectedAccount != null) {
            Map<String, Object> rawStats = dailyTradeSummaryMapper.findAccountTotalStats(selectedAccount.getId());
            Map<String, Object> rawMonthStats = dailyTradeSummaryMapper.findAccountMonthStats(selectedAccount.getId());
            stats = formatHistoryStats(rawStats, rawMonthStats, trades);
        } else {
            stats = emptyHistoryStats();
        }

        model.addAttribute("menu", "trading-history");
        model.addAttribute("accounts", accounts);
        model.addAttribute("selectedAccount", selectedAccount);
        model.addAttribute("trades", trades);
        model.addAttribute("stats", stats);
        return "trading-history";
    }

    /**
     * 요약 테이블 데이터 + trades(streak용)를 기반으로 8개 통계 카드 데이터 포맷
     */
    private Map<String, Object> formatHistoryStats(
            Map<String, Object> raw, Map<String, Object> rawMonth, List<Trade> trades) {

        int totalTrades = ((Number) raw.get("total_trades")).intValue();
        if (totalTrades == 0) {
            return emptyHistoryStats();
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTrades", totalTrades);

        int wins = ((Number) raw.get("win_count")).intValue();
        BigDecimal totalPnl = (BigDecimal) raw.get("total_pnl");
        BigDecimal totalProfit = (BigDecimal) raw.get("total_profit");
        BigDecimal totalLoss = (BigDecimal) raw.get("total_loss");
        BigDecimal bestPnl = (BigDecimal) raw.get("best_pnl");
        BigDecimal worstPnl = (BigDecimal) raw.get("worst_pnl");
        BigDecimal monthPnl = (BigDecimal) rawMonth.get("total_pnl");

        /* Win Rate */
        BigDecimal winRate = BigDecimal.valueOf(wins)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalTrades), 1, RoundingMode.HALF_UP);
        stats.put("winRate", winRate.toPlainString() + "%");
        stats.put("winRatePositive", winRate.compareTo(BigDecimal.valueOf(50)) >= 0);

        /* Profit Factor */
        if (totalLoss.signum() > 0) {
            BigDecimal pf = totalProfit.divide(totalLoss, 2, RoundingMode.HALF_UP);
            stats.put("profitFactor", pf.toPlainString());
            stats.put("profitFactorPositive", pf.compareTo(BigDecimal.ONE) > 0);
            stats.put("profitFactorNegative", pf.compareTo(BigDecimal.ONE) < 0);
        } else if (totalProfit.signum() > 0) {
            stats.put("profitFactor", "\u221E");
            stats.put("profitFactorPositive", true);
            stats.put("profitFactorNegative", false);
        } else {
            stats.put("profitFactor", "-");
            stats.put("profitFactorPositive", false);
            stats.put("profitFactorNegative", false);
        }

        /* Avg P&L */
        BigDecimal avgPnl = totalPnl.divide(BigDecimal.valueOf(totalTrades), 2, RoundingMode.HALF_UP);
        stats.put("avgPnl", avgPnl);

        /* Best / Worst */
        stats.put("bestTrade", bestPnl != null ? bestPnl : BigDecimal.ZERO);
        stats.put("worstTrade", worstPnl != null ? worstPnl : BigDecimal.ZERO);

        /* Streak: 최근 거래부터 연속 승/패 카운트 (trades는 traded_at DESC 정렬) */
        int streak = 0;
        String streakType = "";
        for (Trade trade : trades) {
            int sign = trade.getPnl().signum();
            String dir = sign > 0 ? "W" : (sign < 0 ? "L" : "");
            if (streak == 0) {
                if (!dir.isEmpty()) {
                    streakType = dir;
                    streak = 1;
                }
            } else if (dir.equals(streakType)) {
                streak++;
            } else {
                break;
            }
        }
        stats.put("streak", streak > 0 ? streakType + streak : "-");
        stats.put("streakPositive", "W".equals(streakType));
        stats.put("streakNegative", "L".equals(streakType));

        /* This Month P&L */
        stats.put("monthPnl", monthPnl != null ? monthPnl : BigDecimal.ZERO);

        return stats;
    }

    private Map<String, Object> emptyHistoryStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTrades", 0);
        stats.put("winRate", "-");
        stats.put("winRatePositive", false);
        stats.put("profitFactor", "-");
        stats.put("profitFactorPositive", false);
        stats.put("profitFactorNegative", false);
        stats.put("avgPnl", BigDecimal.ZERO);
        stats.put("bestTrade", BigDecimal.ZERO);
        stats.put("worstTrade", BigDecimal.ZERO);
        stats.put("streak", "-");
        stats.put("streakPositive", false);
        stats.put("streakNegative", false);
        stats.put("monthPnl", BigDecimal.ZERO);
        return stats;
    }
}
