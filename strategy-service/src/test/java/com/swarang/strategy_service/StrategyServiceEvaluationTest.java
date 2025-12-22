package com.swarang.strategy_service;

import com.swarang.strategy_service.config.StrategyProperties;
import com.swarang.strategy_service.domain.StrategyDecision;
import com.swarang.strategy_service.domain.StrategyResult;
import com.swarang.strategy_service.domain.TickerPrice;
import com.swarang.strategy_service.service.StrategyEvaluationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StrategyServiceEvaluationTest {

    private StrategyEvaluationService strategyEvaluationService;

    @BeforeEach
    void setup() {
        StrategyProperties properties = new StrategyProperties();
        properties.setThreshold(BigDecimal.valueOf(0.02));
        properties.setRollingWindowSize(5);
        properties.setWarmUpUnits(5);

        strategyEvaluationService = new StrategyEvaluationService(properties);
    }

    private TickerPrice price(String ticker, String price, int minutes) {
        return TickerPrice.builder()
                .ticker(ticker)
                .price(new BigDecimal(price))
                .timestamp(LocalDateTime.now().minusMinutes(minutes))
                .build();
    }

    @Test
    void shouldHoldDuringWarmUp() {
        TickerPrice tickerPrice = TickerPrice.builder()
                .ticker("TCS")
                .price(BigDecimal.valueOf(100.00))
                .timestamp(LocalDateTime.now())
                .build();

        StrategyResult result = strategyEvaluationService.applyMeanReversion(tickerPrice);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(StrategyDecision.HOLD, result.decision());
    }

    @Test
    void shouldGenerateBuySignal() {
        strategyEvaluationService.applyMeanReversion(price("TCS", "100.0", 5));
        strategyEvaluationService.applyMeanReversion(price("TCS", "101.0", 4));
        strategyEvaluationService.applyMeanReversion(price("TCS", "102.0", 3));
        strategyEvaluationService.applyMeanReversion(price("TCS", "103.0", 2));
        strategyEvaluationService.applyMeanReversion(price("TCS", "104.0", 1));

        StrategyResult result = strategyEvaluationService.applyMeanReversion(price("TCS", "99.0", 0));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(StrategyDecision.BUY, result.decision());
    }

    @Test
    void shouldGenerateSellSignal() {
        strategyEvaluationService.applyMeanReversion(price("TCS", "100.0", 5));
        strategyEvaluationService.applyMeanReversion(price("TCS", "101.0", 4));
        strategyEvaluationService.applyMeanReversion(price("TCS", "102.0", 3));
        strategyEvaluationService.applyMeanReversion(price("TCS", "103.0", 2));
        strategyEvaluationService.applyMeanReversion(price("TCS", "104.0", 1));

        StrategyResult result = strategyEvaluationService.applyMeanReversion(price("TCS", "106.0", 0));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(StrategyDecision.SELL, result.decision());
    }
}
