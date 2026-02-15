package com.trade.folio.domain.trade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeUpdateRequest {

    @NotBlank(message = "유형은 필수입니다.")
    private String type;  // STOCK / CRYPTO / FUTURES

    @NotBlank(message = "티커는 필수입니다.")
    @Size(max = 30, message = "티커는 30자 이내로 입력하세요.")
    private String ticker;

    @NotBlank(message = "포지션은 필수입니다.")
    private String position;  // LONG / SHORT

    @NotNull(message = "손익은 필수입니다.")
    private BigDecimal pnl;

    @Size(max = 200, message = "메모는 200자 이내로 입력하세요.")
    private String memo;
}
