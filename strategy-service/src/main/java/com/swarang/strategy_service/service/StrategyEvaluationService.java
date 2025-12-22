package com.swarang.strategy_service.service;

import com.swarang.strategy_service.config.StrategyProperties;
import com.swarang.strategy_service.domain.StrategyDecision;
import com.swarang.strategy_service.domain.StrategyEngine;
import com.swarang.strategy_service.domain.StrategyResult;
import com.swarang.strategy_service.domain.TickerPrice;
import com.swarang.strategy_service.store.PriceWindowStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class StrategyEvaluationService {

    private final PriceWindowStore windowStore;
    private final StrategyEngine strategyEngine;
    private final int warmUpUnits;

    public StrategyEvaluationService(StrategyProperties properties) {
        this.warmUpUnits = properties.getWarmUpUnits();
        this.windowStore = new PriceWindowStore(Duration.ofMinutes(properties.getRollingWindowSize()));
        this.strategyEngine = new StrategyEngine(properties.getThreshold());
    }

    public StrategyResult applyMeanReversion(TickerPrice tickerPrice) {
        // Add ticker price to rolling window
        windowStore.add(tickerPrice);

        // Fetch the current rolling window
        List<TickerPrice> window = windowStore.getWindow(tickerPrice.ticker());

        // Hold position during warm up phase
        if (window.size() < warmUpUnits) {
            return StrategyResult.builder()
                    .ticker(tickerPrice.ticker())
                    .decision(StrategyDecision.HOLD)
                    .build();
        }

        // Compute rolling average
        BigDecimal averagePrice = window.stream()
                .map(TickerPrice::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(window.size()), 2, RoundingMode.HALF_UP);

        // Make a decision
        StrategyDecision decision = strategyEngine.evaluate(tickerPrice.price(), averagePrice);
        log.info("Strategy decided as {}", decision.name());

        return StrategyResult.builder()
                .ticker(tickerPrice.ticker())
                .decision(decision)
                .build();
    }
}
