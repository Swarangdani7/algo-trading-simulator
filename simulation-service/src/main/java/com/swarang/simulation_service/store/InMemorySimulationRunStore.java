package com.swarang.simulation_service.store;

import com.swarang.simulation_service.domain.SimulationRun;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySimulationRunStore {
    private final Map<String, SimulationRun> simulationRunMap = new ConcurrentHashMap<>();

    public void save(SimulationRun run){
        simulationRunMap.put(run.getRunId(), run);
    }

    public Optional<SimulationRun> findById(String runId){
        return Optional.ofNullable(simulationRunMap.get(runId));
    }

    public void removeById(String runId){
        simulationRunMap.remove(runId);
    }

    public Collection<SimulationRun> findAll(){
        return simulationRunMap.values();
    }
}
