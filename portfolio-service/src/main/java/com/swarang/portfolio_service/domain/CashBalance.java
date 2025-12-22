package com.swarang.portfolio_service.domain;

import java.math.BigDecimal;

public record CashBalance(BigDecimal amount) {
    public CashBalance add(BigDecimal value){
        return new CashBalance(amount.add(value));
    }
    public CashBalance subtract(BigDecimal value){
        return new CashBalance(amount.subtract(value));
    }
    public boolean hasSufficientBalance(BigDecimal value){
        return amount.compareTo(value) >= 0;
    }
}
