package com.trade.folio.domain.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionCreateRequest {

    @NotBlank(message = "유형은 필수입니다.")
    private String type;  // DEPOSIT / WITHDRAWAL

    @NotNull(message = "금액은 필수입니다.")
    @DecimalMin(value = "0.01", message = "금액은 0보다 커야 합니다.")
    private BigDecimal amount;

    @Size(max = 200, message = "메모는 200자 이내로 입력하세요.")
    private String memo;
}
