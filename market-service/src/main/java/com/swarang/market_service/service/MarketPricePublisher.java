package com.swarang.market_service.service;

import com.swarang.market.grpc.MarketPrice;
import com.swarang.market_service.model.TickerPrice;
import com.swarang.market_service.registry.MarketSubscriberRegistry;
import io.grpc.stub.ServerCallStreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketPricePublisher {

    private final MarketSubscriberRegistry registry;

    public void publish(TickerPrice tickerPrice){
        MarketPrice marketPrice = MarketPrice.newBuilder()
                .setTicker(tickerPrice.ticker().name())
                .setPrice(tickerPrice.price().toPlainString())
                .setTimestamp(tickerPrice.timestamp().toString())
                .build();

        registry.fetchAllSubscribers()
                .forEach(observer -> {
                    try{
                        ServerCallStreamObserver<MarketPrice> serverObserver =
                                (ServerCallStreamObserver<MarketPrice>) observer;

                        if(serverObserver.isReady()){
                            serverObserver.onNext(marketPrice);
                        }
                    }
                    catch (Exception e){
                        try{
                            observer.onError(e);
                        }
                        finally {
                            // Perform cleanup operations
                            log.info("Performing cleanup operations, removing observer from market registry");
                            registry.removeObserver(observer);
                        }
                    }
                });
    }
}
