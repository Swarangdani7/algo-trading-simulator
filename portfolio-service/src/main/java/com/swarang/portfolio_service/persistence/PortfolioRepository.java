package com.swarang.portfolio_service.persistence;

import com.swarang.portfolio_service.domain.Portfolio;

import java.util.Optional;

public interface PortfolioRepository {
    Optional<Portfolio> findByUserId(String userId);
    Portfolio save(Portfolio portfolio);
}
