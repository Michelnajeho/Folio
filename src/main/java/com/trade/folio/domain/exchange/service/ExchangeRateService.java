package com.trade.folio.domain.exchange.service;

import com.trade.folio.domain.exchange.dto.ExchangeRateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final RestTemplate restTemplate;

    private static final String ER_API_URL = "https://open.er-api.com/v6/latest/USD";
    private static final String COINGECKO_URL = "https://api.coingecko.com/api/v3/simple/price?ids=tether&vs_currencies=krw,usd";

    /** 인메모리 캐시 */
    private volatile List<ExchangeRateDto> cachedRates;
    private volatile long cacheTimestamp;
    private static final long CACHE_TTL_MS = 60_000L; // 1분 캐시

    /** 이전 환율 (변동 계산용) */
    private final Map<String, Double> previousRates = new ConcurrentHashMap<>();

    /** 통화 메타데이터 */
    private record CurrencyInfo(String code, String nameEn, String nameKo, String flag, String unit) {}

    private static final List<CurrencyInfo> CURRENCIES = List.of(
            new CurrencyInfo("USD", "US Dollar", "미국 달러", "\uD83C\uDDFA\uD83C\uDDF8", "1 USD"),
            new CurrencyInfo("JPY", "Japanese Yen", "일본 엔", "\uD83C\uDDEF\uD83C\uDDF5", "100 JPY"),
            new CurrencyInfo("EUR", "Euro", "유로", "\uD83C\uDDEA\uD83C\uDDFA", "1 EUR"),
            new CurrencyInfo("GBP", "British Pound", "영국 파운드", "\uD83C\uDDEC\uD83C\uDDE7", "1 GBP"),
            new CurrencyInfo("CNY", "Chinese Yuan", "중국 위안", "\uD83C\uDDE8\uD83C\uDDF3", "1 CNY")
    );

    /**
     * 현재 환율 조회 (캐시 → API)
     */
    public List<ExchangeRateDto> getCurrentRates() {
        if (cachedRates != null && System.currentTimeMillis() - cacheTimestamp < CACHE_TTL_MS) {
            return cachedRates;
        }
        return refreshRates();
    }

    /**
     * 환율 강제 새로고침
     */
    @SuppressWarnings("unchecked")
    public List<ExchangeRateDto> refreshRates() {
        List<ExchangeRateDto> rates = new ArrayList<>();

        try {
            // ExchangeRate-API 호출 (USD 기준)
            Map<String, Object> response = restTemplate.getForObject(ER_API_URL, Map.class);
            if (response != null && "success".equals(response.get("result"))) {
                Map<String, Object> ratesMap = (Map<String, Object>) response.get("rates");

                double krwPerUsd = toDouble(ratesMap.get("KRW"));

                for (CurrencyInfo ci : CURRENCIES) {
                    double ratePerUnit = ratesMap.get(ci.code) != null
                            ? toDouble(ratesMap.get(ci.code)) : 0;

                    double krwRate;
                    if ("USD".equals(ci.code)) {
                        krwRate = krwPerUsd;
                    } else if ("JPY".equals(ci.code)) {
                        // JPY: 100엔당 KRW
                        krwRate = ratePerUnit > 0 ? (krwPerUsd / ratePerUnit) * 100 : 0;
                    } else {
                        // 다른 통화: 1단위당 KRW
                        krwRate = ratePerUnit > 0 ? krwPerUsd / ratePerUnit : 0;
                    }

                    double prevRate = previousRates.getOrDefault(ci.code, krwRate);
                    double change = krwRate - prevRate;
                    double changePct = prevRate > 0 ? (change / prevRate) * 100 : 0;

                    rates.add(ExchangeRateDto.builder()
                            .currencyCode(ci.code)
                            .currencyNameEn(ci.nameEn)
                            .currencyNameKo(ci.nameKo)
                            .flag(ci.flag)
                            .unit(ci.unit)
                            .rate(Math.round(krwRate * 100.0) / 100.0)
                            .previousRate(Math.round(prevRate * 100.0) / 100.0)
                            .change(Math.round(change * 100.0) / 100.0)
                            .changePercent(Math.round(changePct * 100.0) / 100.0)
                            .build());

                    previousRates.put(ci.code, krwRate);
                }
            }
        } catch (Exception e) {
            log.warn("ExchangeRate-API 호출 실패: {}", e.getMessage());
            if (cachedRates != null) {
                return cachedRates;
            }
        }

        // USDT/KRW (CoinGecko)
        try {
            Map<String, Object> cgResponse = restTemplate.getForObject(COINGECKO_URL, Map.class);
            if (cgResponse != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tether = (Map<String, Object>) cgResponse.get("tether");
                if (tether != null) {
                    double usdtKrw = toDouble(tether.get("krw"));
                    double prevRate = previousRates.getOrDefault("USDT", usdtKrw);
                    double change = usdtKrw - prevRate;
                    double changePct = prevRate > 0 ? (change / prevRate) * 100 : 0;

                    rates.add(ExchangeRateDto.builder()
                            .currencyCode("USDT")
                            .currencyNameEn("Tether (USDT)")
                            .currencyNameKo("테더 (USDT)")
                            .flag("\uD83D\uDCB2")
                            .unit("1 USDT")
                            .rate(Math.round(usdtKrw * 100.0) / 100.0)
                            .previousRate(Math.round(prevRate * 100.0) / 100.0)
                            .change(Math.round(change * 100.0) / 100.0)
                            .changePercent(Math.round(changePct * 100.0) / 100.0)
                            .build());

                    previousRates.put("USDT", usdtKrw);
                }
            }
        } catch (Exception e) {
            log.warn("CoinGecko API 호출 실패: {}", e.getMessage());
        }

        if (!rates.isEmpty()) {
            cachedRates = rates;
            cacheTimestamp = System.currentTimeMillis();
        }

        return rates.isEmpty() && cachedRates != null ? cachedRates : rates;
    }

    private double toDouble(Object value) {
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        return 0;
    }
}
