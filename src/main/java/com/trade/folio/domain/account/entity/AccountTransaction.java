package com.trade.folio.domain.account.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Data
public class AccountTransaction {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private Long id;
    private Long accountId;
    private String type;           // DEPOSIT / WITHDRAWAL
    private BigDecimal amount;
    private String memo;
    private OffsetDateTime transactedAt;
    private OffsetDateTime createdAt;

    /** KST 기준 거래일시 (Thymeleaf 표시용) */
    public LocalDateTime getTransactedAtKst() {
        return transactedAt != null
                ? transactedAt.atZoneSameInstant(SEOUL).toLocalDateTime()
                : null;
    }

    /** KST 기준 생성일시 (Thymeleaf 표시용) */
    public LocalDateTime getCreatedAtKst() {
        return createdAt != null
                ? createdAt.atZoneSameInstant(SEOUL).toLocalDateTime()
                : null;
    }
}
