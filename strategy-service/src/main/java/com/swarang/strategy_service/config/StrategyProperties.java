package com.swarang.strategy_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "strategy")
@Getter
@Setter
public class StrategyProperties {
    private int rollingWindowSize;
    private int warmUpUnits;
    BigDecimal threshold;
}
