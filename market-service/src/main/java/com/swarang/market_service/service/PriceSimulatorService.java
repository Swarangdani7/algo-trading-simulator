package com.swarang.market_service.service;

import com.swarang.market_service.dto.TickerPriceResponse;
import com.swarang.market_service.model.Ticker;
import com.swarang.market_service.model.TickerPrice;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceSimulatorService {
    @Getter
    private final Map<Ticker, TickerPrice> tickerMap = new ConcurrentHashMap<>();
    private final MarketPricePublisher marketPricePublisher;
    private final Random random = new Random();
    private static final MathContext PRICE_CONTEXT = new MathContext(12, RoundingMode.HALF_UP);

    @PostConstruct
    public void initializePrices() {
        for (Ticker ticker : Ticker.values()) {
            tickerMap.put(ticker, TickerPrice.builder()
                    .ticker(ticker)
                    .price(ticker.getInitialPrice())
                    .timestamp(LocalDateTime.now())
                    .build());
        }
        log.info("Initialized {} tickers", tickerMap.size());
    }

    private void updatePrice(Ticker ticker) {
        double random_factor = (random.nextDouble() * 2 - 1) * 0.01; // 1% (+/-) change
        BigDecimal currentPrice = tickerMap.get(ticker).price();
        BigDecimal newPrice = currentPrice.multiply(BigDecimal.valueOf(1 + random_factor), PRICE_CONTEXT)
                .max(BigDecimal.ONE)
                .setScale(2, RoundingMode.HALF_UP);

        TickerPrice updatedPrice = TickerPrice.builder()
                .ticker(ticker)
                .price(newPrice)
                .timestamp(LocalDateTime.now())
                .build();

        tickerMap.put(ticker, updatedPrice);
        marketPricePublisher.publish(updatedPrice);
    }

    public void updateAllPrices() {
        Arrays.stream(Ticker.values()).forEach(this::updatePrice);
    }

    public List<TickerPriceResponse> getCurrentPrices() {
        return tickerMap.values()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TickerPriceResponse getTickerPrice(Ticker ticker) {
        TickerPrice tickerPrice = tickerMap.get(ticker);
        return toResponse(tickerPrice);
    }

    private TickerPriceResponse toResponse(TickerPrice tickerPrice){
        return TickerPriceResponse.builder()
                .ticker(tickerPrice.ticker())
                .price(tickerPrice.price().toPlainString())
                .timestamp(tickerPrice.timestamp())
                .build();
    }
}
