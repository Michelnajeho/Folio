package com.trade.folio.domain.account.service;

import com.trade.folio.domain.account.entity.AccountBalanceSnapshot;
import com.trade.folio.domain.account.mapper.AccountBalanceSnapshotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class BalanceSnapshotService {

    private final AccountBalanceSnapshotMapper snapshotMapper;

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    /**
     * 잔고 변경 시 오늘(KST) 스냅샷 upsert
     * — 하루에 여러 번 호출돼도 마지막 잔고가 기록됨
     */
    public void recordSnapshot(Long accountId, BigDecimal newBalance) {
        snapshotMapper.upsert(accountId, newBalance);
    }

    /**
     * 오늘 시작 잔고(SOD) 조회
     * = 어제(KST) 이전의 가장 최근 스냅샷
     * 스냅샷이 없으면 null 반환 (최초 사용, 계좌 생성 당일 등)
     */
    public BigDecimal getStartOfDayBalance(Long accountId) {
        LocalDate todayKst = LocalDate.now(SEOUL);
        AccountBalanceSnapshot snapshot = snapshotMapper.findLatestBefore(accountId, todayKst);
        return snapshot != null ? snapshot.getBalance() : null;
    }

    /**
     * 특정 날짜의 EOD 잔고 조회
     */
    public BigDecimal getBalanceAtDate(Long accountId, LocalDate date) {
        AccountBalanceSnapshot snapshot = snapshotMapper.findByAccountIdAndDate(accountId, date);
        return snapshot != null ? snapshot.getBalance() : null;
    }
}
