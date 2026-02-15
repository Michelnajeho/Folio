package com.trade.folio.domain.account.mapper;

import com.trade.folio.domain.account.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountMapper {

    List<Account> findByMemberId(@Param("memberId") Long memberId);

    Account findById(@Param("id") Long id);

    void insert(Account account);

    int countByMemberId(@Param("memberId") Long memberId);

    int countByMemberIdAndAccountName(@Param("memberId") Long memberId, @Param("accountName") String accountName);

    void update(Account account);

    void deleteById(@Param("id") Long id);

    void updateBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);
}
