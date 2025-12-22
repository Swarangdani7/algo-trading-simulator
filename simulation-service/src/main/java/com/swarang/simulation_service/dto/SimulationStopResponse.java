package com.swarang.simulation_service.dto;

import lombok.Builder;

@Builder
public record SimulationStopResponse(
        String message,
        String runId
) { }
