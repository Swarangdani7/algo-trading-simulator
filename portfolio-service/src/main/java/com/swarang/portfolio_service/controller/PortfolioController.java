package com.swarang.portfolio_service.controller;

import com.swarang.portfolio_service.application.PortfolioApplicationService;
import com.swarang.portfolio_service.dto.PortfolioResponseSummary;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "Portfolio Controller", description = "Fetch Portfolio Summary")
@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioApplicationService portfolioApplicationService;

    @GetMapping("/summary")
    public Mono<PortfolioResponseSummary> getSummary(){
        String userId = "dummyUser@123";
        return Mono.fromSupplier(() -> portfolioApplicationService.getPortfolioSummary(userId));
    }
}
