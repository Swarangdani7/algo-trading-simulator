package com.swarang.portfolio_service.application;

import com.swarang.common.grpc.TradeDecision;
import com.swarang.portfolio.grpc.Allocation;
import com.swarang.portfolio_service.domain.Portfolio;
import com.swarang.portfolio_service.domain.StrategyDecision;
import com.swarang.portfolio_service.domain.TickerAllocation;
import com.swarang.portfolio_service.dto.PortfolioResponseSummary;
import com.swarang.portfolio_service.persistence.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PortfolioApplicationService {

    private final PortfolioRepository portfolioRepository;
    private static final BigDecimal INITIAL_PORTFOLIO_CAPITAL = new BigDecimal("100000");

    // @Transactional
    public void handleTradeDecision(TradeDecision tradeDecision) {
        Portfolio portfolio = portfolioRepository.findByUserId(tradeDecision.getUserId())
                .orElseThrow(() -> new IllegalStateException("Portfolio not found"));

        portfolio.executeTradeDecision(
                tradeDecision.getTicker(),
                mapDecision(tradeDecision),
                new BigDecimal(tradeDecision.getPrice()),
                LocalDateTime.parse(tradeDecision.getTimestamp())
        );
        portfolioRepository.save(portfolio);
    }

    // @Transactional
    public void initializePortfolio(String userId, List<Allocation> allocations) {
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
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found"));

        BigDecimal totPnl = portfolio.getCurrentBalance().amount().subtract(portfolio.getInitialCapital().amount());
        BigDecimal totPnlPercent = totPnl.divide(portfolio.getInitialCapital().amount(), 2, RoundingMode.DOWN);

        return PortfolioResponseSummary.builder()
                .userId(portfolio.getUserId())
                .initialCapital(portfolio.getInitialCapital().amount())
                .cashBalance(portfolio.getCurrentBalance().amount())
                .investedAmount(portfolio.getInvestedAmount().amount())
                .totalPnL(totPnl)
                .totalPnLPercentage(totPnlPercent)
                .build();
    }
}
