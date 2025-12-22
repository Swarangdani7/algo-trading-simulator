package com.swarang.portfolio_service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PortfolioResponseSummary(
        String userId,
        BigDecimal initialCapital,
        BigDecimal cashBalance,
        BigDecimal investedAmount,
        BigDecimal totalPnL,
        BigDecimal totalPnLPercentage
) { }
