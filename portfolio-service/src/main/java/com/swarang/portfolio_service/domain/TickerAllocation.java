package com.swarang.portfolio_service.domain;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TickerAllocation(
        String ticker,
        BigDecimal allocationAmount
) { }
