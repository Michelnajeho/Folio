package com.trade.folio.domain.trade.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface DailyTradeSummaryMapper {

    /** 특정 계좌+날짜의 거래 요약을 raw trade 데이터에서 재계산하여 UPSERT */
    void upsertFromTrades(@Param("accountId") Long accountId, @Param("summaryDate") LocalDate summaryDate);

    /** 해당 날짜에 거래가 0건이면 요약 행 삭제 */
    void deleteIfEmpty(@Param("accountId") Long accountId, @Param("summaryDate") LocalDate summaryDate);

    /** 회원의 전체 계좌에 대한 날짜별 요약 (대시보드 캘린더용) */
    List<Map<String, Object>> findDailySummaryByMemberId(@Param("memberId") Long memberId);

    /** 회원의 전체 계좌 누적 통계 (대시보드 스탯 카드용) */
    Map<String, Object> findMemberTotalStats(@Param("memberId") Long memberId);

    /** 특정 계좌의 전체 누적 통계 (Trading History 스탯 카드용) */
    Map<String, Object> findAccountTotalStats(@Param("accountId") Long accountId);

    /** 특정 계좌의 이번 달(KST) 통계 */
    Map<String, Object> findAccountMonthStats(@Param("accountId") Long accountId);
}
