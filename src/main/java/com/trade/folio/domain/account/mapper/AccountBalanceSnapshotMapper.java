package com.trade.folio.domain.account.mapper;

import com.trade.folio.domain.account.entity.AccountBalanceSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

@Mapper
public interface AccountBalanceSnapshotMapper {

    /**
     * 오늘 날짜(UTC)로 스냅샷 upsert
     * INSERT → 신규, ON CONFLICT → balance 덮어쓰기
     */
    void upsert(@Param("accountId") Long accountId, @Param("balance") BigDecimal balance);

    /**
     * 특정 날짜 이전의 가장 최근 스냅샷 조회 (SOD 잔고 용도)
     */
    AccountBalanceSnapshot findLatestBefore(@Param("accountId") Long accountId, @Param("date") LocalDate date);

    /**
     * 특정 날짜의 스냅샷 조회
     */
    AccountBalanceSnapshot findByAccountIdAndDate(@Param("accountId") Long accountId, @Param("date") LocalDate date);
}
