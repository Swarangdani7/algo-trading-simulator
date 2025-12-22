package com.swarang.strategy_service.domain;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record StrategyResult(
        String ticker,
        StrategyDecision decision
) { }
