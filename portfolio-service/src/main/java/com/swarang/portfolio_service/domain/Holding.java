package com.swarang.portfolio_service.domain;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record Holding(
        String ticker,
        int quantity,
        BigDecimal averageBuyPrice,
        LocalDateTime openedAt
) { }
