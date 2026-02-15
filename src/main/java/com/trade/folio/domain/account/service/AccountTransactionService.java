package com.trade.folio.domain.account.service;

import com.trade.folio.domain.account.dto.TransactionCreateRequest;
import com.trade.folio.domain.account.dto.TransactionUpdateRequest;
import com.trade.folio.domain.account.entity.Account;
import com.trade.folio.domain.account.entity.AccountTransaction;
import com.trade.folio.domain.account.mapper.AccountMapper;
import com.trade.folio.domain.account.mapper.AccountTransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

    private final AccountTransactionMapper transactionMapper;
    private final AccountMapper accountMapper;

    /**
     * 입출금 처리
     * 1) account_transaction INSERT
     * 2) account.balance UPDATE (DEPOSIT → +, WITHDRAWAL → -)
     */
    @Transactional
    public AccountTransaction createTransaction(Long accountId, Long memberId, TransactionCreateRequest request) {

        /* 계좌 소유자 검증 */
        Account account = accountMapper.findById(accountId);
        if (account == null || !account.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("계좌를 찾을 수 없습니다.");
        }

        /* 유형 검증 */
        String type = request.getType().toUpperCase();
        if (!"DEPOSIT".equals(type) && !"WITHDRAWAL".equals(type)) {
            throw new IllegalArgumentException("유형은 DEPOSIT 또는 WITHDRAWAL만 가능합니다.");
        }

        /* 출금 시 잔고 부족 검증 */
        if ("WITHDRAWAL".equals(type) && account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("잔고가 부족합니다.");
        }

        /* 트랜잭션 기록 */
        AccountTransaction tx = new AccountTransaction();
        tx.setAccountId(accountId);
        tx.setType(type);
        tx.setAmount(request.getAmount());
        tx.setMemo(request.getMemo());
        transactionMapper.insert(tx);

        /* 잔고 업데이트 */
        BigDecimal balanceChange = "DEPOSIT".equals(type)
                ? request.getAmount()
                : request.getAmount().negate();
        accountMapper.updateBalance(accountId, balanceChange);

        return tx;
    }

    public List<AccountTransaction> getTransactions(Long accountId) {
        return transactionMapper.findByAccountId(accountId);
    }

    /**
     * 오늘 입출금 합계 조회 (계좌별)
     */
    public Map<String, BigDecimal> getTodaySummary(Long accountId) {
        BigDecimal deposits = transactionMapper.sumTodayByAccountIdAndType(accountId, "DEPOSIT");
        BigDecimal withdrawals = transactionMapper.sumTodayByAccountIdAndType(accountId, "WITHDRAWAL");
        return Map.of(
                "deposits", deposits != null ? deposits : BigDecimal.ZERO,
                "withdrawals", withdrawals != null ? withdrawals : BigDecimal.ZERO
        );
    }

    /**
     * 전체 입출금 합계 조회 (계좌별)
     */
    public Map<String, BigDecimal> getTotalSummary(Long accountId) {
        BigDecimal deposits = transactionMapper.sumAllByAccountIdAndType(accountId, "DEPOSIT");
        BigDecimal withdrawals = transactionMapper.sumAllByAccountIdAndType(accountId, "WITHDRAWAL");
        return Map.of(
                "deposits", deposits != null ? deposits : BigDecimal.ZERO,
                "withdrawals", withdrawals != null ? withdrawals : BigDecimal.ZERO
        );
    }

    /**
     * 트랜잭션 수정
     * 기존 금액/타입의 잔고 영향을 되돌린 뒤, 새 금액/타입으로 반영
     */
    @Transactional
    public AccountTransaction updateTransaction(Long transactionId, Long memberId, TransactionUpdateRequest request) {
        AccountTransaction old = transactionMapper.findById(transactionId);
        if (old == null) {
            throw new IllegalArgumentException("거래 내역을 찾을 수 없습니다.");
        }

        Account account = accountMapper.findById(old.getAccountId());
        if (account == null || !account.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("계좌를 찾을 수 없습니다.");
        }

        String newType = request.getType().toUpperCase();
        if (!"DEPOSIT".equals(newType) && !"WITHDRAWAL".equals(newType)) {
            throw new IllegalArgumentException("유형은 DEPOSIT 또는 WITHDRAWAL만 가능합니다.");
        }

        /* 1) 기존 트랜잭션의 잔고 영향을 되돌림 */
        BigDecimal revert = "DEPOSIT".equals(old.getType())
                ? old.getAmount().negate()
                : old.getAmount();
        accountMapper.updateBalance(old.getAccountId(), revert);

        /* 2) 새 값으로 잔고 적용 전 검증 */
        Account refreshed = accountMapper.findById(old.getAccountId());
        if ("WITHDRAWAL".equals(newType) && refreshed.getBalance().compareTo(request.getAmount()) < 0) {
            /* 되돌린 것을 다시 원복 */
            accountMapper.updateBalance(old.getAccountId(), revert.negate());
            throw new IllegalArgumentException("잔고가 부족합니다.");
        }

        /* 3) 새 값으로 잔고 반영 */
        BigDecimal apply = "DEPOSIT".equals(newType)
                ? request.getAmount()
                : request.getAmount().negate();
        accountMapper.updateBalance(old.getAccountId(), apply);

        /* 4) 트랜잭션 레코드 업데이트 */
        old.setType(newType);
        old.setAmount(request.getAmount());
        old.setMemo(request.getMemo());
        transactionMapper.update(old);

        return old;
    }
}
