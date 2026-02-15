package com.trade.folio.domain.account.service;

import com.trade.folio.domain.account.dto.AccountCreateRequest;
import com.trade.folio.domain.account.dto.AccountSummary;
import com.trade.folio.domain.account.dto.AccountUpdateRequest;
import com.trade.folio.domain.account.entity.Account;
import com.trade.folio.domain.account.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final int MAX_ACCOUNT_COUNT = 3;

    private final AccountMapper accountMapper;

    public List<Account> getAccountsByMemberId(Long memberId) {
        return accountMapper.findByMemberId(memberId);
    }

    public Account getAccountById(Long id) {
        return accountMapper.findById(id);
    }

    public boolean isAccountNameDuplicate(Long memberId, String accountName) {
        return accountMapper.countByMemberIdAndAccountName(memberId, accountName) > 0;
    }

    public Account createAccount(Long memberId, AccountCreateRequest request) {
        /* 계좌 수 제한 */
        int currentCount = accountMapper.countByMemberId(memberId);
        if (currentCount >= MAX_ACCOUNT_COUNT) {
            throw new IllegalArgumentException("계좌는 최대 " + MAX_ACCOUNT_COUNT + "개까지 등록할 수 있습니다.");
        }

        if (isAccountNameDuplicate(memberId, request.getAccountName())) {
            throw new IllegalArgumentException("이미 동일한 이름의 계좌가 존재합니다.");
        }

        Account account = new Account();
        account.setMemberId(memberId);
        account.setAccountName(request.getAccountName());
        account.setBroker(request.getBroker());
        account.setCurrency(request.getCurrency());

        accountMapper.insert(account);
        return account;
    }

    public Account updateAccount(Long accountId, Long memberId, AccountUpdateRequest request) {
        Account account = accountMapper.findById(accountId);
        if (account == null || !account.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("계좌를 찾을 수 없습니다.");
        }

        /* 이름 변경 시 중복 검사 (기존 이름과 다를 때만) */
        if (!account.getAccountName().equals(request.getAccountName())
                && isAccountNameDuplicate(memberId, request.getAccountName())) {
            throw new IllegalArgumentException("이미 동일한 이름의 계좌가 존재합니다.");
        }

        account.setAccountName(request.getAccountName());
        account.setBroker(request.getBroker());
        account.setCurrency(request.getCurrency());

        accountMapper.update(account);
        return account;
    }

    public void deleteAccount(Long accountId, Long memberId) {
        Account account = accountMapper.findById(accountId);
        if (account == null || !account.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("계좌를 찾을 수 없습니다.");
        }
        accountMapper.deleteById(accountId);
    }

    /**
     * 계좌 요약 정보 계산
     * - totalBalance: 모든 계좌의 balance 합계
     * - accountCount: 계좌 수
     * - monthlyPnl: 이번 달 P&L (추후 trading_history 테이블 연동 시 계산)
     */
    public AccountSummary getSummary(Long memberId) {
        List<Account> accounts = accountMapper.findByMemberId(memberId);

        AccountSummary summary = new AccountSummary();
        summary.setAccountCount(accounts.size());

        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalBalance(totalBalance);

        // TODO: trading_history 테이블 연동 후 실제 P&L 계산
        summary.setMonthlyPnl(BigDecimal.ZERO);

        return summary;
    }
}
