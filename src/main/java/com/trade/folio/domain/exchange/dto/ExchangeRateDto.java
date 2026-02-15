package com.trade.folio.domain.exchange.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRateDto {
    private String currencyCode;
    private String currencyNameEn;
    private String currencyNameKo;
    private String flag;
    private String unit;
    private double rate;
    private double previousRate;
    private double change;
    private double changePercent;
}
