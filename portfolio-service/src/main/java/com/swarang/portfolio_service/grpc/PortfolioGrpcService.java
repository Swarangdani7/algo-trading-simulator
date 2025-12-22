package com.swarang.portfolio_service.grpc;

import com.google.protobuf.Empty;
import com.swarang.common.grpc.TradeDecision;
import com.swarang.portfolio.grpc.PortfolioInitRequest;
import com.swarang.portfolio.grpc.PortfolioServiceGrpc;
import com.swarang.portfolio_service.application.PortfolioApplicationService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class PortfolioGrpcService extends PortfolioServiceGrpc.PortfolioServiceImplBase {

    private final PortfolioApplicationService portfolioApplicationService;

    @Override
    public void executeTrade(TradeDecision request, StreamObserver<Empty> responseObserver) {
        log.info("Received trade execution request from simulation service");
        portfolioApplicationService.handleTradeDecision(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void initializePortfolio(PortfolioInitRequest request, StreamObserver<Empty> responseObserver) {
        log.info("Received portfolio initialization request");
        portfolioApplicationService.initializePortfolio(request.getUserId(), request.getAllocationsList());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
