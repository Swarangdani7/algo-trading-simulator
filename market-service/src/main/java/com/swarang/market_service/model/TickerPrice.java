package com.swarang.market_service.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TickerPrice(
        Ticker ticker,
        BigDecimal price,
        LocalDateTime timestamp
) {
}
