package com.trade.folio.domain.account.mapper;

import com.trade.folio.domain.account.entity.AccountTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountTransactionMapper {

    void insert(AccountTransaction transaction);

    List<AccountTransaction> findByAccountId(@Param("accountId") Long accountId);

    BigDecimal sumTodayByAccountIdAndType(@Param("accountId") Long accountId, @Param("type") String type);

    BigDecimal sumAllByAccountIdAndType(@Param("accountId") Long accountId, @Param("type") String type);

    AccountTransaction findById(@Param("id") Long id);

    void update(AccountTransaction transaction);

    void deleteById(@Param("id") Long id);
}
