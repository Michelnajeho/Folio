package com.trade.folio.domain.account.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountSummary {

    private BigDecimal totalBalance;
    private int accountCount;
    private BigDecimal monthlyPnl;
}
