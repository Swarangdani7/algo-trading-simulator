package com.swarang.portfolio_service.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
public class Portfolio {
    private final String userId;
    private final CashBalance initialCapital;
    private CashBalance currentBalance;
    private CashBalance investedAmount;

    private final Map<String, TickerAllocation> allocations = new HashMap<>();
    private final Map<String, Holding> holdings = new HashMap<>();
    private final List<Trade> trades = new ArrayList<>();

    private static final Duration MAX_HOLD_DURATION = Duration.ofMinutes(15);
    private static final BigDecimal STOP_LOSS_PERCENT = new BigDecimal("0.02");

    public Portfolio(String userId, BigDecimal initialCapital) {
        this.initialCapital = new CashBalance(initialCapital);
        this.currentBalance = new CashBalance(initialCapital);
        this.investedAmount = new CashBalance(BigDecimal.ZERO);
        this.userId = userId;
    }

    public void allocate(TickerAllocation tickerAllocation) {
        if (!currentBalance.hasSufficientBalance(tickerAllocation.allocationAmount())) {
            log.info("Insufficient balance for allocation [Required: {}, Available: {}]", currentBalance, tickerAllocation.allocationAmount());
            throw new IllegalArgumentException("Insufficient cash for allocation");
        }
        currentBalance = currentBalance.subtract(tickerAllocation.allocationAmount());
        investedAmount.add(tickerAllocation.allocationAmount());

        log.info("Allocated {} for ticker: {}, [Current Balance = {}]", tickerAllocation.allocationAmount(), tickerAllocation.ticker(), currentBalance);
        allocations.put(tickerAllocation.ticker(), new TickerAllocation(tickerAllocation.ticker(), currentBalance.amount()));
    }

    public void executeTradeDecision(String ticker, StrategyDecision decision, BigDecimal currentPrice, LocalDateTime now) {
        PositionState positionState = holdings.containsKey(ticker)
                ? PositionState.LONG
                : PositionState.FLAT;

        if (positionState == PositionState.FLAT) {
            if (decision == StrategyDecision.BUY) {
                buy(ticker, currentPrice, now);
            }
            return;
        }
        // Position is Long (Bought/Holding)
        Holding holding = holdings.get(ticker);
        if (decision == StrategyDecision.SELL
                || isStopLossHit(holding, currentPrice)
                || isMaxHoldExceeded(holding, now)) {

            sell(ticker, currentPrice, now);
        }
    }

    private void buy(String ticker, BigDecimal currentPrice, LocalDateTime now) {
        if (holdings.containsKey(ticker)) return;
        TickerAllocation allocation = allocations.get(ticker);
        if (allocation == null) return;

        int quantity = allocation.allocationAmount().divide(currentPrice, RoundingMode.DOWN).intValue();
        if (quantity <= 0) return;

        log.info("Buying {} units of {} at price = {}", quantity, ticker, currentPrice);

        holdings.put(ticker, Holding.builder().ticker(ticker).quantity(quantity).averageBuyPrice(currentPrice).openedAt(now).build());
        trades.add(Trade.builder()
                .ticker(ticker)
                .tradeType(TradeType.BUY)
                .quantity(quantity)
                .price(currentPrice)
                .openedAt(now)
                .build());
    }

    private void sell(String ticker, BigDecimal currentPrice, LocalDateTime now) {
        Holding holding = holdings.remove(ticker);
        if (holding == null) return;

        BigDecimal priceAfterSelling = currentPrice.multiply(BigDecimal.valueOf(holding.quantity()));
        currentBalance = currentBalance.add(priceAfterSelling);
        allocations.remove(ticker);

        log.info("Selling {} units of {} at price = {}", holding.quantity(), ticker, currentPrice);

        trades.add(Trade.builder()
                .ticker(ticker)
                .tradeType(TradeType.SELL)
                .quantity(holding.quantity())
                .price(currentPrice)
                .openedAt(now)
                .build());
    }

    private boolean isStopLossHit(Holding holding, BigDecimal currentPrice) {
        BigDecimal lossPercent = holding.averageBuyPrice().subtract(currentPrice).divide(holding.averageBuyPrice(), 4, RoundingMode.HALF_UP);
        boolean retVal = lossPercent.compareTo(STOP_LOSS_PERCENT) >= 0;

        if (retVal) {
            log.info("Stop loss hit for {}", holding.ticker());
        }
        return retVal;
    }

    private boolean isMaxHoldExceeded(Holding holding, LocalDateTime now) {
        boolean retVal = Duration.between(holding.openedAt(), now)
                .compareTo(MAX_HOLD_DURATION) > 0;

        if (retVal) {
            log.info("Maximum HOLD duration(15 minutes) exceeded for {}", holding.ticker());
        }
        return retVal;
    }

    public BigDecimal getTotalPnL(Map<String, BigDecimal> currentTickerPrices) {
        BigDecimal holdingsValue = holdings.values()
                .stream()
                .map(h -> currentTickerPrices.get(h.ticker()).multiply(BigDecimal.valueOf(h.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return currentBalance.amount()
                .add(holdingsValue)
                .subtract(initialCapital.amount());
    }
}
