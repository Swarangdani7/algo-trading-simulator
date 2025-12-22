package com.swarang.portfolio_service.application;

import com.swarang.common.grpc.TradeDecision;
import com.swarang.portfolio.grpc.Allocation;
import com.swarang.portfolio.grpc.TradeFinishResponse;
import com.swarang.portfolio_service.domain.Portfolio;
import com.swarang.portfolio_service.domain.SimulationState;
import com.swarang.portfolio_service.domain.StrategyDecision;
import com.swarang.portfolio_service.domain.TickerAllocation;
import com.swarang.portfolio_service.dto.PortfolioResponseSummary;
import com.swarang.portfolio_service.exception.PortfolioNotFoundException;
import com.swarang.portfolio_service.exception.SimulationNotStartedException;
import com.swarang.portfolio_service.persistence.PortfolioRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PortfolioApplicationService {

    private SimulationState simulationState;
    private final PortfolioRepository portfolioRepository;
    private static final BigDecimal INITIAL_PORTFOLIO_CAPITAL = new BigDecimal("100000");

    @PostConstruct
    public void setup(){
        simulationState = SimulationState.builder()
                .isSimulationStarted(false)
                .isTradeFinished(false)
                .build();
    }

    // @Transactional
    public void handleTradeDecision(TradeDecision tradeDecision) {
        Portfolio portfolio = portfolioRepository.findByUserId(tradeDecision.getUserId())
                .orElseThrow(() -> new IllegalStateException("Portfolio not found"));

        boolean tradeFinished = portfolio.executeTradeDecision(
                tradeDecision.getTicker(),
                mapDecision(tradeDecision),
                new BigDecimal(tradeDecision.getPrice()),
                LocalDateTime.parse(tradeDecision.getTimestamp())
        );
        portfolioRepository.save(portfolio);
        if(tradeFinished){
            boolean simStarted = simulationState.isSimulationStarted();
            simulationState = SimulationState.builder().isSimulationStarted(simStarted).isTradeFinished(true).build();
        }
    }

    // @Transactional
    public void initializePortfolio(String userId, List<Allocation> allocations) {
        simulationState = SimulationState.builder().isSimulationStarted(true).isTradeFinished(false).build();
        Portfolio portfolio = new Portfolio(userId, INITIAL_PORTFOLIO_CAPITAL);
        log.info("Portfolio initialized with {} initial amount", INITIAL_PORTFOLIO_CAPITAL);

        allocations.stream()
                .map(this::mapAllocations)
                .forEach(portfolio::allocate);

        portfolioRepository.save(portfolio);
    }

    private StrategyDecision mapDecision(TradeDecision tradeDecision) {
        return switch (tradeDecision.getDecision()) {
            case BUY -> StrategyDecision.BUY;
            case SELL -> StrategyDecision.SELL;
            case HOLD, UNRECOGNIZED -> StrategyDecision.HOLD;
        };
    }

    private TickerAllocation mapAllocations(Allocation allocation) {
        return new TickerAllocation(allocation.getTicker(), new BigDecimal(allocation.getAmount()));
    }

    public PortfolioResponseSummary getPortfolioSummary(String userId) {
        if(!simulationState.isSimulationStarted()){
            throw new SimulationNotStartedException("Simulation has not yet started");
        }

        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));

        BigDecimal totPnl = portfolio.getTotalPnL();
        BigDecimal totPnlPercent = totPnl.divide(portfolio.getInitialCapital().amount(), 2, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(100.0)).setScale(2, RoundingMode.HALF_UP);

        return PortfolioResponseSummary.builder()
                .userId(portfolio.getUserId())
                .initialCapital(portfolio.getInitialCapital().amount())
                .cashBalance(portfolio.getCurrentBalance().amount())
                .investedAmount(portfolio.getInvestedAmount())
                .totalPnL(totPnl)
                .totalPnLPercentage(totPnlPercent)
                .build();
    }

    public TradeFinishResponse getTradeFinishResponse() {
         return TradeFinishResponse.newBuilder()
                .setFinish(simulationState.isTradeFinished())
                .build();
    }
}
