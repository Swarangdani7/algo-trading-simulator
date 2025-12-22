package com.swarang.simulation_service.grpc;

import com.google.protobuf.Empty;
import com.swarang.common.grpc.TradeDecision;
import com.swarang.common.grpc.TradeDecisionType;
import com.swarang.portfolio.grpc.Allocation;
import com.swarang.portfolio.grpc.PortfolioInitRequest;
import com.swarang.portfolio.grpc.PortfolioServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PortfolioGrpcClient {

    @GrpcClient("portfolio-service")
    private PortfolioServiceGrpc.PortfolioServiceStub asyncStub;

    public void initializePortfolio(String userId, Map<String, BigDecimal> allocationMap){
        PortfolioInitRequest initRequest = PortfolioInitRequest.newBuilder()
                .setUserId(userId)
                .addAllAllocations(mapAllocation(allocationMap))
                .build();

        asyncStub.initializePortfolio(initRequest, new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty empty) { }

            @Override
            public void onError(Throwable throwable) {
                log.error("Failed to initialize portfolio for user {}", userId, throwable);
            }

            @Override
            public void onCompleted() {
                log.info("Portfolio initialized for user {}", userId);
            }
        });
    }

    public void sendTradeDecision(String userId, String ticker, TradeDecisionType tradeDecisionType,
                                  BigDecimal price, LocalDateTime timestamp) {
        TradeDecision tradeDecision = TradeDecision.newBuilder()
                .setUserId(userId)
                .setTicker(ticker)
                .setDecision(tradeDecisionType)
                .setPrice(price.toPlainString())
                .setTimestamp(timestamp.toString())
                .build();

        log.info("Sending trade decision to portfolio [Ticker: {}, Decision: {}]", tradeDecision.getTicker(), tradeDecision.getDecision().name());
        asyncStub.executeTrade(tradeDecision, new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty empty) { }

            @Override
            public void onError(Throwable throwable) {
                log.error("Portfolio trade execution failed for ticker: {}", ticker, throwable);
            }

            @Override
            public void onCompleted() {
                log.info("Trade decision received for {}", ticker);
            }
        });
    }

    private List<Allocation> mapAllocation(Map<String, BigDecimal> allocations){
        return allocations.entrySet()
                .stream()
                .map(entry -> Allocation.newBuilder()
                        .setTicker(entry.getKey())
                        .setAmount(entry.getValue().toPlainString())
                        .build()
                )
                .toList();
    }
}
