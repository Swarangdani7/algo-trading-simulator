package com.swarang.market_service.grpc;

import com.google.protobuf.Empty;
import com.swarang.market.grpc.MarketPrice;
import com.swarang.market.grpc.MarketPriceStreamGrpc;
import com.swarang.market.grpc.MarketSubscription;
import com.swarang.market.grpc.MarketUnsubscriptionRequest;
import com.swarang.market_service.registry.MarketSubscriberRegistry;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class MarketGrpcService extends MarketPriceStreamGrpc.MarketPriceStreamImplBase {

    private final MarketSubscriberRegistry registry;

    @Override
    public void streamPrices(MarketSubscription request, StreamObserver<MarketPrice> responseObserver) {
        ServerCallStreamObserver<MarketPrice> streamObserver =
                (ServerCallStreamObserver<MarketPrice>) responseObserver;

        registry.add(request.getRunId(), responseObserver);

        streamObserver.setOnCancelHandler(() -> {
            registry.remove(request.getRunId());
        });
    }

    @Override
    public void unsubscribe(MarketUnsubscriptionRequest request, StreamObserver<Empty> responseObserver) {
        registry.remove(request.getRunId());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
