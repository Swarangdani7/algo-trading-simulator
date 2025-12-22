package com.swarang.portfolio_service.exception;

public class PortfolioNotFoundException extends RuntimeException{
    public PortfolioNotFoundException(String message){
        super(message);
    }
}
