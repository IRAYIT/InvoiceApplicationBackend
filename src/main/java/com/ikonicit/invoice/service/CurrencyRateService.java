package com.ikonicit.invoice.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

@Service
public class CurrencyRateService {

    // Base currency is SEK. Replace this static map with a live FX
    // lookup (cached, refreshed daily) when ready — every caller here
    // goes through convert(), so only this map needs to change later.
    private static final Map<String, BigDecimal> RATES_FROM_SEK = Map.of(
            "SEK", BigDecimal.ONE,
            "INR", new BigDecimal("8.00") // 1 SEK ≈ 8 INR — update to the live rate
    );

    public static final Set<String> SUPPORTED_CURRENCIES = RATES_FROM_SEK.keySet();

    public boolean isSupported(String currency) {
        return currency != null && RATES_FROM_SEK.containsKey(currency);
    }

    /**
     * Converts an amount from one supported currency to another.
     * Everything routes through SEK as the pivot currency.
     */
    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (amount == null) return BigDecimal.ZERO;
        if (fromCurrency.equals(toCurrency)) return amount;

        BigDecimal fromRate = rateOrThrow(fromCurrency);
        BigDecimal toRate = rateOrThrow(toCurrency);

        // amount (in fromCurrency) → SEK → toCurrency
        BigDecimal amountInSek = amount.divide(fromRate, 6, RoundingMode.HALF_UP);
        return amountInSek.multiply(toRate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal rateOrThrow(String currency) {
        BigDecimal rate = RATES_FROM_SEK.get(currency);
        if (rate == null) {
            throw new RuntimeException("Unsupported currency: " + currency
                    + ". Supported currencies: " + SUPPORTED_CURRENCIES);
        }
        return rate;
    }
}