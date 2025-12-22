package com.swarang.portfolio_service.domain;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record Trade(
        String ticker,
        TradeType tradeType,
        int quantity,
        BigDecimal price,
        LocalDateTime openedAt
) { }
