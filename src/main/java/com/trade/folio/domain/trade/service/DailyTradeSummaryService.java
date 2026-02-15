package com.trade.folio.domain.trade.service;

import com.trade.folio.domain.trade.mapper.DailyTradeSummaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class DailyTradeSummaryService {

    private final DailyTradeSummaryMapper summaryMapper;

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    /**
     * 특정 계좌의 오늘(KST) 요약을 재계산
     * TradeService에서 create 후 호출
     */
    public void refreshSummary(Long accountId) {
        LocalDate todayKst = LocalDate.now(SEOUL);
        refreshSummary(accountId, todayKst);
    }

    /**
     * 특정 계좌+날짜의 요약을 재계산
     * 해당 날짜의 모든 trade를 다시 집계 (full recalc)
     * 거래가 0건이면 해당 요약 행도 삭제
     */
    public void refreshSummary(Long accountId, LocalDate summaryDate) {
        summaryMapper.upsertFromTrades(accountId, summaryDate);
        summaryMapper.deleteIfEmpty(accountId, summaryDate);
    }
}
