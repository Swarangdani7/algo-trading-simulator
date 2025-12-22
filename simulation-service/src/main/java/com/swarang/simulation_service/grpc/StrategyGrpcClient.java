package com.swarang.simulation_service.grpc;

import com.swarang.strategy.grpc.PriceUpdateRequest;
import com.swarang.strategy.grpc.StrategyServiceGrpc;
import com.swarang.strategy.grpc.StrategySignal;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@Component
@Slf4j
public class StrategyGrpcClient {

    @GrpcClient("strategy-service")
    private StrategyServiceGrpc.StrategyServiceStub asyncStub;

    public void evaluateAsync(String ticker, BigDecimal price, LocalDateTime now,
                              Consumer<StrategySignal> strategySignalCallback) {
        PriceUpdateRequest priceUpdateRequest = PriceUpdateRequest.newBuilder()
                .setTicker(ticker)
                .setPrice(price.toPlainString())
                .setTimestamp(now.toString())
                .build();

        asyncStub.evaluate(priceUpdateRequest, new StreamObserver<StrategySignal>() {
            @Override
            public void onNext(StrategySignal strategySignal) {
                strategySignalCallback.accept(strategySignal);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Error evaluating strategy for ticker: {}", ticker, throwable);
            }

            @Override
            public void onCompleted() { }
        });
    }
}
