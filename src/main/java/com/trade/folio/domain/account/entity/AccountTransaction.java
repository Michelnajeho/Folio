package com.trade.folio.domain.account.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class AccountTransaction {

    private Long id;
    private Long accountId;
    private String type;           // DEPOSIT / WITHDRAWAL
    private BigDecimal amount;
    private String memo;
    private OffsetDateTime transactedAt;
    private OffsetDateTime createdAt;
}
