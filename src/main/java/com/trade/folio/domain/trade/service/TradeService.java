package com.trade.folio.domain.trade.service;

import com.trade.folio.domain.account.entity.Account;
import com.trade.folio.domain.account.mapper.AccountMapper;
import com.trade.folio.domain.account.service.BalanceSnapshotService;
import com.trade.folio.domain.trade.dto.TradeCreateRequest;
import com.trade.folio.domain.trade.dto.TradeUpdateRequest;
import com.trade.folio.domain.trade.entity.Trade;
import com.trade.folio.domain.trade.mapper.TradeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeMapper tradeMapper;
    private final AccountMapper accountMapper;
    private final BalanceSnapshotService balanceSnapshotService;

    private static final Set<String> VALID_TYPES = Set.of("STOCK", "CRYPTO", "FUTURES");
    private static final Set<String> VALID_POSITIONS = Set.of("LONG", "SHORT");

    /**
     * 거래 기록 생성
     * P&L 만큼 계좌 잔고 업데이트 + 스냅샷 기록
     */
    @Transactional
    public Trade createTrade(Long accountId, Long memberId, TradeCreateRequest request) {
        Account account = accountMapper.findById(accountId);
        if (account == null || !account.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("계좌를 찾을 수 없습니다.");
        }

        String type = request.getType().toUpperCase();
        if (!VALID_TYPES.contains(type)) {
            throw new IllegalArgumentException("유형은 STOCK, CRYPTO, FUTURES만 가능합니다.");
        }

        String position = request.getPosition().toUpperCase();
        if (!VALID_POSITIONS.contains(position)) {
            throw new IllegalArgumentException("포지션은 LONG 또는 SHORT만 가능합니다.");
        }

        Trade trade = new Trade();
        trade.setAccountId(accountId);
        trade.setType(type);
        trade.setTicker(request.getTicker().trim().toUpperCase());
        trade.setPosition(position);
        trade.setPnl(request.getPnl());
        trade.setMemo(request.getMemo());
        tradeMapper.insert(trade);

        /* 잔고 업데이트: P&L 만큼 반영 */
        accountMapper.updateBalance(accountId, request.getPnl());

        /* 잔고 스냅샷 기록 */
        Account updated = accountMapper.findById(accountId);
        balanceSnapshotService.recordSnapshot(accountId, updated.getBalance());

        return trade;
    }

    /**
     * 계좌별 거래 목록 조회
     */
    public List<Trade> getTradesByAccountId(Long accountId) {
        return tradeMapper.findByAccountId(accountId);
    }

    /**
     * 거래 기록 수정
     * 기존 P&L을 되돌린 뒤 새 P&L로 잔고 반영 + 스냅샷 기록
     */
    @Transactional
    public Trade updateTrade(Long tradeId, Long memberId, TradeUpdateRequest request) {
        Trade trade = tradeMapper.findById(tradeId);
        if (trade == null) {
            throw new IllegalArgumentException("거래 기록을 찾을 수 없습니다.");
        }

        Account account = accountMapper.findById(trade.getAccountId());
        if (account == null || !account.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("계좌를 찾을 수 없습니다.");
        }

        String type = request.getType().toUpperCase();
        if (!VALID_TYPES.contains(type)) {
            throw new IllegalArgumentException("유형은 STOCK, CRYPTO, FUTURES만 가능합니다.");
        }

        String position = request.getPosition().toUpperCase();
        if (!VALID_POSITIONS.contains(position)) {
            throw new IllegalArgumentException("포지션은 LONG 또는 SHORT만 가능합니다.");
        }

        /* 1) 기존 P&L의 잔고 영향을 되돌림 */
        BigDecimal oldPnl = trade.getPnl();
        accountMapper.updateBalance(trade.getAccountId(), oldPnl.negate());

        /* 2) 새 P&L로 잔고 반영 */
        accountMapper.updateBalance(trade.getAccountId(), request.getPnl());

        /* 잔고 스냅샷 기록 */
        Account updated = accountMapper.findById(trade.getAccountId());
        balanceSnapshotService.recordSnapshot(trade.getAccountId(), updated.getBalance());

        /* 3) 거래 레코드 업데이트 */
        trade.setType(type);
        trade.setTicker(request.getTicker().trim().toUpperCase());
        trade.setPosition(position);
        trade.setPnl(request.getPnl());
        trade.setMemo(request.getMemo());
        tradeMapper.update(trade);

        return trade;
    }

    /**
     * 계좌별 이번 달(KST) P&L 합계
     */
    public BigDecimal getMonthlyPnlByAccountId(Long accountId) {
        return tradeMapper.sumMonthlyPnlByAccountId(accountId);
    }

    /**
     * 계좌별 오늘(KST) P&L 합계
     */
    public BigDecimal getTodayPnlByAccountId(Long accountId) {
        return tradeMapper.sumTodayPnlByAccountId(accountId);
    }

    /**
     * 회원 전체 계좌의 이번 달(KST) P&L 합계
     */
    public BigDecimal getMonthlyPnlByMemberId(Long memberId) {
        return tradeMapper.sumMonthlyPnlByMemberId(memberId);
    }

    /**
     * 회원의 전체 거래 목록 (traded_at DESC) — 대시보드 스탯 카드용
     */
    public List<Trade> getTradesByMemberId(Long memberId) {
        return tradeMapper.findByMemberId(memberId);
    }

    /**
     * 회원의 날짜별 거래 요약 (trade_date, trade_count, daily_pnl) — 대시보드 캘린더용
     */
    public List<Map<String, Object>> getDailySummaryByMemberId(Long memberId) {
        return tradeMapper.findDailySummaryByMemberId(memberId);
    }

    /**
     * 거래 기록 삭제
     * 해당 P&L의 잔고 영향을 되돌린 뒤 레코드 삭제 + 스냅샷 기록
     */
    @Transactional
    public void deleteTrade(Long tradeId, Long memberId) {
        Trade trade = tradeMapper.findById(tradeId);
        if (trade == null) {
            throw new IllegalArgumentException("거래 기록을 찾을 수 없습니다.");
        }

        Account account = accountMapper.findById(trade.getAccountId());
        if (account == null || !account.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("계좌를 찾을 수 없습니다.");
        }

        /* 잔고 되돌림: P&L의 반대 값 적용 */
        accountMapper.updateBalance(trade.getAccountId(), trade.getPnl().negate());

        /* 잔고 스냅샷 기록 */
        Account updated = accountMapper.findById(trade.getAccountId());
        balanceSnapshotService.recordSnapshot(trade.getAccountId(), updated.getBalance());

        /* 레코드 삭제 */
        tradeMapper.deleteById(tradeId);
    }
}
