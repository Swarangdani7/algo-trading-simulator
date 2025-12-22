package com.swarang.market_service.dto;

import com.swarang.market_service.model.Ticker;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TickerPriceResponse(
        Ticker ticker,
        String price,
        LocalDateTime timestamp
) { }
