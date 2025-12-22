package com.swarang.market_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketScheduler {

    private final PriceSimulatorService priceSimulatorService;

    @Scheduled(fixedRateString = "${market.price-interval-ms}")
    public void generatePrices(){
        priceSimulatorService.updateAllPrices();
    }
}
