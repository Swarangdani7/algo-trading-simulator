package com.swarang.strategy_service.grpc;

import com.swarang.strategy.grpc.PriceUpdateRequest;
import com.swarang.strategy.grpc.StrategyServiceGrpc;
import com.swarang.strategy.grpc.StrategySignal;
import com.swarang.strategy_service.domain.StrategyResult;
import com.swarang.strategy_service.domain.TickerPrice;
import com.swarang.strategy_service.service.StrategyEvaluationService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class StrategyGrpcService extends StrategyServiceGrpc.StrategyServiceImplBase {
    private final StrategyEvaluationService strategyEvaluationService;
    private final StrategyProtoMapper mapper;

    @Override
    public void evaluate(PriceUpdateRequest request, StreamObserver<StrategySignal> responseObserver) {
        TickerPrice tickerPrice = TickerPrice.builder()
                .ticker(request.getTicker())
                .price(new BigDecimal(request.getPrice()))
                .timestamp(LocalDateTime.parse(request.getTimestamp()))
                .build();

        StrategyResult result = strategyEvaluationService.applyMeanReversion(tickerPrice);
        StrategySignal strategySignal = StrategySignal.newBuilder()
                .setTicker(result.ticker())
                .setDecision(mapper.mapDecision(result.decision()))
                .build();

        responseObserver.onNext(strategySignal);
        responseObserver.onCompleted();
    }
}
