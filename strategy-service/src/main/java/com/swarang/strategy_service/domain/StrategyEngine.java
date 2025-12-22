package com.swarang.strategy_service.domain;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class StrategyEngine {
    // Using Mean reversion strategy
    private final BigDecimal threshold;

    public StrategyEngine(BigDecimal threshold){
        this.threshold = threshold;
    }

    public StrategyDecision evaluate(BigDecimal currentPrice, BigDecimal averagePrice){
        /*BigDecimal lowerAverageThreshold = averagePrice.multiply(BigDecimal.ONE.subtract(threshold));
        BigDecimal upperAverageThreshold = averagePrice.multiply(BigDecimal.ONE.add(threshold));*/

        // Buy if current price drops below average
        if(currentPrice.compareTo(averagePrice) <= 0){
            return StrategyDecision.BUY;
        }
        // Sell if current price rises above average
        if(currentPrice.compareTo(averagePrice) >= 0){
            return StrategyDecision.SELL;
        }
        return StrategyDecision.HOLD;
    }
}
