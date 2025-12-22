package com.swarang.strategy_service.domain;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TickerPrice(
        String ticker,
        BigDecimal price,
        LocalDateTime timestamp
) { }
