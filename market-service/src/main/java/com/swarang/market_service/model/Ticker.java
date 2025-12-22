package com.swarang.market_service.model;

import java.math.BigDecimal;

public enum Ticker {
    TCS("Tata Consultancy Services", BigDecimal.valueOf(3000.0)),
    INFY("Infosys", BigDecimal.valueOf(1500.0)),
    RELIANCE("Reliance Industries", BigDecimal.valueOf(1200.0)),
    HDFCBANK("HDFC Bank", BigDecimal.valueOf(900.0)),
    BAJFINANCE("Bajaj Finance", BigDecimal.valueOf(800.0)),
    ICICIBANK("ICICI Bank", BigDecimal.valueOf(1350.0)),
    WIPRO("WIPRO", BigDecimal.valueOf(300.0)),
    HCLTECH("HCL Tech", BigDecimal.valueOf(1600.0)),
    LT("Larsen & Toubro", BigDecimal.valueOf(3500.0)),
    SBIN("State Bank of India", BigDecimal.valueOf(900.0));

    private final String companyName;
    private final BigDecimal initialPrice;

    Ticker(String companyName, BigDecimal initialPrice) {
        this.companyName = companyName;
        this.initialPrice = initialPrice;
    }

    public String getCompanyName() {
        return companyName;
    }

    public BigDecimal getInitialPrice() {
        return initialPrice;
    }
}
