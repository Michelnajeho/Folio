package com.trade.folio.common.controller;

import com.trade.folio.domain.account.dto.AccountSummary;
import com.trade.folio.domain.account.service.AccountService;
import com.trade.folio.domain.member.entity.Member;
import com.trade.folio.domain.member.mapper.MemberMapper;
import com.trade.folio.domain.trade.mapper.DailyTradeSummaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AccountService accountService;
    private final DailyTradeSummaryMapper dailyTradeSummaryMapper;
    private final MemberMapper memberMapper;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        Member member = memberMapper.findByLoginId(principal.getName());
        AccountSummary summary = accountService.getSummary(member.getId());

        /* 스탯 카드: 요약 테이블에서 집계 */
        Map<String, Object> rawStats = dailyTradeSummaryMapper.findMemberTotalStats(member.getId());
        Map<String, Object> stats = formatDashboardStats(rawStats);

        /* 날짜별 거래 요약: 요약 테이블에서 조회 후 JS-safe 타입으로 변환 */
        List<Map<String, Object>> rawDaily = dailyTradeSummaryMapper.findDailySummaryByMemberId(member.getId());
        List<Map<String, Object>> dailySummary = new ArrayList<>();
        for (Map<String, Object> row : rawDaily) {
            Map<String, Object> clean = new HashMap<>();
            clean.put("trade_date", String.valueOf(row.get("trade_date")));
            clean.put("trade_count", ((Number) row.get("trade_count")).intValue());
            clean.put("daily_pnl", ((BigDecimal) row.get("daily_pnl")).doubleValue());
            dailySummary.add(clean);
        }

        model.addAttribute("menu", "dashboard");
        model.addAttribute("summary", summary);
        model.addAttribute("stats", stats);
        model.addAttribute("dailySummary", dailySummary);
        return "dashboard";
    }

    /**
     * 요약 테이블에서 조회한 raw 집계 데이터를 대시보드 스탯 카드 포맷으로 변환
     */
    private Map<String, Object> formatDashboardStats(Map<String, Object> raw) {
        Map<String, Object> stats = new HashMap<>();
        DecimalFormat df = new DecimalFormat("#,##0.00");

        int totalTrades = ((Number) raw.get("total_trades")).intValue();
        stats.put("totalTrades", totalTrades);

        if (totalTrades == 0) {
            stats.put("winRate", "-");
            stats.put("winRatePositive", false);
            stats.put("totalPnl", "+0.00");
            stats.put("totalPnlPositive", false);
            stats.put("avgReturn", "-");
            stats.put("avgReturnPositive", false);
            return stats;
        }

        int wins = ((Number) raw.get("win_count")).intValue();
        BigDecimal totalPnl = (BigDecimal) raw.get("total_pnl");

        /* Win Rate */
        BigDecimal winRate = BigDecimal.valueOf(wins)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalTrades), 1, RoundingMode.HALF_UP);
        stats.put("winRate", winRate.toPlainString() + "%");
        stats.put("winRatePositive", winRate.compareTo(BigDecimal.valueOf(50)) >= 0);

        /* Total P&L */
        String totalPnlStr = (totalPnl.signum() >= 0 ? "+" : "") + df.format(totalPnl);
        stats.put("totalPnl", totalPnlStr);
        stats.put("totalPnlPositive", totalPnl.signum() >= 0);

        /* Avg Return */
        BigDecimal avgReturn = totalPnl.divide(BigDecimal.valueOf(totalTrades), 2, RoundingMode.HALF_UP);
        String avgReturnStr = (avgReturn.signum() >= 0 ? "+" : "") + df.format(avgReturn);
        stats.put("avgReturn", avgReturnStr);
        stats.put("avgReturnPositive", avgReturn.signum() >= 0);

        return stats;
    }
}
