package com.trade.folio.domain.account.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class Account {

    private Long id;
    private Long memberId;
    private String accountName;
    private String broker;
    private String currency;
    private BigDecimal balance;
    private OffsetDateTime updatedAt;
    private OffsetDateTime createdAt;
}
