package com.trade.folio.common.controller;

import com.trade.folio.domain.account.entity.Account;
import com.trade.folio.domain.account.service.AccountService;
import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.mapper.MemberMapper;
import com.trade.folio.domain.trade.entity.Trade;
import com.trade.folio.domain.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class TradingHistoryController {

    private final AccountService accountService;
    private final MemberMapper memberMapper;
    private final TradeService tradeService;

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

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

        /* 선택된 계좌의 거래 기록 조회 */
        List<Trade> trades = Collections.emptyList();
        if (selectedAccount != null) {
            trades = tradeService.getTradesByAccountId(selectedAccount.getId());
        }

        /* 통계 계산 */
        Map<String, Object> stats = computeStats(trades);

        model.addAttribute("menu", "trading-history");
        model.addAttribute("accounts", accounts);
        model.addAttribute("selectedAccount", selectedAccount);
        model.addAttribute("trades", trades);
        model.addAttribute("stats", stats);
        return "trading-history";
    }

    /**
     * trades 목록을 기반으로 8개 통계 카드에 필요한 데이터 계산
     */
    private Map<String, Object> computeStats(List<Trade> trades) {
        Map<String, Object> stats = new HashMap<>();

        int totalTrades = trades.size();
        stats.put("totalTrades", totalTrades);

        if (totalTrades == 0) {
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

        int wins = 0;
        BigDecimal totalPnl = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;
        BigDecimal totalLoss = BigDecimal.ZERO;
        BigDecimal best = null;
        BigDecimal worst = null;
        BigDecimal monthPnl = BigDecimal.ZERO;

        LocalDate nowKst = LocalDate.now(SEOUL);
        int currentMonth = nowKst.getMonthValue();
        int currentYear = nowKst.getYear();

        for (Trade trade : trades) {
            BigDecimal pnl = trade.getPnl();
            totalPnl = totalPnl.add(pnl);

            if (pnl.signum() > 0) {
                wins++;
                totalProfit = totalProfit.add(pnl);
            } else if (pnl.signum() < 0) {
                totalLoss = totalLoss.add(pnl.abs());
            }

            if (best == null || pnl.compareTo(best) > 0) best = pnl;
            if (worst == null || pnl.compareTo(worst) < 0) worst = pnl;

            /* 이번 달 P&L */
            if (trade.getTradedAtKst() != null) {
                LocalDate tradeDate = trade.getTradedAtKst().toLocalDate();
                if (tradeDate.getMonthValue() == currentMonth && tradeDate.getYear() == currentYear) {
                    monthPnl = monthPnl.add(pnl);
                }
            }
        }

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
        stats.put("bestTrade", best);
        stats.put("worstTrade", worst);

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
        stats.put("monthPnl", monthPnl);

        return stats;
    }
}
