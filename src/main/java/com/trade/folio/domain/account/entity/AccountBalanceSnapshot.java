package com.trade.folio.domain.account.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class AccountBalanceSnapshot {

    private Long id;
    private Long accountId;
    private BigDecimal balance;
    private LocalDate snapshotDate;
    private OffsetDateTime createdAt;
}
