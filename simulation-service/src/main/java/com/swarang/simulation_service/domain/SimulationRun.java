package com.swarang.simulation_service.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
public class SimulationRun {
    private final String runId;
    private final String userId;
    private final Map<String, BigDecimal> allocations;
    private volatile boolean isActive;

    public SimulationRun(String userId, Map<String, BigDecimal> allocations){
        this.runId = UUID.randomUUID().toString();
        this.userId = userId;
        this.allocations = allocations;
        this.isActive = true;
    }

    public boolean isTracking(String ticker){
        return allocations.containsKey(ticker);
    }

    public void stop(){
        this.isActive = false;
    }
}
