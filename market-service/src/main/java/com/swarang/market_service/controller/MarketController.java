package com.swarang.market_service.controller;

import com.swarang.market_service.dto.TickerPriceResponse;
import com.swarang.market_service.model.Ticker;
import com.swarang.market_service.service.PriceSimulatorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "Market Controller", description = "Fetch real time market prices")
@RestController
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {

    private final PriceSimulatorService priceSimulatorService;

    @GetMapping("/prices")
    public Mono<List<TickerPriceResponse>> getLatestPrices(){
        List<TickerPriceResponse> tickerPriceResponseList = priceSimulatorService.getCurrentPrices();
        return Mono.fromSupplier(() -> tickerPriceResponseList);
    }

    @GetMapping("/prices/{ticker}")
    public Mono<TickerPriceResponse> getPrice(@PathVariable Ticker ticker){
        TickerPriceResponse tickerPriceResponse = priceSimulatorService.getTickerPrice(ticker);
        return Mono.fromSupplier(() -> tickerPriceResponse);
    }
}
