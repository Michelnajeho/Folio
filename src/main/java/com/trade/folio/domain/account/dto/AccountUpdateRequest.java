package com.trade.folio.domain.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccountUpdateRequest {

    @NotBlank(message = "계좌명은 필수입니다.")
    @Size(max = 100, message = "계좌명은 100자 이내로 입력하세요.")
    private String accountName;

    @Size(max = 50, message = "브로커명은 50자 이내로 입력하세요.")
    private String broker;

    @NotBlank(message = "통화는 필수입니다.")
    @Size(max = 10)
    private String currency;
}
