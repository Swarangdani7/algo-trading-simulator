package com.swarang.portfolio_service.persistence;

import com.swarang.portfolio_service.domain.Portfolio;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPortfolioRepository implements PortfolioRepository{
    private final Map<String, Portfolio> store = new ConcurrentHashMap<>();
    @Override
    public Optional<Portfolio> findByUserId(String userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        store.put(portfolio.getUserId(), portfolio);
        return portfolio;
    }
}
