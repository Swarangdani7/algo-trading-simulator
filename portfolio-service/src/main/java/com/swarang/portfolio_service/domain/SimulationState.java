package com.swarang.portfolio_service.domain;

import lombok.Builder;

@Builder
public record SimulationState(
        boolean isSimulationStarted,
        boolean isTradeFinished
) { }
