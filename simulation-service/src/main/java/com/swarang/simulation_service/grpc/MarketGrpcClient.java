package com.swarang.simulation_service.grpc;

import com.google.protobuf.Empty;
import com.swarang.market.grpc.MarketPrice;
import com.swarang.market.grpc.MarketPriceStreamGrpc;
import com.swarang.market.grpc.MarketSubscription;
import com.swarang.market.grpc.MarketUnsubscriptionRequest;
import com.swarang.simulation_service.application.SimulationService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@Slf4j
public class MarketGrpcClient {

    @GrpcClient("market-service")
    private MarketPriceStreamGrpc.MarketPriceStreamStub asyncStub;

    public void subscribe(String runId, Map<String, BigDecimal> allocations, SimulationService simulationService){
        MarketSubscription subscription = MarketSubscription.newBuilder()
                .setRunId(runId)
                .addAllTickers(allocations.keySet())
                .build();

        asyncStub.streamPrices(subscription, new StreamObserver<MarketPrice>() {
            @Override
            public void onNext(MarketPrice price) {
                simulationService.onMarketPrice(
                        price.getTicker(),
                        new BigDecimal(price.getPrice()),
                        LocalDateTime.parse(price.getTimestamp())
                );
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Error fetching market prices", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("Market stream completed");
            }
        });
    }

    public void unsubscribe(String runId){
        MarketUnsubscriptionRequest unsubscriptionRequest = MarketUnsubscriptionRequest.newBuilder()
                .setRunId(runId)
                .build();

        asyncStub.unsubscribe(unsubscriptionRequest, new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty empty) { }

            @Override
            public void onError(Throwable throwable) {
                log.error("An error occurred while unsubscribing for subID: {}", runId, throwable);
            }

            @Override
            public void onCompleted() {
                log.info("Client unsubscribed with subId: {}", runId);
            }
        });
    }
}
