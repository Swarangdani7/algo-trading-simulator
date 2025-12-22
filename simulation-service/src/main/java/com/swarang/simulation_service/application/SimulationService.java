package com.swarang.simulation_service.application;

import com.swarang.common.grpc.TradeDecisionType;
import com.swarang.simulation_service.domain.SimulationRun;
import com.swarang.simulation_service.dto.AllocationRequest;
import com.swarang.simulation_service.dto.SimulationStopResponse;
import com.swarang.simulation_service.grpc.MarketGrpcClient;
import com.swarang.simulation_service.grpc.PortfolioGrpcClient;
import com.swarang.simulation_service.grpc.StrategyGrpcClient;
import com.swarang.simulation_service.store.InMemorySimulationRunStore;
import com.swarang.strategy.grpc.StrategyDecisionType;
import com.swarang.strategy.grpc.StrategySignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimulationService {

    private final InMemorySimulationRunStore runStore;
    private final StrategyGrpcClient strategyGrpcClient;
    private final PortfolioGrpcClient portfolioGrpcClient;
    private final MarketGrpcClient marketGrpcClient;

    public SimulationRun startSimulation(String userId, Set<AllocationRequest> allocations) {
        Map<String, BigDecimal> allocationMap = allocations.stream()
                .collect(Collectors.toMap(
                        AllocationRequest::ticker,
                        AllocationRequest::amount,
                        (a,b) -> {
                            throw new IllegalArgumentException("Duplicate allocation for ticker");
                        },
                        LinkedHashMap::new
                ));

        log.info("Simulation has been started");
        SimulationRun run = new SimulationRun(userId, allocationMap);
        runStore.save(run);

        // Initialize user portfolio
        portfolioGrpcClient.initializePortfolio(run.getUserId(), allocationMap);

        // subscribe to market service to receive streaming prices
        marketGrpcClient.subscribe(run.getRunId(), allocationMap, this);

        return run;
    }

    public SimulationStopResponse stopSimulation(String runId) {
        runStore.findById(runId).ifPresent(SimulationRun::stop);
        marketGrpcClient.unsubscribe(runId);
        log.info("Client unsubscribed with subId: {}", runId);
        return SimulationStopResponse.builder()
                .message("Simulation has been stopped")
                .runId(runId)
                .build();
    }

    public void onMarketPrice(String ticker, BigDecimal price, LocalDateTime timestamp) {
        portfolioGrpcClient.checkForTradeFinish();
        runStore.findAll()
                .stream()
                .filter(SimulationRun::isActive)
                .filter(run -> run.isTracking(ticker))
                .forEach(run -> evaluateStrategy(run, ticker, price, timestamp));
    }

    private void evaluateStrategy(SimulationRun run, String ticker, BigDecimal price, LocalDateTime timestamp) {
        log.info("Received updated market price, starting strategy evaluation");
        Consumer<StrategySignal> strategyResultCallback = signal -> handleStrategyResult(run, signal, price, timestamp);
        strategyGrpcClient.evaluateAsync(ticker, price, timestamp, strategyResultCallback);
    }

    private void handleStrategyResult(SimulationRun run, StrategySignal strategySignal, BigDecimal price, LocalDateTime timestamp) {
        log.info("Received strategy result [Ticker: {}, Result: {}]", strategySignal.getTicker(), strategySignal.getDecision().name());
        if (strategySignal.getDecision() != StrategyDecisionType.HOLD) {
            portfolioGrpcClient.sendTradeDecision(
                    run.getUserId(),
                    strategySignal.getTicker(),
                    mapDecision(strategySignal.getDecision()),
                    price,
                    timestamp
            );
        }
    }

    private TradeDecisionType mapDecision(StrategyDecisionType decisionType) {
        return switch (decisionType) {
            case BUY -> TradeDecisionType.BUY;
            case SELL -> TradeDecisionType.SELL;
            case HOLD, UNRECOGNIZED -> TradeDecisionType.HOLD;
        };
    }
}
