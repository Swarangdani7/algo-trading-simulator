package com.swarang.strategy_service.store;

import com.swarang.strategy_service.domain.TickerPrice;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PriceWindowStore {
    private final Map<String, Deque<TickerPrice>> price = new ConcurrentHashMap<>();
    private final Duration windowSize;

    public PriceWindowStore(Duration windowSize) {
        this.windowSize = windowSize;
    }

    public void add(TickerPrice tickerPrice) {
        Deque<TickerPrice> priceDeque = price.computeIfAbsent(tickerPrice.ticker(), t -> new ArrayDeque<>());
        synchronized (priceDeque){
            priceDeque.addLast(tickerPrice);
            evictOldPrice(priceDeque, tickerPrice.timestamp());
        }
    }

    public List<TickerPrice> getWindow(String ticker) {
        Deque<TickerPrice> priceDeque = price.get(ticker);
        if(priceDeque == null) return List.of();
        synchronized (priceDeque){
            return new ArrayList<>(priceDeque);
        }
    }

    private void evictOldPrice(Deque<TickerPrice> priceDeque, LocalDateTime now) {
        while (!priceDeque.isEmpty() && Duration.between(priceDeque.peekFirst().timestamp(), now)
                .compareTo(windowSize) > 0) {
            priceDeque.pollFirst();
        }
    }
}
