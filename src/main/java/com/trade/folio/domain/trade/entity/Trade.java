package com.trade.folio.domain.trade.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Data
public class Trade {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private Long id;
    private Long accountId;
    private String type;           // STOCK / CRYPTO / FUTURES
    private String ticker;         // 종목 (BTC, AAPL 등)
    private String position;       // LONG / SHORT
    private BigDecimal pnl;        // 손익
    private String memo;
    private OffsetDateTime tradedAt;
    private OffsetDateTime createdAt;

    /** KST 기준 거래일시 (Thymeleaf 표시용) */
    public LocalDateTime getTradedAtKst() {
        return tradedAt != null
                ? tradedAt.atZoneSameInstant(SEOUL).toLocalDateTime()
                : null;
    }

    /** KST 기준 생성일시 (Thymeleaf 표시용) */
    public LocalDateTime getCreatedAtKst() {
        return createdAt != null
                ? createdAt.atZoneSameInstant(SEOUL).toLocalDateTime()
                : null;
    }
}
