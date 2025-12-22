package com.swarang.strategy_service.grpc;

import com.swarang.strategy.grpc.StrategyDecisionType;
import com.swarang.strategy_service.domain.StrategyDecision;
import org.springframework.stereotype.Component;

@Component
public class StrategyProtoMapper {
    public StrategyDecisionType mapDecision(StrategyDecision decision){
        return switch(decision){
            case BUY -> StrategyDecisionType.BUY;
            case SELL -> StrategyDecisionType.SELL;
            case HOLD -> StrategyDecisionType.HOLD;
        };
    }
}
