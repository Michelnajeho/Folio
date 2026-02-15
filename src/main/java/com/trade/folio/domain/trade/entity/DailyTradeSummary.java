package com.trade.folio.domain.trade.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class DailyTradeSummary {

    private Long id;
    private Long accountId;
    private LocalDate summaryDate;
    private Integer tradeCount;
    private Integer winCount;
    private Integer lossCount;
    private BigDecimal totalPnl;
    private BigDecimal totalProfit;
    private BigDecimal totalLoss;
    private BigDecimal bestPnl;
    private BigDecimal worstPnl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
