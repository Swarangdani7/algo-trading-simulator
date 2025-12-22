package com.swarang.simulation_service.dto;

import java.math.BigDecimal;

public record AllocationRequest(
        String ticker,
        BigDecimal amount
) { }
