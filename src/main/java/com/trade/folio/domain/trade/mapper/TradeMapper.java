package com.trade.folio.domain.trade.mapper;

import com.trade.folio.domain.trade.entity.Trade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface TradeMapper {

    void insert(Trade trade);

    List<Trade> findByAccountId(@Param("accountId") Long accountId);

    Trade findById(@Param("id") Long id);

    void update(Trade trade);

    void deleteById(@Param("id") Long id);

    /** 계좌별 이번 달(KST) P&L 합계 */
    BigDecimal sumMonthlyPnlByAccountId(@Param("accountId") Long accountId);

    /** 계좌별 오늘(KST) P&L 합계 */
    BigDecimal sumTodayPnlByAccountId(@Param("accountId") Long accountId);

    /** 회원 전체 계좌의 이번 달(KST) P&L 합계 */
    BigDecimal sumMonthlyPnlByMemberId(@Param("memberId") Long memberId);

    /** 회원의 전체 거래 목록 (traded_at DESC) — 대시보드 스탯 카드용 */
    List<Trade> findByMemberId(@Param("memberId") Long memberId);

    /** 회원의 날짜별 거래 요약 (trade_date, trade_count, daily_pnl) — 대시보드 캘린더용 */
    List<Map<String, Object>> findDailySummaryByMemberId(@Param("memberId") Long memberId);
}
